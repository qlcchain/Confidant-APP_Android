package com.stratagile.pnrouter.ui.activity.admin

import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JAdminUpdataPasswordRsp
import com.stratagile.pnrouter.entity.ResetRouterKeyReq
import com.stratagile.pnrouter.ui.activity.admin.component.DaggerAdminUpPasswordComponent
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminUpPasswordContract
import com.stratagile.pnrouter.ui.activity.admin.module.AdminUpPasswordModule
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminUpPasswordPresenter
import com.stratagile.pnrouter.utils.RxEncryptTool
import kotlinx.android.synthetic.main.activity_adminupcode.*
import kotlinx.android.synthetic.main.activity_adminuppassword.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: $description
 * @date 2019/01/19 15:30:48
 */

class AdminUpPasswordActivity : BaseActivity(), AdminUpPasswordContract.View , PNRouterServiceMessageReceiver.AdminUpdataPassWordCallBack{
    override fun updataPassWord(jAdminUpdataPasswordRsp: JAdminUpdataPasswordRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        when (jAdminUpdataPasswordRsp.params.retCode) {
            0 -> {
                runOnUiThread {
                    toast("update successfully")
                }
                var intent = Intent(this, AdminLoginSuccessActivity::class.java)
                intent.putExtra("adminRouterId",adminRouterId)
                intent.putExtra("adminUserSn",adminUserSn)
                intent.putExtra("adminIdentifyCode",adminQrcode)
                intent.putExtra("adminQrcode",adminQrcode)
                startActivity(intent)
                finish()
            }
            1 -> {
                runOnUiThread {
                    toast("The target device ID is incorrect")
                }
            }
            2 -> {
                runOnUiThread {
                    toast("Password error")
                }
            }
            3 -> {
                runOnUiThread {
                    toast("Other mistakes")
                }
            }

        }
    }
    var adminRouterId = ""
    var adminUserSn = ""
    var adminIdentifyCode = ""
    var adminQrcode = ""
    @Inject
    internal lateinit var mPresenter: AdminUpPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
       setContentView(R.layout.activity_adminuppassword)
    }
    override fun initData() {
        title.text = getString(R.string.Modify_Router_Password)
        adminRouterId = intent.getStringExtra("adminRouterId")
        adminIdentifyCode = intent.getStringExtra("adminIdentifyCode")
        adminUserSn = intent.getStringExtra("adminUserSn")
        adminQrcode = intent.getStringExtra("adminQrcode")
        if(AppConfig.instance.messageReceiver != null)
        {
            AppConfig.instance.messageReceiver!!.adminUpdataPassWordCallBack = this
        }
        updateAdminPasswordBtn.setOnClickListener {
            if (oldPassword.text.toString().equals("") || newPassword.text.toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if (oldPassword.text.toString().length != 8 || newPassword.text.toString().length != 8) {
                toast(getString(R.string.routerpasswordupdata))
                return@setOnClickListener
            }
            var oldPassword = RxEncryptTool.encryptSHA256ToString(oldPassword.text.toString())
            var newPassword = RxEncryptTool.encryptSHA256ToString(newPassword.text.toString())
            showProgressDialog("waiting...")
            var ResetRouterKeyReq = ResetRouterKeyReq(adminRouterId,oldPassword,newPassword)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,ResetRouterKeyReq))
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.adminUpdataPassWordCallBack = null
    }
    override fun setupActivityComponent() {
       DaggerAdminUpPasswordComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .adminUpPasswordModule(AdminUpPasswordModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: AdminUpPasswordContract.AdminUpPasswordContractPresenter) {
            mPresenter = presenter as AdminUpPasswordPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}