package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.message.UserProvider
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.NewFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewFriendPresenter
import com.stratagile.pnrouter.ui.adapter.user.NewFriendListAdapter
import kotlinx.android.synthetic.main.activity_new_friend.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator




/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 21:25:01
 */

class NewFriendActivity : BaseActivity(), NewFriendContract.View {


    @Inject
    internal lateinit var mPresenter: NewFriendPresenter

    lateinit var viewModel:ScanViewModel
    var newFriendListAdapter: NewFriendListAdapter? = null
    var handleUser: UserEntity? = null
    var friendStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun initView() {
        setContentView(R.layout.activity_new_friend)
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun initData() {
        title.text = getString(R.string.new_requests)
        var titles = ArrayList<String>()
        titles.add(getString(R.string.add_contact))
        titles.add("Group Chats")
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    return NewUserFragment()
                } else {
                    return NewGroupFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles.get(position)
            }
        }
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return if (titles == null) 0 else titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.setText(titles.get(index))
                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        viewPager.setCurrentItem(index)
                    }
                })
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        indicator.setNavigator(commonNavigator)
        val titleContainer = commonNavigator.titleContainer // must after setNavigator
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerDrawable = object : ColorDrawable() {
            override fun getIntrinsicWidth(): Int {
                return resources.getDimension(R.dimen.x160).toInt()
            }
        }
        ViewPagerHelper.bind(indicator, viewPager);
//        tabLayout.setupWithViewPager(viewPager)
//        //val transactionVpnRecordList = transactionRecordDao.queryBuilder().where(TransactionRecordDao.Properties.AssetName.eq(AppConfig.currentUseVpn.getVpnName()), TransactionRecordDao.Properties.IsMainNet.eq(isMainNet)).list()
//        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
//        var userIDStr:String = "";
//        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
//        for (i in list) {
//            if (userIDStr.indexOf(i.userId+",") > -1) {
//                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
//                continue
//            }
//            userIDStr = i.userId+","
//        }
//        list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
//        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
//        var showlist = arrayListOf<UserEntity>()
//        /* for (i in list) {
//             if (i.routerUserId !=null && i.routerUserId.equals(selfUserId)) {
//                 if (i.friendStatus != 7) {
//                     showlist.add(i)
//                 }
//             }
//
//         }*/
//        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
//        for (j in localFriendStatusList) {
//            if (j.userId.equals(userId)) {
//                var hasSame = false
//                for (i in showlist) {
//                    if (i.userId.equals(j.friendId)) {
//                        hasSame = true
//                        KLog.i("同一个好友至少有两条好友状态 " + j.friendId)
//                        LogUtil.addLog("同一个好友至少有两条好友状态 " + j.friendId)
//                    }
//                }
//                if (j.friendLocalStatus != 7 && !hasSame) {
//                    var it = UserEntity()
//                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(j.friendId)).list()
//                    if (localFriendList.size > 0)
//                        it = localFriendList.get(0)
//                    showlist.add(it)
//                }
//            }
//        }
//        viewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
//        viewModel.toAddUserId.observe(this, android.arch.lifecycle.Observer<String> { toAddUserId ->
//            KLog.i(toAddUserId)
//            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
//            if (toAddUserId!!.contains(selfUserId!!)) {
//                return@Observer
//            }
//            if (!"".equals(toAddUserId)) {
//                var toAddUserIdTemp = toAddUserId!!.substring(0,toAddUserId!!.indexOf(","))
//                var intent = Intent(this, SendAddFriendActivity::class.java)
//                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
//                for (i in useEntityList) {
//                    if (i.userId.equals(toAddUserIdTemp)) {
//                        var freindStatusData = FriendEntity()
//                        freindStatusData.friendLocalStatus = 7
//                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
//                        if (localFriendStatusList.size > 0)
//                            freindStatusData = localFriendStatusList[0]
//
//                        if(freindStatusData.friendLocalStatus == 0)
//                        {
//                            intent.putExtra("user", i)
//                            startActivity(intent)
//                        }else{
//                            intent = Intent(this, SendAddFriendActivity::class.java)
//                            intent.putExtra("user", i)
//                            startActivity(intent)
//                        }
//                        return@Observer
//                    }
//                }
//                intent = Intent(this, SendAddFriendActivity::class.java)
//                var userEntity = UserEntity()
//                //userEntity.friendStatus = 7
//                userEntity.userId = toAddUserId!!.substring(0,toAddUserId!!.indexOf(","))
//                userEntity.nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") +1,toAddUserId.lastIndexOf(","))
//                userEntity.signPublicKey = toAddUserId!!.substring(toAddUserId!!.lastIndexOf(",") +1,toAddUserId.length)
//                userEntity.timestamp = Calendar.getInstance().timeInMillis
//                userEntity.routerUserId = selfUserId
//                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
//
//
//                var userId = SpUtil.getString(this, ConstantValue.userId, "")
//                var newFriendStatus = FriendEntity()
//                newFriendStatus.userId = userId;
//                newFriendStatus.friendId = toAddUserId!!.substring(0,toAddUserId!!.indexOf(","))
//                newFriendStatus.friendLocalStatus = 3
//                newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
//                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
//
//                intent.putExtra("user", userEntity)
//                startActivity(intent)
//            }
//        })
//        newFriendListAdapter = NewFriendListAdapter(showlist)
//        newFriendListAdapter?.setOnItemClickListener { adapter, view, position ->
//            handleUser = newFriendListAdapter!!.getItem(position)
//            var intent = Intent(this, UserInfoActivity::class.java)
//            intent.putExtra("user", newFriendListAdapter?.getItem(position))
//            startActivityForResult(intent, 1)
//        }
//        newFriendListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
//            when (view.id) {
//                R.id.tvAccept -> {
//                    handleUser = newFriendListAdapter!!.getItem(position)
//                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
//                    var userId = SpUtil.getString(this, ConstantValue.userId, "")
////                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 0)
//                    friendStatus = 0
//
////                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
//                    if(newFriendListAdapter!!.getItem(position)!!.signPublicKey != null)
//                    {
//                        UserProvider.getInstance().accepteAddFriend(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, newFriendListAdapter!!.getItem(position)!!.signPublicKey)
//                        showProgressDialog()
//                    }
//                }
//                R.id.tvRefuse -> {
//                    handleUser = newFriendListAdapter!!.getItem(position)
//                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
//                    var userId = SpUtil.getString(this, ConstantValue.userId, "")
//                    UserProvider.getInstance().refuseAddFriend(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, "")
////                    var addFriendDealReq = AddFriendDealReq(nickName!!, newFriendListAdapter!!.getItem(position)!!.nickName, userId!!, newFriendListAdapter!!.getItem(position)!!.userId, 1)
//                    friendStatus = 5
////                    AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
////                    UserProvider.getInstance().refreshFriend()
//                    showProgressDialog()
//                }
//            }
//        }
//        recyclerView.adapter = newFriendListAdapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        initData()
    }

    override fun setupActivityComponent() {
        DaggerNewFriendComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .newFriendModule(NewFriendModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: NewFriendContract.NewFriendContractPresenter) {
        mPresenter = presenter as NewFriendPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.qrCode) {
//            mPresenter.getScanPermission()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
    override fun getScanPermissionSuccess() {
        var intent = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent, 1)
    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.qr_code, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result")
            if(!result.contains("type_0"))
            {
                toast(getString(R.string.codeerror))
                return;
            }
            viewModel.toAddUserId.value = result.substring(7,result.length)
            return
        }
    }
}