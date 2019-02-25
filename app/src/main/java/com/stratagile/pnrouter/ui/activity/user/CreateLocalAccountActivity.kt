package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stratagile.pnrouter.R
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerCreateLocalAccountComponent
import com.stratagile.pnrouter.ui.activity.user.contract.CreateLocalAccountContract
import com.stratagile.pnrouter.ui.activity.user.module.CreateLocalAccountModule
import com.stratagile.pnrouter.ui.activity.user.presenter.CreateLocalAccountPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_create_local_account.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/02/20 14:14:35
 */

class CreateLocalAccountActivity : BaseActivity(), CreateLocalAccountContract.View, PNRouterServiceMessageReceiver.RecoveryMessageCallback {

    @Inject
    internal lateinit var mPresenter: CreateLocalAccountPresenter
    val REQUEST_SCAN_QRCODE = 1
    var scanType = 0 // 0 admin   1 其他
    private var handler: Handler? = null
    var isHasConnect = false
    var threadInit = false
    var RouterMacStr = ""
    var isFromScanAdmim = false
    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {

        ConstantValue.unSendMessage.remove("recovery")
        ConstantValue.unSendMessageFriendId.remove("recovery")
        ConstantValue.unSendMessageSendCount.remove("recovery")
        closeProgressDialog();
        when (recoveryRsp.params.retCode) {
            0 -> {
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {

                }else{
                    var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.dataFilePay = ""
                    newRouterEntity.loginKey = ""
                    var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
                    newRouterEntity.routerName = String(RxEncodeTool.base64Decode(recoveryRsp.params!!.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }
                startActivity(Intent(this, LoginActivityActivity::class.java))
                finish()
            }
            1 -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            2 -> {
                runOnUiThread {
                    toast("error")
                }
            }
            3 -> {
                var intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("flag", 1)
                startActivity(intent)
                finish()
            }
            4 -> {

            }
            else -> {
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_create_local_account)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
    }
    override fun initData() {
        var isStartWebsocket = false
        var this_ = this
        title.text = getString(R.string.create_an_account)
        importOtherAccount.setOnClickListener {
            startActivity(Intent(this, ImportAccountActivity::class.java))
            finish()
        }
        setNext.setOnClickListener {
            if (imputUserName.text.toString().equals("")) {
                toast(getString(R.string.please_type_your_username))
                return@setOnClickListener
            }
           var result =  createLocalUserData(imputUserName.text.toString())
            if(result)
            {
                startActivity(Intent(this, LoginActivityActivity::class.java))
                finish()
                //mPresenter.getScanPermission()
            }else{
                toast(getString(R.string.Create_failure))
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
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
                                            if(ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                            {
                                                ConstantValue.currentRouterIp = udpRouterArray[0]
                                                ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                ConstantValue.port = ":18006"
                                                ConstantValue.filePort = ":18007"
                                                break;
                                            }
                                        }else{
                                            ConstantValue.curreantNetworkType = "WIFI"
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
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
                                if(isHasConnect)
                                {
                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                }else{
                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                }
                                AppConfig.instance.messageReceiver!!.recoveryBackListener = this_
                                isStartWebsocket = true
                            }
                        }

                    }
                }
            }
        }
    }
    fun createLocalUserData(userName:String):Boolean
    {
        ConstantValue.libsodiumprivateSignKey = ""
        ConstantValue.libsodiumpublicSignKey = ""
        ConstantValue.libsodiumprivateMiKey = ""
        ConstantValue.libsodiumpublicMiKey = ""
        ConstantValue.localUserName = ""
        if(ConstantValue.libsodiumprivateSignKey.equals("") && ConstantValue.libsodiumpublicSignKey.equals(""))
        {
            val gson = Gson()
            var signData = FileUtil.readKeyData("libsodiumdata_sign")
            var miData = FileUtil.readKeyData("libsodiumdata_mi")
            val localSignArrayList: ArrayList<CryptoBoxKeypair>
            val localMiArrayList: ArrayList<CryptoBoxKeypair>
            if(signData.equals(""))
            {
                var dst_public_SignKey = ByteArray(32)
                var dst_private_Signkey = ByteArray(64)
                var crypto_box_keypair_result = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)

                val strSignPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Signkey)
                val strSignPublic =  RxEncodeTool.base64Encode2String(dst_public_SignKey)
                ConstantValue.libsodiumprivateSignKey = strSignPrivate
                ConstantValue.libsodiumpublicSignKey = strSignPublic
                ConstantValue.localUserName = userName
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                localSignArrayList = ArrayList()
                var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                SignData.privateKey = strSignPrivate
                SignData.publicKey = strSignPublic
                SignData.userName = userName
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
                ConstantValue.localUserName = userName
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                localMiArrayList = ArrayList()
                var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                RSAData.privateKey = strMiPrivate
                RSAData.publicKey = strMiPublic
                RSAData.userName = userName
                localMiArrayList.add(RSAData)
                FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")


            }else{
                var signStr = signData
                if (signStr != "") {
                    localSignArrayList = gson.fromJson<ArrayList<CryptoBoxKeypair>>(signStr, object : TypeToken<ArrayList<CryptoBoxKeypair>>() {

                    }.type)
                    if(localSignArrayList.size > 0)
                    {
                        ConstantValue.libsodiumprivateSignKey = localSignArrayList.get(0).privateKey
                        ConstantValue.libsodiumpublicSignKey =  localSignArrayList.get(0).publicKey
                        ConstantValue.localUserName =  localSignArrayList.get(0).userName
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)}
                }

