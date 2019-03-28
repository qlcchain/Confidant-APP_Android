package com.stratagile.pnrouter.ui.activity.router

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.StopTox
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerSelectCircleComponent
import com.stratagile.pnrouter.ui.activity.router.contract.SelectCircleContract
import com.stratagile.pnrouter.ui.activity.router.module.SelectCircleModule
import com.stratagile.pnrouter.ui.activity.router.presenter.SelectCirclePresenter
import com.stratagile.pnrouter.ui.adapter.router.RouterListAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_select_circle.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import org.libsodium.jni.Sodium
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/03/28 13:52:55
 */

class SelectCircleActivity : BaseActivity(), SelectCircleContract.View, PNRouterServiceMessageReceiver.SelcectCircleCallBack {
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
            ConstantValue.logining = true
            LogUtil.addLog("loginBack:" + "begin", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userId, "userid")
            //FileUtil.saveUserData2Local(loginRsp.params!!.index,"userIndex")
            LogUtil.addLog("loginBack:" + "a", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn, "usersn")
            LogUtil.addLog("loginBack:" + "b", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid, "routerid")
            LogUtil.addLog("loginBack:" + "c", "LoginActivityActivity")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            ConstantValue.currentRouterId = loginRsp.params!!.routerid
            newRouterEntity.userId = loginRsp.params!!.userId
            newRouterEntity.index = loginRsp.params!!.index
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            //SpUtil.putString(this, ConstantValue.username, username)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if (loginRsp.params.nickName != null)
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
            LogUtil.addLog("loginBack:" + "d", "LoginActivityActivity")
            var needUpdate: ArrayList<MyRouter> = ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter: MyRouter = MyRouter();
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
            LogUtil.addLog("loginBack:" + "e", "LoginActivityActivity")
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
                KLog.i("333")
            }
            LogUtil.addLog("loginBack:" + "f", "LoginActivityActivity")
            /*loginOk = true
            isToxLoginOverTime = false*/
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            resetUnCompleteFileRecode()
            AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("flag", "logout")
            startActivity(intent)
        }
    }

    override fun logOutBack(jLogOutRsp: JLogOutRsp) {

        if(jLogOutRsp.params.retCode == 0)
        {
            ConstantValue.isHasWebsocketInit = true
            if(AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.close()

            ConstantValue.loginOut = true
            ConstantValue.logining = false
            ConstantValue.isHeart = false
            ConstantValue.currentRouterIp = ""
            resetUnCompleteFileRecode()
            if (ConstantValue.isWebsocketConnected) {
                FileMangerDownloadUtils.init()
                ConstantValue.webSockeFileMangertList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSockeFileMangertList.remove(it)
                }
                ConstantValue.webSocketFileList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSocketFileList.remove(it)
                }
            }else{
                val intentTox = Intent(this, KotlinToxService::class.java)
                this.stopService(intentTox)
            }
            ConstantValue.loginReq = null
            ConstantValue.isWebsocketReConnect = false
            connectRouter(currentRouterEntity!!)
        }
    }

    @Inject
    internal lateinit var mPresenter: SelectCirclePresenter
    var currentRouterEntity:RouterEntity? = null
    var isUnlock = false
    var routerListAdapter : RouterListAdapter? = null
    var newRouterEntity = RouterEntity()
    private lateinit var standaloneCoroutine : Job
    var routerId = ""
    var userSn = ""
    var userId = ""
    var username = ""
    var dataFileVersion = 0
    var lastLoginUserId = ""
    var lastLoginUserSn = ""
    var loginBack = false
    var isFromScan = false
    var isFromScanAdmim = false
    //是否点击了登陆按钮
    var isClickLogin = false
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
    var isStartWebsocket = false
    private var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    var routerEntity : RouterEntity? = null

    override fun initView() {
        setContentView(R.layout.activity_select_circle)
        tvTitle.text = "Select a Circle"
        llCancel.setOnClickListener {
            finish()
        }
    }
    override fun initData() {
        var this_ = this
        AppConfig.instance.messageReceiver?.selcectCircleCallBack = this
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        routerListAdapter = RouterListAdapter(routerList.MutableListToArrayList())
        routerList.forEachIndexed { index, it ->
            if (it.lastCheck) {
                routerEntity = it
                routerListAdapter?.selectedItem = index
            }
        }
        recyclerView.adapter = routerListAdapter
        routerListAdapter?.setOnItemClickListener { adapter, view, position ->
            if (routerListAdapter!!.isCkeckMode) {
                routerListAdapter!!.data[position].isMultChecked = !routerListAdapter!!.data[position].isMultChecked
                routerListAdapter!!.notifyItemChanged(position)
            } else {
                routerListAdapter!!.selectedItem = position
                routerListAdapter!!.notifyDataSetChanged()
                logOutRouter(routerListAdapter!!.data[position])
            }
        }
        multiSelectBtn.setOnClickListener {
            if (routerListAdapter != null) {
                routerListAdapter?.isCkeckMode = !routerListAdapter!!.isCkeckMode
                if (routerListAdapter!!.isCkeckMode) {
                    tvLeaveCircle.visibility = View.VISIBLE
                } else {
                    tvLeaveCircle.visibility = View.GONE
                }
                routerListAdapter!!.notifyDataSetChanged()
            }
        }
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {                   
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
                                    AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this_
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    //先退出当前的路由器
    fun logOutRouter(router : RouterEntity) {
        currentRouterEntity = router
        if(ConstantValue.logining)
        {
            var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
            var msgData = LogOutReq(routerEntity!!.routerId,selfUserId!!,routerEntity!!.userSn)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
            } else if (ConstantValue.isToxConnected) {
                val baseData = BaseData(2,msgData)
                val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(routerEntity!!.routerId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerEntity!!.routerId.substring(0, 64))
                }
            }
        }else{
            ConstantValue.isHasWebsocketInit = true
            if(AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.close()

            ConstantValue.loginOut = true
            ConstantValue.logining = false
            ConstantValue.isHeart = false
            ConstantValue.currentRouterIp = ""
            resetUnCompleteFileRecode()
            if (ConstantValue.isWebsocketConnected) {
                FileMangerDownloadUtils.init()
                ConstantValue.webSockeFileMangertList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSockeFileMangertList.remove(it)
                }
                ConstantValue.webSocketFileList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSocketFileList.remove(it)
                }
            }else{
                val intentTox = Intent(this, KotlinToxService::class.java)
                this.stopService(intentTox)
            }
            ConstantValue.loginReq = null
            ConstantValue.isWebsocketReConnect = false
            connectRouter(currentRouterEntity!!)
        }

    }


    /**
     * 重新登录路由的方法
     */
    fun connectRouter(router : RouterEntity) {
        if (NetUtils.isNetworkAvalible(this)) {
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            if (routerList.size == 0) {
                return
            }
            if(!WiFiUtil.isNetworkConnected())
            {
                toast("Please check the network")
                return
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

                return
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
                                         AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this*/
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
                AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this
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
                    AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this
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
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.name)
                    AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!!.selcectCircleCallBack +"##" +AppConfig.instance.name)
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
                AppConfig.instance.messageReceiver!!.selcectCircleCallBack = this
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
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
    /**
     * 批量删除圈子
     */
    fun leaveCircles() {

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

    override fun setupActivityComponent() {
        DaggerSelectCircleComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectCircleModule(SelectCircleModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SelectCircleContract.SelectCircleContractPresenter) {
        mPresenter = presenter as SelectCirclePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (handler != null) {
            handler?.removeCallbacksAndMessages(null)
        }
        if (handler != null) {
            handler = null
        }
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.selcectCircleCallBack = null
    }
}