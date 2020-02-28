package com.stratagile.pnrouter.ui.activity.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.QLCAccountDao
import com.stratagile.pnrouter.entity.PublishBlock
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEncryptMsgTypeComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgTypeContract
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgTypeModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgTypePresenter
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.NetUtils
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.encrypt_reg_activity.*
import org.libsodium.jni.Sodium
import qlc.network.QlcClient
import qlc.rpc.impl.DpkiRpc
import qlc.utils.Helper
import java.text.SimpleDateFormat
import java.util.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2020/02/19 16:13:36
 */

class EncryptMsgTypeActivity : BaseActivity(), EncryptMsgTypeContract.View {

    @Inject
    internal lateinit var mPresenter: EncryptMsgTypePresenter
    var verifiers= arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encrypt_reg_activity)
    }
    override fun initData() {
        title.text = "Encrypt And Decrypt"
        rechargeBtn.setOnClickListener {
            var qlcAccountEntityList = AppConfig.instance.mDaoMaster!!.newSession().qlcAccountDao.queryBuilder().where(QLCAccountDao.Properties.IsCurrent.eq(true)).list()
            if(qlcAccountEntityList != null && qlcAccountEntityList.size > 0) {
                var qlcAccount = qlcAccountEntityList.get(0)
                showDialog("Seed:",qlcAccount.seed +"      "+"Address:" +qlcAccount.address)
            }
        }
        VerifymailboxBtn.setOnClickListener {
            var code = "SSAJ6aTejQ2Cb0AJ"
            val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            val currentTime = mDateFormat.format(Date())
            var singStr =  LibsodiumUtil.cryptoSign(code +currentTime,ConstantValue.libsodiumprivateSignKey!!)
            var orignTxt =ConstantValue.libsodiumpublicSignKey +code+"79b1b1ed5fdcd7e9e8e962859d703140c80a893d4e3d5a456e2c777fa65444e8"+currentTime+singStr
            var dst_public_TemKey_My = ByteArray(32)
            var dst_private_Temkey_My = ByteArray(32)
            var crypto_box_keypair_Temresult = Sodium.crypto_box_keypair(dst_public_TemKey_My,dst_private_Temkey_My)
            var libsodiumprivateTemKeyNew = RxEncodeTool.base64Encode2String(dst_private_Temkey_My)
            var libsodiumpublicTemKeyNew  =  RxEncodeTool.base64Encode2String(dst_public_TemKey_My)
            var friendMiPublic = Helper.hexStringToBytes("0ae6c2ade291b398c3dc4b4c0164bf72813d6150b25da69371bb3008e4942211")
            var msgMap = LibsodiumUtil.EncryptSendMsg(orignTxt,friendMiPublic,ConstantValue.libsodiumprivateSignKey!!,libsodiumprivateTemKeyNew,libsodiumpublicTemKeyNew,ConstantValue.libsodiumpublicMiKey!!)
            var minTxt = msgMap.get("encryptedBase64") as String
            var NonceBase64 =  msgMap.get("NonceBase64") as String
            //var msgSouce = LibsodiumUtil.DecryptMyMsg(minTxt, NonceBase64, msgMap.get("dst_shared_key_Mi_My64") as String, ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
            var miStrBegin ="UUxDSUQ="+libsodiumpublicTemKeyNew+"04"+NonceBase64+minTxt

            var email = arrayOf("19464572@qq.com"); // 需要注意，email必须以数组形式传入
            var intent = Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // 设置邮件格式
            intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
            //intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
            intent.putExtra(Intent.EXTRA_SUBJECT, "dpki_verify"); // 主题
            intent.putExtra(Intent.EXTRA_TEXT, miStrBegin); // 正文
            startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
        }
        copyBtn.setOnClickListener {
            var code = ConstantValue.oracleEmailCode
            val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            val currentTime = mDateFormat.format(Date())
            var singStr =  LibsodiumUtil.cryptoSign(code +currentTime,ConstantValue.libsodiumprivateSignKey!!)
            var orignTxt =ConstantValue.libsodiumpublicSignKey +code+ConstantValue.oracleEmailhash+currentTime+singStr
            var dst_public_TemKey_My = ByteArray(32)
            var dst_private_Temkey_My = ByteArray(32)
            var crypto_box_keypair_Temresult = Sodium.crypto_box_keypair(dst_public_TemKey_My,dst_private_Temkey_My)
            var libsodiumprivateTemKeyNew = RxEncodeTool.base64Encode2String(dst_private_Temkey_My)
            var libsodiumpublicTemKeyNew  =  RxEncodeTool.base64Encode2String(dst_public_TemKey_My)
            var friendMiPublic = Helper.hexStringToBytes(ConstantValue.oracleEmailPubKey)
            var msgMap = LibsodiumUtil.EncryptSendMsg(orignTxt,friendMiPublic,ConstantValue.libsodiumprivateSignKey!!,libsodiumprivateTemKeyNew,libsodiumpublicTemKeyNew,ConstantValue.libsodiumpublicMiKey!!)
            var minTxt = msgMap.get("encryptedBase64") as String
            var NonceBase64 =  msgMap.get("NonceBase64") as String
            //var msgSouce = LibsodiumUtil.DecryptMyMsg(minTxt, NonceBase64, msgMap.get("dst_shared_key_Mi_My64") as String, ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
            var miStrBegin ="UUxDSUQ="+libsodiumpublicTemKeyNew+"04"+NonceBase64+minTxt
            var emailAdress ="Addressee:"+ConstantValue.oracleEmailAdress+"      subject:dpki_verify"
            runOnUiThread {
                showDialog("Email:",emailAdress+ "       body:"+miStrBegin)
            }
        }
        regeditBtn.setOnClickListener {
            var emaiLAccount = account_editText.text.toString()
            if(emaiLAccount =="")
            {
                toast(R.string.Account_cannot_be_empty)
                return@setOnClickListener
            }
            emaiLAccount = emaiLAccount.trim().toLowerCase()
            if (!NetUtils.isNetworkAvalible(this)) {
                toast(getString(R.string.internet_unavailable))
                return@setOnClickListener
            }
            val qlcClient = QlcClient(ConstantValue.qlcNode)
            val rpc = DpkiRpc(qlcClient)
            verifiers= arrayListOf<String>()
            verifiers.add(0,ConstantValue.oracleEmailQlcAdress)
            Thread(Runnable() {
                run() {
                    runOnUiThread {
                        showProgressDialog(getString(R.string.waiting))
                    }
                    try {
                        var getAllVerifiersResult =  rpc.getAllVerifiers(null)
                        if(getAllVerifiersResult != null)
                        {
                            var firstResult = getAllVerifiersResult.get("result") as JSONArray
                            for (i in 0..(firstResult.size - 1)){
                                if(i>3)
                                {
                                    break;
                                }
                                var obj: JSONObject = firstResult.get(i) as JSONObject
                                var obj_account = obj.getString("account")
                                var obj_type = obj.getString("type")
                                var obj_id = obj.getString("id")
                                verifiers.add(obj_account)
                            }
                            var qlcAccountEntityList = AppConfig.instance.mDaoMaster!!.newSession().qlcAccountDao.queryBuilder().where(QLCAccountDao.Properties.IsCurrent.eq(true)).list()
                            if(qlcAccountEntityList != null && qlcAccountEntityList.size > 0)
                            {
                                var qlcAccount = qlcAccountEntityList.get(0)
                                val publicBlock = JSONArray()
                                var stateBlock = PublishBlock()
                                stateBlock.account = qlcAccount.address  //"id" -> "test2.dpk@qlc.com"
                                stateBlock.type ="email"
                                stateBlock.id = emaiLAccount
                                stateBlock.fee = "500000000"
                                var pubkey = Helper.byteToHexString(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicMiKey)).toLowerCase();
                                pubkey = pubkey.substring(0,64)
                                stateBlock.pubkey = pubkey;
                                stateBlock.verifiers = verifiers;
                                publicBlock.add(JSONObject.parseObject(Gson().toJson(stateBlock)))
                                val publicBlock2 = JSONArray()
                                publicBlock2.add(publicBlock)
                                publicBlock2.add(qlcAccount.privKey)
                                try {
                                    var getPublishBlockResult =  rpc.getPublishBlockAndProcess(publicBlock2)
                                    if(getPublishBlockResult == null)
                                    {
                                        runOnUiThread {
                                            closeProgressDialog()
                                            toast(R.string.fail)
                                        }
                                        return@Runnable
                                    }
                                }catch (e:Exception)
                                {
                                    e.printStackTrace()
                                    runOnUiThread {
                                        closeProgressDialog()
                                        toast(getString(R.string.fail)+":"+e.message)
                                    }
                                    return@Runnable
                                }

                                //var getPublishBlockResult =  rpc.getPublishBlock(publicBlock)
                                /* var firstResult = getPublishBlockResult.get("result")  as JSONObject
                                 var verifiers = firstResult.get("verifiers")
                                 var block = firstResult.get("block")*/

                                runOnUiThread {
                                    closeProgressDialog()
                                    toast(R.string.success)

                                }
                                /* val verifiersBlock = JSONArray()
                                 verifiersBlock.add("email")
                                 verifiersBlock.add(emaiLAccount)
                                 var getPubKeyByTypeAndIDResult =  rpc.getPubKeyByTypeAndID(verifiersBlock)


                                 val verifiersBlock22 = JSONArray()
                                 verifiersBlock22.add(qlcAccount.address)
                                 verifiersBlock22.add("email")
                                 var getPubKeyByTypeAndIDResult2 =  rpc.getPublishInfosByAccountAndType(verifiersBlock22)
                                 var bb = ""*/
                            }else{
                                runOnUiThread {
                                    closeProgressDialog()
                                    toast("qlcAccountEntityList.size ==0")

                                }
                            }

                        }else{
                            runOnUiThread {
                                closeProgressDialog()
                                toast("getAllVerifiersResult ==null")

                            }
                        }
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
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
        DaggerEncryptMsgTypeComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .encryptMsgTypeModule(EncryptMsgTypeModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EncryptMsgTypeContract.EncryptMsgTypeContractPresenter) {
        mPresenter = presenter as EncryptMsgTypePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}