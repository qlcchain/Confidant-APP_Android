package com.stratagile.pnrouter.ui.activity.login

import android.app.Activity
import android.content.Context
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
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.fingerprint.MyAuthCallback.*
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
import java.util.ArrayList
import javax.inject.Inject


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View, PNRouterServiceMessageReceiver.LoginMessageCallback {
    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        closeProgressDialog()
        when (recoveryRsp.params.retCode) {
            0 -> {
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
                    EventBus.getDefault().post(NameChange(routerEntity.routerName))
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
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            2 -> {
                toast("error")
            }
            3 -> {

            }
            4 -> {

            }
            else -> {
            }
        }
    }

    val REQUEST_SELECT_ROUTER = 2
    val REQUEST_SCAN_QRCODE = 1
    var loginBack = false
    var isFromScan = false
    var isClickLogin = false
    private var exitTime: Long = 0
    override fun loginBack(loginRsp: JLoginRsp) {
        KLog.i(loginRsp.toString())
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("need Verification")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("rid error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("uid error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("password error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast("Verification code error")
                    closeProgressDialog()
                }
            }
            if (loginRsp.params.retCode == 5) {
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
            FileUtil.saveUserData2Local(loginRsp.params!!.userId,"userid")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn,"usersn")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid,"routerid")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            newRouterEntity.userId = loginRsp.params!!.userId
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            SpUtil.putString(this, ConstantValue.username,username)
            SpUtil.putString(this, ConstantValue.routerId, routerId)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = routerId
            newRouterEntity.routerName = "Router " + (routerList.size + 1)
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = loginKey.text.toString();
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
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }

            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


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
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        isFromScan = false
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_login)
//        CrashReport.testNativeCrash()
    }
    override fun onResume() {
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
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {

        when (connectStatus.status) {
            0 -> {
                if(isFromScan)
                {
                    closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))
                }else{
                    loginBack = false
                    closeProgressDialog()
                    showProgressDialog("login...")
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
                    var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,login))
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
            1 -> {
                ConstantValue.isToxConnected = true
                AppConfig.instance.getPNRouterServiceMessageToxReceiver()
                AppConfig.instance.messageReceiver!!.loginBackListener = this
                if(isFromScan)
                {
                    InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                    var baseData = BaseData(2,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    var friendKey:FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    InterfaceScaleUtil.addFriend( routerId,this)
                    if(isClickLogin)
                    {
                        loginBack = false
                        closeProgressDialog()
                        showProgressDialog("login...")
                       standaloneCoroutine = launch(CommonPool) {
                            delay(15000)
                            if (!loginBack) {
                                runOnUiThread {
                                    //closeProgressDialog()
                                    //toast("login time out")
                                }
                            }
                        }
                        var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                        var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)

                        var baseData = BaseData(2,login)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        isClickLogin = false;
                    }

                }
            }
        }

    }
    override fun initData() {
        newRouterEntity = RouterEntity()
        lastLoginUserId = FileUtil.getLocalUserData("userid")
        lastLoginUserSn = FileUtil.getLocalUserData("usersn")
        EventBus.getDefault().register(this)
        loginBtn.setOnClickListener {

            if (loginKey.text.toString().equals("")) {
                toast(getString(R.string.please_type_your_password))
                return@setOnClickListener
            }
            if( ConstantValue.curreantNetworkType.equals("TOX"))
            {
                if(ConstantValue.isToxConnected)
                {
                    var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                    var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                    var baseData = BaseData(2,login)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))
                    AppConfig.instance.messageReceiver!!.loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(10000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                //toast("login time out")
                            }
                        }
                    }
                    MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    showProgressDialog("wait...")
                    isClickLogin = true
                    ConstantValue.curreantNetworkType = "TOX"
                    var intent = Intent(this, ToxService::class.java)
                    startService(intent)
                }

            }else{
                if(!ConstantValue.isWebsocketConnected)
                {
                    if (intent.hasExtra("flag")) {
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                        AppConfig.instance.messageReceiver!!.loginBackListener = this
                        showProgressDialog("connecting...")
//                onWebSocketConnected(ConnectStatus(0))
                    } else {
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                        AppConfig.instance.messageReceiver!!.loginBackListener = this
                        showProgressDialog("connecting...")
                    }
                }else{
                    var LoginKeySha = RxEncryptTool.encryptSHA256ToString(loginKey.text.toString())
                    var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
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
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,login))
                }
            }


