package com.stratagile.pnrouter.ui.activity.encryption

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.EaseSmileUtils
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.AESCipher
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.SMSEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionNodelSecondComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelSecondContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelSecondModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelSecondPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.SMSNodeSecondAdapter
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_node_sms_list.*
import kotlinx.android.synthetic.main.activity_node_sms_list.actionButton
import kotlinx.android.synthetic.main.activity_node_sms_list.backBtn
import kotlinx.android.synthetic.main.activity_node_sms_list.cancelBtn
import kotlinx.android.synthetic.main.activity_node_sms_list.editBtn
import kotlinx.android.synthetic.main.activity_node_sms_list.recyclerView
import kotlinx.android.synthetic.main.activity_node_sms_list.refreshLayout
import kotlinx.android.synthetic.main.activity_node_sms_list.tvTitle
import kotlinx.android.synthetic.main.activity_sms_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/07 23:33:10
 */

class SMSEncryptionNodelSecondActivity : BaseActivity(), SMSEncryptionNodelSecondContract.View, PNRouterServiceMessageReceiver.PullBakContentCallback  {
    override fun pullBakContentBack(jPullBakContentRsp: JPullBakContentRsp) {
        runOnUiThread {
            refreshLayout.finishLoadMore()
        }
        if(jPullBakContentRsp.params.retCode ==0)
        {
            var dataList = jPullBakContentRsp.params.payload
            if(dataList.size != 0)
            {
                for(item in dataList)
                {
                    if(cancelBtn.visibility == View.VISIBLE)
                    {
                        item.isMultChecked = true;
                    }else{
                        item.isMultChecked = false;
                    }

                }
                lastPayload = jPullBakContentRsp.params.payload.last()
                runOnUiThread {
                    SMSNodeSecondAdapter!!.addData(dataList)
                    SMSNodeSecondAdapter!!.notifyDataSetChanged();
                }

            }else{
                runOnUiThread {
                    toast(R.string.nomore)
                }
            }
        }else{
            runOnUiThread {
                toast(R.string.fail)
            }
        }
    }

