package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.DelBakContentReq
import com.stratagile.pnrouter.entity.PullBakContentReq
import com.stratagile.pnrouter.entity.SendSMSData
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

class SMSEncryptionNodelSecondActivity : BaseActivity(), SMSEncryptionNodelSecondContract.View {

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelSecondPresenter
    var SMSNodeSecondAdapter : SMSNodeSecondAdapter? = null
    var sendSMSData: SendSMSData? = null
    var nodeStartId = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_node_sms_list)
    }
    override fun initData() {
        sendSMSData = intent.getParcelableExtra("smsMeaasgeData")
        if(sendSMSData!!.user != "")
        {
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.user))
            title.setText(userSouce)
        }else{
            var userSouce = String(RxEncodeTool.base64Decode(sendSMSData!!.tel))
            title.setText(userSouce)
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
        emailMessageEntityList50.add(SendSMSDataTemp)
        SendSMSDataTemp.send = 2;
        emailMessageEntityList50.add(SendSMSDataTemp)
        SMSNodeSecondAdapter = SMSNodeSecondAdapter(emailMessageEntityList50)
        recyclerView.adapter = SMSNodeSecondAdapter
        recyclerView.scrollToPosition(0)
        SMSNodeSecondAdapter!!.setOnItemClickListener { adapter, view, position ->

        }

        getNodeData(20,0)


        if(refreshLayout != null)
        {
            refreshLayout.isEnabled = true
        }
        refreshLayout.setOnRefreshListener {

        }
        refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
        refreshLayout.setEnableLoadMore(false);//是否启用上拉加载功能
        refreshLayout.setOnLoadMoreListener { refreshLayout ->
            getNodeData(20,nodeStartId)
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
    fun getNodeData(count:Int,startId:Int)
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var pullBakContent = PullBakContentReq(selfUserId!! ,1,"", count,startId)
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