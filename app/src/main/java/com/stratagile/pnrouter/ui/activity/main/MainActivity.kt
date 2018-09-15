package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.LinearLayout
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.ui.EaseContactListFragment
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.socks.library.KLog
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.login.SelectRouterActivity
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View, PNRouterServiceMessageReceiver.MainInfoBack{
    //别人删除我，服务器给我的推送
    override fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp) {
        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (jDelFriendPushRsp.params.friendId.equals(i.userId)) {
                i.friendStatus = 4
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                EventBus.getDefault().post(FriendChange())
                return
            }
        }
    }

    /**
     * 目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友请求的结果
     */
    override fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp) {
        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            //jAddFriendReplyRsp.params.userId==对方的id
            if (i.userId.equals(jAddFriendReplyRsp.params.userId)) {
                if (jAddFriendReplyRsp.params.result == 0) {
                    i.friendStatus = 0
                } else if (jAddFriendReplyRsp.params.result == 1) {
                    i.friendStatus = 2
                }
                i.nickName = jAddFriendReplyRsp.params.nickname
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                var addFriendReplyReq = AddFriendReplyReq(0, "")
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendReplyReq))
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                return
            }
        }
    }

    /**
     * 当一个用户A被其他用户B请求添加好友，router推送消息到A
     */
    override fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp) {
        runOnUiThread {
            toast(jAddFriendPushRsp.params.friendId)
        }
        var newFriend = UserEntity()
        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (i.userId.equals(jAddFriendPushRsp.params.friendId)) {
                if (i.friendStatus == 0) {
                    return
                } else {
                    i.friendStatus = 3
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                    runOnUiThread {
                        viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                    }
                    return
                }
            }
        }
        newFriend.nickName = jAddFriendPushRsp.params.nickName
        newFriend.friendStatus = 3
        newFriend.userId = jAddFriendPushRsp.params.friendId
        newFriend.addFromMe = false
        newFriend.timestamp = Calendar.getInstance().timeInMillis
        newFriend.noteName = ""
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)
        var addFriendPushReq = AddFriendPushReq(0, "")
        runOnUiThread {
            viewModel.freindChange.value = Calendar.getInstance().timeInMillis
        }
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendPushReq))
    }

    lateinit var viewModel : MainViewModel
    private var conversationListFragment: EaseConversationListFragment? = null
    private var contactListFragment: EaseContactListFragment? = null
    override fun showToast() {
        toast("点击啦。。。。哈哈哈")
        showProgressDialog()
    }

    @Inject
    internal lateinit var mPresenter: MainPresenter

    override fun setPresenter(presenter: MainContract.MainContractPresenter) {
        mPresenter = presenter as MainPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun initData() {
        swipeBackLayout.setEnableGesture(false)
        AppConfig.instance.messageReceiver!!.mainInfoBack = this
        EventBus.getDefault().register(this)
        tvTitle.setOnClickListener {
            startActivity(Intent(this, SelectRouterActivity::class.java))
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.toAddUserId.observe(this, android.arch.lifecycle.Observer<String> { toAddUserId ->
            KLog.i(toAddUserId)
            if (!"".equals(toAddUserId)) {
                var intent  = Intent(this, UserInfoActivity::class.java)
                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                for (i in useEntityList) {
                    if (i.userId.equals(toAddUserId)) {
                        intent.putExtra("user", i)
                        startActivity(intent)
                        return@Observer
                    }
                }
                var userEntity = UserEntity()
                userEntity.friendStatus = 7
                userEntity.userId = toAddUserId
                userEntity.nickName = ""
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
                intent.putExtra("user", userEntity)
                startActivity(intent)
            }
        })
        setToNews()
        ivQrCode.setOnClickListener {
            mPresenter.getScanPermission()
        }
//        SpUtil.putString(this, ConstantValue.userId, "271D61D2976D9A06A7F07274D5198EB511C8A334ACC07844868A9C260233F15E80D50696CC76")
//        bottomNavigation.enableAnimation(false)
//        bottomNavigation.enableShiftingMode(false)
//        bottomNavigation.enableItemShiftingMode(false)
//        bottomNavigation.setTextSize(10F)
//        viewPager.offscreenPageLimit = 2
//        bottomNavigation.setIconSizeAt(0, 17.6f, 21.2f)
//        bottomNavigation.setIconSizeAt(1, 23.6f, 18.8f)
//        bottomNavigation.setIconSizeAt(2, 22F, 18.8f)
//        bottomNavigation.setIconsMarginTop(resources.getDimension(R.dimen.x22).toInt())
//        bottomNavigation.selectedItemId = R.id.item_news
        contactListFragment?.setContactsMap(getContacts())
        conversationListFragment?.setConversationListItemClickListener(
                EaseConversationListFragment.EaseConversationListItemClickListener
                {
                    conversation -> startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId())) })
        //contactListFragment?.setContactListItemClickListener(EaseContactListFragment.EaseContactListItemClickListener { user -> startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, user.username)) })
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when(position) {
                    0 -> return conversationListFragment!!
                    1 -> return FileFragment()
                    2 -> return ContactFragment()
                    else -> return MyFragment()
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        // 为ViewPager添加页面改变事件
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                // 将当前的页面对应的底部标签设为选中状态
                bottomNavigation.getMenu().getItem(position).setChecked(true)
                when (position) {
                    0 -> setToNews()
                    1 -> setToFile()
                    2 -> setToContact()
                    3 -> setToMy()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        // 为bnv设置选择监听事件
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.getItemId()) {
                R.id.item_news -> viewPager.setCurrentItem(0, false)
                R.id.item_file -> viewPager.setCurrentItem(1, false)
                R.id.item_contacts -> viewPager.setCurrentItem(2, false)
                R.id.item_my -> viewPager.setCurrentItem(3, false)
            }
            true
        }
//        tv_hello.text = "hahhaha"
//        tv_hello.setOnClickListener {
//            mPresenter.showToast()
//            startActivity(Intent(this, TestActivity::class.java))
//        }
//        tv_hello.typeface.style
        viewPager.offscreenPageLimit = 4

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange : ConnectStatus) {
        if (statusChange.status == 0) {
            reConnect.visibility = View.GONE
        } else {
            reConnect.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun  getEaseConversationListFragment(): EaseConversationListFragment?
    {
        return conversationListFragment
    }

    fun setToNews() {
        tvTitle.text = getString(R.string.news)
        mainIv1.visibility = View.GONE
        llSort.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
    }

    fun setToFile() {
        tvTitle.text = getString(R.string.file_)
        mainIv1.visibility = View.VISIBLE
        ivQrCode.visibility = View.VISIBLE
        llSort.visibility = View.VISIBLE
    }

    fun setToContact() {
        tvTitle.text = getString(R.string.contacts)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
        llSort.visibility = View.GONE
    }

    fun setToMy() {
        tvTitle.text = getString(R.string.my)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.GONE
        llSort.visibility = View.GONE
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        tvTitle.text = getString(R.string.news)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        contactListFragment = EaseContactListFragment()
        contactListFragment?.hideTitleBar()
    }

    override fun setupActivityComponent() {
        DaggerMainComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, 1)
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.toAddUserId.value = data!!.getStringExtra("result")
            return
        }
    }

    private fun getContacts(): Map<String, EaseUser> {
        val contacts = HashMap<String, EaseUser>()
        val aa = arrayOf("aa", "cc", "ff", "gg", "kk", "ll", "bb", "jj", "oo", "zz", "mm")
        for (i in 1..10) {
            val user = EaseUser(aa[i])
            contacts[aa[i]] = user
        }
        return contacts
    }
}
