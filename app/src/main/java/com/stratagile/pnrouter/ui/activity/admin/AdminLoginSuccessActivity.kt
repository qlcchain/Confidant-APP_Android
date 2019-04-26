package com.stratagile.pnrouter.ui.activity.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.admin.component.DaggerAdminLoginSuccessComponent
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginSuccessContract
import com.stratagile.pnrouter.ui.activity.admin.module.AdminLoginSuccessModule
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminLoginSuccessPresenter
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.router.RouterAliasSetActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_adminqrcode.*
import org.libsodium.jni.Sodium
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: $description
 * @date 2019/01/19 17:18:46
 */

class AdminLoginSuccessActivity : BaseActivity(), AdminLoginSuccessContract.View, PNRouterServiceMessageReceiver.AdminRecoveryCallBack {
    override fun loginBack(loginRsp: JLoginRsp) {
        KLog.i(loginRsp.toString())
//        runOnUiThread {
//            toast(loginRsp.toString())
//        }
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast(R.string.need_Verification)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast(R.string.rid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast(R.string.uid_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast(R.string.Validation_failed)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast(R.string.Verification_code_error)
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 7) {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                    closeProgressDialog()
                }
            }
            else{
                runOnUiThread {
                    toast(R.string.other_error)
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast(R.string.userId_is_empty)
                closeProgressDialog()
            }
        } else {
            ConstantValue.loginOut = false
            ConstantValue.logining = true
            LogUtil.addLog("loginBack:"+"begin","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userId,"userid")
            //FileUtil.saveUserData2Local(loginRsp.params!!.index,"userIndex")
            LogUtil.addLog("loginBack:"+"a","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn,"usersn")
            LogUtil.addLog("loginBack:"+"b","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid,"routerid")
            LogUtil.addLog("loginBack:"+"c","LoginActivityActivity")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            ConstantValue.currentRouterId = loginRsp.params!!.routerid
            newRouterEntity.userId = loginRsp.params!!.userId
            newRouterEntity.index = ""
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            //SpUtil.putString(this, ConstantValue.username,ConstantValue.localUserName!!)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
//            newRouterEntity.dataFileVersion = loginRsp.params!!.dataFileVersion
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = ConstantValue.localUserName!!
            newRouterEntity.lastCheck = true
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.loginKey = ""
            newRouterEntity.dataFileVersion = dataFileVersion
            var myUserData = UserEntity()
            myUserData.userId = loginRsp.params!!.userId
            myUserData.nickName = loginRsp.params!!.nickName
            UserDataManger.myUserData = myUserData
            var contains = false
            for (i in routerList) {
                if (i.userSn.equals(loginRsp.params!!.userSn)) {
                    contains = true
                    newRouterEntity = i

                    break
                }
            }
            LogUtil.addLog("loginBack:"+"d","LoginActivityActivity")
            var needUpdate :ArrayList<MyRouter> = ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter:MyRouter = MyRouter();
                myRouter.setType(0)
                myRouter.setRouterEntity(it)
                needUpdate.add(myRouter);
            }
            LocalRouterUtils.updateList(needUpdate)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = ""
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            newRouterEntity.dataFileVersion = 0
            newRouterEntity.dataFilePay =  ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {
                newRouterEntity.routerAlias = newRouterEntity.routerName
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            LogUtil.addLog("loginBack:"+"e","LoginActivityActivity")
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
                KLog.i("333")
            }
            LogUtil.addLog("loginBack:"+"f","AdminLoginSuccessActivity")
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            startActivity(Intent(this, MainActivity::class.java))
            LogUtil.addLog("loginBack:"+"g","AdminLoginSuccessActivity")
            finish()
        }
    }
    override fun registerBack(registerRsp: JRegisterRsp) {
//        runOnUiThread {
//            toast(registerRsp.toString())
//        }
        if (registerRsp.params.retCode != 0) {
            if (registerRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("QR code has been activated by other users.")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("Error Verification Code")
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Other Error")
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(registerRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                closeProgressDialog()
            }
        } else {
            runOnUiThread {
                closeProgressDialog()
                KLog.i("1111")
            }
            runOnUiThread {
                showProgressDialog("logining...")
            }
            var newRouterEntity = RouterEntity()
            newRouterEntity.routerId = registerRsp.params.routeId
            newRouterEntity.userSn = registerRsp.params.userSn
            newRouterEntity.username = ConstantValue.localUserName
            newRouterEntity.userId = registerRsp.params.userId
            newRouterEntity.loginKey = "";
            newRouterEntity.dataFileVersion = registerRsp.params.dataFileVersion
            newRouterEntity.dataFilePay = registerRsp.params.dataFilePay
            newRouterEntity.adminId = registerRsp.params!!.adminId
            newRouterEntity.adminName = registerRsp.params!!.adminName
            newRouterEntity.adminKey = registerRsp.params!!.adminKey
            var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(registerRsp.params!!.routerName))
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() /1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
            //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
            var login = LoginReq_V4(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,signBase64, registerRsp.params.dataFileVersion,NickName)
            /*runOnUiThread {
                toast(login.toString())
            }*/
            ConstantValue.loginReq = login
            if(ConstantValue.isWebsocketConnected)
            {

                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(registerRsp.params.routeId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, registerRsp.params.routeId.substring(0, 64))
                }
            }
        }
    }
    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        runOnUiThread {
//            toast(recoveryRsp.toString())
            closeProgressDialog()
        }
        when (recoveryRsp.params.retCode) {
            0 ->{
                ConstantValue.lastNetworkType = "";
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {

                }else{
                    dataFileVersion = recoveryRsp.params.dataFileVersion
                   /* var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.loginKey = ""
                    newRouterEntity.dataFilePay = ""
                    newRouterEntity.routerName = String(RxEncodeTool.base64Decode(recoveryRsp.params!!.routerName))
                    //routerNameTips.text = newRouterEntity.routerName
                    EventBus.getDefault().post(NameChange(newRouterEntity.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)*/
                }
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var login = LoginReq_V4(  recoveryRsp.params.routeId,recoveryRsp.params.userSn, recoveryRsp.params.userId,signBase64, recoveryRsp.params.dataFileVersion,recoveryRsp.params.routerName)
                runOnUiThread {
                    showProgressDialog("logining...")
//                    toast(login.toString())
                }
                ConstantValue.loginReq = login
                if(ConstantValue.isWebsocketConnected)
                {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,login)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(recoveryRsp.params.routeId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                    }
                }
               /* var intent = Intent(this, LoginActivityActivity::class.java)
                intent.putExtra("adminRouterIdOK",recoveryRsp.params.routeId)
                intent.putExtra("adminUserSnOK",recoveryRsp.params.userSn)
                intent.putExtra("adminUserIdOK",recoveryRsp.params.userId)
                intent.putExtra("adminUserNameOK",String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName)))
                startActivity(intent)
                finish()*/
            }
            1 -> {

                runOnUiThread {
                    showProgressDialog("waiting...")
                }

                val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                var regeister = RegeisterReq_V4( recoveryRsp.params.routeId, recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
//                runOnUiThread {
//                    toast(regeister.toString())
//                }
                if(ConstantValue.isWebsocketConnected)
                {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,regeister)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(recoveryRsp.params.routeId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                    }
                }
                /*ConstantValue.scanRouterId = recoveryRsp.params.routeId
                ConstantValue.scanRouterSN = recoveryRsp.params.userSn
                AppConfig.instance.messageReceiver!!.adminRecoveryCallBack = null
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()*/
            }
            2 -> {
                runOnUiThread {
                    toast(R.string.rid_error)
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
                    toast(R.string.other_error)
                }
            }
            5 -> {
                runOnUiThread {
                    toast(R.string.The_QR_code_has_been_occupied_by_others)
                }
            }
            6 -> {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                }
            }
            else -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: AdminLoginSuccessPresenter
    var newRouterEntity = RouterEntity()
    var dataFileVersion = 0;
    var dataFilePay = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adminqrcode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        showViewNeedFront()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
    }

    var adminRouterId = ""
    var adminUserSn = ""
    var adminIdentifyCode = ""
    var adminQrcode = ""
    var routerName = ""
    override fun initData() {

        if(intent.getStringExtra("adminRouterId") != null)
        {
            adminRouterId = intent.getStringExtra("adminRouterId")
            adminUserSn = intent.getStringExtra("adminUserSn")
            adminIdentifyCode = intent.getStringExtra("adminIdentifyCode")
            adminQrcode = intent.getStringExtra("adminQrcode")
            routerName = intent.getStringExtra("routerName")
            routerName =  String(RxEncodeTool.base64Decode(routerName))
            adminName.text   = routerName
            ivAvatarAdmin.withShape = true
            ivAvatarAdmin.setTextWithShape(routerName)
            Activationcode.setRightTitleText(adminIdentifyCode)
        }
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
            intent.putExtra("routerName",routerName)
            startActivity(intent)
        }
        Routerpassword.setOnClickListener {
            var intent = Intent(this, AdminUpPasswordActivity::class.java)
            intent.putExtra("adminUserSn",adminUserSn)
            intent.putExtra("adminRouterId",adminRouterId)
            intent.putExtra("adminIdentifyCode",adminIdentifyCode)
            intent.putExtra("adminQrcode",adminQrcode)
            intent.putExtra("routerName",routerName)
            startActivity(intent)
        }
        llModifyName.setOnClickListener {
            var intent = Intent(this, RouterAliasSetActivity::class.java)
            intent.putExtra("flag",1)
            intent.putExtra("adminRouterId",adminRouterId)
            intent.putExtra("adminUserSn",adminUserSn)
            intent.putExtra("adminIdentifyCode",adminIdentifyCode)
            intent.putExtra("adminQrcode",adminQrcode)
            intent.putExtra("routerName",routerName)
            startActivityForResult(intent,1)
        }
        LoginInBtn.setOnClickListener {
            AppConfig.instance.mAppActivityManager.finishActivity(LoginActivityActivity::class.java)
            showProgressDialog("wait...")
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var recovery = RecoveryReq(adminRouterId, adminUserSn,pulicMiKey)
            if(ConstantValue.isWebsocketConnected)
            {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,recovery))
            }
            else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4,recovery)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        ivScan.setOnClickListener {

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            adminRouterId = data!!.getStringExtra("adminRouterId")
            adminUserSn = data!!.getStringExtra("adminUserSn")
            adminIdentifyCode = data!!.getStringExtra("adminIdentifyCode")
            adminQrcode = data!!.getStringExtra("adminQrcode")
            routerName = data!!.getStringExtra("routerName")
            routerName =  String(RxEncodeTool.base64Decode(routerName))
            adminName.text   = routerName
            ivAvatarAdmin.setText(routerName)
            return
        }
    }
    override fun onDestroy() {
        AppConfig.instance.getPNRouterServiceMessageReceiver().adminRecoveryCallBack = null
        super.onDestroy()
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