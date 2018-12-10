package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullUserRsp
import com.stratagile.pnrouter.entity.PullUserReq
import com.stratagile.pnrouter.ui.activity.router.component.DaggerUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.UserContract
import com.stratagile.pnrouter.ui.activity.router.module.UserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.UserPresenter
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.adapter.user.UsertListAdapter
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.SweetAlertDialog
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.fragment_user.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 14:25:44
 */

class UserFragment: BaseFragment(), UserContract.View , PNRouterServiceMessageReceiver.PullUserCallBack{
    override fun userList(jPullUserRsp: JPullUserRsp) {

        var comUserList = arrayListOf<RouterUserEntity>()
        var tempUserList = arrayListOf<RouterUserEntity>()
        for (i in jPullUserRsp.params.payload) {
            //是否为本地多余的好友
            if (i.userType == 2) {
                comUserList.add(i)
            } else if (i.userType == 3){
                if(i.nickName == null || i.nickName.equals(""))
                    i.nickName ="dGVtcFVzZXI="
                tempUserList.add(i)
            }
        }

        runOnUiThread {
            contactAdapter = UsertListAdapter(comUserList,false)
            recyclerViewUser.adapter = contactAdapter
            usersTips.text = "User("+jPullUserRsp.params.normalUserNum.toString() +"/"+ (jPullUserRsp.params.normalUserNum + jPullUserRsp.params.tempUserNum ).toString()+")"
            tempUsersTips.text = "Temporary("+jPullUserRsp.params.tempUserNum.toString() +"/"+ (jPullUserRsp.params.normalUserNum + jPullUserRsp.params.tempUserNum ).toString()+")"
            contactTempAdapter = UsertListAdapter(tempUserList,false)
            recyclerViewTempUser.adapter = contactTempAdapter

            contactAdapter!!.setOnItemClickListener { adapter, view, position ->
                var intent = Intent(activity!!, UserQRCodeActivity::class.java)
                intent.putExtra("user", contactAdapter!!.getItem(position))
                startActivity(intent)
            }
            contactTempAdapter!!.setOnItemClickListener { adapter, view, position ->
                var intent = Intent(activity!!, UserQRCodeActivity::class.java)
                intent.putExtra("user", contactTempAdapter!!.getItem(position))
                startActivity(intent)
            }
            closeProgressDialog()
        }

    }

    @Inject
    lateinit internal var mPresenter: UserPresenter
    var contactAdapter : UsertListAdapter? = null
    var routerEntity: RouterEntity? = null
    var contactTempAdapter : UsertListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        routerEntity = arguments!!.get("routerEntity") as RouterEntity
        var view = inflater.inflate(R.layout.fragment_user, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConfig.instance.messageReceiver!!.pullUserCallBack = this
        newUser.setOnClickListener {
            var intent = Intent(activity!!, RouterCreateUserActivity::class.java)
            intent.putExtra("routerUserEntity", routerEntity)
            startActivity(intent)
        }
        newTempUser.setOnClickListener {
            toast("developing")
            //showDialog()
        }
        initData()
        refreshLayoutUser.setOnRefreshListener {
            //pullFriendList()
            KLog.i("拉取用户列表")
        }
        pullFriendList()

    }
    fun showDialog() {
        SweetAlertDialog(AppConfig.instance, SweetAlertDialog.BUTTON_NEUTRAL)
                .setContentText(getString(R.string.delete_contact_text))
                .setConfirmClickListener {
                    showProgressDialog()
                    closeProgressDialog()
                    showCode()
                }
                .show()

    }
    fun showCode()
    {

    }
    fun pullFriendList() {
        showProgressDialog()
        refreshLayoutUser.isRefreshing = false
        var pullFriend = PullUserReq(0,0,"")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,pullFriend))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,pullFriend)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

    }

    fun initData() {


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