//            mThread = CustomThread(routerId, userId)
//            mThread!!.start()
        }
        scanIconLogin.setOnClickListener {
            mPresenter.getScanPermission()
        }
        miniScanIconLogin.setOnClickListener {
            mPresenter.getScanPermission()
        }

        viewLogLogin.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        var this_ = this
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
                                        if(ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                        {
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                            break;
                                        }

                                    }
                                }
                                index ++

                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                                isFromScan = true
                                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                AppConfig.instance.messageReceiver!!.loginBackListener = this_
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
                    if (it.lastCheck) {
                        routerId = it.routerId
                        userSn = it.userSn
                        userId = it.userId
                        username = it.username
                        dataFileVersion = it.dataFileVersion

                        if(it.routerName != null){
                            routerNameTips.text = it.routerName
                        }else{
                            routerNameTips.text =  "Router 1"
                        }
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
            if (!hasCheckedRouter) {
                routerId = routerList[0].routerId
                userSn =  routerList[0].userSn
                userId = routerList[0].userId
                username = routerList[0].username
                dataFileVersion = routerList[0].dataFileVersion
                if(routerList[0].routerName != null){
                    routerNameTips.text = routerList[0].routerName
                }else{
                    routerNameTips.text = "Router 1"
                }
                if(routerList[0].loginKey != null){
                    loginKey.setText(routerList[0].loginKey)
                }else{
                    loginKey.setText("")
                }
            }
            hasRouterParentLogin.visibility = View.VISIBLE
            noRoutergroupLogin.visibility = View.INVISIBLE
            scanParentLogin.visibility = View.INVISIBLE
        } else {
            hasRouterParentLogin.visibility = View.INVISIBLE
            noRoutergroupLogin.visibility = View.VISIBLE
            scanParentLogin.visibility = View.VISIBLE
        }
        if (routerList.size > 0) {
            routerNameTips.setOnClickListener { view1 ->
                PopWindowUtil.showSelectRouterPopWindow(this, routerNameTips, object : PopWindowUtil.OnRouterSelectListener{
                    override fun onSelect(position: Int) {
                        /* routerList.forEach {
                             if(it.lastCheck) {
                                 it.lastCheck = false
                                 AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                             }
                         }*/
                        routerId = routerList[position].routerId
                        userSn = routerList[position].userSn
                        userId = routerList[position].userId
                        username = routerList[position].username
                        dataFileVersion = routerList[position].dataFileVersion
                        routerNameTips.text = routerList[position].routerName
                        //routerList[position].lastCheck = true
                        //AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerList[position])
                    }
                })
            }
        }
        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SpUtil.getBoolean(this, ConstantValue.fingerprintUnLock, true)) {
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
                            android.os.Process.killProcess(android.os.Process.myPid())
                            System.exit(0)
                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this
                        builderTips = builder.create()
                        //builderTips?.show()
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
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(intent)
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    }
                    dialog.setNegativeButton(android.R.string.cancel
                    ) { dialog, which ->
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
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

    override fun getScanPermissionSuccess() {
        showProgressDialog("wait...")
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
            finish()
            System.exit(0)
        }
        return false
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            hasRouterParentLogin.visibility = View.VISIBLE
            noRoutergroupLogin.visibility = View.INVISIBLE
            try {
                var result = data!!.getStringExtra("result");
                var soureData:ByteArray =  AESCipher.aesDecryptByte(result,"welcometoqlc0101")
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
                    ConstantValue.currentRouterIp = ""
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
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                            break;
                                        }else{
                                            isFromScan = true
                                            ConstantValue.curreantNetworkType = "TOX"
                                            var intent = Intent(this, ToxService::class.java)
                                            startService(intent)

                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                            break;
                                        }

                                    }
                                    count ++;
                                    MobileSocketClient.getInstance().init(handler,this)
                                    var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                                    MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                                    MobileSocketClient.getInstance().receive()
                                    KLog.i("测试计时器" + count)
                                    Thread.sleep(1000)
                                }

                            }
                        }).start()
                    }else{
                        isFromScan = true
                        ConstantValue.curreantNetworkType = "TOX"
                        var intent = Intent(this, ToxService::class.java)
                        startService(intent)
                    }
                   /* MobileSocketClient.getInstance().init(handler,this)
                    var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                    var aa = AESCipher.aesDecryptString(toxIdMi,"slph\$%*&^@-78231")
                    MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                    MobileSocketClient.getInstance().receive()*/
                }else{
                    toast(R.string.code_error)
                }
                /* for (data in ConstantValue.updRouterData)
                 {
                     var key:String = data.key;
                     if(key.equals(RouterIdStr))
                     {
                         ConstantValue.currentRouterIp = ConstantValue.updRouterData.get(key)!!
                         ConstantValue.currentRouterId = RouterIdStr
                         ConstantValue.currentRouterSN = UserSnStr
                         break;
                     }
                 }
                 if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                 {
                     isFromScan = true
                     AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                     AppConfig.instance.messageReceiver!!.loginBackListener = this
                 }*/
            }catch (e:Exception)
            {
                runOnUiThread {
                    toast(R.string.code_error)
                }
            }

            /*var result = data!!.getStringExtra("result");
            var soureData:ByteArray =  AESCipher.aesDecryptByte(result,"welcometoqlc0101")
            val keyId:ByteArray = ByteArray(6) //密钥ID
            val RouterId:ByteArray = ByteArray(76) //路由器id
            val UserSn:ByteArray = ByteArray(32)  //用户SN
            System.arraycopy(soureData, 0, keyId, 0, 6)
            System.arraycopy(soureData, 6, RouterId, 0, 76)
            System.arraycopy(soureData, 82, UserSn, 0, 32)
            var keyIdStr = String(keyId)
            var RouterIdStr = String(RouterId)
            var UserSnStr = String(UserSn)
            for (data in ConstantValue.updRouterData)
            {
                var key:String = data.key;
                if(key.equals(RouterIdStr))
                {
                    ConstantValue.currentRouterIp = ConstantValue.updRouterData.get(key)!!
                    ConstantValue.currentRouterId = RouterIdStr
                    ConstantValue.currentRouterSN = UserSnStr
                }
            }*/
            /* var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
             if (routerList != null && routerList.size != 0) {
                 routerList.forEach { itt ->
                     if (itt.routerId.equals(data!!.getStringExtra("result"))) {
                         routerNameTips?.setText(itt.routerName)
                         routerId = data!!.getStringExtra("result")
                         KLog.i("routerId为：" + routerId)
                         routerList.forEach {
                             if (it.lastCheck) {
                                 it.lastCheck = false
                                 AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(it)
                             }
                         }
                         itt.lastCheck = true
                         AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(itt)
                         return
                     }
                 }
                 routerId = data!!.getStringExtra("result")
                 routerNameTips.text = "Router " + (routerList.size + 1)
                 newRouterEntity.routerId = data!!.getStringExtra("result")
                 return
             } else {
                 routerNameTips?.setText("Router 1")
                 routerId = data!!.getStringExtra("result")
             }
             return*/
        }else{
            runOnUiThread {
                closeProgressDialog()
            }
        }
    }
}