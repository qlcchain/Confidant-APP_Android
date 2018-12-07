package com.stratagile.pnrouter.ui.activity.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
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
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.entity.events.UnReadContactCount
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.user.ContactListAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import im.tox.tox4j.core.enums.ToxMessageType
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
        if (jPullFriendRsp.params.payload == null || jPullFriendRsp.params.payload.size ==0) {
            for (i in localFriendList) {
                //是否为本地多余的好友
                if (i.friendStatus == 3 || i.friendStatus == 1) {
                    //等待验证的好友，不能处理
                    continue
                } else {
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
                }
            }
            runOnUiThread {
                initData()
            }
            return
        }
        //添加新的好友
        for (i in jPullFriendRsp.params.payload) {
            //是否为本地好友
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    j.friendStatus = 0
                    j.nickName = i.name
                    j.publicKey = i.userKey
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.publicKey = i.userKey
                userEntity.friendStatus = 0
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }
        }
        //把本地的多余好友清除
        localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in localFriendList) {
            //是否为本地多余的好友
            if (i.friendStatus == 3 || i.friendStatus == 1) {
                //等待验证的S好友，不能处理
                continue
            }
            var isLocalDeletedFriend = true
            for (j in jPullFriendRsp.params.payload) {
                if (i.userId.equals(j.id)) {
                    isLocalDeletedFriend = false
                }
            }
            if (isLocalDeletedFriend) {
                i.friendStatus = 7
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
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
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullFriend))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(pullFriend)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

    }

    fun initData() {
        var bundle = getArguments();
        var hasNewFriendRequest = false
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var newFriendCount = 0
        for (i in list) {

            if (i.userId.equals(selfUserId)) {
                continue
            }
            if (i.routerUserId !=null && i.routerUserId.equals(selfUserId)) {
                if (i.friendStatus == 0) {
                    contactList.add(i)
                }
                if (i.friendStatus == 3) {
                    hasNewFriendRequest = true
                    newFriendCount++
                }
            }

        }
        if (hasNewFriendRequest) {
            new_contact_dot.visibility = View.VISIBLE
            EventBus.getDefault().post(UnReadContactCount(newFriendCount))
        } else {
            new_contact_dot.visibility = View.GONE
            EventBus.getDefault().post(UnReadContactCount(0))
        }
        if(bundle == null)
        {
            newFriend.visibility = View.VISIBLE
            contactAdapter = ContactListAdapter(contactList,false)
        }else{
            newFriend.visibility = View.GONE
            contactAdapter = ContactListAdapter(contactList,true)
        }

        recyclerView.adapter = contactAdapter
        contactAdapter!!.setOnItemClickListener { adapter, view, position ->
            if(bundle == null)
            {
                var intent = Intent(activity!!, UserInfoActivity::class.java)
                intent.putExtra("user", contactAdapter!!.getItem(position))
                startActivity(intent)
            }else{
               var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,position,R.id.checkBox) as CheckBox
                checkBox.setChecked(!checkBox.isChecked)
                var itemCount =  contactAdapter!!.itemCount -1
                var count :Int = 0;
                for (i in 0..itemCount) {
                    var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
                    if(checkBox.isChecked)
                    {
                        count ++
                    }
                }
                EventBus.getDefault().post(SelectFriendChange(count,0))
            }

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