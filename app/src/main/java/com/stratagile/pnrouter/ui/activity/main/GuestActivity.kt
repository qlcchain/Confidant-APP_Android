package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import com.nightonke.wowoviewpager.Animation.ViewAnimation
import com.nightonke.wowoviewpager.Animation.WoWoPositionAnimation
import com.nightonke.wowoviewpager.Animation.WoWoTranslationAnimation
import com.nightonke.wowoviewpager.Enum.Ease
import com.nightonke.wowoviewpager.WoWoViewPagerAdapter
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.StopTox
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerGuestComponent
import com.stratagile.pnrouter.ui.activity.main.contract.GuestContract
import com.stratagile.pnrouter.ui.activity.main.module.GuestModule
import com.stratagile.pnrouter.ui.activity.main.presenter.GuestPresenter
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.CreateLocalAccountActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.utils.NetUtils.isMacAddress
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_guest.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/18 14:25:55
 */

class GuestActivity : BaseActivity(), GuestContract.View , PNRouterServiceMessageReceiver.RecoveryMessageCallback{

    @Inject
    internal lateinit var mPresenter: GuestPresenter

    private var animationAdded = false

    private var r: Int = 0

    private var handler: Handler? = null
    protected var screenW: Int = 0
    protected var screenH: Int = 0
    val REQUEST_SELECT_ROUTER = 2
    private var exitTime: Long = 0
    val REQUEST_SCAN_QRCODE = 1
    var isHasConnect = false
    var RouterMacStr = ""
    var scanType = 0 // 0 admin   1 其他
    var isFromScanAdmim = false
    var threadInit = false
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
        RouterMacStr = ""
        needFront = true
        isFromScanAdmim = false
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    protected fun fragmentNumber(): Int {
        return 3
    }

