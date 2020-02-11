package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.AESToolsCipher
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionNodelListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelListPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.SMSNodeAdapter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_node_sms_list.*
import kotlinx.android.synthetic.main.email_search_bar.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/05 14:49:08
 */

class SMSEncryptionNodelListActivity : BaseActivity(), SMSEncryptionNodelListContract.View, PNRouterServiceMessageReceiver.PullBakContentCallback {
    override fun pullBakContentBack(jPullBakContentRsp: JPullBakContentRsp) {
        runOnUiThread {
            //closeProgressDialog()
            refreshLayout.finishLoadMore()
        }
        if(jPullBakContentRsp.params.retCode ==0)
        {
            var dataList = jPullBakContentRsp.params.payload
            if(dataList.size != 0)
            {
                lastPayload = jPullBakContentRsp.params.payload.last()
                runOnUiThread {
                    SMSNodeAdapter!!.addData(dataList)
                    SMSNodeAdapter!!.notifyDataSetChanged();
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

    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelListPresenter
    var SMSNodeAdapter : SMSNodeAdapter? = null
    var nodeStartId = 0;
    var lastPayload : SendSMSData? = null
    var sentSMSLocalDataList = arrayListOf<SendSMSData>()
    var sentSMSChooseDataList = arrayListOf<SendSMSData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_node_sms_list)
    }
    override fun initData() {
        title.text = getString(R.string.nodemsg_local)
        AppConfig.instance.messageReceiver?.pullBakContentCallback = this
        var emailMessageEntityList50 = mutableListOf<SendSMSData>()
        SMSNodeAdapter = SMSNodeAdapter(emailMessageEntityList50)
        recyclerView.adapter = SMSNodeAdapter
        recyclerView.scrollToPosition(0)
        SMSNodeAdapter!!.setOnItemClickListener { adapter, view, position ->
            var smsMeaasgeData =  SMSNodeAdapter!!.getItem(position) as SendSMSData
            var intent =  Intent(this, SMSEncryptionNodelSecondActivity::class.java)
            intent.putExtra("smsMeaasgeData",smsMeaasgeData)
            startActivity(intent);
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
            SMSNodeAdapter!!.data.forEachIndexed { index, it ->
                if(it.isLastCheck)
                {
                    deletIndex += it.index.toString()+","
                    count ++;
                }
            }
            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var bakContentReq = DelBakContentReq("1",selfUserId!!,count,deletIndex)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,bakContentReq))

        }
        //initQuerData()
    }
    fun initQuerData() {
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {



            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
    fun fiter(key: String, emailMessageList: ArrayList<SendSMSData>) {

    }
    fun showMenuUI()
    {
        var isNeedShow = false;
        SMSNodeAdapter!!.data.forEachIndexed { index, it ->
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
        //showProgressDialog(getString(R.string.waiting))
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var pullBakContent = PullBakContentReq(selfUserId!! ,1,"", count,startId)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,pullBakContent))
    }
    override fun setupActivityComponent() {
        DaggerSMSEncryptionNodelListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .sMSEncryptionNodelListModule(SMSEncryptionNodelListModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionNodelListContract.SMSEncryptionNodelListContractPresenter) {
        mPresenter = presenter as SMSEncryptionNodelListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {

        AppConfig.instance.messageReceiver?.pullBakContentCallback = null
        super.onDestroy()
    }
}