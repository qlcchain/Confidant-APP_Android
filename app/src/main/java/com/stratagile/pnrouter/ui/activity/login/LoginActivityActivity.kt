package com.stratagile.pnrouter.ui.activity.login

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.*
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
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.*
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
    var loginBack = false
    var isFromScan = false
    var isFromScanAdmim = false
    var isClickLogin = false
    var isStartLogin = false
    var stopTox = false;
    var loginOk = false
    var isToxLoginOverTime = false;
    var maxLogin = 0
    var threadInit = false
    var RouterMacStr = ""
    var scanType = 0 // 0 admin   1 其他


    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        closeProgressDialog()
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
                    dataFileVersion = routerEntity.dataFileVersion
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
                AppConfig.instance.messageReceiver!!.loginBackListener = null
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            2 -> {
                runOnUiThread {
                    toast("Rid error")
                }

            }
            3-> {
                ConstantValue.lastNetworkType = "";
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    for( i in routerEntityList)
                    {

                        if(i!= null && !i.userId.equals(""))
                        {
                            routerId = i.routerId
                            userSn = i.userSn
                            userId = i.userId
                            username = i.username
                            dataFileVersion = i.dataFileVersion
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
                            getServer(routerId,userSn,true)
                        }
                    }
                }else{
                    AppConfig.instance.messageReceiver!!.loginBackListener = null
                    var intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra("flag", 1)
                    startActivity(intent)
                }

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



    private var exitTime: Long = 0
    private var loginGoMain:Boolean = false
    override fun loginBack(loginRsp: JLoginRsp) {
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
                    toast("need Verification")
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("rid error")
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("uid error")
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Validation failed")
                    closeProgressDialog()
                }
            }
            else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast("Verification code error")
                    closeProgressDialog()
                }
            }
            else{
                runOnUiThread {
                    toast("other error")
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast("userId is empty")
                closeProgressDialog()
            }
        } else {
            ConstantValue.loginOut = false
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
            newRouterEntity.index = loginRsp.params!!.index
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            SpUtil.putString(this, ConstantValue.username,username)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.loginKey = loginKey.text.toString()
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
            newRouterEntity.loginKey = loginKey.text.toString();
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
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
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
//        CrashReport.testNativeCrash()
    }
    override fun onResume() {
        if(AppConfig.instance.messageReceiver != null)
        {
            AppConfig.instance.messageReceiver!!.loginBackListener = this
        }
        exitTime = System.currentTimeMillis() - 2001

        super.onResume()
    }
    override fun onDestroy() {

        AppConfig.instance.messageReceiver?.loginBackListener = null
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
        if (builderTips != null) {
            builderTips?.dismiss()
            builderTips = null
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
        ivAvatar.setText(nameChange.name)
        loginKey.setText(nameChange.loginkey)
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
                }
                else if(isFromScan)
                {
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("wait...")
                    }
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))
                    isFromScan = false
                }else{
                    if(isClickLogin)
                    {
                        loginBack = false
                        runOnUiThread {
                            showProgressDialog("login...")
                        }
                        standaloneCoroutine = launch(CommonPool) {
                            delay(10000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
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
                    closeProgressDialog()
                    //toast("login time out")
                }
                ConstantValue.isToxConnected = true

                if(ConstantValue.curreantNetworkType.equals("TOX"))
                {
                    ConstantValue.isHasWebsocketInit = true
                    AppConfig.instance.getPNRouterServiceMessageReceiver()
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
                        var tips = "login..."
                        if(ConstantValue.freindStatus == 1)
                        {
                            tips = "wait..."
                        }else{
                            tips = "router connecting..."
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
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                    var baseData = BaseData(2,recovery)
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
                                    toast("login time out")
                                }
                            }
                        }
                        runOnUiThread {
                            var tips = "login..."
                            if(ConstantValue.freindStatus == 1)
                            {
                                tips = "login..."
                            }else{
                                tips = "router connecting..."
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
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
        }
        var adminRouterId = intent.getStringExtra("adminRouterIdOK")
        var adminUserSn = intent.getStringExtra("adminUserSnOK")
        var adminUserId = intent.getStringExtra("adminUserIdOK")
        var adminUserName = intent.getStringExtra("adminUserNameOK")

        newRouterEntity = RouterEntity()
        lastLoginUserId = FileUtil.getLocalUserData("userid")
        lastLoginUserSn = FileUtil.getLocalUserData("usersn")
        EventBus.getDefault().register(this)
        loginBtn.setOnClickListener {

            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            if (routerList.size == 0) {
                return@setOnClickListener
            }
            startLogin()
        }
        scanIconLogin.setOnClickListener {
            mPresenter.getScanPermission()
        }
        miniScanIconLogin.setOnClickListener {
            mPresenter.getScanPermission()
        }
        ivNoCircle.setOnClickListener {
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
                        setResultInfo(R.string.fingerprint_success)
                        cancellationSignal = null
                    }
                    MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                    MyAuthCallback.MSG_UPD_DATA -> {
                        var obj:String = msg.obj.toString()
                        if(!obj.equals(""))
                        {
                            var objArray = obj.split("##")
                            var index = 0;
                            for(item in objArray)
                            {
                                if(!item.equals(""))
                                {
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
                                    AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                }

                            }
                        }

                    }
                }
            }
        }
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        if (routerList.size != 0) {
            var hasCheckedRouter = false
            run breaking@ {
                routerList.forEach {
                    if(adminUserSn != null && !adminUserSn.equals(""))
                    {
                        if (it.userSn.equals(adminUserSn)) {
                            routerId = it.routerId
                            userSn = it.userSn
                            userId = it.userId
                            username = it.username
                            dataFileVersion = it.dataFileVersion
                            routerNameTips.setTextColor(resources.getColor(R.color.white))
                            if(it.routerName != null){
                                routerNameTips.text = it.routerName
                                ivAvatar.setText(it.routerName)
                            }else{
                                routerNameTips.text =  "Router 1"
                                ivAvatar.setText("Router 1")
                            }
                            tvUserName.text = "Hello\n"+it.username+"\nWelcome back!"
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
                        if(!autoLoginRouterSn.equals(""))
                        {
                            if (it.userSn.equals(autoLoginRouterSn)) {
                                routerId = it.routerId
                                userSn = it.userSn
                                userId = it.userId
                                username = it.username
                                dataFileVersion = it.dataFileVersion

                                if(it.routerName != null){
                                    routerNameTips.text = it.routerName
                                    ivAvatar.setText(it.routerName)
                                }else{
                                    routerNameTips.text =  "Router 1"
                                    ivAvatar.setText("Router 1")
                                }
                                tvUserName.text = "Hello\n"+it.username+"\nWelcome back!"
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
                                username = it.username
                                dataFileVersion = it.dataFileVersion
                                routerNameTips.setTextColor(resources.getColor(R.color.white))
                                if(it.routerName != null){
                                    routerNameTips.text = it.routerName
                                    ivAvatar.setText(it.routerName)
                                }else{
                                    routerNameTips.text =  "Router 1"
                                    ivAvatar.setText("Router 1")
                                }
                                tvUserName.text = "Hello\n"+it.username+"\nWelcome back!"
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
                tvUserName.text = "Hello\n"+ username+"\nWelcome back!"
                dataFileVersion = routerList[0].dataFileVersion
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
            tvUserName.visibility = View.INVISIBLE
            routerNameTips.text = "You haven't joined any circle"
            loginBtn.background = resources.getDrawable(R.drawable.btn_login_norouter)
            routerNameTips.setTextColor(resources.getColor(R.color.color_b2b2b2))
            routerNameTipsmore.visibility = View.VISIBLE
            hasRouterParentLogin.visibility = View.GONE
            noRoutergroupLogin.visibility = View.GONE
            scanParentLogin.visibility = View.GONE
        }
        if(routerId!= null && !routerId.equals("") && ConstantValue.currentRouterIp.equals(""))
        {
            getServer(routerId,userSn,false)
        }
        if (routerList.size > 0) {
            routerNameTips.setOnClickListener { view1 ->
                PopWindowUtil.showSelectRouterPopWindow(this, routerNameTips, object : PopWindowUtil.OnSelectListener{
                    override fun onSelect(position: Int, obj : Any) {
                        /* routerList.forEach {
                             if(it.lastCheck) {
                                 it.lastCheck = false
                                 AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                             }
                         }*/
                        try
                        {
                            routerId = routerList[position].routerId
                            userSn = routerList[position].userSn
                            userId = routerList[position].userId
                            username = routerList[position].username
                            dataFileVersion = routerList[position].dataFileVersion
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
                            /* if(AppConfig.instance.messageReceiver != null)
                                 AppConfig.instance.messageReceiver!!.close()*/
                            getServer(routerId,userSn,false)
                            //routerList[position].lastCheck = true
                            //AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerList[position])
                        }catch (e:Exception)
                        {

                        }

                    }
                })
            }
        }
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SpUtil.getBoolean(this, ConstantValue.fingerprintUnLock, true)) {
        if (false && !ConstantValue.loginOut && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // init fingerprint.
            try {
                val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                    try {
                        myAuthCallback = MyAuthCallback(handler)
                        val cryptoObjectHelper = CryptoObjectHelper()
                        if (cancellationSignal == null) {
                            cancellationSignal = CancellationSignal()
                        }
                        fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                myAuthCallback, null)
                        val builder = AlertDialog.Builder(this)
                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                        builder.setView(view)
                        builder.setCancelable(false)
                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容

                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮

                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            builderTips?.dismiss()
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
                        builderTips = builder.create()
                        builderTips?.show()
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
                            builderTips = builder.create()
                            builderTips?.show()
                        } catch (er: Exception) {
                            er.printStackTrace()
                            builderTips?.dismiss()
                            toast(R.string.Fingerprint_init_failed_Try_again)
                        }

                    }

                } else {
                    SpUtil.putString(this, ConstantValue.fingerPassWord, "")
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(R.string.No_fingerprints_do_you_want_to_set_them_up)
                    dialog.setCancelable(false)

                    dialog.setPositiveButton(android.R.string.ok
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

                AppConfig.instance.messageReceiver!!.loginBackListener = this
                standaloneCoroutine = launch(CommonPool) {
                    delay(60000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            toast("login time out")
                        }
                    }
                }
                runOnUiThread {
                    var tips = "login..."
                    if(ConstantValue.freindStatus == 1)
                    {
                        tips = "login..."
                    }else{
                        tips = "router connecting..."
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
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    AppConfig.instance.messageReceiver!!.loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(10000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                            }
                        }
                    }
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if(standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                false
                            } else false
                        })
                    }
//                onWebSocketConnected(ConnectStatus(0))
                } else {
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    AppConfig.instance.messageReceiver!!.loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(10000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                            }
                        }
                    }
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if(standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                false
                            } else false
                        })
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
                            toast("login time out")
                        }
                    }
                }
                runOnUiThread {
                    showProgressDialog("login...")
                }
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
        }

    }

    private fun getServer(routerId:String ,userSn:String,startToxFlag:Boolean)
    {
        runOnUiThread {
            closeProgressDialog()
            showProgressDialog("")
        }
        if(WiFiUtil.isWifiConnect())
        {
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
                                runOnUiThread {
                                    closeProgressDialog()
                                }
                                KLog.i("走本地：" + ConstantValue.currentRouterIp)
                                var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                if(!autoLoginRouterSn.equals("") && !isStartLogin)
                                {
                                    runOnUiThread {
                                        startLogin()
                                    }

                                }
                                Thread.currentThread().interrupt(); //方法调用终止线程
                                break;
                            }else{
                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + routerId,  object : OkHttpUtils.OkCallback {
                                    override fun onFailure( e :Exception) {
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
                                                if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                {
                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                    ConstantValue.currentRouterIp = httpData.serverHost
                                                    ConstantValue.port = ":"+httpData.serverPort.toString()
                                                    ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                    ConstantValue.currentRouterId = routerId
                                                    ConstantValue.currentRouterSN =  userSn
                                                    KLog.i("走远程：" + ConstantValue.currentRouterIp+ConstantValue.port)
                                                    /* AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                     AppConfig.instance.messageReceiver!!.loginBackListener = this*/
                                                    runOnUiThread {
                                                        closeProgressDialog()
                                                    }
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin)
                                                    {
                                                        runOnUiThread {
                                                            startLogin()
                                                        }

                                                    }
                                                    Thread.currentThread().interrupt() //方法调用终止线程
                                                }else{
                                                    startTox(startToxFlag)
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
                                            if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
                            if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
                                        KLog.i("走远程：" + ConstantValue.currentRouterIp+ConstantValue.port)
                                        /* AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                         AppConfig.instance.messageReceiver!!.loginBackListener = this*/
                                        runOnUiThread {
                                            closeProgressDialog()
                                        }
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt() //方法调用终止线程
                                    }else{
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
                                if(!autoLoginRouterSn.equals("") && !isStartLogin)
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
            builderTips?.dismiss()
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
            exitToast()
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
            hasRouterParentLogin.visibility = View.GONE
            noRoutergroupLogin.visibility = View.GONE
            try {
                var result = data!!.getStringExtra("result");
                if(!result.contains("type_"))
                {
                    toast(R.string.code_error)
                    return
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
                }
            }catch (e:Exception)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.code_error)
                }
            }

        }else{
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
            AppConfig.instance.messageReceiver!!.loginBackListener = this
            if (ConstantValue.isAntox) {
                InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
            }else{
                ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
            }

            var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
            var baseData = BaseData(2, recovery)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
            }
        }
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