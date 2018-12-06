package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullUserRsp
import com.stratagile.pnrouter.entity.PullFriendReq
import com.stratagile.pnrouter.ui.activity.router.component.DaggerUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.UserContract
import com.stratagile.pnrouter.ui.activity.router.module.UserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.UserPresenter
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.user.UsertListAdapter
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.fragment_contact.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 14:25:44
 */

class UserFragment : BaseFragment(), UserContract.View , PNRouterServiceMessageReceiver.PullUserCallBack{
    override fun userList(jPullUserRsp: JPullUserRsp) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Inject
    lateinit internal var mPresenter: UserPresenter
    var contactAdapter : UsertListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_user, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConfig.instance.messageReceiver!!.pullUserCallBack = this

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
            if (i.friendStatus == 0) {
                contactList.add(i)
            }
            if (i.friendStatus == 3) {
                hasNewFriendRequest = true
                newFriendCount++
            }
        }
        if(bundle == null)
        {
            newFriend.visibility = View.VISIBLE
            contactAdapter = UsertListAdapter(contactList,false)
        }else{
            newFriend.visibility = View.GONE
            contactAdapter = UsertListAdapter(contactList,true)
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
            }

        }
    }
    override fun setupFragmentComponent() {
        DaggerUserComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .userModule(UserModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: UserContract.UserContractPresenter) {
        mPresenter = presenter as UserPresenter
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
        AppConfig.instance.messageReceiver!!.pullUserCallBack = null
    }
}