package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetCountCallback
import com.smailnet.eamil.Callback.GetReceiveCallback
import com.smailnet.eamil.Callback.MarkCallback
import com.smailnet.eamil.EmailCount
import com.smailnet.eamil.EmailMessage
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.smailnet.eamil.Utils.AESCipher
import com.smailnet.islands.Islands
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.ui.activity.email.EmailInfoActivity
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEmailMessageComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EmailMessageContract
import com.stratagile.pnrouter.ui.activity.main.module.EmailMessageModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EmailMessagePresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiMessageAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.email_search_bar.*
import kotlinx.android.synthetic.main.fragment_mail_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/07/11 16:19:12
 */

class EmailMessageFragment : BaseFragment(), EmailMessageContract.View , PNRouterServiceMessageReceiver.PullMailListCallback{
    override fun PullMailListBack(JPullMailListRsp: JPullMailListRsp) {

        runOnUiThread {
            closeProgressDialog()

            if(nodeUpandDown == "up")
            {
                refreshLayout.finishRefresh()
                refreshLayout.resetNoMoreData()
            }else if(nodeUpandDown == "down")
            {
                refreshLayout.finishLoadMore()
            }

        }
        if(JPullMailListRsp.params.retCode == 0)
        {
            var emailMessageEntityList = mutableListOf<EmailMessageEntity>()
            var dataList = JPullMailListRsp.params.payload
            if(dataList.size != 0)
            {
                lastPayload = JPullMailListRsp.params.payload.last()
            }
            for (item in dataList)
            {
                var userKey = item.userkey
                var aesKey = LibsodiumUtil.DecryptShareKey(userKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!);
                var mailInfoStr = item.mailInfo
                var miContentSoucreBase = RxEncodeTool.base64Decode(mailInfoStr)
                val miContent = AESCipher.aesDecryptBytes(miContentSoucreBase, aesKey.toByteArray())
                var sourceContent = ""
                try {
                    sourceContent = String(miContent)
                    var gson = GsonUtil.getIntGson()
                    var mainInfo =  gson.fromJson(sourceContent, EmailInfo::class.java)
                    var toUserJosnStr = mainInfo.toUserJosn
                    var toStr = ""
                    if(toUserJosnStr != null)
                    {
                        var toUserJosn = gson.fromJson<ArrayList<EmailContact>>(toUserJosnStr, object : TypeToken<ArrayList<EmailContact>>() {

                        }.type)
                        //283619512 <283619512@qq.com>,emaildev <emaildev@qlink.mobi>

                        for (user in toUserJosn)
                        {
                            toStr += user.userName +" "+user.userAddress +","
                        }
                        if(toStr!= "")
                        {
                            toStr = toStr.substring(0,toStr.length -1)
                        }
                    }

                    var ccUserJosnStr = mainInfo.ccUserJosn
                    var ccStr = ""
                    if(ccUserJosnStr != null) {

                        var ccUserJosn = gson.fromJson<ArrayList<EmailContact>>(ccUserJosnStr, object : TypeToken<ArrayList<EmailContact>>() {

                        }.type)
                        //283619512 <283619512@qq.com>,emaildev <emaildev@qlink.mobi>

                        for (user in ccUserJosn)
                        {
                            ccStr += user.userName +" "+user.userAddress +","
                        }
                        if(ccStr!= "")
                        {
                            ccStr = ccStr.substring(0,ccStr.length -1)
                        }
                    }

                    var bccStr = ""
                    var bccUserJosnStr = mainInfo.bccUserJosn
                    if(bccUserJosnStr != null)
                    {
                        var bccUserJosn = gson.fromJson<ArrayList<EmailContact>>(bccUserJosnStr, object : TypeToken<ArrayList<EmailContact>>() {

                        }.type)
                        //283619512 <283619512@qq.com>,emaildev <emaildev@qlink.mobi>

                        for (user in bccUserJosn)
                        {
                            bccStr += user.userName +" "+user.userAddress +","
                        }
                        if(bccStr!= "")
                        {
                            bccStr = bccStr.substring(0,bccStr.length -1)
                        }
                    }

                    var eamilMessage = EmailMessageEntity()
                    eamilMessage.account = AppConfig.instance.emailConfig().account
                    eamilMessage.msgId = item.id.toString()
                    eamilMessage.menu = ConstantValue.chooseEmailMenuName
                    eamilMessage.from = mainInfo.fromName +" "+mainInfo.fromEmailBox
                    eamilMessage.to = toStr
                    eamilMessage.cc = ccStr
                    eamilMessage.bcc = bccStr
                    eamilMessage.setIsContainerAttachment(if(mainInfo.attchCount>0){true}else{false})
                    eamilMessage.setIsSeen(true)
                    eamilMessage.setIsStar(false)
                    eamilMessage.setIsReplySign(false)
                    eamilMessage.setAttachmentCount(mainInfo.attchCount)
                    eamilMessage.subject = mainInfo.subTitle
                    eamilMessage.content= ""
                    eamilMessage.contentText= mainInfo.content
                    eamilMessage.originalText = ""
                    eamilMessage.aesKey  = aesKey
                    eamilMessage.emailAttachPath = item.emailPath
                    eamilMessage.date = DateUtil.getDateToString((mainInfo.revDate *1000).toLong(),"yyyy-MM-dd HH:mm:ss");
                    emailMessageEntityList.add(eamilMessage)
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
            runOnUiThread {
                emaiMessageChooseAdapter!!.addData(emailMessageEntityList);
                emaiMessageChooseAdapter!!.setNewData(emaiMessageChooseAdapter!!.data)
            }
        }else{

        }
    }


    @Inject
    lateinit internal var mPresenter: EmailMessagePresenter
    var emaiMessageChooseAdapter : EmaiMessageAdapter? = null
    var name = "name"
    var menu = "INBOX"
    var isChangeMenu = false
    var from = ""
    var nodeStartId = 0;
    var nodeUpandDown = "up";
    var lastPayload : JPullMailListRsp.ParamsBean.PayloadBean? = null
    var emailConfigEntityChooseList= mutableListOf<EmailConfigEntity>()
    var emailConfigEntityChoose:EmailConfigEntity? = null
    var deleteEmailMeaasgeData:EmailMessageEntity? = null
    var positionDeleteIndex = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changEmailMenu(changEmailMenu: ChangEmailMenu) {
        name = changEmailMenu.name
        menu = changEmailMenu.menu
        ConstantValue.chooseEmailMenuServer = menu
        if(menu == "star")
        {
            if(refreshLayout != null)
            {
                refreshLayout.isEnabled = false
            }

        }else {
            if(refreshLayout != null)
            {
                refreshLayout.isEnabled = true
            }
        }
        /*var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
        if(localMessageList == null || localMessageList.size ==0)
        {
            showProgressDialog()
            pullMoreMessageList(0)
        }else{
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localMessageList);
            }
        }
        recyclerView.scrollToPosition(0)*/
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_mail_list, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppConfig.instance.messageReceiver!!.pullMailListCallback = this
        emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChooseList.size > 0)
        {
            emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
        }
        from = arguments!!.getString("from","")
        var account = AppConfig.instance.emailConfig().account
        var emailMessageEntityList = mutableListOf<EmailMessageEntity>()
        if(account != null)
        {
            emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
        }
        emaiMessageChooseAdapter = EmaiMessageAdapter(emailMessageEntityList)
        emaiMessageChooseAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            if(name == "Drafts")
            {
                deleteEmailMeaasgeData =  emaiMessageChooseAdapter!!.getItem(position)
                positionDeleteIndex = position
                val commonDialog = CommonDialog(activity)
                val view1 = activity!!.layoutInflater.inflate(R.layout.dialog_conversation_layout, null, false)
                commonDialog.setView(view1)
                commonDialog.show()
                val tvDelete = view1.findViewById<TextView>(R.id.tvDelete)
                tvDelete.setOnClickListener {
                    showProgressDialog(AppConfig.instance.resources.getString(R.string.waiting))
                    deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2)
                    commonDialog.cancel()
                }
            }

            /*   val floatMenu = FloatMenu(activity)
              floatMenu.inflate(R.menu.popup_menu_voice)
               var left = view.left
               var top = view.top
               var point = Point(left,top)
               floatMenu.show(point,0,0)*/
            true
        }
        recyclerView.adapter = emaiMessageChooseAdapter
        recyclerView.scrollToPosition(0)
        emaiMessageChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
            var emailMeaasgeData =  emaiMessageChooseAdapter!!.getItem(position)
            if(name == "Drafts")
            {
                var intent = Intent(activity!!, EmailSendActivity::class.java)
                intent.putExtra("emailMeaasgeInfoData",emailMeaasgeData)
                intent.putExtra("foward",3)
                intent.putExtra("flag",1)
                intent.putExtra("menu", menu)
                intent.putExtra("attach",1)
                intent.putExtra("positionIndex", position)
                startActivity(intent)
            }else{
                var intent = Intent(activity!!, EmailInfoActivity::class.java)
                intent.putExtra("emailMeaasgeData",emailMeaasgeData)
                intent.putExtra("menu", menu)
                intent.putExtra("positionIndex", position)
                startActivity(intent)
            }
            if(name != "Nodebackedup")
            {
                emailMeaasgeData!!.setIsSeen(true)
                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                emaiMessageChooseAdapter!!.notifyItemChanged(position)
            }

        }
        /* refreshLayout.setOnRefreshListener {
             pullMoreMessageList()
             if (refreshLayout != null)
                 refreshLayout.isRefreshing = false
         }*/
        if(from != null && from !="")
        {
            if(refreshLayout != null)
            {
                refreshLayout.isEnabled = false
            }

        }else{
            if(refreshLayout != null)
            {
                refreshLayout.isEnabled = true
            }

            refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
            refreshLayout.setOnRefreshListener { refreshLayout ->
                /* refreshLayout.layout.postDelayed({
                     var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).list()
                     pullMoreMessageList(if (localMessageList!= null){localMessageList.size}else{0})
                     refreshLayout.finishRefresh()
                     refreshLayout.resetNoMoreData()//setNoMoreData(false);
                 }, 2000)*/

                var account= AppConfig.instance.emailConfig().account
                if(account != null && account != "")
                {
                    if(menu == "node")
                    {
                        nodeUpandDown = "up";
                        if(lastPayload == null)
                        {
                            nodeStartId = 0;
                        }else{
                            nodeStartId = lastPayload!!.id
                        }
                        var type = AppConfig.instance.emailConfig().emailType.toInt()
                        var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                        var pullMailList = PullMailList(type ,accountBase64,nodeStartId, 20)
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,pullMailList))
                    }else{
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                        pullNewMessageList(0L)
                    }

                }else{
                    refreshLayout.finishRefresh()
                    refreshLayout.resetNoMoreData()
                }


            }
            refreshLayout.setOnLoadMoreListener { refreshLayout ->
                var account= AppConfig.instance.emailConfig().account
                if(account != null && account != "")
                {
                    if(menu == "node")
                    {
                        refreshLayout.finishLoadMore()
                        /*  nodeUpandDown = "down";
                          if(lastPayload == null)
                          {pupu
                              nodeStartId = 0;
                          }else{
                              nodeStartId = lastPayload!!.id
                          }
                          var type = AppConfig.instance.emailConfig().emailType.toInt()
                          var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                          var pullMailList = PullMailList(type ,accountBase64,nodeStartId, 20)
                          AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,pullMailList))*/
                    }else{
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                        pullMoreMessageList(if (localMessageList!= null){localMessageList.size}else{0})
                    }

                }else{
                    refreshLayout.finishLoadMore()
                }
                /* refreshLayout.layout.postDelayed({

                     *//*if (mAdapter.getItemCount() > 30) {
                    Toast.makeText(AppConfig.instance, "数据全部加载完毕", Toast.LENGTH_SHORT).show()
                    refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                } else {
                    pullMoreMessageList()
                    refreshLayout.finishLoadMore()
                }*//*

            }, 2000)*/


            }
        }


        //触发自动刷新
        //refreshLayout.autoRefresh()
        EventBus.getDefault().register(this)
        initQuerData()
        if(from != null && from !="")
        {
            shouUI(true)
        }
    }
    fun deleteAndMoveEmailSend(menuTo:String,flag:Int)
    {
        /*tipDialog.show()*/
        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
        emailReceiveClient
                .imapMarkEmail(activity, object : MarkCallback {
                    override fun gainSuccess(result: Boolean) {
                        //tipDialog.dismiss()
                        closeProgressDialog()
                        if(result)
                        {
                            deleteEmail()
                        }else{
                            Toast.makeText(activity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun gainFailure(errorMsg: String) {
                        //tipDialog.dismiss()
                        closeProgressDialog()
                        Toast.makeText(activity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                    }
                },menu,deleteEmailMeaasgeData!!.msgId,flag,true,menuTo)
    }
    fun deleteEmail()
    {
        AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.delete(deleteEmailMeaasgeData)
        EventBus.getDefault().post(ChangEmailMessage(positionDeleteIndex,1))
        if(emailConfigEntityChoose != null)
        {
            when(menu)
            {
                ConstantValue.currentEmailConfigEntity!!.inboxMenu->
                {
                    emailConfigEntityChoose!!.totalCount -= 1

                }
                ConstantValue.currentEmailConfigEntity!!.drafMenu->
                {
                    emailConfigEntityChoose!!.drafTotalCount -= 1
                }
                ConstantValue.currentEmailConfigEntity!!.sendMenu->
                {
                    emailConfigEntityChoose!!.sendTotalCount -= 1
                }
                ConstantValue.currentEmailConfigEntity!!.garbageMenu->
                {
                    emailConfigEntityChoose!!.garbageCount -= 1
                }
                ConstantValue.currentEmailConfigEntity!!.deleteMenu->
                {
                    emailConfigEntityChoose!!.deleteTotalCount -= 1
                }
            }
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update( emailConfigEntityChoose)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changEmailMessage(changEmailMessage: ChangEmailMessage) {
        if(changEmailMessage.type == 0)
        {
            var emailMeaasgeData =  emaiMessageChooseAdapter!!.getItem(changEmailMessage.positon)
            emailMeaasgeData!!.setIsSeen(false)
            emaiMessageChooseAdapter!!.notifyItemChanged(changEmailMessage.positon)
        }else{
            emaiMessageChooseAdapter!!.remove(changEmailMessage.positon)
            emaiMessageChooseAdapter!!.notifyDataSetChanged()
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changEmailStar(changEmailStar: ChangEmailStar) {
        var emailMeaasgeData =  emaiMessageChooseAdapter!!.getItem(changEmailStar.positon)
        if(name == "Starred")
        {
            if(changEmailStar.type == 0)
            {
                emaiMessageChooseAdapter!!.remove(changEmailStar.positon)
                emaiMessageChooseAdapter!!.notifyDataSetChanged()
            }
        }else{
            if(changEmailStar.type == 0)
            {
                emailMeaasgeData!!.setIsStar(false)
            }else{
                emailMeaasgeData!!.setIsStar(true)
            }

            emaiMessageChooseAdapter!!.notifyItemChanged(changEmailStar.positon)
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendEmailSuccess(sendEmailSuccess: SendEmailSuccess) {
        if(name == "Sent")
        {
            var emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
            if(emailConfigEntityChooseList.size > 0)
            {
                var emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
                if(emailConfigEntityChoose.sendMenuRefresh)
                {
                    var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                    if (localMessageList== null || localMessageList.size == 0)
                    {
                        showProgressDialog()
                        pullMoreMessageList(0)
                    }else{
                        showProgressDialog()
                        pullNewMessageList(0L)
                    }
                }else{
                    var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                    if (localMessageList== null || localMessageList.size == 0)
                    {
                        showProgressDialog()
                        pullMoreMessageList(0)
                    }else{
                        runOnUiThread {
                            emaiMessageChooseAdapter!!.setNewData(localMessageList);
                        }
                    }
                }
            }
        }else if(name == "Drafts")
        {
            var emailMeaasgeData =  emaiMessageChooseAdapter!!.getItem(sendEmailSuccess.positon)
            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.delete(emailMeaasgeData)
            emaiMessageChooseAdapter!!.remove(sendEmailSuccess.positon)
            emaiMessageChooseAdapter!!.notifyDataSetChanged()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDrawerOpened(onDrawerOpened: OnDrawerOpened) {
        var localMessageList = mutableListOf<EmailMessageEntity>()
        runOnUiThread {
            emaiMessageChooseAdapter!!.setNewData(localMessageList);
        }
        getMailUnReadCount()
        if(menu.equals("star"))
        {
            var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.IsStar.eq(true)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localMessageList);
            }
            return;
        }else if(menu.equals("node")){
            showProgressDialog()
            /*if(lastPayload == null)
            {
                nodeStartId = 0;
            }else{
                nodeStartId = lastPayload!!.id
            }*/
            nodeUpandDown = ""
            nodeStartId = 0;
            var type = AppConfig.instance.emailConfig().emailType.toInt()
            var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
            var pullMailList = PullMailList(type ,accountBase64,nodeStartId, 20)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,pullMailList))
            return;
        }else if(menu.equals("star")|| menu.equals("")){
            return;
        }
        if(AppConfig.instance.emailConfig().account != null && !AppConfig.instance.emailConfig().account.equals(""))
        {
            if(name == "Sent")
            {
                var emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                if(emailConfigEntityChooseList.size > 0)
                {
                    var emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
                    if(emailConfigEntityChoose.sendMenuRefresh)
                    {
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                        if (localMessageList== null || localMessageList.size == 0)
                        {
                            showProgressDialog()
                            pullMoreMessageList(0)
                        }else{
                            showProgressDialog()
                            pullNewMessageList(0L)
                        }
                    }else{
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                        if (localMessageList== null || localMessageList.size == 0)
                        {
                            showProgressDialog()
                            pullMoreMessageList(0)
                        }else{
                            runOnUiThread {
                                emaiMessageChooseAdapter!!.setNewData(localMessageList);
                            }
                        }
                    }
                }
            }else{
                var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                if (localMessageList== null || localMessageList.size == 0)
                {
                    showProgressDialog()
                    pullMoreMessageList(0)
                }else{
                    runOnUiThread {
                        emaiMessageChooseAdapter!!.setNewData(localMessageList);
                    }
                }
            }

        }
    }
    fun getMailUnReadCount()
    {
        var menuList = arrayListOf<String>( ConstantValue.currentEmailConfigEntity!!.inboxMenu,ConstantValue.currentEmailConfigEntity!!.drafMenu,ConstantValue.currentEmailConfigEntity!!.sendMenu,ConstantValue.currentEmailConfigEntity!!.garbageMenu,ConstantValue.currentEmailConfigEntity!!.deleteMenu)
        Islands.circularProgress(AppConfig.instance)
                .setCancelable(false)
                .setMessage(getString(R.string.waiting))
                .run { progressDialog ->
                    val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                    emailReceiveClient
                            .imapReceiveAsynCount(activity, object : GetCountCallback {
                                override fun gainSuccess(messageList: List<EmailCount>, count: Int) {
                                    progressDialog.dismiss()
                                    if(messageList.size >0)
                                    {
                                        var emailMessage = messageList.get(0)
                                        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                        var EmailMessage = false
                                        if(emailConfigEntityList.size > 0)
                                        {
                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                            emailConfigEntity.totalCount = emailMessage.totalCount     //Inbox消息总数
                                            emailConfigEntity.unReadCount = emailMessage.unReadCount    //Inbox未读数量
                                            emailConfigEntity.starTotalCount= emailMessage.starTotalCount       //star消息总数
                                            emailConfigEntity.starunReadCount= emailMessage.starunReadCount       //star未读数量
                                            emailConfigEntity.drafTotalCount= emailMessage.drafTotalCount       //draf消息总数
                                            emailConfigEntity.drafUnReadCount= emailMessage.drafUnReadCount       //draf未读数量
                                            emailConfigEntity.sendTotalCount= emailMessage.sendTotalCount       //send消息总数
                                            emailConfigEntity.sendunReadCount= emailMessage.sendunReadCount       //send未读数量
                                            emailConfigEntity.garbageCount= emailMessage.garbageCount         //garbage未读邮件总数
                                            emailConfigEntity.garbageUnReadCount= emailMessage.garbageUnReadCount       //garbage未读数量
                                            emailConfigEntity.deleteTotalCount= emailMessage.deleteTotalCount       //delete消息总数
                                            emailConfigEntity.deleteUnReadCount= emailMessage.deleteUnReadCount       //delete未读数量
                                            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                        }
                                    }
                                    EventBus.getDefault().post(ChangeEmailConfig())
                                }

                                override fun gainFailure(errorMsg: String) {
                                    progressDialog.dismiss()
                                    //Toast.makeText(AppConfig.instance, "IMAP邮件收取失败", Toast.LENGTH_SHORT).show()
                                }
                            },menuList)
                }
    }
    override fun onResume() {
        super.onResume()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser)
        {
            EventBus.getDefault().post(ChangFragmentMenu("Email"))
            //pullMoreMessageList()
            /*if(AppConfig.instance.emailConfig().account != null && AppConfig.instance.emailConfig().account.equals("susan.zhou@qlink.mobi"))
            //if(AppConfig.instance.emailConfig().account != null)
            {
                var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                if (localMessageList == null ||localMessageList.size == 0)
                {
                    showProgressDialog()
                    pullMoreMessageList(0)
                }
            }*/


        }
    }
    override fun setupFragmentComponent() {
        DaggerEmailMessageComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .emailMessageModule(EmailMessageModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: EmailMessageContract.EmailMessageContractPresenter) {
        mPresenter = presenter as EmailMessagePresenter
    }
    fun pullNewMessageList(localSize:Long) {
        var root_ = this.activity
        var account= AppConfig.instance.emailConfig().account
        var smtpHost = AppConfig.instance.emailConfig().smtpHost
        Log.i("pullMoreMessageList",account +":"+smtpHost)

        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        //var lastTotalCount = 0L;
        var minUUID = 0L
        var maxUUID = 0L
        if(emailConfigEntityChoose.size > 0)
        {
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
            when(menu)
            {
                /* emailConfigEntity.inboxMenu->
                 {
                     lastTotalCount = emailConfigEntity.totalCount
                 }
                 emailConfigEntity.drafMenu->
                 {
                     lastTotalCount = emailConfigEntity.drafTotalCount
                 }
                 emailConfigEntity.sendMenu->
                 {
                     lastTotalCount = emailConfigEntity.sendTotalCount
                 }
                 emailConfigEntity.garbageMenu->
                 {
                     lastTotalCount = emailConfigEntity.garbageCount
                 }
                 emailConfigEntity.deleteMenu->
                 {
                     lastTotalCount = emailConfigEntity.deleteTotalCount
                 }*/
                emailConfigEntity.inboxMenu->
                {
                    minUUID = emailConfigEntity.inboxMinMessageId
                    maxUUID = emailConfigEntity.inboxMaxMessageId
                }
                emailConfigEntity.drafMenu->
                {
                    minUUID = emailConfigEntity.drafMinMessageId
                    maxUUID = emailConfigEntity.drafMaxMessageId
                }
                emailConfigEntity.sendMenu->
                {
                    minUUID = emailConfigEntity.sendMinMessageId
                    maxUUID = emailConfigEntity.sendMaxMessageId
                }
                emailConfigEntity.garbageMenu->
                {
                    minUUID = emailConfigEntity.garbageMinMessageId
                    maxUUID = emailConfigEntity.garbageMaxMessageId
                }
                emailConfigEntity.deleteMenu->
                {
                    minUUID = emailConfigEntity.deleteMinMessageId
                    maxUUID = emailConfigEntity.deleteMaxMessageId
                }
            }
        }
        // var verifyList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.queryBuilder().where(GroupVerifyEntityDao.Properties.Aduit.eq(selfUserId)).list()

        if(true)
        {
            var beginIndex = localSize
            /*  AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.deleteAll()
              AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.deleteAll()*/
            Islands.circularProgress(this.activity)
                    .setCancelable(false)
                    .setMessage("同步中...")
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .imapReceiveNewAsyn(this.activity, object : GetReceiveCallback {
                                    override fun gainSuccess(messageList: List<EmailMessage>, minUUID: Long, maxUUID: Long, noMoreData:Boolean, errorMs:String,menuFlag:String) {
                                        if(noMoreData)
                                        {
                                            runOnUiThread {
                                                closeProgressDialog()
                                                refreshLayout.finishRefresh()
                                                refreshLayout.resetNoMoreData()
                                                //toast(R.string.No_mail)
                                                //refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                                            }
                                        }else{
                                            runOnUiThread {
                                                closeProgressDialog()
                                                refreshLayout.finishRefresh()
                                                refreshLayout.resetNoMoreData()
                                            }
                                        }
                                       /* if(errorMs != null && errorMs  != "" && "susan.zhou@qlink.mobi" == AppConfig.instance.emailConfig().account)
                                        {
                                            runOnUiThread {
                                                SweetAlertDialog(root_, SweetAlertDialog.BUTTON_NEUTRAL)
                                                        .setCancelText(getString(R.string.close))
                                                        .setConfirmText(getString(R.string.yes))
                                                        .setContentText(errorMs)
                                                        .setConfirmClickListener {

                                                        }.setCancelClickListener {

                                                        }
                                                        .show()
                                            }
                                        }*/
                                        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                                        if(emailConfigEntityChoose.size > 0)
                                        {
                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
                                            when(menu)
                                            {
                                                emailConfigEntity.inboxMenu->
                                                {
                                                    emailConfigEntity.totalCount += messageList.size
                                                    emailConfigEntity.inboxMaxMessageId = maxUUID
                                                    emailConfigEntity.inboxMenuRefresh = false
                                                }
                                                emailConfigEntity.drafMenu->
                                                {
                                                    emailConfigEntity.drafTotalCount += messageList.size
                                                    emailConfigEntity.drafMaxMessageId = maxUUID
                                                    emailConfigEntity.drafMenuRefresh = false
                                                }
                                                emailConfigEntity.sendMenu->
                                                {
                                                    emailConfigEntity.sendTotalCount += messageList.size
                                                    emailConfigEntity.sendMaxMessageId = maxUUID
                                                    emailConfigEntity.sendMenuRefresh = false
                                                }
                                                emailConfigEntity.garbageMenu->
                                                {
                                                    emailConfigEntity.garbageCount += messageList.size
                                                    emailConfigEntity.garbageMaxMessageId = maxUUID
                                                    emailConfigEntity.garbageMenuRefresh = false
                                                }
                                                emailConfigEntity.deleteMenu->
                                                {
                                                    emailConfigEntity.deleteTotalCount += messageList.size
                                                    emailConfigEntity.deleteMaxMessageId = maxUUID
                                                    emailConfigEntity.deleteMenuRefresh = false
                                                }
                                            }
                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                        }
                                        var list = messageList;
                                        for (item in messageList)
                                        {
                                            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu),EmailMessageEntityDao.Properties.MsgId.eq(item.id)).list()
                                            var name = ""
                                            var account  = ""
                                            if(localEmailMessage ==null || localEmailMessage.size == 0)
                                            {
                                                var eamilMessage = EmailMessageEntity()
                                                eamilMessage.account = AppConfig.instance.emailConfig().account
                                                eamilMessage.msgId = item.id
                                                eamilMessage.menu = menuFlag
                                                eamilMessage.from = item.from
                                                eamilMessage.to = item.to
                                                eamilMessage.cc = item.cc
                                                eamilMessage.bcc = item.bcc
                                                eamilMessage.setIsContainerAttachment(item.isContainerAttachment)
                                                eamilMessage.setIsSeen(item.isSeen)
                                                eamilMessage.setIsStar(item.isStar)
                                                eamilMessage.setIsReplySign(item.isReplySign)
                                                eamilMessage.setAttachmentCount(item.attachmentCount)
                                                eamilMessage.subject = item.subject
                                                println("time_" + "imapStoreBeginHelp:"+item.subject + menuFlag + "##" + System.currentTimeMillis())
                                                eamilMessage.content= item.content
                                                eamilMessage.contentText= item.contentText
                                                var originMap = getOriginalText(eamilMessage)
                                                eamilMessage.originalText = originMap.get("originalText")
                                                eamilMessage.aesKey  = originMap.get("aesKey")
                                                eamilMessage.userId  = originMap.get("userId")
                                                eamilMessage.date = item.date
                                                eamilMessage.setTimeStamp(DateUtil.getDateTimeStame(item.date))
                                                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.insert(eamilMessage)
                                                if(eamilMessage.from.indexOf("<") >= 0)
                                                {
                                                    name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("<"))
                                                    account= eamilMessage.from.substring(eamilMessage.from.indexOf("<")+1,eamilMessage.from.length -1)
                                                }else{
                                                    name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("@"))
                                                    account= eamilMessage.from.substring(0,eamilMessage.from.length)
                                                }
                                                name = name.replace("\"","")
                                                name = name.replace("\"","")
                                            }else{
                                                continue
                                            }
                                            var mailAttachmentList: List<MailAttachment> = item.mailAttachmentList
                                            for (attachItem in mailAttachmentList)
                                            {
                                                var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(menu+"_"+item.id),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachList == null || attachList.size == 0)
                                                {
                                                    var eamilAttach = EmailAttachEntity()
                                                    eamilAttach.account = AppConfig.instance.emailConfig().account
                                                    eamilAttach.msgId = menu+"_"+item.id
                                                    eamilAttach.name = attachItem.name
                                                    eamilAttach.data = attachItem.byt
                                                    eamilAttach.hasData = true
                                                    eamilAttach.isCanDelete = false
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)
                                                }
                                            }


                                            account = account.toLowerCase()
                                            var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                                            if(localEmailContacts.size == 0)
                                            {
                                                var emailContactsEntity= EmailContactsEntity();
                                                emailContactsEntity.name = name
                                                emailContactsEntity.account = account
                                                emailContactsEntity.createTime = System.currentTimeMillis()
                                                AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                                            }

                                        }
                                        //var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                        var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                                        var aabb = "'"
                                        runOnUiThread {
                                            emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
                                            progressDialog.dismiss()
                                        }

                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        runOnUiThread {
                                            toast(R.string.Failedmail)
                                            closeProgressDialog()
                                            refreshLayout.finishRefresh()
                                            refreshLayout.resetNoMoreData()
                                        }
                                    }
                                },menu,minUUID,5,maxUUID)
                    }
        }else{
            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).list()
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
            }
        }

    }
    fun pullMoreMessageList(localSize:Int) {
        var root_ = this.activity;
        var account= AppConfig.instance.emailConfig().account
        var smtpHost = AppConfig.instance.emailConfig().smtpHost
        Log.i("pullMoreMessageList",account +":"+smtpHost)

        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        var beginIndex = localSize
        var lastTotalCount = 0;
        if(emailConfigEntityChoose.size > 0)
        {
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
            when(menu)
            {
                emailConfigEntity.inboxMenu->
                {
                    lastTotalCount = emailConfigEntity.totalCount
                }
                emailConfigEntity.drafMenu->
                {
                    lastTotalCount = emailConfigEntity.drafTotalCount
                }
                emailConfigEntity.sendMenu->
                {
                    lastTotalCount = emailConfigEntity.sendTotalCount
                }
                emailConfigEntity.garbageMenu->
                {
                    lastTotalCount = emailConfigEntity.garbageCount
                }
                emailConfigEntity.deleteMenu->
                {
                    lastTotalCount = emailConfigEntity.deleteTotalCount
                }
                /*emailConfigEntity.inboxMenu->
                {
                    lastTotalCount = emailConfigEntity.inboxMaxMessageId
                    beginIndex = emailConfigEntity.inboxMinMessageId
                }
                emailConfigEntity.drafMenu->
                {
                    lastTotalCount = emailConfigEntity.drafMaxMessageId
                    beginIndex = emailConfigEntity.drafMinMessageId
                }
                emailConfigEntity.sendMenu->
                {
                    lastTotalCount = emailConfigEntity.sendMaxMessageId
                    beginIndex = emailConfigEntity.sendMinMessageIdpu
                }
                emailConfigEntity.garbageMenu->
                {
                    lastTotalCount = emailConfigEntity.garbageMaxMessageId
                    beginIndex = emailConfigEntity.garbageMinMessageId
                }
                emailConfigEntity.deleteMenu->
                {
                    lastTotalCount = emailConfigEntity.deleteMaxMessageId
                    beginIndex = emailConfigEntity.deleteMinMessageId
                }*/
            }
        }
        // var verifyList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.queryBuilder().where(GroupVerifyEntityDao.Properties.Aduit.eq(selfUserId)).list()

        if(true)
        {

            /*  AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.deleteAll()
              AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.deleteAll()*/
            Islands.circularProgress(this.activity)
                    .setCancelable(false)
                    .setMessage("同步中...")
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .imapReceiveMoreAsyn(this.activity, object : GetReceiveCallback {
                                    override fun gainSuccess(messageList: List<EmailMessage>, minUUID: Long, maxUUID: Long, noMoreData:Boolean, errorMs:String,menuFlag:String) {
                                        if(noMoreData)
                                        {
                                            runOnUiThread {
                                                closeProgressDialog()
                                                refreshLayout.finishLoadMore()
                                                //toast(R.string.No_mail)
                                                //refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                                            }
                                        }else{
                                            runOnUiThread {
                                                closeProgressDialog()
                                                refreshLayout.finishLoadMore()
                                            }
                                        }
                                        /*if(errorMs != null && errorMs  != "" && "susan.zhou@qlink.mobi" == AppConfig.instance.emailConfig().account)
                                        {
                                            runOnUiThread {
                                                SweetAlertDialog(root_, SweetAlertDialog.BUTTON_NEUTRAL)
                                                        .setCancelText(getString(R.string.close))
                                                        .setConfirmText(getString(R.string.yes))
                                                        .setContentText(errorMs)
                                                        .setConfirmClickListener {

                                                        }.setCancelClickListener {

                                                        }
                                                        .show()
                                            }
                                        }*/
                                        var list = messageList;
                                        for (item in messageList)
                                        {
                                            var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                                            if(emailConfigEntityChoose.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
                                                when(menu)
                                                {
                                                    emailConfigEntity.inboxMenu->
                                                    {
                                                        if(emailConfigEntity.inboxMaxMessageId == 0L)
                                                        {
                                                            emailConfigEntity.totalCount = minUUID.toInt()
                                                            emailConfigEntity.inboxMaxMessageId = item.id.toLong()
                                                            emailConfigEntity.inboxMinMessageId = item.id.toLong()
                                                        }

                                                    }
                                                }
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                            }
                                            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu),EmailMessageEntityDao.Properties.MsgId.eq(item.id)).list()
                                            var name = ""
                                            var account  = ""
                                            if(localEmailMessage ==null || localEmailMessage.size == 0)
                                            {
                                                var eamilMessage = EmailMessageEntity()
                                                eamilMessage.account = AppConfig.instance.emailConfig().account
                                                eamilMessage.msgId = item.id
                                                eamilMessage.menu = menuFlag
                                                eamilMessage.from = item.from
                                                eamilMessage.to = item.to
                                                eamilMessage.cc = item.cc
                                                eamilMessage.bcc = item.bcc
                                                eamilMessage.setIsContainerAttachment(item.isContainerAttachment)
                                                eamilMessage.setIsSeen(item.isSeen)
                                                eamilMessage.setIsStar(item.isStar)
                                                eamilMessage.setIsReplySign(item.isReplySign)
                                                eamilMessage.setAttachmentCount(item.attachmentCount)
                                                eamilMessage.subject = item.subject
                                                println("time_" + "imapStoreBeginHelp:"+item.subject + menuFlag + "##" + System.currentTimeMillis())
                                                eamilMessage.content= item.content
                                                eamilMessage.contentText= item.contentText
                                                var originMap = getOriginalText(eamilMessage)
                                                eamilMessage.originalText = originMap.get("originalText")
                                                eamilMessage.aesKey  = originMap.get("aesKey")
                                                eamilMessage.userId  = originMap.get("userId")
                                                eamilMessage.date = item.date
                                                eamilMessage.setTimeStamp(DateUtil.getDateTimeStame(item.date))
                                                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.insert(eamilMessage)
                                                if(eamilMessage.from.indexOf("<") >= 0)
                                                {
                                                    name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("<"))
                                                    account= eamilMessage.from.substring(eamilMessage.from.indexOf("<")+1,eamilMessage.from.length -1)
                                                }else{
                                                    name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("@"))
                                                    account= eamilMessage.from.substring(0,eamilMessage.from.length)
                                                }
                                                name = name.replace("\"","")
                                                name = name.replace("\"","")
                                            }else{
                                                continue
                                            }
                                            var mailAttachmentList: List<MailAttachment> = item.mailAttachmentList
                                            for (attachItem in mailAttachmentList)
                                            {
                                                var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(menu+"_"+item.id),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachList == null || attachList.size == 0)
                                                {
                                                    var eamilAttach = EmailAttachEntity()
                                                    eamilAttach.account = AppConfig.instance.emailConfig().account
                                                    eamilAttach.msgId = menu+"_"+item.id
                                                    eamilAttach.name = attachItem.name
                                                    eamilAttach.data = attachItem.byt
                                                    eamilAttach.hasData = true
                                                    eamilAttach.isCanDelete = false
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)
                                                }
                                            }
                                            account = account.toLowerCase()
                                            var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                                            if(localEmailContacts.size == 0)
                                            {
                                                var emailContactsEntity= EmailContactsEntity();
                                                emailContactsEntity.name = name
                                                emailContactsEntity.account = account
                                                emailContactsEntity.createTime = System.currentTimeMillis()
                                                AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                                            }

                                        }
                                        //var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                        var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                                        runOnUiThread {
                                            emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
                                            progressDialog.dismiss()
                                        }
                                    }
                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        runOnUiThread {
                                            toast(R.string.Failedmail)
                                            closeProgressDialog()
                                            refreshLayout.finishLoadMore()
                                        }
                                    }
                                },menu,beginIndex,7,lastTotalCount)
                    }
        }else{
            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
            }
        }

    }
    fun getOriginalText(emailMeaasgeData:EmailMessageEntity): HashMap<String, String>
    {
        var contactMapList = HashMap<String, String>()
        var userID = ""
        if(emailMeaasgeData!!.content.contains("confidantKey") || emailMeaasgeData!!.content.contains("confidantkey"))
        {

            try {
                var endStr = ""
                if(emailMeaasgeData!!.content.contains("myconfidantbegin"))
                {
                    endStr =  "<div myconfidantbegin=''>"+
                            "<br />"+
                            " <br />"+
                            " <br />"+
                            "<span>"+
                            getString(R.string.sendfromconfidant)+
                            "</span>"+
                            "</div>"
                }
                var miContentSoucreBgeinIndex= 0
                var miContentSoucreEndIndex = emailMeaasgeData!!.content.indexOf("<span style='display:none' confidantkey=")
                if(miContentSoucreEndIndex == -1)
                {
                    miContentSoucreEndIndex = emailMeaasgeData!!.content.indexOf("<span style='display:none' confidantKey=")
                }
                if(miContentSoucreEndIndex == -1)
                {
                    miContentSoucreEndIndex = emailMeaasgeData!!.content.indexOf("<span style=\"display:none\" confidantkey=")
                }
                if(miContentSoucreEndIndex == -1)
                {
                    miContentSoucreEndIndex = emailMeaasgeData!!.content.indexOf("<span style=\"display:none\" confidantKey=")
                }
                var beginIndex = emailMeaasgeData!!.content.indexOf("confidantkey='")
                if(beginIndex == -1)
                {
                    beginIndex = emailMeaasgeData!!.content.indexOf("confidantKey='")
                }
                if(beginIndex == -1)
                {
                    beginIndex = emailMeaasgeData!!.content.indexOf("confidantkey=\"")
                }
                if(beginIndex == -1)
                {
                    beginIndex = emailMeaasgeData!!.content.indexOf("confidantKey=\"")
                }
                if(beginIndex < 0)
                {
                    beginIndex = 0;
                }
                if(miContentSoucreEndIndex < 0)
                {
                    miContentSoucreEndIndex = 0;
                }
                var miContentSoucreBase64 = emailMeaasgeData!!.content.substring(miContentSoucreBgeinIndex,miContentSoucreEndIndex)
                var endIndexd = emailMeaasgeData!!.content.length
                if(endIndexd < beginIndex)
                {
                    endIndexd = beginIndex
                }
                var confidantkeyBefore = emailMeaasgeData!!.content.substring(beginIndex,endIndexd)

                if(confidantkeyBefore.contains("confidantuserid"))
                {
                    var userIDBeginStr = "confidantuserid='"
                    userID = ""
                    var userIDBeginIndex = confidantkeyBefore.indexOf(userIDBeginStr)
                    if(userIDBeginIndex == -1)
                    {
                        userIDBeginStr = "confidantuserid=\""
                        userIDBeginIndex = confidantkeyBefore.indexOf("confidantuserid=\"")
                    }
                    var userIDEndIndex = confidantkeyBefore.lastIndexOf("'></span>")
                    if(userIDEndIndex < 0)
                    {
                        userIDEndIndex = confidantkeyBefore.lastIndexOf("\"></span>")
                    }
                    userID = confidantkeyBefore.substring(userIDBeginIndex+userIDBeginStr.length,userIDEndIndex)
                    var aa = ""
                }
                var endIndex = confidantkeyBefore.indexOf("'></span>")
                if(endIndex < 0)
                {
                    endIndex = confidantkeyBefore.indexOf("\"></span>")
                }
                if(endIndex < 14)
                {
                    endIndex = 14
                }
                var confidantkey = confidantkeyBefore.substring(14,endIndex)

                var confidantkeyArr = listOf<String>()
                var accountMi = ""
                var shareMiKey = ""
                var account =  String(RxEncodeTool.base64Decode(accountMi))
                if(confidantkey!!.contains("##"))
                {
                    var confidantkeyList = confidantkey.split("##")
                    for(item in confidantkeyList)
                    {
                        if(item.contains("&&"))
                        {
                            confidantkeyArr = item.split("&&")
                        }else{
                            confidantkeyArr = item.split("&amp;&amp;")
                        }

                        accountMi = confidantkeyArr.get(0)
                        shareMiKey = confidantkeyArr.get(1)
                        account =  String(RxEncodeTool.base64Decode(accountMi))
                        if(account != "" && account.toLowerCase().contains(AppConfig.instance.emailConfig().account.toLowerCase()))
                        {
                            break;
                        }
                    }

                }else{
                    if(confidantkey.contains("&&"))
                    {
                        confidantkeyArr = confidantkey.split("&&")
                    }else{
                        confidantkeyArr = confidantkey.split("&amp;&amp;")
                    }
                    accountMi = confidantkeyArr.get(0)
                    shareMiKey = confidantkeyArr.get(1)
                }
                var aesKey = LibsodiumUtil.DecryptShareKey(shareMiKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!);
                var miContentSoucreBase = RxEncodeTool.base64Decode(miContentSoucreBase64)
                val miContent = AESCipher.aesDecryptBytes(miContentSoucreBase, aesKey.toByteArray())
                var sourceContent = ""
                try{
                    sourceContent = String(miContent)
                    contactMapList.put("originalText",sourceContent + endStr)
                    contactMapList.put("aesKey",aesKey)
                    contactMapList.put("userId",userID)
                }catch (e:Exception)
                {
                    contactMapList.put("originalText","")
                    contactMapList.put("aesKey","")
                    contactMapList.put("userId",userID)
                }finally {
                    return contactMapList
                }
            }catch (e:Exception)
            {
                contactMapList.put("originalText","")
                contactMapList.put("aesKey","")
                contactMapList.put("userId",userID)
            }finally {
                return contactMapList
            }

        }else{
            contactMapList.put("originalText","")
            contactMapList.put("aesKey","")
            contactMapList.put("userId",userID)
            return contactMapList
        }
    }
    override fun initDataFromLocal() {

    }
    fun initQuerData() {
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
                if (localMessageList== null || localMessageList.size > 0)
                {
                    var localMessageListData = arrayListOf<EmailMessageEntity>()
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

    override fun showProgressDialog() {
        progressDialog.show()
    }
    fun showProgressDialog(text: String) {
        try {
            KLog.i("弹窗：showProgressDialog_"+text)
            progressDialog.setDialogText(text)
            progressDialog.show()
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    override fun closeProgressDialog() {
        if(progressDialog!= null)
            progressDialog.hide()
    }

    fun shouUI(flag: Boolean) {
        searchParent.visibility = if (flag) View.VISIBLE else View.GONE
        if(flag && AppConfig.instance.emailConfig().account!= null)
        {
            var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.TimeStamp).list()
            if (localMessageList== null || localMessageList.size > 0)
            {
                var localMessageListData = arrayListOf<EmailMessageEntity>()
                for (item in localMessageList)
                {
                    localMessageListData.add(item);
                }
                fiter(query.text.toString(), localMessageListData)
            }
        }
    }
    fun fiter(key: String, emailMessageList: ArrayList<EmailMessageEntity>) {
        var contactListTemp: ArrayList<EmailMessageEntity> = arrayListOf<EmailMessageEntity>()
        for (i in emailMessageList) {
            var content = ""
            var aa = i.originalText
            if(aa != null && aa != "")
            {
                content = aa;
            }else{
                content = i.content
            }
            if (i.from.toLowerCase().contains(key) || content.toLowerCase().contains(key)) {
                contactListTemp.add(i)
            }
        }
        if(key != "")
        {
            emaiMessageChooseAdapter!!.setNewData(contactListTemp);
        }else{
            emaiMessageChooseAdapter!!.setNewData(emailMessageList);
        }
    }
    public fun updateMenu(menuName:String)
    {
        menu = menuName
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.pullMailListCallback = null
        super.onDestroy()
    }
}