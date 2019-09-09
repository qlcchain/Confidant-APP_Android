package com.stratagile.pnrouter.ui.activity.login

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.*
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.jaeger.library.StatusBarUtil
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.MailUtil
import com.smailnet.islands.Islands
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
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
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.NameChange
import com.stratagile.pnrouter.entity.events.StopTox
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.fingerprint.MyAuthCallback.*
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.login.component.DaggerLoginActivityComponent
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.utils.NetUtils.isMacAddress
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxSendInfoEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View, PNRouterServiceMessageReceiver.LoginMessageCallback {


    @Inject
    internal lateinit var mPresenter: LoginActivityPresenter

    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null

    private var handler: Handler? = null

    private var builderTips: AlertDialog? = null

    var formatDialog : CommonDialog? = null
    var importUserDialog : CommonDialog? = null

    internal var finger: ImageView? = null

    var newRouterEntity = RouterEntity()
    private lateinit var standaloneCoroutine : Job

    var routerId = ""
    var userSn = ""
    var userId = ""
    var username = ""
    var dataFileVersion = 0
    var lastLoginUserId = ""
    var lastLoginUserSn = ""

    val REQUEST_SELECT_ROUTER = 2
    val REQUEST_SCAN_QRCODE = 1
    val AuthenticationScreen = 3
    var loginBack = false
    var isFromScan = false
    var isFromScanAdmim = false
    //是否点击了登陆按钮
    var isClickLogin = false
    //是否正在登陆
    var isStartLogin = false
    var stopTox = false;
    var loginOk = false
    var isToxLoginOverTime = false;
    var maxLogin = 0
    var threadInit = false
    var RouterMacStr = ""
    var islogining = false
    var isloginOutTime = false
    var scanType = 0 // 0 admin   1 其他
    var adminUserSn:String?  = null
    var hasFinger = false
    var name:Long  = 0;
    var openNewOnPow = true

    override fun registerBack(registerRsp: JRegisterRsp) {
        if (registerRsp.params.retCode != 0) {
            val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(registerRsp.params.userSn)).list()
            if (routerEntityList != null && routerEntityList!!.size != 0) {
                for (i in routerEntityList) {
                    var deleteRouterEntity:RouterEntity =  LocalRouterUtils.deleteLocalAssets(i.userSn)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.delete(i)
                }
            }
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
            val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(registerRsp.params.userSn)).list()
            if (routerEntityList != null && routerEntityList!!.size != 0) {
                for (i in routerEntityList) {
                    var deleteRouterEntity:RouterEntity =  LocalRouterUtils.deleteLocalAssets(i.userSn)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.delete(i)
                }
            }
            ConstantValue.isNewUser = true;
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
            islogining = true
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
            closeProgressDialog()
        }
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        KLog.i("222")
        ConstantValue.unSendMessage.remove("recovery")
        ConstantValue.unSendMessageFriendId.remove("recovery")
        ConstantValue.unSendMessageSendCount.remove("recovery")
        when (recoveryRsp.params.retCode) {
            0 ->{
                ConstantValue.lastNetworkType = "";
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    var routerEntity:RouterEntity = routerEntityList[0]
                    routerId = routerEntity.routerId
                    userSn = routerEntity.userSn
                    userId = routerEntity.userId
                    username = routerEntity.username
                    if(routerEntity.dataFileVersion == null)
                    {
                        dataFileVersion = 0
                    }else{
                        dataFileVersion = routerEntity.dataFileVersion
                    }
                    /*runOnUiThread {
                        routerNameTips.text = newRouterEntity.routerName
                    }*/
                    EventBus.getDefault().post(NameChange(routerEntity.routerName,routerEntity.loginKey))
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
                    newRouterEntity.routerName = String(RxEncodeTool.base64Decode(recoveryRsp.params!!.routerName))
                    routerId = recoveryRsp.params.routeId
                    userSn = recoveryRsp.params.userSn
                    userId = recoveryRsp.params.userId
                    username =String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    dataFileVersion =recoveryRsp.params.dataFileVersion
                    //routerNameTips.text = newRouterEntity.routerName
                    EventBus.getDefault().post(NameChange(newRouterEntity.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }

            }
            1 -> {
                /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                  startActivity(Intent(this, RegisterActivity::class.java))*/

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
                var regeister = RegeisterReq_V4( recoveryRsp.params.routeId,  recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
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
            }
            2 -> {
                runOnUiThread {
                    toast(R.string.rid_error)
                }

            }
            3-> {
                ConstantValue.lastNetworkType = "";
                /*val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    for( i in routerEntityList)
                    {

                        if(i!= null && !i.userId.equals(""))
                        {
                            routerId = i.routerId
                            userSn = i.userSn
                            userId = i.userId
                            username = i.username
                            if(i.dataFileVersion == null)
                            {
                                dataFileVersion = 0
                            }else{
                                dataFileVersion = i.dataFileVersion
                            }
                            runOnUiThread()
                            {
                                routerNameTips.text = i.routerName
                                ivAvatar.setText(i.routerName)
                                loginKey.setText(i.loginKey)
                            }
                            ConstantValue.currentRouterIp = ""
                            //ConstantValue.scanRouterId = routerId;
                            isClickLogin = false
                            isStartLogin = true
                            getServer(routerId,userSn,true,true)
                        }
                    }
                }else{*/
                /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                  var intent = Intent(this, RegisterActivity::class.java)
                  intent.putExtra("flag", 1)
                  startActivity(intent)*/
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
                var regeister = RegeisterReq_V4( recoveryRsp.params.routeId,  recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
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
                //}

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
                    toast(R.string.The_account_has_expired_start_register)

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
                    var regeister = RegeisterReq_V4(recoveryRsp.params.routeId,  recoveryRsp.params.userSn, signBase64,pulicMiKey,NickName)
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
                }
            }
            else -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
        }
    }



    private var exitTime: Long = 0
    private var loginGoMain:Boolean = false
    var isUnlock = false
    override fun loginBack(loginRsp: JLoginRsp) {
//        if (!loginRsp.params.userId.equals(userId)) {
//            KLog.i("过滤掉userid错误的请求")
//            return
//        }
        islogining = false
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        KLog.i(loginRsp.toString())
        LogUtil.addLog("loginBack:"+loginRsp.params.retCode,"LoginActivityActivity")
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
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
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq(loginRsp.params!!.routerid, loginRsp.params!!.userSn,pulicMiKey)
                    var baseData = BaseData(4, recovery)
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(baseData)
                    }else{
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, loginRsp.params!!.routerid.substring(0, 64))
                    }


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
            islogining = false
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
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.loginKey = loginKey.text.toString().trim()
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
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
            LocalRouterUtils.updateList(needUpdate)
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = loginKey.text.toString().trim();
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            newRouterEntity.dataFileVersion = 0
            newRouterEntity.dataFilePay =  ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            ConstantValue.isCurrentRouterAdmin =  loginRsp.params!!.userSn.indexOf("01") == 0
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

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
            LogUtil.addLog("loginBack:"+"f","LoginActivityActivity")
            loginOk = true
            isToxLoginOverTime = false
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            resetUnCompleteFileRecode()
            if(loginGoMain)
                return
            startActivity(Intent(this, MainActivity::class.java))
            loginGoMain  = true
            LogUtil.addLog("loginBack:"+"g","LoginActivityActivity")
            finish()
        }
    }

    fun resetUnCompleteFileRecode()
    {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList)
        {
            if(myFie.upLoadFile.isComplete == false)
            {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.isStop = "1"
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.hasExtra("flag")) {
            isUnlock = true
        }
        name = System.currentTimeMillis();
        maxLogin = 0
        loginGoMain = false
        needFront = true
        isFromScan = false
        isFromScanAdmim = false
        isClickLogin = false
        stopTox = false
        RouterMacStr = ""
        ConstantValue.lastNetworkType = ""
        ConstantValue.currentRouterMac = ""
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        StatusBarUtil.setTransparent(this)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
        ivAvatar.withShape = true
    }
    override fun onResume() {
        if(AppConfig.instance.messageReceiver != null)
        {
            KLog.i("没有初始化。。设置loginBackListener")
            AppConfig.instance.messageReceiver!!.loginBackListener = this
        }
        exitTime = System.currentTimeMillis() - 2001

        super.onResume()
    }
    override fun onDestroy() {

        KLog.i("没有初始化。。onDestroy")
        AppConfig.instance.messageReceiver?.loginBackListener = null
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
        if (formatDialog != null) {
            formatDialog?.dismissWithAnimation()
            formatDialog = null
        }
        if (myAuthCallback != null) {
            myAuthCallback?.removeHandle()
            myAuthCallback = null
        }
        if (handler != null) {
            handler?.removeCallbacksAndMessages(null)
        }
        if (handler != null) {
            handler = null
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNameChange(nameChange: NameChange) {
        routerNameTips.text = nameChange.name
        ivAvatar.setTextWithShape(nameChange.name)
        loginKey.setText(nameChange.loginkey)
        getServer(routerId,userSn,true,true)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息："+toxSendInfoEvent.info)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {

        if(toxFriendStatusEvent.status == 1)
        {

            ConstantValue.freindStatus = 1
            if(!threadInit)
            {
                Thread(Runnable() {
                    run() {

                        while (true)
                        {
                            if(ConstantValue.unSendMessage.size >0)
                            {
                                for (key in ConstantValue.unSendMessage.keys)
                                {
                                    var sendData = ConstantValue.unSendMessage.get(key)
                                    var friendId = ConstantValue.unSendMessageFriendId.get(key)
                                    var sendCount:Int = ConstantValue.unSendMessageSendCount.get(key) as Int
                                    if(sendCount < 5)
                                    {
                                        if (ConstantValue.isAntox) {
                                            var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, sendData, ToxMessageType.NORMAL)
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(sendData, friendId)
                                        }
                                        ConstantValue.unSendMessageSendCount.put(key,sendCount++)
                                    }else{
                                        closeProgressDialog()
                                        break
                                    }
                                }

                            }else{
                                closeProgressDialog()
                                break
                            }
                            Thread.sleep(2000)
                        }

                    }
                }).start()
                threadInit = true
            }


            LogUtil.addLog("P2P检测路由好友上线，可以发消息:","LoginActivityActivity")
        }else{
            ConstantValue.freindStatus = 0;
            LogUtil.addLog("P2P检测路由好友未上线，不可以发消息:","LoginActivityActivity")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopTox(stopTox: StopTox) {
        try {
            MessageHelper.clearAllMessage()
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态:"+connectStatus.status)
        when (connectStatus.status) {
            0 -> {
                if(standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                ConstantValue.isHasWebsocketInit = true
                if(isFromScanAdmim)
                {
                    runOnUiThread {
                        closeProgressDialog()
                    }
                    var intent = Intent(this, AdminLoginActivity::class.java)
                    startActivity(intent)
                    /*closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))*/
                    isFromScanAdmim = false
//                    finish()
                }
                else if(isFromScan)
                {
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("wait...")
                    }
                    standaloneCoroutine = launch(CommonPool) {
                        delay(10000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                toast("time out")
                            }
                        }
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN,pulicMiKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,recovery))
                    isFromScan = false
                }else{
                    if(isClickLogin)
                    {
                        KLog.i("开始用websocket登录路由器")
                        loginBack = false
                        runOnUiThread {
                            showProgressDialog(getString(R.string.login_))
                        }
                        standaloneCoroutine = launch(CommonPool) {
                            delay(10000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                }
                            }
                        }
//            standaloneCoroutine.cancel()
                        var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        KLog.i("没有初始化。。登录接口设置loginBackListener"+"##" +AppConfig.instance.name +"##"+this.name+"##"+AppConfig.instance.messageReceiver)
                        AppConfig.instance.messageReceiver!!.loginBackListener = this
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                    }

                }

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Network_error)
                }

            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        when (toxStatusEvent.status) {
            0 -> {
                KLog.i("P2P连接成功")
                LogUtil.addLog("P2P连接成功:","LoginActivityActivity")
                runOnUiThread {
                    KLog.i("444")
                    closeProgressDialog()
                    //toast("login time out")
                }
                ConstantValue.isToxConnected = true

                if(ConstantValue.curreantNetworkType.equals("TOX"))
                {
                    ConstantValue.isHasWebsocketInit = true
                    AppConfig.instance.getPNRouterServiceMessageReceiver()
                    KLog.i("没有初始化。。设置loginBackListener")
                    AppConfig.instance.messageReceiver!!.loginBackListener = this
                }
                if( stopTox ||  ConstantValue.curreantNetworkType.equals("WIFI"))
                    return
                if(isFromScan)
                {

                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend( ConstantValue.scanRouterId)
                    }
                    standaloneCoroutine = launch(CommonPool) {
                        delay(60000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                toast("time out")
                            }
                        }
                    }

                    runOnUiThread {
                        var tips = getString(R.string.login_)
                        if(ConstantValue.freindStatus == 1)
                        {
                            tips = "wait..."
                        }else{
                            tips = "circle connecting..."
                        }
                        showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if(standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                EventBus.getDefault().post(StopTox())
                                false
                            } else false
                        })
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
                    var baseData = BaseData(4,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                    ConstantValue.unSendMessage.put("recovery",baseDataJson)
                    ConstantValue.unSendMessageFriendId.put("recovery",ConstantValue.scanRouterId.substring(0, 64))
                    ConstantValue.unSendMessageSendCount.put("recovery",0)
                    //ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    isFromScan = false
                }else{
                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend(routerId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend(routerId)
                    }
                    if(isClickLogin)
                    {
                        //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))
                        loginBack = false

                        standaloneCoroutine = launch(CommonPool) {
                            delay(60000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                }
                            }
                        }
                        runOnUiThread {
                            var tips = getString(R.string.login_)
                            if(ConstantValue.freindStatus == 1)
                            {
                                tips = getString(R.string.login_)
                            }else{
                                tips = "Circle connecting..."
                            }
                            showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    if(standaloneCoroutine != null)
                                        standaloneCoroutine.cancel()
                                    EventBus.getDefault().post(StopTox())
                                    false
                                } else false
                            })
                        }
                        var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())

                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        var baseData = BaseData(4,login)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        ConstantValue.unSendMessage.put("login",baseDataJson)
                        ConstantValue.unSendMessageFriendId.put("login",routerId.substring(0, 64))
                        ConstantValue.unSendMessageSendCount.put("login",0)
                        //ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        isClickLogin = false;
                    }

                }
            }
            1 -> {
                LogUtil.addLog("P2P连接中Reconnecting:","LoginActivityActivity")
            }
        }

    }
    override fun initData() {
        version.text = getString(R.string.version) +""+ BuildConfig.VERSION_NAME +"("+getString(R.string.Build)+BuildConfig.VERSION_CODE+")"
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
        }
        var adminRouterId = intent.getStringExtra("adminRouterIdOK")
        adminUserSn = intent.getStringExtra("adminUserSnOK")
        var adminUserId = intent.getStringExtra("adminUserIdOK")
        var adminUserName = intent.getStringExtra("adminUserNameOK")

        newRouterEntity = RouterEntity()
        lastLoginUserId = FileUtil.getLocalUserData("userid")
        lastLoginUserSn = FileUtil.getLocalUserData("usersn")
        EventBus.getDefault().register(this)
        loginBtn.setOnClickListener {
            if (!isUnlock) {
                showUnlock()
                return@setOnClickListener
            }
            isFromScanAdmim = false
            if(!openNewOnPow)
            {
                isFromScan = false
            }
            if (NetUtils.isNetworkAvalible(this)) {
                var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
                if (routerList.size == 0 && !openNewOnPow) {
                    return@setOnClickListener
                }
                if(!WiFiUtil.isNetworkConnected())
                {
                    toast("Please check the network")
                    return@setOnClickListener
                }
                if(islogining)
                {
                    if(isloginOutTime)
                    {
                        isloginOutTime = false
                        runOnUiThread {
                            var tips = getString(R.string.login_)
                            showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    if(standaloneCoroutine != null)
                                        standaloneCoroutine.cancel()
                                    EventBus.getDefault().post(StopTox())
                                    false
                                } else false
                            })
                        }
                    }else{
                        toast("logining")
                    }

                    return@setOnClickListener
                }
                islogining = true
                ConstantValue.currentRouterIp = ""
                //ConstantValue.scanRouterId = routerId;
                //MessageRetrievalService.stopThisService(this_)
                if(AppConfig.instance.messageReceiver != null)
                    AppConfig.instance.messageReceiver!!.close()
                ConstantValue.isWebsocketConnected = false
                ConstantValue.lastRouterId=""
                ConstantValue.lastPort=""
                ConstantValue.lastFilePort=""
                ConstantValue.lastRouterId=""
                ConstantValue.lastRouterSN=""
                ConstantValue.lastNetworkType =""
                isClickLogin = false
                try {
                    MessageHelper.clearAllMessage()
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }
                isStartLogin = true
                getServer(routerId, userSn, true, true)
