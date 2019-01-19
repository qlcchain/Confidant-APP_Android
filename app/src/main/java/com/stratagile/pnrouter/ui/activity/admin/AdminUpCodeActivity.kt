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
import com.stratagile.pnrouter.entity.JAdminUpdataCodeRsp
import com.stratagile.pnrouter.entity.ResetUserIdcodeReq
import com.stratagile.pnrouter.ui.activity.admin.component.DaggerAdminUpCodeComponent
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminUpCodeContract
import com.stratagile.pnrouter.ui.activity.admin.module.AdminUpCodeModule
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminUpCodePresenter
import kotlinx.android.synthetic.main.activity_adminupcode.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: $description
 * @date 2019/01/19 15:31:09
 */

class AdminUpCodeActivity : BaseActivity(), AdminUpCodeContract.View , PNRouterServiceMessageReceiver.AdminUpdataCodeCallBack{
    override fun updataCode(jAdminUpdataCodeRsp: JAdminUpdataCodeRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        when (jAdminUpdataCodeRsp.params.retCode) {
            0 -> {
                runOnUiThread {
                    toast("update success")
                }
                var intent = Intent(this, AdminLoginSuccessActivity::class.java)
                intent.putExtra("adminRouterId",adminRouterId)
                intent.putExtra("adminUserSn",adminUserSn)
                intent.putExtra("adminIdentifyCode",activationCode.text.toString())
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
                    toast("Error in input parameters")
                }
            }
            3 -> {
                runOnUiThread {
                    toast("Original code error")
                }
            }
            4 -> {
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
    internal lateinit var mPresenter: AdminUpCodePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adminupcode)
    }
    override fun initData() {
        title.text = getString(R.string.Modify_Activation_Code)
        adminRouterId = intent.getStringExtra("adminRouterId")
        adminIdentifyCode = intent.getStringExtra("adminIdentifyCode")
        adminUserSn = intent.getStringExtra("adminUserSn")
        adminQrcode = intent.getStringExtra("adminQrcode")
        if(AppConfig.instance.messageReceiver != null)
        {
            AppConfig.instance.messageReceiver!!.adminUpdataCodeCallBack = this
        }
        adminUpCodeBtn.setOnClickListener {
            if (activationCode.text.toString().equals("")) {
                toast(getString(R.string.Cannot_be_empty))
                return@setOnClickListener
            }
            if (activationCode.text.toString().length != 8) {
                toast(getString(R.string.routercodeupdata))
                return@setOnClickListener
            }
            var newCode = activationCode.text.toString()
            showProgressDialog("waiting...")
            var ResetUserIdcodeReq = ResetUserIdcodeReq(adminRouterId,adminUserSn,adminIdentifyCode,newCode)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,ResetUserIdcodeReq))
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.adminUpdataCodeCallBack = null
    }
    override fun setupActivityComponent() {
       DaggerAdminUpCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .adminUpCodeModule(AdminUpCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: AdminUpCodeContract.AdminUpCodeContractPresenter) {
            mPresenter = presenter as AdminUpCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}