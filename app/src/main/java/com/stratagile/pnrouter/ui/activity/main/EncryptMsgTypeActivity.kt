package com.stratagile.pnrouter.ui.activity.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.stratagile.pnrouter.utils.NetUtils
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.encrypt_reg_activity.*
import qlc.network.QlcClient
import qlc.rpc.impl.DpkiRpc
import qlc.utils.Helper

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
                                if(i>4)
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