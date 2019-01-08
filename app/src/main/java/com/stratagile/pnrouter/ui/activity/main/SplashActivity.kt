package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.*
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    private var handler: Handler? = null
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
        needFront = true
        AppConfig.instance.stopAllService()
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        LogUtil.addLog("app version :", BuildConfig.VERSION_NAME)
        var nickSouceName = String(RxEncodeTool.base64Decode("")).toLowerCase()
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
        mPresenter.getPermission()
       /* var aesKey = "0F578ED5897A958A"

        LogUtil.addLog("sendMsg aesKey:",aesKey)
        var my = RxEncodeTool.base64Decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDuGX8sjCbr2W62ygQNIanARcsqo8tUwK3AXuIeRUGtkLVJ+1BhH19ibn0MF8SrIjh2+4ndMD54gszCMdNtMyb93fKJZ2xsdHNiE71vi5Ms1UPYFIC4oMSEfq8qhefMwCgIJZpLmTaDHjLyETfjZ0RmnvVXIIiieUC7vNfnGLz4zQIDAQAB")
        LogUtil.addLog("sendMsg myKey:","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDuGX8sjCbr2W62ygQNIanARcsqo8tUwK3AXuIeRUGtkLVJ+1BhH19ibn0MF8SrIjh2+4ndMD54gszCMdNtMyb93fKJZ2xsdHNiE71vi5Ms1UPYFIC4oMSEfq8qhefMwCgIJZpLmTaDHjLyETfjZ0RmnvVXIIiieUC7vNfnGLz4zQIDAQAB")
        var friend = RxEncodeTool.base64Decode("nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6fLXHHG4HCmmXnrN6IjZJ2oRlnd7zfdFEfNZtCvuDWTt9ozDRJMHuxPwRbQFWrNmK9lP4wr8AxeGjh4cpSFvxiXnA3n0ea9yvrQe/ItbKIHcLjIUHUPi2DHoONpi4x3nbL+VrtEIZyyuiHKqaz3mc5wEKFKnU9yi88K1ecpmqUL5bQIDAQABn")
        LogUtil.addLog("sendMsg friendKey:","nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6fLXHHG4HCmmXnrN6IjZJ2oRlnd7zfdFEfNZtCvuDWTt9ozDRJMHuxPwRbQFWrNmK9lP4wr8AxeGjh4cpSFvxiXnA3n0ea9yvrQe/ItbKIHcLjIUHUPi2DHoONpi4x3nbL+VrtEIZyyuiHKqaz3mc5wEKFKnU9yi88K1ecpmqUL5bQIDAQABn")
        var SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),my))
        LogUtil.addLog("sendMsg SrcKey:",SrcKey.toString())
        var aa = RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),friend)
        var DstKey = RxEncodeTool.base64Encode(aa)
        LogUtil.addLog("sendMsg DstKey:",SrcKey.toString())*/
        //mPresenter.getLastVersion()
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