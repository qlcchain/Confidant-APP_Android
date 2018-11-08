package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.easeui.utils.PathUtils
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.RSAData
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.RxEncryptTool
import com.stratagile.pnrouter.utils.SpUtil
import java.nio.ByteOrder
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    override fun loginSuccees() {
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToLogin() {
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToGuest() {
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


        var rsaData = FileUtil.readRSAData();
        val localRSAArrayList: ArrayList<RSAData>
        val gson = Gson()
        if(rsaData.equals(""))
        {
            val KeyPair = RxEncryptTool.generateRSAKeyPair(1024)
            val aahh = KeyPair!!.private.format
            val strBase64Private:String = Base64.encodeToString(KeyPair.private.encoded, Base64.NO_WRAP)
            val strBase64Public = Base64.encodeToString(KeyPair.public.encoded, Base64.NO_WRAP)
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
        var private = Base64.decode(ConstantValue.privateRAS, Base64.NO_WRAP)
        var public = Base64.decode(ConstantValue.publicRAS, Base64.NO_WRAP)
        var source = "0123456789";
        var keyPulic = RxEncryptTool.encryptByPublicKey(source.toByteArray(),public)

        val strBase64keyPulic:String = Base64.encodeToString(keyPulic, Base64.NO_WRAP)
        var keyOld = RxEncryptTool.decryptByPrivateKey(keyPulic,private)
        var keybb = String(keyOld);

        var key2 =  RxEncryptTool.generateKey();
        var AES_KEY = "123456789"
        var keybb2 = RxEncryptTool.encrypt(key2,AES_KEY)
        var keydd2 = RxEncryptTool.decrypt(key2,keybb2)
        //var keyaa = RxEncryptTool.encryptAES2Base64(keyStr.toByteArray(),AES_KEY.toByteArray())
        //var keybb = RxEncryptTool.decryptAES(keyaa,AES_KEY.toByteArray())
        PathUtils.getInstance().initDirs("", "", this)
        System.out.println(ByteOrder.nativeOrder());
        SpUtil.putString(this, ConstantValue.testValue, "test")
        mPresenter.getLastVersion()
        mPresenter.getPermission()
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