//                    startLogin()
            } else {
                toast(getString(R.string.internet_unavailable))
            }
        }
        scanIconLogin.setOnClickListener {
            if (!isUnlock) {
                showUnlock()
                return@setOnClickListener
            }
            mPresenter.getScanPermission()
        }
        miniScanIconLogin.setOnClickListener {
            if (!isUnlock) {
                showUnlock()
                return@setOnClickListener
            }
            mPresenter.getScanPermission()
        }
        ivNoCircle.setOnClickListener {
            if (!isUnlock) {
                showUnlock()
                return@setOnClickListener
            }
            mPresenter.getScanPermission()
        }
        viewLogLogin.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        var this_ = this
        var isStartWebsocket = false
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_AUTH_SUCCESS -> {
                        isUnlock = true
                        setResultInfo(R.string.fingerprint_success)
                        cancellationSignal = null
                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                        if(!autoLoginRouterSn.equals("") && autoLoginRouterSn!!.equals(userSn))
                        {
                            runOnUiThread {
                                getServer(routerId,userSn,true,true)
                            }

                        }
                    }
                    MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                    MyAuthCallback.MSG_UPD_DATA -> {
                        KLog.i("收到了组播的回复了 ")
                        var obj:String = msg.obj.toString()
                        if(!obj.equals(""))
                        {
                            var objArray = obj.split("##")
                            var index = 0;
                            for(item in objArray)
                            {
                                if(!item.equals(""))
                                {
                                    try {
                                        var udpData = AESCipher.aesDecryptString(objArray[index],"slph\$%*&^@-78231")
                                        var udpRouterArray = udpData.split(";")

                                        if(udpRouterArray.size > 1)
                                        {
                                            println("ipdizhi:"+udpRouterArray[1] +" ip: "+udpRouterArray[0])
                                            //ConstantValue.updRouterData.put(udpRouterArray[1],udpRouterArray[0])
                                            if(scanType == 1)//不是admin二维码
                                            {
                                                if(!ConstantValue.scanRouterId.equals("") && ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                                {
                                                    ConstantValue.currentRouterIp = udpRouterArray[0]
                                                    ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                    ConstantValue.port= ":18006"
                                                    ConstantValue.filePort = ":18007"
                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                    break;
                                                }else if(!routerId.equals("") && routerId.equals(udpRouterArray[1]))
                                                {
                                                    ConstantValue.currentRouterIp = udpRouterArray[0]
                                                    ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                    ConstantValue.port= ":18006"
                                                    ConstantValue.filePort = ":18007"
                                                    ConstantValue.currentRouterId = routerId
                                                    ConstantValue.currentRouterSN =  userSn
                                                    break;
                                                }
                                            }else{
                                                ConstantValue.curreantNetworkType = "WIFI"
                                                ConstantValue.currentRouterIp = udpRouterArray[0]
                                                ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                ConstantValue.port= ":18006"
                                                ConstantValue.filePort = ":18007"
                                                ConstantValue.currentRouterMac = RouterMacStr
                                                break;
                                            }


                                        }
                                    }catch (e:Exception)
                                    {
                                        e.printStackTrace()
                                    }

                                }
                                index ++

                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                                if(isFromScan || isFromScanAdmim)
                                {
                                    if(ConstantValue.isHasWebsocketInit)
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                    }else{
                                        ConstantValue.isHasWebsocketInit = true
                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                    }
                                    isStartWebsocket = true
                                    KLog.i("没有初始化。。设置loginBackListener"+this_)
                                    AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                }

                            }
                        }

                    }
                }
            }
        }
        initRouterUI()
        showUnlock()
    }

    fun showUnlock() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SpUtil.getBoolean(this, ConstantValue.fingerprintUnLock, true)) {
        //!BuildConfig.DEBUG &&
        if(BuildConfig.DEBUG)
        {
            isUnlock = true
            hasFinger = false
            return
        }
        var fingerprintSwitchFlag = SpUtil.getString(AppConfig.instance, ConstantValue.fingerprintSetting, "1")
        if(fingerprintSwitchFlag.equals("0") || fingerprintSwitchFlag.equals("-1"))
        {
            isUnlock = true
            hasFinger = true
            return;
        }
        if (!ConstantValue.loginOut && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // init fingerprint.
            try {
                val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                    try {
                        hasFinger = true
                        myAuthCallback = MyAuthCallback(handler)
                        val cryptoObjectHelper = CryptoObjectHelper()
                        if (cancellationSignal == null) {
                            cancellationSignal = CancellationSignal()
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                myAuthCallback, null)
//                        val builder = AlertDialog.Builder(this)
                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
//                        builder.setView(view)
//                        builder.setCancelable(false)

                        formatDialog = CommonDialog(this)
                        formatDialog?.setCancelable(false)

                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容

                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            formatDialog?.dismissWithAnimation()
//                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                            finish();
                            //android进程完美退出方法。
                            var intent = Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
                            this.startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            //System.runFinalizersOnExit(true);
                            System.exit(0);
                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this

                        formatDialog?.setView(view)
                        formatDialog?.show()

//                        builderTips = builder.create()
//                        builderTips?.show()
                    } catch (e: Exception) {
                        try {
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                            /* fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                                cancellationSignal, myAuthCallback, null);*/
                            val builder = AlertDialog.Builder(this)
                            val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                            builder.setView(view)
                            builder.setCancelable(false)
                            val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                            val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//
                            btn_comfirm.setText(R.string.cancel_btn_dialog)
                            tvContent.setText(R.string.choose_finger_dialog_title)
                            val currentContext = this
                            formatDialog = CommonDialog(this)
                            formatDialog?.setView(view)
                            formatDialog?.show()
//                            builderTips = builder.create()
//                            builderTips?.show()
                        } catch (er: Exception) {
                            er.printStackTrace()
                            formatDialog?.dismissWithAnimation()
                            toast(R.string.Fingerprint_init_failed_Try_again)
                        }

                    }

                } else {
                    var mKeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (!mKeyguardManager.isKeyguardSecure()) {
                        KLog.i("没有设置密码。。。。")
                        SpUtil.putString(this, ConstantValue.fingerPassWord, "")
                        val dialog = AlertDialog.Builder(this)
                        dialog.setMessage(R.string.No_fingerprints_do_you_want_to_set_them_up)
                        dialog.setCancelable(false)
                        dialog.setPositiveButton(android.R.string.ok
                        ) { dialog, which ->
                            var intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                            startActivity(intent)
                        }
                        dialog.setNegativeButton(android.R.string.cancel
                        ) { dialog, which ->
                            finish();
                            //android进程完美退出方法。
                            var intent = Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
                            this.startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            //System.runFinalizersOnExit(true);
                            System.exit(0);
                        }
                        dialog.create().show()

                    } else {
                        var intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                        if (intent != null) {
                            startActivityForResult(intent, AuthenticationScreen)
                        }
                    }
                }

            } catch (e: Exception) {
                SpUtil.putString(this, ConstantValue.fingerPassWord, "")
            }

        } else {
            SpUtil.putString(this, ConstantValue.fingerPassWord, "")
        }
    }
    private fun startLogin()
    {

        isloginOutTime = false
        isStartLogin = true
        if(!ConstantValue.lastNetworkType.equals(""))
        {
            isFromScan = false
            ConstantValue.curreantNetworkType = ConstantValue.lastNetworkType
            ConstantValue.currentRouterIp = ConstantValue.lastRouterIp
            ConstantValue.port = ConstantValue.lastPort
            ConstantValue.filePort = ConstantValue.lastFilePort
            ConstantValue.currentRouterId = ConstantValue.lastRouterId
            ConstantValue.currentRouterSN =  ConstantValue.lastRouterSN
            ConstantValue.lastRouterId=""
            ConstantValue.lastPort=""
            ConstantValue.lastFilePort=""
            ConstantValue.lastRouterId=""
            ConstantValue.lastRouterSN=""
            ConstantValue.lastNetworkType =""
        }
        /* if (loginKey.text.toString().equals("")) {
             toast(getString(R.string.please_type_your_password))
             return
         }*/
        if( ConstantValue.curreantNetworkType.equals("TOX"))
        {

            if(ConstantValue.isToxConnected)
            {
                isToxLoginOverTime = true
                //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))

                var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                KLog.i("没有初始化。。设置loginBackListener")
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                standaloneCoroutine = launch(CommonPool) {
                    delay(60000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                        }
                    }
                }
                runOnUiThread {
                    var tips = getString(R.string.login_)
                    if(ConstantValue.freindStatus == 1)
                    {
                        tips = getString(R.string.login_)
                    }else{
                        tips = "Circle connecting..."
                    }
                    showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if(standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            EventBus.getDefault().post(StopTox())
                            false
                        } else false
                    })
                }
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                }
            }else{
//                    if (!ConstantValue.isToxConnected) {
//                        loadLibrary()
//                    }
                isToxLoginOverTime = true
                isClickLogin = true
                stopTox = false
                ConstantValue.curreantNetworkType = "TOX"
                runOnUiThread {
                    showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            stopTox = true
                            false
                        } else false
                    })
                }
                LogUtil.addLog("P2P启动连接:","LoginActivityActivity")

                if(ConstantValue.isAntox)
                {
                    var intent = Intent(AppConfig.instance, ToxService::class.java)
                    startService(intent)
                }else{
                    var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
                    startService(intent)
                }

            }

        }else{
            isClickLogin = true
            if(!ConstantValue.isWebsocketConnected)
            {
                if (intent.hasExtra("flag")) {
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener")
                    AppConfig.instance.getPNRouterServiceMessageReceiver().loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                            }
                        }
                    }
                } else {
                    KLog.i("走不带flag")
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        KLog.i("已经初始化了。。走重连逻辑" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true).reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.name)
                    AppConfig.instance.messageReceiver!!.loginBackListener = this
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!!.loginBackListener +"##" +AppConfig.instance.name)
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!! + "##" +AppConfig.instance.name)
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                            }
                        }
                    }
                }
            }else{
                var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                standaloneCoroutine = launch(CommonPool) {
                    delay(10000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                        }
                    }
                }
                runOnUiThread {
                    showProgressDialog(getString(R.string.login_))
                }
                KLog.i("没有初始化。。设置loginBackListener")
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
        }

    }

    override fun onPause() {
        if (!isUnlock && hasFinger) {
            KLog.i("退出app...")
            super.onPause()
            cancellationSignal?.cancel()
            cancellationSignal = null
            AppConfig.instance.stopAllService()
            //android进程完美退出方法。
            var intent = Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
            this.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.runFinalizersOnExit(true);
            System.exit(0);
        } else {
            super.onPause()
        }
    }

    private fun getServer(routerId:String ,userSn:String,startToxFlag:Boolean,autoLogin:Boolean)
    {
        ConstantValue.currentRouterIp = ""
        islogining = false
        runOnUiThread {
            KLog.i("777")
            closeProgressDialog()
            showProgressNoCanelDialog("Connecting...")
        }
        if(WiFiUtil.isWifiConnect())
        {
            var count =0;
            KLog.i("测试计时器" + count)
            Thread(Runnable() {
                run() {

                    while (true)
                    {
                        KLog.i("currentRouterIp== " + ConstantValue.currentRouterIp)
                        if(count >=3)
                        {
                            //如果本地收到广播了，这个 currentRouterIp 肯定有值了。
                            if(!ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoInner
                                KLog.i("走本地：" + ConstantValue.currentRouterIp)
                                var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                {
                                    runOnUiThread {
                                        startLogin()
                                    }

                                }
                                Thread.currentThread().interrupt(); //方法调用终止线程
                                break;
                            }else{
                                // 通过http看是否有远程的路由器可以登录
                                KLog.i("通过http看是否有远程的路由器可以登录")
                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + routerId,  object : OkHttpUtils.OkCallback {
                                    override fun onFailure( e :Exception) {
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                    }

                                    override fun  onResponse(json:String ) {

                                        val gson = GsonUtil.getIntGson()
                                        var httpData: HttpData? = null
                                        try {
                                            if (json != null) {
                                                httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                KLog.i("http的返回为：" + httpData.toString())
                                                if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                {
                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                    ConstantValue.currentRouterIp = httpData.serverHost
                                                    ConstantValue.port = ":"+httpData.serverPort.toString()
                                                    ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                    ConstantValue.currentRouterId = routerId
                                                    ConstantValue.currentRouterSN =  userSn
                                                    ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoOuterNet
                                                    KLog.i("走远程：这个远程websocket如果连不上，会一直重连下去" + ConstantValue.currentRouterIp+ConstantValue.port)
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                                    {
                                                        runOnUiThread {
                                                            startLogin()
                                                        }

                                                    }
                                                    Thread.currentThread().interrupt() //方法调用终止线程
                                                }else{
                                                    //没有远程，开启tox
                                                    KLog.i("没有远程，开启tox")
                                                    startTox(startToxFlag)
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                                    {
                                                        runOnUiThread {
                                                            startLogin()
                                                        }

                                                    }
                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                }

                                            }
                                        } catch (e: Exception) {
                                            startTox(startToxFlag)
                                            var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                            if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                            {
                                                runOnUiThread {
                                                    startLogin()
                                                }

                                            }
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                        }
                                    }
                                })
                                break
                            }

                        }
                        // 走广播，本地的路由器
                        count ++;
                        MobileSocketClient.getInstance().init(handler,this)
                        var toxIdMi = AESCipher.aesEncryptString(routerId,"slph\$%*&^@-78231")
                        MobileSocketClient.getInstance().destroy()
                        MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                        MobileSocketClient.getInstance().receive()
                        KLog.i("测试计时器" + count)
                        Thread.sleep(1000)
                    }

                }
            }).start()
        }else{

            Thread(Runnable() {
                run() {

                    OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + routerId,  object : OkHttpUtils.OkCallback {
                        override fun onFailure( e :Exception) {
                            startTox(startToxFlag)
                            var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                            if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                            {
                                runOnUiThread {
                                    startLogin()
                                }

                            }
                            Thread.currentThread().interrupt(); //方法调用终止线程
                        }

                        override fun  onResponse(json:String ) {

                            val gson = GsonUtil.getIntGson()
                            var httpData: HttpData? = null
                            try {
                                if (json != null) {
                                    var  httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                    if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                    {
                                        ConstantValue.curreantNetworkType = "WIFI"
                                        ConstantValue.currentRouterIp = httpData.serverHost
                                        ConstantValue.port = ":"+httpData.serverPort.toString()
                                        ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                        ConstantValue.currentRouterId = routerId
                                        ConstantValue.currentRouterSN =  userSn
                                        ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoOuterNet
                                        KLog.i("走远程：" + ConstantValue.currentRouterIp+ConstantValue.port)
                                        /* AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                         AppConfig.instance.messageReceiver!!.loginBackListener = this*/
                                        runOnUiThread {
                                            KLog.i("555")
//                                            standaloneCoroutine.cancel()
//                                            closeProgressDialog()
                                        }
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt() //方法调用终止线程
                                    }else{
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                    }

                                }
                            } catch (e: Exception) {
                                startTox(startToxFlag)
                                var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                {
                                    runOnUiThread {
                                        startLogin()
                                    }

                                }
                                Thread.currentThread().interrupt(); //方法调用终止线程
                            }
                        }
                    })
                }
            }).start()

        }
    }
    private fun startTox(startToxFlag:Boolean)
    {
        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if(!ConstantValue.isToxConnected && startToxFlag)
        {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:","LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        }else{
            runOnUiThread {
                KLog.i("666")
                closeProgressDialog()
            }
        }
    }
    override fun getScanPermissionSuccess() {
        //showProgressDialog("wait...")
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
            //case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, FingerprintManager.FINGERPRINT_ERROR_NO_SPACE, FingerprintManager.FINGERPRINT_ERROR_TIMEOUT, FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                setResultInfo(R.string.ErrorHwUnavailable_warning)
            }
        }
    }

    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD, FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY, FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT, FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> setResultInfo(R.string.AcquiredToSlow_warning)
        }
    }

    private fun setResultInfo(stringId: Int) {
        if (stringId == R.string.fingerprint_success) {
            finger?.setImageDrawable(resources.getDrawable(R.mipmap.icon_fingerprint_complete))
            setResult(RESULT_OK, intent)
            SpUtil.putString(this, ConstantValue.fingerPassWord, "888888")
            formatDialog?.dismissWithAnimation()
        } else {
            toast(stringId)
        }
    }

    override fun setupActivityComponent() {
        DaggerLoginActivityComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .loginActivityModule(LoginActivityModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: LoginActivityContract.LoginActivityContractPresenter) {
        mPresenter = presenter as LoginActivityPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CustomPopWindow.onBackPressed()) {
            } else {
                exitToast()
            }

        }
        return false
    }

    fun exitToast(): Boolean {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.Press_again, Toast.LENGTH_SHORT)
                    .show()
            exitTime = System.currentTimeMillis()
        } else {

            AppConfig.instance.stopAllService()
            //android进程完美退出方法。
            var intent = Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
            this.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.runFinalizersOnExit(true);
            System.exit(0);
        }
        return false
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            if(!WiFiUtil.isNetworkConnected())
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Please_connect_to_network)
                }
                return
            }
            hasRouterParentLogin.visibility = View.GONE
            noRoutergroupLogin.visibility = View.GONE
            try {
                var result = data!!.getStringExtra("result");
                if(!result.contains("type_"))
                {
                    if (isMacAddress(result)) {
                        //todo
                        scanType = 0;
                        RouterMacStr = result
                        if(RouterMacStr != null && !RouterMacStr.equals(""))
                        {
                            if(AppConfig.instance.messageReceiver != null)
                                AppConfig.instance.messageReceiver!!.close()
                            if(WiFiUtil.isWifiConnect())
                            {
                                showProgressDialog("wait...")
                                ConstantValue.currentRouterMac  = ""
                                isFromScanAdmim = true
                                var count =0;
                                KLog.i("测试计时器Mac" + count)
                                Thread(Runnable() {
                                    run() {

                                        while (true)
                                        {
                                            if(count >=3)
                                            {
                                                /* if(ConstantValue.currentRouterMac.equals(""))
                                                 {
                                                     runOnUiThread {
                                                         closeProgressDialog()
                                                         RouterMacStr = ""
                                                         isFromScanAdmim = false
                                                         toast(R.string.Unable_to_connect_to_router)
                                                     }
                                                 }
                                                 Thread.currentThread().interrupt(); //方法调用终止线程
                                                 break;*/
                                                if(!ConstantValue.currentRouterMac.equals(""))
                                                {
                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                    break;
                                                }else{
                                                    getMacFromRemote(this_)
                                                    break;
                                                }
                                            }
                                            count ++;
                                            MobileSocketClient.getInstance().init(handler,this)
                                            var toMacMi = AESCipher.aesEncryptString(RouterMacStr,"slph\$%*&^@-78231")
                                            MobileSocketClient.getInstance().destroy()
                                            MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                            MobileSocketClient.getInstance().receive()
                                            KLog.i("测试计时器Mac" + count)
                                            Thread.sleep(1000)
                                        }

                                    }
                                }).start()
                            }else{
                                showProgressDialog("wait...")
                                getMacFromRemote(this_)
                            }


                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                toast(R.string.code_error)
                            }
                        }
                        return
                    } else {
                        toast(R.string.code_error)
                        return
                    }
                    toast(R.string.code_error)
                    return;
                }
                var type = result.substring(0,6);
                var data = result.substring(7,result.length);
                var soureData:ByteArray =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
                if(type.equals("type_1"))
                {
                    scanType = 1
                    val keyId:ByteArray = ByteArray(6) //密钥ID
                    val RouterId:ByteArray = ByteArray(76) //路由器id
                    val UserSn:ByteArray = ByteArray(32)  //用户SN
                    System.arraycopy(soureData, 0, keyId, 0, 6)
                    System.arraycopy(soureData, 6, RouterId, 0, 76)
                    System.arraycopy(soureData, 82, UserSn, 0, 32)
                    var keyIdStr = String(keyId)
                    var RouterIdStr = String(RouterId)
                    var UserSnStr = String(UserSn)

                    ConstantValue.scanRouterId = RouterIdStr
                    ConstantValue.scanRouterSN = UserSnStr
                    if(RouterIdStr != null && !RouterIdStr.equals("")&& UserSnStr != null && !UserSnStr.equals(""))
                    {
                        if(AppConfig.instance.messageReceiver != null)
                            AppConfig.instance.messageReceiver!!.close()
                        ConstantValue.lastNetworkType = ConstantValue.curreantNetworkType

                        ConstantValue.lastRouterIp =  ConstantValue.currentRouterIp
                        ConstantValue.lastPort=  ConstantValue.port
                        ConstantValue.lastFilePort= ConstantValue.filePort
                        ConstantValue.lastRouterId =   ConstantValue.currentRouterId
                        ConstantValue.lastRouterSN =  ConstantValue.currentRouterSN

                        isFromScan = true
                        ConstantValue.currentRouterIp = ""
                        if(WiFiUtil.isWifiConnect())
                        {
                            showProgressDialog("wait...")
                            var count =0;
                            KLog.i("测试计时器" + count)
                            Thread(Runnable() {
                                run() {

                                    while (true)
                                    {
                                        if(count >=3)
                                        {
                                            if(!ConstantValue.currentRouterIp.equals(""))
                                            {
                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                                break;
                                            }else{

                                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                                    override fun onFailure( e :Exception) {
                                                        startToxAndRecovery()
                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                    }
                                                    override fun  onResponse(json:String ) {

                                                        val gson = GsonUtil.getIntGson()
                                                        var httpData: HttpData? = null
                                                        try {
                                                            if (json != null) {
                                                                httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                                if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                                {
                                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                                    ConstantValue.currentRouterIp = httpData.serverHost
                                                                    ConstantValue.port = ":"+httpData.serverPort.toString()
                                                                    ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                    if(ConstantValue.isHasWebsocketInit)
                                                                    {
                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                    }else{
                                                                        ConstantValue.isHasWebsocketInit = true
                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                    }
                                                                    KLog.i("没有初始化。。设置loginBackListener"+this_)
                                                                    AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                                                    Thread.currentThread().interrupt() //方法调用终止线程
                                                                }else{
                                                                    startToxAndRecovery()
                                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                                }

                                                            }
                                                        } catch (e: Exception) {
                                                            startToxAndRecovery()
                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                        }
                                                    }
                                                })
                                                break;
                                            }

                                        }
                                        count ++;
                                        MobileSocketClient.getInstance().init(handler,this)
                                        var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                                        MobileSocketClient.getInstance().destroy()
                                        MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                                        MobileSocketClient.getInstance().receive()
                                        KLog.i("测试计时器" + count)
                                        Thread.sleep(1000)
                                    }

                                }
                            }).start()
                        }else{
                            showProgressDialog("wait...")
                            Thread(Runnable() {
                                run() {
                                    OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                        override fun onFailure( e :Exception) {
                                            startToxAndRecovery()
                                        }

                                        override fun  onResponse(json:String ) {

                                            val gson = GsonUtil.getIntGson()
                                            var httpData: HttpData? = null
                                            try {
                                                if (json != null) {
                                                    var  httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                    if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                    {
                                                        ConstantValue.curreantNetworkType = "WIFI"
                                                        ConstantValue.currentRouterIp = httpData.serverHost
                                                        ConstantValue.port = ":"+httpData.serverPort.toString()
                                                        ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                        ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                        ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                        if(ConstantValue.isHasWebsocketInit)
                                                        {
                                                            AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                        }else{
                                                            ConstantValue.isHasWebsocketInit = true
                                                            AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                        }
                                                        KLog.i("没有初始化。。设置loginBackListener"+this_)
                                                        AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                                    }else{
                                                        startToxAndRecovery()
                                                    }

                                                }
                                            } catch (e: Exception) {
                                                startToxAndRecovery()
                                            }
                                        }
                                    })
                                }
                            }).start()
                        }
                    }else{
                        toast(R.string.code_error)
                    }
                }else  if(type.equals("type_2"))
                {
                    scanType = 0;
                    RouterMacStr = String(soureData)
                    if(RouterMacStr != null && !RouterMacStr.equals(""))
                    {
                        if(AppConfig.instance.messageReceiver != null)
                            AppConfig.instance.messageReceiver!!.close()
                        if(WiFiUtil.isWifiConnect())
                        {
                            showProgressDialog("wait...")
                            ConstantValue.currentRouterMac  = ""
                            isFromScanAdmim = true
                            var count =0;
                            KLog.i("测试计时器Mac" + count)
                            Thread(Runnable() {
                                run() {

                                    while (true)
                                    {
                                        if(count >=3)
                                        {
                                            if(ConstantValue.currentRouterMac.equals(""))
                                            {
                                                runOnUiThread {
                                                    closeProgressDialog()
                                                    RouterMacStr = ""
                                                    isFromScanAdmim = false
                                                    toast(R.string.Unable_to_connect_to_router)
                                                }
                                            }
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                            break;
                                        }else if(!ConstantValue.currentRouterMac.equals(""))
                                        {
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                            break;
                                        }
                                        count ++;
                                        MobileSocketClient.getInstance().init(handler,this)
                                        var toMacMi = AESCipher.aesEncryptString(RouterMacStr,"slph\$%*&^@-78231")
                                        MobileSocketClient.getInstance().destroy()
                                        MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                        MobileSocketClient.getInstance().receive()
                                        KLog.i("测试计时器Mac" + count)
                                        Thread.sleep(1000)
                                    }

                                }
                            }).start()

                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                toast(R.string.Please_connect_to_WiFi)
                            }
                        }
                    }else{
                        runOnUiThread {
                            closeProgressDialog()
                            toast(R.string.code_error)
                        }
                    }
                }else if(type.equals("type_3"))
                {
                    var left = result.substring(7,result.length)
                    var signprivatek = left.substring(0,left.indexOf(","))
                    left = left.substring(signprivatek.length+1,left.length)
                    var usersn = left.substring(0,left.indexOf(","))
                    left = left.substring(usersn.length+1,left.length)
                    var username = left.substring(0,left.length)
                    username = String(RxEncodeTool.base64Decode(username))
                    SpUtil.putString(this@LoginActivityActivity, ConstantValue.username, username)
                    var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()

                    if(signprivatek.equals(ConstantValue.libsodiumprivateSignKey))
                    {
                        toast(R.string.Same_account_no_need_to_import)
                        return;
                    }else{
                        runOnUiThread {
                            importUserDialog = CommonDialog(this)
                            val realContent = getString(R.string.overwritten)
//                            val builder = android.app.AlertDialog.Builder(this)
                            val view = View.inflate(this, R.layout.dialog_layout, null)
//                            builder.setView(view)
//                            builder.setCancelable(true)
                            val title = view.findViewById<View>(R.id.title) as TextView//设置标题
                            val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                            val btn_cancel = view.findViewById<View>(R.id.btn_left) as Button//取消按钮
                            val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//确定按钮
                            title.setText(R.string.Importing_users)
                            tvContent.setText(realContent)
                            //取消或确定按钮监听事件处l
                            importUserDialog?.setView(view)
//                            val dialog = builder.create()
                            btn_cancel.text = getString(R.string.cancel)
                            btn_cancel.setOnClickListener {
                                importUserDialog?.dismissWithAnimation()
                            }
                            btn_comfirm.setOnClickListener {
                                importUserDialog?.dismissWithAnimation()
                                FileUtil.deleteFile(Environment.getExternalStorageDirectory().getPath()+ConstantValue.localPath + "/RouterList/routerData.json")
                                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
                                val localSignArrayList: ArrayList<CryptoBoxKeypair>
                                val localMiArrayList: ArrayList<CryptoBoxKeypair>
                                val gson = Gson()

                                var strSignPublicSouce = ByteArray(32)
                                var  signprivatekByteArray = RxEncodeTool.base64Decode(signprivatek)
                                System.arraycopy(signprivatekByteArray, 32, strSignPublicSouce, 0, 32)


                                var dst_public_SignKey = strSignPublicSouce
                                var dst_private_Signkey = signprivatekByteArray
                                val strSignPrivate:String =  signprivatek
                                val strSignPublic =  RxEncodeTool.base64Encode2String(strSignPublicSouce)
                                ConstantValue.libsodiumprivateSignKey = strSignPrivate
                                ConstantValue.libsodiumpublicSignKey = strSignPublic
                                ConstantValue.localUserName = username
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                localSignArrayList = ArrayList()
                                var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                                SignData.privateKey = strSignPrivate
                                SignData.publicKey = strSignPublic
                                SignData.userName = username
                                localSignArrayList.add(SignData)
                                FileUtil.saveKeyData(gson.toJson(localSignArrayList),"libsodiumdata_sign")


                                var dst_public_MiKey = ByteArray(32)
                                var dst_private_Mikey = ByteArray(32)
                                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKey)
                                var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkey)

                                val strMiPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Mikey)
                                val strMiPublic =  RxEncodeTool.base64Encode2String(dst_public_MiKey)
                                ConstantValue.libsodiumprivateMiKey = strMiPrivate
                                ConstantValue.libsodiumpublicMiKey = strMiPublic
                                ConstantValue.localUserName = username
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                localMiArrayList = ArrayList()
                                var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                                RSAData.privateKey = strMiPrivate
                                RSAData.publicKey = strMiPublic
                                RSAData.userName = username
                                localMiArrayList.add(RSAData)
                                FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")
                                AppConfig.instance.deleteEmailData()
                                runOnUiThread {
                                    toast("Import success")
                                    initRouterUI()
                                }
                            }
                            importUserDialog?.show()
                        }
                    }
                }
            }catch (e:Exception)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.code_error)
                }
            }

        }else if (requestCode == AuthenticationScreen){
            if (resultCode == Activity.RESULT_OK) {
                handler!!.obtainMessage(MSG_AUTH_SUCCESS).sendToTarget()
            } else {
                KLog.i("密码错误。。。。")
            }
        }
    }
    private fun startToxAndRecovery() {

        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if (!ConstantValue.isToxConnected) {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:", "LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        } else {
            //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
            runOnUiThread {
                showProgressDialog("wait...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        EventBus.getDefault().post(StopTox())
                        false
                    } else false
                })
            }
            KLog.i("没有初始化。。设置loginBackListener")
            AppConfig.instance.messageReceiver!!.loginBackListener = this
            if (ConstantValue.isAntox) {
                InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
            }else{
                ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
            }
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
            var baseData = BaseData(4, recovery)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
            }
        }
    }

    fun initRouterUI()
    {
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        if (routerList.size != 0) {
            openNewOnPow = false
            var hasCheckedRouter = false
            loginBtn.background = resources.getDrawable(R.drawable.btn_white)
            run breaking@ {
                routerList.forEach {
                    if(adminUserSn != null && !adminUserSn.equals(""))
                    {
                        val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                        if (it.userSn.equals(adminUserSn)) {
                            routerId = it.routerId
                            userSn = it.userSn
                            userId = it.userId
                            username = name!!
                            if(it.dataFileVersion == null)
                            {
                                dataFileVersion = 0
                            }else{
                                dataFileVersion = it.dataFileVersion
                            }
                            routerNameTips.setTextColor(resources.getColor(R.color.white))
                            if(it.routerName != null){
                                routerNameTips.text = it.routerName
                                ivAvatar.setText(it.routerName)
                            }else{
                                routerNameTips.text =  "Router 1"
                                ivAvatar.setText("Router 1")
                            }

                            tvUserName.text = "Hello\n"+name+"\nWelcome back!"
                            if(it.loginKey != null){
                                loginKey.setText(it.loginKey)
                            }else{
                                loginKey.setText("")
                            }

                            hasCheckedRouter = true
                            return@breaking
                        }
                    }else{
                        var autoLoginRouterSn = SpUtil.getString(this, ConstantValue.autoLoginRouterSn, "")
                        val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                        if(!autoLoginRouterSn.equals(""))
                        {
                            if (it.userSn.equals(autoLoginRouterSn)) {
                                routerId = it.routerId
                                userSn = it.userSn
                                userId = it.userId
                                username = it.username
                                if(it.dataFileVersion == null)
                                {
                                    dataFileVersion = 0
                                }else{
                                    dataFileVersion = it.dataFileVersion
                                }

                                if(it.routerName != null){
                                    routerNameTips.text = it.routerName
                                    ivAvatar.setText(it.routerName)
                                }else{
                                    routerNameTips.text =  "Router 1"
                                    ivAvatar.setText("Router 1")
                                }
                                desc.text = getString(R.string.login_has_router)
                                val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                                tvUserName.text = "Hello\n"+name+"\nWelcome back!"
                                if(it.loginKey != null){
                                    loginKey.setText(it.loginKey)
                                }else{
                                    loginKey.setText("")
                                }

                                hasCheckedRouter = true
                                return@breaking
                            }
                        }else{
                            if (it.lastCheck) {
                                routerId = it.routerId
                                userSn = it.userSn
                                userId = it.userId
                                username = name!!
                                if (it.dataFileVersion == null) {
                                    dataFileVersion = 0
                                } else {
                                    dataFileVersion = it.dataFileVersion
                                }
                                routerNameTips.setTextColor(resources.getColor(R.color.white))
                                if(it.routerName != null){
                                    routerNameTips.text = it.routerName
                                    ivAvatar.setText(it.routerName)
                                }else{
                                    routerNameTips.text =  "Router 1"
                                    ivAvatar.setText("Router 1")
                                }
                                desc.text = getString(R.string.login_has_router)
                                val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                                tvUserName.text = "Hello\n"+name+"\nWelcome back!"
                                if(it.loginKey != null){
                                    loginKey.setText(it.loginKey)
                                }else{
                                    loginKey.setText("")
                                }

                                hasCheckedRouter = true
                                return@breaking
                            }
                        }

                    }

                }
            }
            if (!hasCheckedRouter) {
                routerId = routerList[0].routerId
                userSn =  routerList[0].userSn
                userId = routerList[0].userId
                username = routerList[0].username
                desc.text = getString(R.string.login_has_router)
                val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                tvUserName.text = "Hello\n"+name+"\nWelcome back!"
                dataFileVersion = routerList[0].dataFileVersion
                if(routerList[0].dataFileVersion == null)
                {
                    dataFileVersion = 0
                }else{
                    dataFileVersion = routerList[0].dataFileVersion
                }
                routerNameTips.setTextColor(resources.getColor(R.color.white))
                if(routerList[0].routerName != null){
                    routerNameTips.text = routerList[0].routerName
                    ivAvatar.setText(routerList[0].routerName)
                }else{
                    routerNameTips.text = "Router 1"
                    ivAvatar.setText("Router 1")
                }

                if(routerList[0].loginKey != null){
                    loginKey.setText(routerList[0].loginKey)
                }else{
                    loginKey.setText("")
                }
            }
            ivAvatar.visibility = View.VISIBLE
            tvUserName.visibility = View.VISIBLE
            ivNoCircle.visibility = View.GONE
            joincircle.visibility = View.INVISIBLE
            loginBtn.background = resources.getDrawable(R.drawable.btn_white)
            if(routerList.size > 1)
            {
                routerNameTipsmore.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
                routerNameTipsmore.visibility = View.VISIBLE
            }else{
                routerNameTipsmore.visibility = View.INVISIBLE
            }
            hasRouterParentLogin.visibility = View.GONE
            noRoutergroupLogin.visibility = View.GONE
            scanParentLogin.visibility = View.GONE
        } else {
            if(openNewOnPow)
            {
                routerId = "3089876DD8A1A76274A3150FB87F9B24EF0C4C9AF16FAA37BAFE99C955DA2538155D6D0E5998"
                userSn =  "03F0000100163E04B79700005C6FDFE2"
                userId = "A470AC000000000000007520C91DBB951062C74EAD7C052B5F5382D4624C381AB31B53F00000"
                username = ConstantValue.localUserName!!
                desc.text = getString(R.string.login_has_router)
                val name = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                tvUserName.text = "Hello\n"+name+"\nWelcome back!"
                dataFileVersion = 0
                routerNameTips.setTextColor(resources.getColor(R.color.white))
                routerNameTips.text = "pow node"
                ivAvatar.setText("pow node")
                loginKey.setText("")
                ivAvatar.visibility = View.VISIBLE
                tvUserName.visibility = View.VISIBLE
                ivNoCircle.visibility = View.GONE
                joincircle.visibility = View.INVISIBLE
                loginBtn.background = resources.getDrawable(R.drawable.btn_white)
                routerNameTipsmore.visibility = View.INVISIBLE
                hasRouterParentLogin.visibility = View.GONE
                noRoutergroupLogin.visibility = View.GONE
                scanParentLogin.visibility = View.GONE
                ConstantValue.currentRouterId = routerId
                ConstantValue.currentRouterSN = userSn;
                isFromScan = true
            }else{
                var options = RequestOptions()
                        .centerCrop()
                        .transform(GlideCircleTransform())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .priority(Priority.HIGH)
                Glide.with(this)
                        .load(R.mipmap.icon_no_circle)
                        .apply(options)
                        .into(ivNoCircle)
                ivNoCircle.visibility = View.VISIBLE
                joincircle.visibility = View.VISIBLE
                ivAvatar.visibility = View.GONE
                tvUserName.visibility = View.VISIBLE
                username = ConstantValue.localUserName!!
                tvUserName.text = "Hello\n"+ ConstantValue.localUserName + "\nWelcome back!"
                routerNameTips.text = "You haven't joined any circle"
                desc.text = resources.getString(R.string.login_no_router)
                loginBtn.background = resources.getDrawable(R.drawable.btn_login_norouter)
                routerNameTips.setTextColor(resources.getColor(R.color.color_b2b2b2))
                routerNameTipsmore.visibility = View.VISIBLE
                hasRouterParentLogin.visibility = View.GONE
                noRoutergroupLogin.visibility = View.GONE
                scanParentLogin.visibility = View.GONE
            }

        }
        if(routerId!= null && !routerId.equals("") && ConstantValue.currentRouterIp.equals(""))
        {
//            getServer(routerId,userSn,false,false)
        }
        if (routerList.size > 0) {
            llCircle.setOnClickListener { view1 ->
                PopWindowUtil.showSelectRouterPopWindow(this, llCircle, object : PopWindowUtil.OnSelectListener{
                    override fun onSelect(position: Int, obj : Any) {
                        try
                        {
                            routerId = routerList[position].routerId
                            userSn = routerList[position].userSn
                            userId = routerList[position].userId
                            username = routerList[position].username
                            if(routerList[position].dataFileVersion != null)
                            {
                                dataFileVersion = routerList[position].dataFileVersion
                            }else{
                                dataFileVersion = 0
                            }
                            routerNameTips.text = routerList[position].routerName

                            ivAvatar.setText(routerList[position].routerName)
                            if(routerList[position].loginKey != null){
                                loginKey.setText(routerList[position].loginKey)
                            }else{
                                loginKey.setText("")
                            }
                            ConstantValue.currentRouterIp = ""
                            //ConstantValue.scanRouterId = routerId;
                            //MessageRetrievalService.stopThisService(this_)
                            if(AppConfig.instance.messageReceiver != null)
                                AppConfig.instance.messageReceiver!!.close()
                            ConstantValue.isWebsocketConnected = false
                            ConstantValue.lastRouterId=""
                            ConstantValue.lastPort=""
                            ConstantValue.lastFilePort=""
                            ConstantValue.lastRouterId=""
                            ConstantValue.lastRouterSN=""
                            ConstantValue.lastNetworkType =""
                            isClickLogin = false
                            try {
                                MessageHelper.clearAllMessage()
                            }catch (e:Exception)
                            {
                                e.printStackTrace()
                            }
                            isStartLogin = true
//                            getServer(routerId,userSn,false,false)
                            if(AppConfig.instance.messageReceiver != null)
                                AppConfig.instance.messageReceiver!!.close()
                            //routerList[position].lastCheck = true
                            //AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerList[position])
                        }catch (e:Exception)
                        {
                            e.printStackTrace()
                        }

                    }
                })
            }
        }
    }
    fun getMacFromRemote(this_:LoginActivityActivity)
    {
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.Unable_to_connect_to_router)
            }
        }
        var RouterMacData = RouterMacStr.replace(":","")
        var httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        if(!BuildConfig.DEBUG)
        {
            httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        }
        OkHttpUtils.getInstance().doGet(httpUrlData + RouterMacData,  object : OkHttpUtils.OkCallback {
            override fun onFailure( e :Exception) {
                runOnUiThread {
                    closeProgressDialog()
                    RouterMacStr = ""
                    isFromScanAdmim = false
                    toast(R.string.Unable_to_connect_to_router)
                }
                Thread.currentThread().interrupt(); //方法调用终止线程
            }
            override fun  onResponse(json:String ) {

                val gson = GsonUtil.getIntGson()
                var httpDataMac: HttpData? = null
                try {
                    if (json != null) {
                        httpDataMac = gson.fromJson<HttpData>(json, HttpData::class.java)
                        if(httpDataMac != null  && httpDataMac.retCode == 0 && httpDataMac.connStatus == 1)
                        {
                            ConstantValue.curreantNetworkType = "WIFI"
                            ConstantValue.currentRouterIp = httpDataMac.serverHost
                            ConstantValue.port = ":"+httpDataMac.serverPort.toString()
                            ConstantValue.filePort = ":"+(httpDataMac.serverPort +1).toString()
                            ConstantValue.currentRouterMac = RouterMacStr
                            /*ConstantValue.currentRouterId = ConstantValue.scanRouterId
                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN*/
                            if(ConstantValue.isHasWebsocketInit)
                            {
                                AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                            }else{
                                ConstantValue.isHasWebsocketInit = true
                                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                            }
                            KLog.i("没有初始化。。设置loginBackListener"+this_)
                            AppConfig.instance.messageReceiver!!.loginBackListener = this_
                            Thread.currentThread().interrupt() //方法调用终止线程
                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                RouterMacStr = ""
                                isFromScanAdmim = false
                                toast(R.string.Unable_to_connect_to_router)
                            }
                            Thread.currentThread().interrupt(); //方法调用终止线程
                        }

                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        closeProgressDialog()
                        RouterMacStr = ""
                        isFromScanAdmim = false
                        toast(R.string.Unable_to_connect_to_router)
                    }
                    Thread.currentThread().interrupt(); //方法调用终止线程
                }
            }
        })
    }
    fun loadLibrary() {
        try{
            KLog.i("load tox库")
            System.loadLibrary("tox")
        } catch (exception : java.lang.Exception) {
            exception.printStackTrace()
        }
    }

//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            try{
//                KLog.i("load tox库")
//                System.loadLibrary("tox")
//            } catch (exception : java.lang.Exception) {
//                exception.printStackTrace()
//            }
//        }
//    }
}