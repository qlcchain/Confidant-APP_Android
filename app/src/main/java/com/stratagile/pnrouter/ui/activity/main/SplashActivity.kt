package com.stratagile.pnrouter.ui.activity.main

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import com.hyphenate.easeui.utils.PathUtils
import com.jaeger.library.StatusBarUtil
import com.smailnet.eamil.Utils.AESCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.*
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import java.io.File
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    private var handler: Handler? = null
    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null
    private var countDownTimerUtils: CountDownTimerUtils? = null
    override fun loginSuccees() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToLogin() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToGuest() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, GuestActivity::class.java))
        finish()
    }
    override fun exitApp() {
        finish()
        System.exit(0)
    }

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        KLog.i("SplashActivityAAAA：onCreate")
        var sodium: Sodium = NaCl.sodium()
        needFront = true
        AppConfig.instance.stopAllService()
        super.onCreate(savedInstanceState)

        /*try {
            var intent = getIntent();
            var action = intent.getAction();//action
            var type = intent.getType();//类型

            //类型
            if (Intent.ACTION_SEND.equals(action) && type != null *//*&& "video/mp4".equals(type)*//*) {
                var uri =  intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
                //如果是媒体类型需要从数据库获取路径
                var filePath=getRealPathFromURI(uri);
                KLog.i("外部分享："+filePath)
                ConstantValue.shareFromLocalPath = filePath;
            }
        }catch (e:Exception)
        {

        }*/

    }
    /**
     * 通过Uri获取文件在本地存储的真实路径
     */
    fun  getRealPathFromURI(contentUri: Uri?):String {
        var proj = arrayOf(MediaStore.MediaColumns.DATA)
        var  cursor=getContentResolver().query(contentUri, proj!!, null, null, null);
        if(cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        }
        cursor.close();
        return "";
    }
    override fun onDestroy() {
        if (myAuthCallback != null) {
            myAuthCallback?.removeHandle()
            myAuthCallback = null
        }
        if (cancellationSignal != null) {
            cancellationSignal!!.cancel()
            cancellationSignal = null
        }
        super.onDestroy()
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
        StatusBarUtil.setColor(this, resources.getColor(R.color.mainColor), 0)
    }
    override fun initData() {
        KLog.i("SplashActivityAAAA：initData")
        if(BuildConfig.DEBUG)
        {
            SpUtil.putString(this, ConstantValue.fingerprintSetting, "0")
            SpUtil.putString(this, ConstantValue.screenshotsSetting, "0")
        }
        AppConfig.instance.isOpenSplashActivity = true
        ConstantValue.isGooglePlayServicesAvailable = SystemUtil.isGooglePlayServicesAvailable(this)
        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
        for(item in localMessageList)
        {
            if(item.timeStamp == null  || item.timeStamp == 0L)
            {
                item.setTimeStamp( DateUtil.getDateTimeStame(item.date))
                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(item)
            }
            if(item.sortId == null  || item.sortId == 0L)
            {
                item.sortId = item.msgId.toLong();
                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(item)
            }
        }
        if(!BuildConfig.DEBUG)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    if (fingerprintManager != null && fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()) {
                        try {
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                            ConstantValue.notNeedVerify = false
                            var fingerprintSwitchFlag = SpUtil.getString(AppConfig.instance, ConstantValue.fingerprintSetting, "1")
                            if(fingerprintSwitchFlag == "-1")
                            {
                                SpUtil.putString(this, ConstantValue.fingerprintSetting, "1")
                            }
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                        } catch (e: Exception) {
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }
                            ConstantValue.notNeedVerify = true
                            SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                        }

                    } else {
                        try {
                            var mKeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                            if (!mKeyguardManager.isKeyguardSecure()) {
                                KLog.i("没有设置密码。。。。")
                                SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                            }else{
                                var fingerprintSwitchFlag = SpUtil.getString(AppConfig.instance, ConstantValue.fingerprintSetting, "1")
                                if(fingerprintSwitchFlag == "-1")
                                {
                                    SpUtil.putString(this, ConstantValue.fingerprintSetting, "1")
                                }
                            }
                            ConstantValue.notNeedVerify = false
                        } catch (e: Exception) {
                            ConstantValue.notNeedVerify = true
                            SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                        }

                    }

                } catch (e: Exception) {
                    ConstantValue.notNeedVerify = true
                    SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                }

            } else {
                try {
                    var mKeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (!mKeyguardManager.isKeyguardSecure()) {
                        KLog.i("没有设置密码。。。。")
                        SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                    }else{
                        var fingerprintSwitchFlag = SpUtil.getString(AppConfig.instance, ConstantValue.fingerprintSetting, "1")
                        if(fingerprintSwitchFlag == "-1")
                        {
                            SpUtil.putString(this, ConstantValue.fingerprintSetting, "1")
                        }
                    }
                    ConstantValue.notNeedVerify = false
                } catch (e: Exception) {
                    ConstantValue.notNeedVerify = true
                    SpUtil.putString(this, ConstantValue.fingerprintSetting, "-1")
                }
            }
        }
        KLog.i("SplashActivityAAAA：initData00")
        LogUtil.addLog("app version :"+BuildConfig.VERSION_NAME)
        ConstantValue.msgIndex = (System.currentTimeMillis() / 1000).toInt() + (Math.random() * 100).toInt();
        var this_ = this
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
                                        if( ConstantValue.currentRouterId.equals(udpRouterArray[1]))
                                        {
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                            ConstantValue.port= ":18006"
                                            ConstantValue.filePort = ":18007"
                                            break;
                                        }

                                    }
                                }
                                index ++
                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                            }
                        }

                    }
                }
            }
        }
        MobileSocketClient.getInstance().init(handler,this)
        KLog.i("SplashActivityAAAA：initData0011")
        mPresenter.getPermission()
        KLog.i("SplashActivityAAAA：initData001122")


        KLog.i("SplashActivityAAAA：initData00112233")
        mPresenter.observeJump()
    }

    override fun setupActivityComponent() {
        DaggerSplashComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SplashContract.SplashContractPresenter) {
        mPresenter = presenter as SplashPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}