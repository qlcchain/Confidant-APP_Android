package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import chat.tox.antox.utils.AntoxLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.easeui.utils.PathUtils
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.MyRouter
import com.stratagile.pnrouter.entity.RSAData
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.nio.ByteOrder
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    private var handler: Handler? = null

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

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        AntoxLog.debug("Registered another error handler when we didn't need to.", AntoxLog.DEFAULT_TAG())
        JavaToScala.init(this);
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
                                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                            break;
                                        }

                                    }
                                }
                                index ++
                            }
                        }

                    }
                }
            }
        }
        var rsaData = FileUtil.readRSAData();
        val localRSAArrayList: ArrayList<RSAData>
        val gson = Gson()

        var lastLoginRouterId = FileUtil.getLocalUserData("routerid")
        var lastLoginUserSn = FileUtil.getLocalUserData("usersn")
        ConstantValue.currentRouterId = lastLoginRouterId;
        if(lastLoginRouterId != null && !lastLoginRouterId.equals("")&& lastLoginUserSn != null && !lastLoginUserSn.equals(""))
        {
            MobileSocketClient.getInstance().init(handler,this)
            var toxIdMi = AESCipher.aesEncryptString(lastLoginRouterId,"slph\$%*&^@-78231")
            MobileSocketClient.getInstance().send("QLC"+toxIdMi)
            MobileSocketClient.getInstance().receive()
        }

        if(rsaData.equals(""))
        {
            val KeyPair = RxEncryptTool.generateRSAKeyPair(1024)
            val aahh = KeyPair!!.private.format
            val strBase64Private:String = RxEncodeTool.base64Encode2String(KeyPair.private.encoded)
            val strBase64Public = RxEncodeTool.base64Encode2String(KeyPair.public.encoded)
            ConstantValue.privateRAS = strBase64Private
            ConstantValue.publicRAS = strBase64Public
            localRSAArrayList = ArrayList()
            var RSAData:RSAData = RSAData()
            RSAData.privateKey = strBase64Private
            RSAData.publicKey = strBase64Public
            localRSAArrayList.add(RSAData)
            FileUtil.saveRSAData(gson.toJson(localRSAArrayList))
        }else{
            var rsaStr = FileUtil.readRSAData()
            if (rsaStr != "") {
                localRSAArrayList = gson.fromJson<ArrayList<RSAData>>(rsaStr, object : TypeToken<ArrayList<RSAData>>() {

                }.type)
                if(localRSAArrayList.size > 0)
                {
                    ConstantValue.privateRAS = localRSAArrayList.get(0).privateKey
                    ConstantValue.publicRAS =  localRSAArrayList.get(0).publicKey
                }
            }
        }
        var secret = Base58.encode("123456".toByteArray())
        var aastrs = String(Base58.decode(secret))
        var miMsg = AESCipher.aesEncryptString("aa","welcometoqlc0101")

      /*  val KeyPairaa = RxEncryptTool.generateRSAKeyPair(2014)
        val strBase64Private:String = RxEncodeTool.base64Encode2String(KeyPairaa.private.encoded)
        val strBase64Public = RxEncodeTool.base64Encode2String(KeyPairaa.public.encoded)

        val strBase64 = RxEncodeTool.base64Decode("Ck1JR2ZNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0R05BRENCaVFLQmdRRE01T0JnN29qei9TVWRHRTlzQmRQVkZrcmsKcUptdGVMUm9GNVlOOWkvc0JTeGVac1c3M2FnUkJ0U255MnZmWFNXVEhiZmw2U1laek5ha0VYZGVPZ3RTY0RPWQo4dnh1bHFTVG9xVitpTWMvQlFHYXlKZk1KWXpQUjFvUjBadG1BRWl1QTY4SGc2ZDdTY3NMMHpBZGFkbmFkVEFDCldUMWIvMEo3cVBRRDE2WVYxUUlEQVFBQgo=".toByteArray())
        var sourceaa = String(strBase64);
        var private = RxEncodeTool.base64Decode(strBase64Private)
        var public = RxEncodeTool.base64Decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDM5OBg7ojz/SUdGE9sBdPVFkrkqJmteLRoF5YN9i/sBSxeZsW73agRBtSny2vfXSWTHbfl6SYZzNakEXdeOgtScDOY8vxulqSToqV+iMc/BQGayJfMJYzPR1oR0ZtmAEiuA68Hg6d7ScsL0zAdadnadTACWT1b/0J7qPQD16YV1QIDAQAB")
        var source = "123456";
        var test =  RxEncodeTool.base64Decode(strBase64)
        var sourceaabb = RxEncodeTool.base64Encode(test);
        var keybbcaac = String(sourceaabb);
        var keyPulic = RxEncryptTool.encryptByPublicKey(source.toByteArray(),test)
        var keybbcc = String(keyPulic);
        val strBase64keyPulic:String = RxEncodeTool.base64Encode2String(keyPulic)
        var keyOld = RxEncryptTool.decryptByPrivateKey(keyPulic,private)
        var keybb = String(keyOld);

        var key2 =  RxEncryptTool.generateAESKey()
        var AES_KEY = "010001D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A47201000001B827EBD089CB00005BE14F55"
        var keybb2 = AESCipher.aesEncryptString(AES_KEY,"welcometoqlc0101")
        var keydd2 = AESCipher.aesDecryptString(keybb2,"welcometoqlc0101")
        var resultByte2 = AESCipher.aesDecryptString("ysPU3+VXYIWLKQkISIzR193yBMGzL+hkUPlRvrQPwh8yJmrX+BMRyGTdpJA349y00HZ6a0m+E0U7UP+TrEABf9UriJD6zff6IdfhQn8lbQBnIVWdONnMWmZzKpvX33ORpSj8qfvBYqc2NRt0Mq132fUHxfSljfYavhNYT2p016w=","welcometoqlc0101")
*/
        //var keyaa = RxEncryptTool.encryptAES2Base64(keyStr.toByteArray(),AES_KEY.toByteArray())
        //var keybb = RxEncryptTool.decryptAES(keyaa,AES_KEY.toByteArray())
        PathUtils.getInstance().initDirs("", "", this)
        System.out.println(ByteOrder.nativeOrder());
        SpUtil.putString(this, ConstantValue.testValue, "test")
        mPresenter.getPermission()
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