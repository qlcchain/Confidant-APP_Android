package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.CryptoBoxKeypair
import com.stratagile.pnrouter.entity.HttpData
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerImportAccountComponent
import com.stratagile.pnrouter.ui.activity.user.contract.ImportAccountContract
import com.stratagile.pnrouter.ui.activity.user.module.ImportAccountModule
import com.stratagile.pnrouter.ui.activity.user.presenter.ImportAccountPresenter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_import_account.*
import org.libsodium.jni.Sodium
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/02/20 14:43:29
 */

class ImportAccountActivity : BaseActivity(), ImportAccountContract.View {

    @Inject
    internal lateinit var mPresenter: ImportAccountPresenter
    val REQUEST_SCAN_QRCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_import_account)
    }
    override fun initData() {
        title.text = "Log in"
        ivScan.setOnClickListener {
            mPresenter.getScanPermission()
        }
    }
    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun setupActivityComponent() {
        DaggerImportAccountComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .importAccountModule(ImportAccountModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: ImportAccountContract.ImportAccountContractPresenter) {
        mPresenter = presenter as ImportAccountPresenter
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result");
            try {
                if(!result.contains("type_3"))
                {
                    toast(R.string.code_error)
                    return
                }
                var type = result.substring(0,6);
                var left = result.substring(7,result.length)
                var signprivatek = left.substring(0,result.indexOf(","))
                left = left.substring(signprivatek.length+1,result.length)
                var usersn = left.substring(0,result.indexOf(","))
                left = left.substring(usersn.length+1,result.length)
                var username = left.substring(0,result.length)
                var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
                var isHas = false
                routerList.forEach {
                    if (it.userSn.equals(usersn)) {
                        isHas = true
                        return@forEach
                    }
                }
                if(isHas)
                {
                    return;
                }

                val localSignArrayList: ArrayList<CryptoBoxKeypair>
                val localMiArrayList: ArrayList<CryptoBoxKeypair>
                val gson = Gson()
                var dst_public_SignKey = ByteArray(32)
                var dst_private_Signkey = ByteArray(64)
                var crypto_box_keypair_result = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)

                val strSignPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Signkey)
                val strSignPublic =  RxEncodeTool.base64Encode2String(dst_public_SignKey)
                ConstantValue.libsodiumprivateSignKey = strSignPrivate
                ConstantValue.libsodiumpublicSignKey = strSignPublic
                ConstantValue.localUserName = username
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                localSignArrayList = ArrayList()
                var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                SignData.privateKey = strSignPrivate
                SignData.publicKey = strSignPublic
                SignData.userName = username
                localSignArrayList.add(SignData)
                //FileUtil.saveKeyData(gson.toJson(localSignArrayList),"libsodiumdata_sign")


                var dst_public_MiKey = ByteArray(32)
                var dst_private_Mikey = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKey)
                var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkey)

                val strMiPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Mikey)
                val strMiPublic =  RxEncodeTool.base64Encode2String(dst_public_MiKey)
                ConstantValue.libsodiumprivateMiKey = strMiPrivate
                ConstantValue.libsodiumpublicMiKey = strMiPublic
                ConstantValue.localUserName = username
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                localMiArrayList = ArrayList()
                var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                RSAData.privateKey = strMiPrivate
                RSAData.publicKey = strMiPublic
                RSAData.userName = username
                localMiArrayList.add(RSAData)
                //FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")
                //startActivity(Intent(this, LoginActivityActivity::class.java))
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
    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}