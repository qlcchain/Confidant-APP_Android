package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JLogOutRsp
import com.stratagile.pnrouter.entity.LogOutReq
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.RouterChange
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterInfoComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterInfoContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterInfoModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterInfoPresenter
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.utils.LocalRouterUtils
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.view.SweetAlertDialog
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_router_info.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject



/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/09/27 16:07:17
 */

class RouterInfoActivity : BaseActivity(), RouterInfoContract.View , PNRouterServiceMessageReceiver.LogOutCallBack {
    override fun logOutBack(jLogOutRsp: JLogOutRsp) {

        if(jLogOutRsp.params.retCode == 0)
        {
            ConstantValue.isHasWebsocketInit = true
            if(AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.close()
            ConstantValue.isWebsocketConnected = false
            isUserExit = true
            runOnUiThread()
            {
                closeProgressDialog()
                onLogOutSuccess()
            }
        }else{
            toast(R.string.logoutfailed)
        }
    }

    @Inject
    internal lateinit var mPresenter: RouterInfoPresenter
    lateinit var routerEntity: RouterEntity
    var isUserExit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        isUserExit = false
        setContentView(R.layout.activity_router_info)
        routerEntity = intent.getParcelableExtra("router")
        if(ConstantValue.currentRouterSN != null && ConstantValue.currentRouterSN .indexOf("01")== 0 && ConstantValue.currentRouterSN.equals(routerEntity.userSn))
        {
            llRouterManagement.visibility =  View.VISIBLE
        }else{
            llRouterManagement.visibility =  View.GONE
        }
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.logOutBack = this
        llRouterQRCode.setOnClickListener {
            var intent = Intent(this, RouterQRCodeActivity::class.java)
            intent.putExtra("router", routerEntity)
            startActivity(intent)
        }
        EventBus.getDefault().register(this)
        tvRouterAlias.text = routerEntity.routerName
        title.text = routerEntity.routerName
        if (routerEntity.lastCheck) {
            tvLogOut.visibility = View.VISIBLE
            tvDeleteRouter.visibility = View.GONE
            tvSwitchRouter.visibility = View.GONE
        }else{
            tvDeleteRouter.visibility = View.VISIBLE
        }
        tvLogOut.setOnClickListener {
            //            onLogOutSuccess()
            showDialog()
        }
        tvDeleteRouter.setOnClickListener {
            //            onLogOutSuccess()
            showDeleteDialog()
        }
        tvSwitchRouter.setOnClickListener {

        }
        llRouterAlias.setOnClickListener {
            var intent = Intent(this, EditNickNameActivity::class.java)
            intent.putExtra("flag", "Alias")
            intent.putExtra("hint", "Edit alias")
            intent.putExtra("alias", routerEntity.routerName)
            startActivityForResult(intent, 1)
        }
        llRouterManagement.setOnClickListener {
            var intent = Intent(this, RouterAddUserActivity::class.java)
            intent.putExtra("routerEntity", routerEntity)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            routerEntity.routerName = data!!.getStringExtra("alias")
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerEntity)
            EventBus.getDefault().unregister(this)
            initData()
            EventBus.getDefault().post(RouterChange())
        }
    }

    fun showDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setTitleText("Log Out")
                .setConfirmClickListener {
                    showProgressDialog("log out")
                    var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                    var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                    } else if (ConstantValue.isToxConnected) {
                        val baseData = BaseData(2,msgData)
                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                        val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }
                    /*ConstantValue.isHasWebsocketInit = true
                    if(AppConfig.instance.messageReceiver != null)
                        AppConfig.instance.messageReceiver!!.close()
                    ConstantValue.isWebsocketConnected = false*/
                    //onLogOutSuccess()
                }
                .show()

    }
    fun showDeleteDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setTitleText(getString(R.string.delete)).
                        setContentText(getString(R.string.askdelete))
                .setConfirmClickListener {
                    onDeleteSuccess()
                }
                .show()

    }
    fun onDeleteSuccess() {
        var deleteRouterEntity:RouterEntity =  LocalRouterUtils.deleteLocalAssets(routerEntity.userSn)
        AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.delete(deleteRouterEntity)
        finish()
    }
    fun onLogOutSuccess() {
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(this, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }

    override fun setupActivityComponent() {
        DaggerRouterInfoComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .routerInfoModule(RouterInfoModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: RouterInfoContract.RouterInfoContractPresenter) {
        mPresenter = presenter as RouterInfoPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        if(isUserExit)
            return
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
                    showProgressNoCanelDialog("network reconnecting...")
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressNoCanelDialog("network reconnecting...")
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver?.logOutBack = null
        EventBus.getDefault().unregister(this)
    }
}