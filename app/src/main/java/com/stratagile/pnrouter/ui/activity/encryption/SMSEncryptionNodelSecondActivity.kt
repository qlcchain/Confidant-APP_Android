package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionNodelSecondComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelSecondContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelSecondModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelSecondPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.SMSNodeSecondAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_node_sms_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/07 23:33:10
 */

class SMSEncryptionNodelSecondActivity : BaseActivity(), SMSEncryptionNodelSecondContract.View, PNRouterServiceMessageReceiver.PullBakContentCallback  {
    override fun pullBakContentBack(jPullBakContentRsp: JPullBakContentRsp) {

    }

    override fun delBakContentBack(jDelBakContentRsp: JDelBakContentRsp) {

    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelSecondPresenter
    var SMSNodeSecondAdapter : SMSNodeSecondAdapter? = null
    var sentSMSChooseDataList = arrayListOf<SendSMSData>()
    var sendSMSData: SendSMSData? = null
    var nodeStartId = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_node_sms_list)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.pullBakContentCallback = this
        sendSMSData = intent.getParcelableExtra("smsMeaasgeData")
        if(sendSMSData!!.user != "")
        {
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.user))
            if(userSouce !="")
            {
                title.setText(userSouce)
            }else{
                title.setText(R.string.SMS)
            }

        }else{
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.tel))
            if(userSouce !="")
            {
                title.setText(userSouce)
            }else{
                title.setText(R.string.SMS)
            }
        }
        var emailMessageEntityList50 = mutableListOf<SendSMSData>()
        var SendSMSDataTemp = SendSMSData()
        SendSMSDataTemp.num =10;
        SendSMSDataTemp.time = 1581137791386L
        SendSMSDataTemp.cont = "SmartRefreshLayout是一个“聪明”或者“智能”的下拉刷新布局。由于它的“智能”，它不只是支持所有的View，还支持多层嵌套的视图结构。它继承自ViewGroup 而不是FrameLayout或LinearLayout，提高了性能。 它也吸取了现在流行的各种刷新布局的优点\n" + "————————————————\n" + "版权声明：本文为CSDN博主「K_Hello」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\n" + "原文链接：https://blog.csdn.net/K_Hello/article/details/90512714"
        SendSMSDataTemp.user = "聪明"
        SendSMSDataTemp.send = 1;
        SendSMSDataTemp.tel = "19000000000"
        SendSMSDataTemp.key =""
        emailMessageEntityList50.add(SendSMSDataTemp)
        SendSMSDataTemp = SendSMSData()
        SendSMSDataTemp.num =10;
        SendSMSDataTemp.time = 1581137791386L
        SendSMSDataTemp.cont = "SmartRefreshLayout是一个“聪明”或者“智能”的下拉刷新布局。由于它的“智能”，它不只是支持所有的View，还支持多层嵌套的视图结构。它继承自ViewGroup 而不是FrameLayout或LinearLayout，提高了性能。 它也吸取了现在流行的各种刷新布局的优点\n" + "————————————————\n" + "版权声明：本文为CSDN博主「K_Hello」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\n" + "原文链接：https://blog.csdn.net/K_Hello/article/details/90512714"
        SendSMSDataTemp.user = "聪明"
        SendSMSDataTemp.send = 1;
        SendSMSDataTemp.tel = "19000000000"
        SendSMSDataTemp.key =""
        SendSMSDataTemp.send = 1;
        emailMessageEntityList50.add(SendSMSDataTemp)
        SendSMSDataTemp = SendSMSData()
        SendSMSDataTemp.num =10;
        SendSMSDataTemp.time = 1581137791386L
        SendSMSDataTemp.cont = "SmartRefreshLayout是一个“聪明”或者“智能”的下拉刷新布局。由于它的“智能”，它不只是支持所有的View，还支持多层嵌套的视图结构。它继承自ViewGroup 而不是FrameLayout或LinearLayout，提高了性能。 它也吸取了现在流行的各种刷新布局的优点\n" + "————————————————\n" + "版权声明：本文为CSDN博主「K_Hello」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\n" + "原文链接：https://blog.csdn.net/K_Hello/article/details/90512714"
        SendSMSDataTemp.user = "聪明"
        SendSMSDataTemp.send = 1;
        SendSMSDataTemp.tel = "19000000000"
        SendSMSDataTemp.key =""
        SendSMSDataTemp.send = 2;
        emailMessageEntityList50.add(SendSMSDataTemp)
        SMSNodeSecondAdapter = SMSNodeSecondAdapter(emailMessageEntityList50)
        recyclerView.adapter = SMSNodeSecondAdapter
        recyclerView.scrollToPosition(0)
        SMSNodeSecondAdapter!!.setOnItemClickListener { adapter, view, position ->
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

        getNodeData(20,0)


        if(refreshLayout != null)
        {
            refreshLayout.isEnabled = true
        }
        refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
        refreshLayout.setEnableRefresh(false);//是否启用上拉加载功能
        refreshLayout.setOnLoadMoreListener { refreshLayout ->
            getNodeData(20,nodeStartId)
            refreshLayout.finishLoadMore()
        }
        actionButton.setOnClickListener {
            var deletIndex = "";
            var count = 0;
            SMSNodeSecondAdapter!!.data.forEachIndexed { index, it ->
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

}