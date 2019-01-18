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
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import java.util.*

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
        var sodium: Sodium = NaCl.sodium()
        needFront = true
        AppConfig.instance.stopAllService()
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        LogUtil.addLog("app version :"+BuildConfig.VERSION_NAME)
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

        var dst_public_SignKeya = StringUitl.toBytes("f5 44 40 55 46 a0 6f 96 5c 42 14 ba 3d 79 7 7f e8 22 8d 4b 67 af 14 20 4f 54 66 25 5b 37 6d d6")
        var dst_private_Signkeyd = StringUitl.toBytes("34 4c b0 74 1c f4 2c 8c e8 93 73 3b 1 41 e4 b3 c4 c5 24 eb 9e 19 81 d9 41 5e 3c 49 53 4e b9 25 f5 44 40 55 46 a0 6f 96 5c 42 14 ba 3d 79 7 7f e8 22 8d 4b 67 af 14 20 4f 54 66 25 5b 37 6d d6")
        var dst_public_MiKey = ByteArray(32)
        var dst_private_Mikey = ByteArray(32)
        var crypto_sign_ed25519_pk_to_curve25519_resulta = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKeya)
        var crypto_sign_ed25519_sk_to_curve25519_resultb = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkeyd)

        var  dst_public_MiKeyStr = StringUitl.bytesToString(dst_public_MiKey)
        var  dst_private_SignkeydStr = StringUitl.bytesToString(dst_private_Mikey)

        var dst_public_TemKey_My = ByteArray(32)
        var dst_private_Temkey_My = ByteArray(32)

        var crypto_box_keypair_Temresult = Sodium.crypto_box_keypair(dst_public_TemKey_My,dst_private_Temkey_My)

        ConstantValue.libsodiumprivateTemKey = StringUitl.bytesToString(dst_private_Temkey_My)
        ConstantValue.libsodiumpublicTemKey =  StringUitl.bytesToString(dst_public_TemKey_My)


        //模拟生成好友的加解密公钥
        var dst_public_SignKey_Friend = ByteArray(32)
        var dst_private_Signkey_Friend = ByteArray(64)
        var crypto_box_keypair_result = Sodium.crypto_sign_keypair(dst_public_SignKey_Friend,dst_private_Signkey_Friend)
        var dst_public_MiKey_Friend = ByteArray(32)
        var dst_private_Mikey_Friend = ByteArray(32)
        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,dst_public_SignKey_Friend)
        var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey_Friend,dst_private_Signkey_Friend)

        //开始加密
        var dst_shared_key  = ByteArray(32)
        var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,dst_public_MiKey_Friend,dst_private_Temkey_My)

        var src_msg = Base58.encode("123456聚隆科技构建我国借我个偶就给我个饿哦go额外".toByteArray()).toByteArray()

        val random = org.libsodium.jni.crypto.Random()
        var src_nonce =  random.randomBytes(24)
        var encrypted = LibsodiumUtil.encrypt_data_symmetric(src_msg,src_nonce,dst_shared_key)


        var dst_public_SignKey = ByteArray(32)
        var dst_private_Signkey = ByteArray(64)
        var crypto_box_keypair_resultaa = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)
        //签名
        var dst_signed_msg = ByteArray(96)
        var signed_msg_len = IntArray(1)
        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,dst_public_TemKey_My,dst_public_TemKey_My.size,StringUitl.toBytes(ConstantValue.libsodiumprivateSignKey))
        var dst_msg = ByteArray(32)
        var msg_len = IntArray(1)
        var crypto_sign_open = Sodium.crypto_sign_open(dst_msg,msg_len,dst_signed_msg,dst_signed_msg.size,StringUitl.toBytes(ConstantValue.libsodiumpublicSignKey))


        var dst_shared_keyStr = StringUitl.bytesToString(dst_shared_key)
        //非对称加密方式crypto_box_seal加密对称密钥
        var dst_shared_key_Mi_My = ByteArray(32 + 48)
        var crypto_box_seal= Sodium.crypto_box_seal(dst_shared_key_Mi_My,dst_shared_key,dst_shared_key.size,StringUitl.toBytes(ConstantValue.libsodiumpublicMiKey))

        var dst_shared_key_Mi_MyStr = StringUitl.bytesToString(dst_shared_key_Mi_My)
        //非对称解密方式crypto_box_seal_open解密出对称密钥
        var dst_shared_key_Soucre_My = ByteArray(32)
        var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,StringUitl.toBytes(ConstantValue.libsodiumpublicMiKey),StringUitl.toBytes(ConstantValue.libsodiumprivateMiKey))
        //解密自己的消息
        var souceStr  = LibsodiumUtil.decrypt_data_symmetric(encrypted,src_nonce,dst_shared_key_Soucre_My)


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