    override fun delBakContentBack(jDelBakContentRsp: JDelBakContentRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        if(jDelBakContentRsp.params.retCode == 0)
        {

            runOnUiThread {
                var offIndex = 0;
                for(position in delSMSLocalPositionList)
                {
                    SMSNodeSecondAdapter!!.remove(position - offIndex)
                    SMSNodeSecondAdapter!!.notifyItemChanged(position - offIndex)
                    offIndex ++;
                }
                for(item in delSMSLocalDataList)
                {
                    var sourceTel = String(RxEncodeTool.base64Decode(item!!.tel))
                    var list = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().where(SMSEntityDao.Properties.UUID.eq(sourceTel+item.time)).list()
                    if(list != null && list!!.size >0 )
                    {
                        var tempItem = list.get(0)
                        tempItem.setIsUpload(false)
                        AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.update(tempItem)
                    }
                }
                actionButton.visibility = View.GONE
                toast(R.string.success)
                delSMSLocalPositionList = arrayListOf<Int>()
            }

        }else{
            runOnUiThread {
                toast(R.string.fail)
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelSecondPresenter
    var SMSNodeSecondAdapter : SMSNodeSecondAdapter? = null
    var sentSMSChooseDataList = arrayListOf<SendSMSData>()
    var sendSMSData: SendSMSData? = null
    var nodeStartId = 0;
    var lastPayload : SendSMSData? = null
    var delSMSLocalDataList = arrayListOf<SendSMSData>()
    var delSMSLocalPositionList = arrayListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_node_sms_list)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.pullSecondBakContentCallback = this
        sendSMSData = intent.getParcelableExtra("smsMeaasgeData")
        if(sendSMSData!!.user != "")
        {
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.user))
            if(userSouce !="")
            {
                tvTitle.setText(userSouce)
            }else{
                tvTitle.setText(R.string.SMS)
            }

        }else{
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.tel))
            if(userSouce !="")
            {
                tvTitle.setText(userSouce)
            }else{
                tvTitle.setText(R.string.SMS)
            }
        }
        var emailMessageEntityList50 = mutableListOf<SendSMSData>()
        SMSNodeSecondAdapter = SMSNodeSecondAdapter(emailMessageEntityList50)
        recyclerView.adapter = SMSNodeSecondAdapter
        recyclerView.scrollToPosition(0)
        SMSNodeSecondAdapter!!.setOnItemClickListener { adapter, view, position ->
            if(cancelBtn.visibility == View.VISIBLE)
            {
                var emailMeaasgeData =  SMSNodeSecondAdapter!!.getItem(position)
                emailMeaasgeData!!.isLastCheck = !emailMeaasgeData!!.isLastCheck;
                SMSNodeSecondAdapter!!.notifyItemChanged(position)
                showMenuUI()
                sentSMSChooseDataList = arrayListOf<SendSMSData>()
                SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
                    if(it.isLastCheck)
                    {
                        sentSMSChooseDataList.add(it)
                    }
                }
            }

        }
        SMSNodeSecondAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            var emailMeaasgeData =  SMSNodeSecondAdapter!!.getItem(position)
            val cm = AppConfig.instance.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            if(emailMeaasgeData!!.key !="")
            {
                var aesKey = LibsodiumUtil.DecryptShareKey(emailMeaasgeData!!.key,ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                var souceContData = AESCipher.aesDecryptString(emailMeaasgeData!!.cont, aesKey)
                var mClipData = ClipData.newPlainText(null, souceContData)
                // 将ClipData内容放到系统剪贴板里。
                cm.primaryClip = mClipData
            }else{
                var mClipData = ClipData.newPlainText(null, emailMeaasgeData!!.cont)
                // 将ClipData内容放到系统剪贴板里。
                cm.primaryClip = mClipData
            }

            toast(R.string.copy_success)
            true
        }
        getNodeData(20,0)


        if(refreshLayout != null)
        {
            refreshLayout.isEnabled = true
        }
        refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
        refreshLayout.setEnableRefresh(false);//是否启用上拉加载功能
        refreshLayout.setOnLoadMoreListener { refreshLayout ->
            if(lastPayload == null)
            {
                nodeStartId = 0;
            }else{
                nodeStartId = lastPayload!!.index
            }
            getNodeData(20,nodeStartId)
        }
        actionButton.setOnClickListener {
            var deletIndex = "";
            var count = 0;
            delSMSLocalPositionList = arrayListOf<Int>()
            delSMSLocalDataList = arrayListOf<SendSMSData>()
            SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
                if(it.isLastCheck)
                {
                    deletIndex += it.index.toString()+","
                    count ++;
                    delSMSLocalPositionList.add(index)
                    delSMSLocalDataList.add(it);
                }
            }
            showProgressDialog(getString(R.string.waiting))
            deletIndex = deletIndex.substring(0,deletIndex.length -1)
            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var bakContentReq = DelBakContentReq("1",selfUserId!!,count,deletIndex)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,bakContentReq))

        }

        editBtn.setOnClickListener {
            editBtn.visibility = View.GONE
            cancelBtn.visibility = View.VISIBLE
            SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
                it.isMultChecked = true
            }
            SMSNodeSecondAdapter!!.notifyDataSetChanged()
        }
        backBtn.setOnClickListener {
            AppConfig.instance.messageReceiver?.pullSecondBakContentCallback = null
            var intent =  Intent(this, SMSEncryptionNodelListActivity::class.java)
            startActivity(intent);
            finish()
        }
        cancelBtn.setOnClickListener {
            editBtn.visibility = View.VISIBLE
            cancelBtn.visibility = View.GONE
            actionButton.visibility = View.GONE
            SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
                it.isLastCheck = false
                it.isMultChecked = false
            }
            SMSNodeSecondAdapter!!.notifyDataSetChanged()
        }
    }
    fun showMenuUI()
    {
        var isNeedShow = false;
        SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
            if(it.isLastCheck)
            {
                isNeedShow = true;
            }
        }

        if(isNeedShow)
        {
            actionButton.visibility = View.VISIBLE
        }else{
            actionButton.visibility = View.GONE
        }

    }
    fun getNodeData(count:Int,startId:Int)
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var pullBakContent = PullBakContentReq(selfUserId!! ,2,sendSMSData!!.tel, count,startId)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,pullBakContent))
    }
    override fun setupActivityComponent() {
        DaggerSMSEncryptionNodelSecondComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .sMSEncryptionNodelSecondModule(SMSEncryptionNodelSecondModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionNodelSecondContract.SMSEncryptionNodelSecondContractPresenter) {
        mPresenter = presenter as SMSEncryptionNodelSecondPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppConfig.instance.messageReceiver?.pullSecondBakContentCallback = null
            var intent =  Intent(this, SMSEncryptionNodelListActivity::class.java)
            startActivity(intent);
            finish()
        }
        return true
    }
    override fun onDestroy() {

        AppConfig.instance.messageReceiver?.pullSecondBakContentCallback = null
        super.onDestroy()
    }
}