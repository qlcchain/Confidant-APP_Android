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
import kotlinx.android.synthetic.main.activity_register.*
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
       /* var dst_msgaaaa = ByteArray(32)
        var sign = "123456".toByteArray()
        System.arraycopy(sign, 0, dst_msgaaaa, 0, sign.size)
        var dst_signed_msg1 = ByteArray(96)
        var signed_msg_len1 = IntArray(1)
        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
        var mypublicTest = ByteArray(32)
        System.arraycopy(mySignPrivate, 32, mypublicTest, 0, 32)
        var aaabb = RxEncodeTool.base64Encode2String(mypublicTest)
        var crypto_sign2 = Sodium.crypto_sign(dst_signed_msg1,signed_msg_len1,dst_msgaaaa,dst_msgaaaa.size,mySignPrivate)
        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg1)

        var dst_msgaa = ByteArray(32)
        var msg_lenaa = IntArray(1)
        var crypto_sign_openaa = Sodium.crypto_sign_open(dst_msgaa,msg_lenaa,dst_signed_msg1,dst_signed_msg1.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
        var dst_msgaaSouce = String(dst_msgaa)

        var op = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
        var op2 = RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey)
        var bb = String(op)
        var cc = bb.toByteArray()
        //这里不要注释
        var dst_public_TemKey_My = ByteArray(32)
        var dst_private_Temkey_My = ByteArray(32)
        var crypto_box_keypair_Temresult = Sodium.crypto_box_keypair(dst_public_TemKey_My,dst_private_Temkey_My)
        var gg = dst_public_TemKey_My.toString()
        var hh = dst_private_Temkey_My.toString()
        ConstantValue.libsodiumprivateTemKey = RxEncodeTool.base64Encode2String(dst_private_Temkey_My)
        ConstantValue.libsodiumpublicTemKey =  RxEncodeTool.base64Encode2String(dst_public_TemKey_My)


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

        var src_msg = "123456聚隆科技构建我国借我个偶就给我个饿哦go额外".toByteArray()

        val random = org.libsodium.jni.crypto.Random()
        var src_nonce =  random.randomBytes(24)
        var encrypted = LibsodiumUtil.encrypt_data_symmetric(src_msg,src_nonce,dst_shared_key)

        //解密自己的消息
        var souceStrinit  = LibsodiumUtil.decrypt_data_symmetric(encrypted,src_nonce,dst_shared_key)

        var src_msg2 = "123456聚隆科技构建我国借我个偶就给我个饿哦go额外".toByteArray()

        var src_msgsrc_msg = "123456聚隆科技构建我国借我个偶就给我个饿哦go额外"
        var encryptedBase64 = LibsodiumUtil.encrypt_data_symmetric_string(src_msgsrc_msg,RxEncodeTool.base64Encode2String(src_nonce),RxEncodeTool.base64Encode2String(dst_shared_key))

        //解密自己的消息
        var souceStrinitBase64  = LibsodiumUtil.decrypt_data_symmetric_string(encryptedBase64,RxEncodeTool.base64Encode2String(src_nonce),RxEncodeTool.base64Encode2String(dst_shared_key))

        var dst_public_SignKey = ByteArray(32)
        var dst_private_Signkey = ByteArray(64)
        var crypto_box_keypair_resultaa = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)
        //签名
        var dst_signed_msg = ByteArray(96)
        var signed_msg_len = IntArray(1)
        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,dst_public_TemKey_My,dst_public_TemKey_My.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey))
        var dst_msg = ByteArray(32)
        var msg_len = IntArray(1)
        var crypto_sign_open = Sodium.crypto_sign_open(dst_msg,msg_len,dst_signed_msg,dst_signed_msg.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
        var dst_shared_keyStr = RxEncodeTool.base64Encode2String(dst_shared_key)
        //非对称加密方式crypto_box_seal加密对称密钥
        var dst_shared_key_Mi_My = ByteArray(32 + 48)
        var crypto_box_seal= Sodium.crypto_box_seal(dst_shared_key_Mi_My,dst_shared_key,dst_shared_key.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicMiKey))

        var dst_shared_key_Mi_MyStr = RxEncodeTool.base64Encode2String(dst_shared_key_Mi_My)
        //非对称解密方式crypto_box_seal_open解密出对称密钥
        var dst_shared_key_Soucre_My = ByteArray(32)
        var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicMiKey),RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateMiKey))
        //解密自己的消息
        var souceStr  = LibsodiumUtil.decrypt_data_symmetric(encrypted,src_nonce,dst_shared_key_Soucre_My)*/


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