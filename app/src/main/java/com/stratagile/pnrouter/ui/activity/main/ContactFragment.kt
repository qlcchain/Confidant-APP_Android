package com.stratagile.pnrouter.ui.activity.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.runOnUiThread
import com.socks.library.KLog

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerContactComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ContactContract
import com.stratagile.pnrouter.ui.activity.main.module.ContactModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactPresenter

import javax.inject.Inject;

import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.AddFriendReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullFriendRsp
import com.stratagile.pnrouter.entity.PullFriendReq
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.user.ContactListAdapter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.fragment_contact.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:33:27
 */

class ContactFragment : BaseFragment(), ContactContract.View, PNRouterServiceMessageReceiver.PullFriendCallBack {
    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()

        for (i in jPullFriendRsp.params.payload) {
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    j.friendStatus = 0
                    j.nickName = i.name
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.friendStatus = 0
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }

        }

        runOnUiThread {
            initData()
        }
    }

    @Inject
    lateinit internal var mPresenter: ContactPresenter

    var contactAdapter : ContactListAdapter? = null

    lateinit var viewModel : MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_contact, null);
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        AppConfig.instance.messageReceiver!!.pullFriendCallBack = this
        viewModel.freindChange.observe(this, Observer<Long> { friendChange ->
            initData()
        })
        newFriend.setOnClickListener {
            startActivity(Intent(activity!!, NewFriendActivity::class.java))
        }
        initData()
        refreshLayout.setOnRefreshListener {
            pullFriendList()
            KLog.i("拉取好友列表")
        }
        pullFriendList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        initData()
    }

    fun pullFriendList() {
        refreshLayout.isRefreshing = false
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var pullFriend = PullFriendReq( selfUserId!!)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullFriend))
    }

    fun initData() {
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var contactList = arrayListOf<UserEntity>()
        for (i in list) {
            if (i.friendStatus == 0) {
                contactList.add(i)
            }
        }
        contactAdapter = ContactListAdapter(contactList)
        recyclerView.adapter = contactAdapter
        contactAdapter!!.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(activity!!, UserInfoActivity::class.java)
            intent.putExtra("user", contactAdapter!!.getItem(position))
            startActivity(intent)
        }
    }

    override fun setupFragmentComponent() {
        DaggerContactComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .contactModule(ContactModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ContactContract.ContactContractPresenter) {
        mPresenter = presenter as ContactPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver!!.pullFriendCallBack = null
    }
}