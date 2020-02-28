package com.stratagile.pnrouter.ui.activity.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEncryptMsgComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgContract
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.encrypt_main_activity.*
import kotlinx.android.synthetic.main.encrypt_source.*
import org.libsodium.jni.Sodium

import javax.inject.Inject;
import qlc.rpc.impl.*
import qlc.network.QlcClient
import qlc.utils.Helper


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2020/02/19 16:13:12
 */

class EncryptMsgActivity : BaseActivity(), EncryptMsgContract.View {

    @Inject
    internal lateinit var mPresenter: EncryptMsgPresenter
    var verifiers= arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encrypt_main_activity)
    }
    override fun initData() {

        tvTitle.text = "Encrypt And Decrypt"
        backBtn.setOnClickListener {
            finish()
        }
        decryptBtn.setOnClickListener {
            var password_editText = password_editText.text.toString()
            if(password_editText =="")
            {
                toast(R.string.Please_enter_content)
                return@setOnClickListener
            }
            var fixedKey = ""
            var libsodiumpublicTemKey = ""
            var encryptType = ""
            var random_nonce = ""
            var qlcchainAdress = ""
            var tokenCount = ""
            var tokenType = ""
            var miTxt = ""
            try {
                fixedKey = password_editText.substring(0,8)
                libsodiumpublicTemKey = password_editText.substring(8,52)
                encryptType = password_editText.substring(52,54)
                random_nonce = password_editText.substring(54,86)
                qlcchainAdress = ""
                tokenCount = ""
                tokenType = ""
                miTxt = ""
                if(encryptType == "00")
                {
                    miTxt = password_editText.substring(86,password_editText.length)
                }else{
                    qlcchainAdress = password_editText.substring(86,150)
                    tokenCount = password_editText.substring(150,160)
                    tokenType = password_editText.substring(160,162)
                    miTxt = password_editText.substring(162,password_editText.length)
                }
                var sourceTxt = LibsodiumUtil.DecryptProtocolMsg(miTxt,random_nonce,ConstantValue.libsodiumprivateMiKey!!,libsodiumpublicTemKey)
                runOnUiThread {
                    closeProgressDialog()
                    if(sourceTxt !="")
                    {
                        showDialog("Original text:",sourceTxt)
                    }else{
                        toast(R.string.Decryption_failed)
                    }
                }
            }catch (e:Exception)
            {
                toast(R.string.Decryption_failed)
                return@setOnClickListener
            }
        }
        registerBtn.setOnClickListener {
            startActivity(Intent(this, EncryptMsgTypeActivity::class.java))
        }
        encryptBtn.setOnClickListener {
            var emaiLAccount = account_editText.text.toString()
            if(emaiLAccount =="")
            {
                toast(R.string.Account_cannot_be_empty)
                return@setOnClickListener
            }
            emaiLAccount = emaiLAccount.trim().toLowerCase()
            var password_editText = password_editText.text.toString()
            if(password_editText =="")
            {
                toast(R.string.Please_enter_content)
                return@setOnClickListener
            }
            if (!NetUtils.isNetworkAvalible(this)) {
                toast(getString(R.string.internet_unavailable))
                return@setOnClickListener
            }
            val qlcClient = QlcClient(ConstantValue.qlcNode)
            val rpc = DpkiRpc(qlcClient)
            Thread(Runnable() {
                run() {
                    val publicBlock = JSONArray()
                    publicBlock.add("email")
                    publicBlock.add(emaiLAccount)
                    runOnUiThread {
                        showProgressDialog(getString(R.string.waiting))
                    }
                    try {
                        var getPubKeyByTypeAndIDResult =  rpc.getPubKeyByTypeAndID(publicBlock)
                        var pubKey = ""
                        if(getPubKeyByTypeAndIDResult != null)
                        {
                            var firstResult = getPubKeyByTypeAndIDResult.get("result")  as JSONArray
                            if(firstResult.size >0)
                            {
                                for (i in 0..(firstResult.size - 1)) {
                                    var obj:JSONObject = firstResult.get(i) as JSONObject
                                    var obj_type = obj.getString("type")
                                    var obj_pubKey = obj.getString("pubKey")
                                    if(obj_type =="email")
                                    {
                                        pubKey = obj_pubKey;
                                        break
                                    }
                                }
                                var dst_public_TemKey_My = ByteArray(32)
                                var dst_private_Temkey_My = ByteArray(32)
                                var crypto_box_keypair_Temresult = Sodium.crypto_box_keypair(dst_public_TemKey_My,dst_private_Temkey_My)
                                var libsodiumprivateTemKeyNew = RxEncodeTool.base64Encode2String(dst_private_Temkey_My)
                                var libsodiumpublicTemKeyNew  =  RxEncodeTool.base64Encode2String(dst_public_TemKey_My)
                                /* if(BuildConfig.DEBUG){
                                     libsodiumprivateTemKeyNew = ConstantValue.libsodiumprivateTemKey!!
                                     libsodiumpublicTemKeyNew = ConstantValue.libsodiumpublicTemKey!!
                                 }*/
                                var friendMiPublic = Helper.hexStringToBytes(pubKey)
                                var msgMap = LibsodiumUtil.EncryptSendMsg(password_editText,friendMiPublic,ConstantValue.libsodiumprivateSignKey!!,libsodiumprivateTemKeyNew,libsodiumpublicTemKeyNew,ConstantValue.libsodiumpublicMiKey!!)
                                var minTxt = msgMap.get("encryptedBase64") as String
                                var NonceBase64 =  msgMap.get("NonceBase64") as String
                                //var msgSouce = LibsodiumUtil.DecryptMyMsg(minTxt, NonceBase64, msgMap.get("dst_shared_key_Mi_My64") as String, ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                                var miStrBegin ="UUxDSUQ="+libsodiumpublicTemKeyNew+"00"+NonceBase64+minTxt
                                runOnUiThread {
                                    closeProgressDialog()
                                    showDialog("Ciphertext:",miStrBegin)
                                }
                            }else{
                                runOnUiThread {
                                    closeProgressDialog()
                                    toast("firstResult size =0")
                                }
                            }

                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                toast("getPubKeyByTypeAndIDResult == null")
                            }

                        }
                    }catch (e:Exception)
                    {
                        runOnUiThread {
                            closeProgressDialog()
                            toast(getString(R.string.Interface_call_failed) +":"+e.message)
                        }
                    }

                }
            }).start()
        }
    }
    fun showDialog(content:String,copyContent:String) {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setContentText(content+copyContent)
                .setConfirmText(getString(R.string.copy))
                .setConfirmClickListener {
                    val cm = AppConfig.instance.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    var mClipData = ClipData.newPlainText(null, copyContent)
                    // 将ClipData内容放到系统剪贴板里。
                    cm.primaryClip = mClipData
                    toast(R.string.copy_success)
                }
                .show()

    }
    override fun setupActivityComponent() {
        DaggerEncryptMsgComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .encryptMsgModule(EncryptMsgModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EncryptMsgContract.EncryptMsgContractPresenter) {
        mPresenter = presenter as EncryptMsgPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}