    protected fun fragmentColorsRes(): Array<Int> {
        return arrayOf(R.color.white, R.color.white, R.color.white)
    }
    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun initView() {
        setContentView(R.layout.activity_guest)
        var wowoAdapter = WoWoViewPagerAdapter.builder()
                .fragmentManager(supportFragmentManager)
                .count(fragmentNumber())                       // Fragment Count
                .colorsRes(*fragmentColorsRes())                // Colors of fragments
                .build()
        wowo.setAdapter(wowoAdapter)
        screenW = UIUtils.getDisplayWidth(this)
        screenH = UIUtils.getDisplayHeigh(this)

        r = Math.sqrt((screenW * screenW + screenH * screenH).toDouble()).toInt() + 10

        wowo.addTemporarilyInvisibleViews(1, llNext, iv2, tvPage2)

        wowo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
//                if (p0 == 2) {
//                    tvNext.text = getString(R.string.QR_Code)
//                    var drawable:Drawable = getResources()!!.getDrawable(R.mipmap.icon_little_scan)
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//                    tvNext.setCompoundDrawables(drawable,null,null,null)
//                } else {
//                    tvNext.text = resources.getString(R.string.next)
//                    var drawable:Drawable = getResources()!!.getDrawable(R.mipmap.no)
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
//                    tvNext.setCompoundDrawables(null,null,null,null)
//                }
            }

        })
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
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        addAnimations()
    }

    private fun addAnimations() {
        if (animationAdded) {
            return
        }
        animationAdded = true
        addBack()
        addIv0()
        addTvPage0()
        addTvPage0TestNet()

        addIv1()
        addTvPage1()

        addIv2()
        addTvPage2()
        addGotIt()
//        addFun()
        addDot()

        wowo.ready()
    }

    protected fun color(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.recoveryBackListener = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    private fun addTvPage0TestNet() {
//        wowo.addAnimation(tvPage0TestNet)
//                .add(WoWoTranslationAnimation.builder().page(0)
//                        .fromX(0f).toX((-screenW).toFloat())
//                        .fromY(0f).toY(-600f).build())
    }

    private fun addTvPage0() {
        wowo.addAnimation(tvPage0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX(-screenW.toFloat())
                        .fromY(0f).toY(0f).build())
    }


    private fun addIv0() {
        wowo.addAnimation(iv0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(0f).build())
    }
    private fun addFun() {
//        wowo.addAnimation(funTv0)
//                .add(WoWoTranslationAnimation.builder().page(0)
//                        .fromX(0f).toX((-screenW).toFloat())
//                        .keepY(0f).toY(0f).build())
//        wowo.addAnimation(funTv1)
//                .add(WoWoTranslationAnimation.builder().page(0)
//                        .fromX((screenW).toFloat()).toX(0f)
//                        .fromY(0f).toY(0f).build())
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .fromX(0f).toX(-screenW.toFloat())
//                        .fromY(0f).toY(0f).build())
//        wowo.addAnimation(funTv2)
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .fromX((screenW).toFloat()).toX(0f)
//                        .fromY(0f).toY(0f).build())
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .fromX(0f).toX(screenW.toFloat())
//                        .fromY(0f).toY(0f).build())
    }

    private fun addTvPage1() {
        wowo.addAnimation(tvPage1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX((screenW).toFloat()).toX(0f)
                        .fromY(0f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(-screenW.toFloat())
                        .fromY(0f).toY(0f).build())
    }


    private fun addIv1() {
        wowo.addAnimation(iv1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(0f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(0f).build())
    }

    private fun addTvPage2() {
        wowo.addAnimation(tvPage2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX((screenW).toFloat()).toX(0f)
                        .fromY(0f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(0f).build())
    }


    private fun addIv2() {
        wowo.addAnimation(iv2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(0f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((screenW).toFloat())
                        .keepY(0f).toY(0f).build())
    }

    private fun addGotIt() {
        wowo.addAnimation(llNext)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .keepX(tvNext.getTranslationX())
                        .fromY(screenH.toFloat()).toY(0f).ease(Ease.OutBack)
                        .build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .keepX(0f)
                        .fromY(0f).toY(screenH.toFloat())
                        .ease(Ease.InCubic).sameEaseBack(false).build())
    }

    private fun addBack() {
        wowo.addAnimation(ivBack)
    }

    private fun addDot() {
        val viewAnimation = ViewAnimation(dot)
        viewAnimation.add(WoWoPositionAnimation.builder().page(0)
                .fromX( dot0.x).toX(dot1.x)
                .keepY(0f)
                .ease(Ease.Linear).build())
        viewAnimation.add(WoWoPositionAnimation.builder().page(1)
                .fromX(dot1.x).toX(dot2.x)
                .keepY(0f)
                .ease(Ease.Linear).build())
        wowo.addAnimation(viewAnimation)
    }

    override fun initData() {
        var this_ = this
        var isStartWebsocket = false
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
        EventBus.getDefault().register(this)
        SpUtil.putInt(this, ConstantValue.LOCALVERSIONCODE, VersionUtil.getAppVersionCode(this))
        llNext.setOnClickListener {
            if (wowo.currentItem == 2) {
                /*if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                 {
                     AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                     AppConfig.instance.messageReceiver!!.recoveryBackListener = this

                 }*/
                if(ConstantValue.libsodiumprivateSignKey.equals(""))
                {
                    startActivity(Intent(this, CreateLocalAccountActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, LoginActivityActivity::class.java))
                    finish()
                    //mPresenter.getScanPermission()
                }
                //startActivity(Intent(this, LoginActivityActivity::class.java))
            } else {
//                wowo.next()
            }
        }
    }

    override fun setupActivityComponent() {
        DaggerGuestComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .guestModule(GuestModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: GuestContract.GuestContractPresenter) {
        mPresenter = presenter as GuestPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
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
                    finish()
                    /*closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))*/
                    isFromScanAdmim = false
                }else{
                    isHasConnect = true
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN,pulicMiKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,recovery))
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
                LogUtil.addLog("P2P连接成功:","GuestActivity")
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
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
                    var baseData = BaseData(4,recovery)
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
                LogUtil.addLog("P2P连接中Reconnecting:","GuestActivity")
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

    }
    override fun onResume() {
        exitTime = System.currentTimeMillis() - 2001
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.close()
        ConstantValue.isWebsocketConnected = false
        super.onResume()
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result");
            try {
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
                                                    getMacFromRemote()
                                                    break;
                                                }
                                            }/*else if(!ConstantValue.currentRouterMac.equals(""))
                                            {
                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                                break;
                                            }*/
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
                               /* runOnUiThread {
                                    closeProgressDialog()
                                    toast(R.string.Please_connect_to_WiFi)
                                }*/
                                getMacFromRemote()
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
        ConstantValue.curreantNetworkType = "TOX"
        if(!ConstantValue.isToxConnected)
        {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:","GuestActivity")
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
    fun getMacFromRemote()
    {
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
                            //KLog.i("没有初始化。。设置loginBackListener"+this_)
                            //AppConfig.instance.messageReceiver!!.loginBackListener = this_
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
}