                var miStr = miData
                if (miStr != "") {
                    localMiArrayList = gson.fromJson<ArrayList<CryptoBoxKeypair>>(miStr, object : TypeToken<ArrayList<CryptoBoxKeypair>>() {

                    }.type)
                    if(localMiArrayList.size > 0)
                    {
                        ConstantValue.libsodiumprivateMiKey = localMiArrayList.get(0).privateKey
                        ConstantValue.libsodiumpublicMiKey =  localMiArrayList.get(0).publicKey
                        ConstantValue.localUserName =  localMiArrayList.get(0).userName
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                        SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                    }
                }
            }
        }
        return true;
    }
   /* @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        when (connectStatus.status) {
            0 -> {
                if(isFromScanAdmim)
                {
                    runOnUiThread {
                        closeProgressDialog()
                    }
                    var intent = Intent(this, AdminLoginActivity::class.java)
                    startActivity(intent)
                    *//*closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))*//*
                    isFromScanAdmim = false
                }else{
                    isHasConnect = true
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))
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
                LogUtil.addLog("P2P连接成功:","CreateLocalAccountActivity")
                ConstantValue.isToxConnected = true
                AppConfig.instance.getPNRouterServiceMessageToxReceiver()

                if(!ConstantValue.scanRouterId.equals(""))
                {

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
                                false
                            } else false
                        })
                    }
                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
                    }

                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                    var baseData = BaseData(2,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{

                        ConstantValue.unSendMessage.put("recovery",baseDataJson)
                        ConstantValue.unSendMessageFriendId.put("recovery",ConstantValue.scanRouterId.substring(0, 64))
                        ConstantValue.unSendMessageSendCount.put("recovery",0)
                        //ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    }
                }
            }
            1 ->{
                LogUtil.addLog("P2P连接中Reconnecting:","CreateLocalAccountActivity")
            }
        }

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
                                            var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
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
            ConstantValue.freindStatus = 0
            LogUtil.addLog("P2P检测路由好友未上线，不可以发消息:","LoginActivityActivity")
        }

    }*/
    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun setupActivityComponent() {
        DaggerCreateLocalAccountComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .createLocalAccountModule(CreateLocalAccountModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: CreateLocalAccountContract.CreateLocalAccountContractPresenter) {
        mPresenter = presenter as CreateLocalAccountPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result");
            try {
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
                                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + ConstantValue.scanRouterId,  object : OkHttpUtils.OkCallback {
                                                    override fun onFailure( e :Exception) {
                                                        startToxAndRecovery()
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
                                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                    if(isHasConnect)
                                                                    {
                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                    }else{
                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                    }
                                                                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this_
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
                            Thread(Runnable() {
                                run() {


                                    OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + ConstantValue.scanRouterId,  object : OkHttpUtils.OkCallback {
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
                                                        if(isHasConnect)
                                                        {
                                                            AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                        }else{
                                                            AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                        }
                                                        AppConfig.instance.messageReceiver!!.recoveryBackListener = this_
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
                    scanType = 0
                    RouterMacStr = String(soureData)
                    if(RouterMacStr != null && !RouterMacStr.equals(""))
                    {
                        if(WiFiUtil.isWifiConnect())
                        {
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
    private fun startToxAndRecovery()
    {
       /* ConstantValue.curreantNetworkType = "TOX"
        if(!ConstantValue.isToxConnected)
        {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:","CreateLocalAccountActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        }else {
            runOnUiThread {
                showProgressDialog("wait...")
            }
            AppConfig.instance.messageReceiver!!.recoveryBackListener = this
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
        }*/
    }
}