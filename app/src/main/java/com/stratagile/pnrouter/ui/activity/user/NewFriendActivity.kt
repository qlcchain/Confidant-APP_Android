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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.stratagile.pnrouter.entity.events.SetBadge
import com.stratagile.pnrouter.entity.events.UnReadContactCount
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewFriendComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewFriendContract
import com.stratagile.pnrouter.ui.activity.user.module.NewFriendModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewFriendPresenter
import com.stratagile.pnrouter.ui.adapter.user.NewFriendListAdapter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_new_friend.*
import kotlinx.android.synthetic.main.fragment_contact.*
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
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 21:25:01
 */

class NewFriendActivity : BaseActivity(), NewFriendContract.View {


    @Inject
    internal lateinit var mPresenter: NewFriendPresenter

    lateinit var viewModel: ScanViewModel
    var newFriendListAdapter: NewFriendListAdapter? = null
    var handleUser: UserEntity? = null
    var friendStatus = 0
    lateinit var commonNavigator : CommonNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        commonNavigator = CommonNavigator(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun initView() {
        setContentView(R.layout.activity_new_friend)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setBadge(setBadge: SetBadge) {
        var hasNewFriendRequest = false
        var hasNewGroupMemberRequest = false
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var newFriendCount = 0
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                if (i.friendLocalStatus == 0) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(i.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    if (it.routeId.equals(ConstantValue.currentRouterId)) {
                        contactList.add(it)
                    }
                }
                if (i.friendLocalStatus == 3) {
                    hasNewFriendRequest = true
                    newFriendCount++
                }
            }

        }
        var localGroupMemberList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.loadAll()
        localGroupMemberList.forEach {
            if (it.userId.equals(selfUserId) && it.verifyType == 1) {
                newFriendCount++
                hasNewGroupMemberRequest = true
            }
        }

        if (hasNewFriendRequest) {
            (commonNavigator.getPagerTitleView(0) as BadgePagerTitleView).badgeView.visibility = View.VISIBLE
        } else {
            (commonNavigator.getPagerTitleView(0) as BadgePagerTitleView).badgeView.visibility = View.GONE
        }
        if (hasNewGroupMemberRequest) {
            (commonNavigator.getPagerTitleView(1) as BadgePagerTitleView).badgeView.visibility = View.VISIBLE
        } else {
            (commonNavigator.getPagerTitleView(1) as BadgePagerTitleView).badgeView.visibility = View.GONE
        }
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
                if (isCanShotNetCoonect) {
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if (isCanShotNetCoonect) {
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
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return if (titles == null) 0 else titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val badgePagerTitleView = BadgePagerTitleView(context)

                val simplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.setText(titles.get(index))
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_999999)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_2B2B2B)
                simplePagerTitleView.setOnClickListener {
                    viewPager.setCurrentItem(index)
//                    badgePagerTitleView.badgeView = null // cancel badge when click tab
                }
                badgePagerTitleView.innerPagerTitleView = simplePagerTitleView

                val badgeImageView = LayoutInflater.from(context).inflate(R.layout.simple_red_dot_badge_layout, null) as ImageView
                badgePagerTitleView.badgeView = badgeImageView

                // set badge position
                badgePagerTitleView.xBadgeRule = BadgeRule(BadgeAnchor.CONTENT_RIGHT, 0)
                badgePagerTitleView.yBadgeRule = BadgeRule(BadgeAnchor.CONTENT_TOP, 0)

                // don't cancel badge when tab selected
                badgePagerTitleView.isAutoCancelBadge = false

                return badgePagerTitleView
//                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
//                colorTransitionPagerTitleView.normalColor = Color.GRAY
//                colorTransitionPagerTitleView.selectedColor = Color.BLACK
//                colorTransitionPagerTitleView.setText(titles.get(index))
//                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
//                    override fun onClick(view: View) {
//                        viewPager.setCurrentItem(index)
//                    }
//                })
//                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                indicator.lineHeight = resources.getDimension(R.dimen.x4)
                return indicator
            }
        }
        indicator.setNavigator(commonNavigator)
        val titleContainer = commonNavigator.titleContainer // must after setNavigator
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerDrawable = object : ColorDrawable() {
            override fun getIntrinsicWidth(): Int {
                return resources.getDimension(R.dimen.x140).toInt()
            }
        }
        ViewPagerHelper.bind(indicator, viewPager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
//        initData()
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
            if (!result.contains("type_0")) {
                toast(getString(R.string.codeerror))
                return;
            }
            viewModel.toAddUserId.value = result.substring(7, result.length)
            return
        }
    }
}