package com.stratagile.pnrouter.ui.activity.admin

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JRecoveryRsp
import com.stratagile.pnrouter.entity.MyRouter
import com.stratagile.pnrouter.entity.RecoveryReq
import com.stratagile.pnrouter.entity.events.NameChange
import com.stratagile.pnrouter.ui.activity.admin.component.DaggerAdminLoginSuccessComponent
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginSuccessContract
import com.stratagile.pnrouter.ui.activity.admin.module.AdminLoginSuccessModule
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminLoginSuccessPresenter
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.utils.LocalRouterUtils
import com.stratagile.pnrouter.utils.RxEncodeTool
import kotlinx.android.synthetic.main.activity_adminqrcode.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_qrcode.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: $description
 * @date 2019/01/19 17:18:46
 */

class AdminLoginSuccessActivity : BaseActivity(), AdminLoginSuccessContract.View, PNRouterServiceMessageReceiver.AdminRecoveryCallBack {
    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        when (recoveryRsp.params.retCode) {
            0 ->{
                ConstantValue.lastNetworkType = "";
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {

                }else{
                    var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.loginKey = ""
                    newRouterEntity.dataFilePay = ""
                    var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
                    newRouterEntity.routerName = "Router " + (localData.size + 1)
                    //routerNameTips.text = newRouterEntity.routerName
                    EventBus.getDefault().post(NameChange(newRouterEntity.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }
                var intent = Intent(this, LoginActivityActivity::class.java)
                intent.putExtra("adminRouterId",recoveryRsp.params.routeId)
                intent.putExtra("adminUserSn",recoveryRsp.params.userSn)
                intent.putExtra("adminUserId",recoveryRsp.params.userId)
                intent.putExtra("adminUserName",String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName)))
                startActivity(intent)
            }
            1 -> {
                ConstantValue.scanRouterId = recoveryRsp.params.routeId
                ConstantValue.scanRouterSN = recoveryRsp.params.userSn
                AppConfig.instance.messageReceiver!!.adminRecoveryCallBack = null
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            2 -> {
                runOnUiThread {
                    toast("Rid error")
                }
            }
            3-> {
                ConstantValue.scanRouterId = recoveryRsp.params.routeId
                ConstantValue.scanRouterSN = recoveryRsp.params.userSn
                AppConfig.instance.messageReceiver!!.adminRecoveryCallBack = null
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            4 -> {
                runOnUiThread {
                    toast("Other mistakes")
                }
            }
            else -> {
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: AdminLoginSuccessPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adminqrcode)
    }
    override fun initData() {

        title.text = getString(R.string.Administrator_account)
        var adminRouterId = intent.getStringExtra("adminRouterId")
        var adminUserSn = intent.getStringExtra("adminUserSn")
        var adminIdentifyCode = intent.getStringExtra("adminIdentifyCode")
        var adminQrcode = intent.getStringExtra("adminQrcode")
        Activationcode.setRightTitleText(adminIdentifyCode)
        Routerpassword.setRightTitleText(getString(R.string.Modify))
        if(AppConfig.instance.messageReceiver != null)
        {
            AppConfig.instance.messageReceiver!!.adminRecoveryCallBack = this
        }
        Thread(Runnable() {
            run() {

                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode(adminQrcode, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor))
                runOnUiThread {
                    ivQrCodeAdmin.setImageBitmap(bitmap)
                }

            }
        }).start()

        Activationcode.setOnClickListener {
            var intent = Intent(this, AdminUpCodeActivity::class.java)

            intent.putExtra("adminUserSn",adminUserSn)
            intent.putExtra("adminRouterId",adminRouterId)
            intent.putExtra("adminIdentifyCode",adminIdentifyCode)
            intent.putExtra("adminQrcode",adminQrcode)
            startActivity(intent)
            finish()
        }
        Routerpassword.setOnClickListener {
            var intent = Intent(this, AdminUpPasswordActivity::class.java)
            intent.putExtra("adminUserSn",adminUserSn)
            intent.putExtra("adminRouterId",adminRouterId)
            intent.putExtra("adminIdentifyCode",adminIdentifyCode)
            intent.putExtra("adminQrcode",adminQrcode)
            startActivity(intent)
            finish()
        }
        LoginInBtn.setOnClickListener {
            showProgressDialog("wait...")
            var recovery = RecoveryReq(adminRouterId, adminUserSn)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.adminRecoveryCallBack = null
    }
    override fun setupActivityComponent() {
        DaggerAdminLoginSuccessComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .adminLoginSuccessModule(AdminLoginSuccessModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: AdminLoginSuccessContract.AdminLoginSuccessContractPresenter) {
        mPresenter = presenter as AdminLoginSuccessPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}