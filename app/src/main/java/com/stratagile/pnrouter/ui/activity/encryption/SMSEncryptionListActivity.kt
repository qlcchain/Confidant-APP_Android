package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Utils.AESToolsCipher
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.SMSEntity
import com.stratagile.pnrouter.db.SMSEntityDao
import com.stratagile.pnrouter.entity.BakContentReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JBakContentRsp
import com.stratagile.pnrouter.entity.SendSMSData
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionListPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.SMSAdapter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_sms_list.*
import kotlinx.android.synthetic.main.email_search_bar.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/05 14:48:11
 */

class SMSEncryptionListActivity : BaseActivity(), SMSEncryptionListContract.View, PNRouterServiceMessageReceiver.BakContentCallback {
    override fun BakContentBack(JBakContentRsp: JBakContentRsp) {
         for(item in sentSMSLocalDataList)
         {
             item.setUpload(true);
             AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.update(item)
         }
    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionListPresenter
    var smsAdapter : SMSAdapter? = null
    var initSize = 30;
    var pageSize = 10;
    var sentSMSLocalDataList = arrayListOf<SMSEntity>()
    var sentSMSChooseDataList = arrayListOf<SMSEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_sms_list)
    }
    override fun initData() {
        title.text = getString(R.string.msg_local)
        var smsMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().orderDesc(SMSEntityDao.Properties.Date).list()
        var emailMessageEntityList50 = mutableListOf<SMSEntity>()
        if(smsMessageEntityList.size >initSize)
        {
            for (index in 0 until initSize){
                emailMessageEntityList50.add(index,smsMessageEntityList.get(index))
            }
        }else{
            emailMessageEntityList50 = smsMessageEntityList;
        }
        smsAdapter = SMSAdapter(emailMessageEntityList50)
        recyclerView.adapter = smsAdapter
        recyclerView.scrollToPosition(0)
        smsAdapter!!.setOnItemClickListener { adapter, view, position ->
            var emailMeaasgeData =  smsAdapter!!.getItem(position)
            emailMeaasgeData!!.isLastCheck = !emailMeaasgeData!!.isLastCheck;
            smsAdapter!!.notifyItemChanged(position)
            showMenuUI()
            sentSMSChooseDataList = arrayListOf<SMSEntity>()
            smsAdapter!!.data.forEachIndexed { index, it ->
                if(it.lastCheck)
                {
                    sentSMSChooseDataList.add(it)
                }
            }
        }
        if(refreshLayout != null)
        {
            refreshLayout.isEnabled = true
        }

        refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener { refreshLayout ->
            refreshLayout.finishRefresh()
            refreshLayout.finishRefreshWithNoMoreData()
        }
        refreshLayout.finishRefreshWithNoMoreData()
        refreshLayout.setOnLoadMoreListener { refreshLayout ->
            var emailMessageEntityNextList = mutableListOf<SMSEntity>()
            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().orderDesc(SMSEntityDao.Properties.Date).list()
            var uiDataSize = smsAdapter!!.data.size;
            if(uiDataSize < localEmailMessage.size)
            {
                for (index in 0 until pageSize){
                    var flagIndex = uiDataSize +index
                    if(flagIndex >= localEmailMessage.size)
                    {
                        break;
                    }
                    emailMessageEntityNextList.add(index,localEmailMessage.get(flagIndex))
                }
            }


            if(emailMessageEntityNextList.size > 0)
            {
                runOnUiThread {
                    refreshLayout.finishLoadMore()
                    var localEmailMessageNewSize = emailMessageEntityNextList.size
                    for (index in 0 until localEmailMessageNewSize){
                        var beginIndex = smsAdapter!!.data.size;
                        smsAdapter!!.addData(beginIndex,emailMessageEntityNextList.get(index))
                    }
                    if(localEmailMessageNewSize > 0)
                    {
                        smsAdapter!!.notifyDataSetChanged()
                    }
                }
            }else{
                runOnUiThread {
                    refreshLayout.finishLoadMore()
                    toast(R.string.nomore)
                }
            }
        }
        actionButton.setOnClickListener {
            var count = 0;
            var sendSMSDataList = arrayListOf<SendSMSData>()
            sentSMSLocalDataList = arrayListOf<SMSEntity>()
            smsAdapter!!.data.forEachIndexed { index, it ->
                if(it.isLastCheck)
                {
                    count ++;
                    sentSMSLocalDataList.add(it)
                    var fileAESKey = RxEncryptTool.generateAESKey()
                    var pulicSignKey = String(RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileAESKey, ConstantValue.libsodiumpublicMiKey!!)))
                    var sendSMSData = SendSMSData();
                    sendSMSData.id = it.smsId;
                    sendSMSData.tel = it.address
                    if(it.personName == null)
                    {
                        sendSMSData.user = ""
                    }else{
                        sendSMSData.user = it.personName
                    }
                    if(it.person == null)
                    {
                        sendSMSData.uid = 0;
                    }else{
                        sendSMSData.uid = it.person ;
                    }
                    sendSMSData.time = it.date;
                    sendSMSData.read = it.read;
                    if(it.type == 1)
                    {
                        sendSMSData.send = 1;
                    }else{
                        sendSMSData.send = 2;
                    }
                    if(it.subject != null && !it.subject.equals(""))
                    {
                        sendSMSData.title = RxEncodeTool.base64Encode2String(sendSMSData.title.toByteArray())
                    }else{
                        sendSMSData.title =""
                    }
                    if(it.body != null && !it.body.equals(""))
                    {
                        val contentBuffer = it.body .toByteArray()
                        var fileKey16 = fileAESKey.substring(0,16)
                        var bodyMiStr = RxEncodeTool.base64Encode2String(AESToolsCipher.aesEncryptBytes(contentBuffer, fileKey16!!.toByteArray(charset("UTF-8"))))
                        sendSMSData.cont = bodyMiStr
                    }else{
                        sendSMSData.cont = ""
                    }
                    sendSMSData.key = pulicSignKey
                    sendSMSDataList.add(sendSMSData)
                }
            }
            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var sendSMSDataJson = sendSMSDataList.baseDataToJson()
            var bakContentReq = BakContentReq("1",selfUserId!!,count,sendSMSDataJson)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,bakContentReq))


            if(BuildConfig.DEBUG)
            {
                for(item in sentSMSLocalDataList)
                {
                    item.setUpload(true);
                    item.lastCheck = false;
                    AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.update(item)
                }
                sentSMSChooseDataList = arrayListOf<SMSEntity>()
                smsAdapter!!.notifyDataSetChanged()
            }

        }
        initQuerData()
    }
    fun initQuerData() {
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().smsEntityDao.queryBuilder().orderDesc(SMSEntityDao.Properties.Date).list()
                if (localMessageList== null || localMessageList.size > 0)
                {
                    var localMessageListData = arrayListOf<SMSEntity>()
                    for (item in localMessageList)
                    {
                        localMessageListData.add(item);
                    }
                    fiter(s.toString(), localMessageListData)
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
    fun fiter(key: String, emailMessageList: ArrayList<SMSEntity>) {
        var contactListTemp: ArrayList<SMSEntity> = arrayListOf<SMSEntity>()
        for (i in emailMessageList) {
            for(item in sentSMSChooseDataList)
            {
                if(item.uuid == i.uuid)
                {
                    i.lastCheck = item.lastCheck;
                }
            }
            if(i.address == null )
            {
                i.address = ""
            }
            if(i.subject == null )
            {
                i.subject = ""
            }
            if(i.personName == null )
            {
                i.personName = ""
            }
            if(i.body == null )
            {
                i.body = ""
            }
            if (i.address.toLowerCase().contains(key) || i.personName.toLowerCase().contains(key)|| i.subject.toLowerCase().contains(key)|| i.body.toLowerCase().contains(key)) {

                contactListTemp.add(i)
            }
        }
        if(key != "")
        {
            smsAdapter!!.setNewData(contactListTemp);
        }else{
            smsAdapter!!.setNewData(emailMessageList);
        }
    }
    fun showMenuUI()
    {
        var isNeedShow = false;
        smsAdapter!!.data.forEachIndexed { index, it ->
            if(it.isLastCheck)
            {
                isNeedShow = true;
            }
        }

        if(isNeedShow)
        {
            actionButton.visibility = View.VISIBLE
            /*var menuArray = arrayListOf<String>()
            var iconArray = arrayListOf<String>()
            menuArray = arrayListOf<String>(getString(R.string.Node_back_up))
            iconArray = arrayListOf<String>("statusbar_download_node")
            PopWindowUtil.showPopMenuWindow(this@SMSEncryptionListActivity, searchParent,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.icon) {
                        "statusbar_download_node" -> {

                        }


                    }
                }

            })*/
        }else{
            actionButton.visibility = View.GONE
        }

    }
    override fun setupActivityComponent() {
        DaggerSMSEncryptionListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .sMSEncryptionListModule(SMSEncryptionListModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionListContract.SMSEncryptionListContractPresenter) {
        mPresenter = presenter as SMSEncryptionListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}