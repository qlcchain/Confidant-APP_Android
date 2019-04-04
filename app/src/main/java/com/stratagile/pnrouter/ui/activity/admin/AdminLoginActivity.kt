package com.stratagile.pnrouter.ui.activity.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JAdminLoginRsp
import com.stratagile.pnrouter.entity.RouterLoginReq
import com.stratagile.pnrouter.ui.activity.admin.component.DaggerAdminLoginComponent
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginContract
import com.stratagile.pnrouter.ui.activity.admin.module.AdminLoginModule
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminLoginPresenter
import com.stratagile.pnrouter.ui.activity.router.RouterAliasSetActivity
import com.stratagile.pnrouter.utils.RxEncryptTool
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_adminlogin.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: $description
 * @date 2019/01/19 15:30:16
 */

class AdminLoginActivity : BaseActivity(), AdminLoginContract.View , PNRouterServiceMessageReceiver.AdminLoginCallBack{
    override fun login(jAdminLoginRsp: JAdminLoginRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        when (jAdminLoginRsp.params.retCode) {
            0 -> {
                runOnUiThread {
                    toast("Login success")
                    var intent = Intent(this, RouterAliasSetActivity::class.java)
                    if(jAdminLoginRsp.params.routerName !=null &&  !jAdminLoginRsp.params.routerName.equals(""))
                    {
                        intent = Intent(this, AdminLoginSuccessActivity::class.java)
                    }
                    intent.putExtra("flag",0)
                    intent.putExtra("adminRouterId",jAdminLoginRsp.params.routerId)
                    intent.putExtra("adminUserSn",jAdminLoginRsp.params.userSn)
                    intent.putExtra("adminIdentifyCode",jAdminLoginRsp.params.identifyCode)
                    intent.putExtra("adminQrcode",jAdminLoginRsp.params.qrcode)
                    if(jAdminLoginRsp.params.routerName == null)
                    {
                        intent.putExtra("routerName","")
                    }else{
                        intent.putExtra("routerName",jAdminLoginRsp.params.routerName!!)
                    }

                    startActivity(intent)
                    finish()
                }


            }
            1 -> {
                runOnUiThread {
                    toast("Equipment temporarily unavailable")
                }
            }
            2 -> {
                runOnUiThread {
                    toast("Device MAC error")
                }
            }
            3 -> {
                runOnUiThread {
                    toast("Password error")
                }
            }
            4 -> {
                runOnUiThread {
                    toast("Other mistakes")
                }
            }

        }

    }

    @Inject
    internal lateinit var mPresenter: AdminLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adminlogin)
    }
    override fun initData() {
        title.text = getString(R.string.LoginAdmin)
        loginBtn.setOnClickListener {
            if(AppConfig.instance.messageReceiver != null)
            {
                AppConfig.instance.messageReceiver!!.adminLoginCallBack = this
            }
            if (adminPassWord.text.toString().trim().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            var LoginKeySha = RxEncryptTool.encryptSHA256ToString(adminPassWord.text.toString())
            showProgressDialog("waiting...")
            var createNormalUser = RouterLoginReq(ConstantValue.currentRouterMac,LoginKeySha)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,createNormalUser))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(2,createNormalUser)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        adminPassWord.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!"".equals(p0.toString())) {
                    loginBtn.background = resources.getDrawable(R.drawable.btn_maincolor)
                } else {
                    loginBtn.background = resources.getDrawable(R.drawable.btn_d5d5d5)
                }
            }

        })
    }

    override fun setupActivityComponent() {
        DaggerAdminLoginComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .adminLoginModule(AdminLoginModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: AdminLoginContract.AdminLoginContractPresenter) {
        mPresenter = presenter as AdminLoginPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.adminLoginCallBack = null
    }
    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}