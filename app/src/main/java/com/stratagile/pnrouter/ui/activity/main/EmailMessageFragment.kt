package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetReceiveCallback
import com.smailnet.eamil.EmailMessage
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.smailnet.islands.Islands
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.ui.activity.email.EmailInfoActivity
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEmailMessageComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EmailMessageContract
import com.stratagile.pnrouter.ui.activity.main.module.EmailMessageModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EmailMessagePresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiMessageAdapter
import com.stratagile.pnrouter.utils.AESCipher
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import kotlinx.android.synthetic.main.fragment_mail_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/07/11 16:19:12
 */

class EmailMessageFragment : BaseFragment(), EmailMessageContract.View {

    @Inject
    lateinit internal var mPresenter: EmailMessagePresenter
    var emaiMessageChooseAdapter : EmaiMessageAdapter? = null
    var name = "name"
    var menu = "INBOX"
    var isChangeMenu = false
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changEmailMenu(changEmailMenu: ChangEmailMenu) {
        name = changEmailMenu.name
        menu = changEmailMenu.menu
        /*var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
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
        var account = AppConfig.instance.emailConfig().account
        var emailMessageEntityList = mutableListOf<EmailMessageEntity>()
        if(account != null)
        {
            emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
        }
        emaiMessageChooseAdapter = EmaiMessageAdapter(emailMessageEntityList)
        emaiMessageChooseAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            /* val floatMenu = FloatMenu(activity)
             floatMenu.items("菜单1", "菜单2", "菜单3")
             floatMenu.show((activity!! as BaseActivity).point,0,0)*/
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
            emailMeaasgeData!!.setIsSeen(true)
            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
            emaiMessageChooseAdapter!!.notifyItemChanged(position)
        }
        /* refreshLayout.setOnRefreshListener {
             pullMoreMessageList()
             if (refreshLayout != null)
                 refreshLayout.isRefreshing = false
         }*/
        refreshLayout.setEnableAutoLoadMore(false)//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener { refreshLayout ->
            /* refreshLayout.layout.postDelayed({
                 var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).list()
                 pullMoreMessageList(if (localMessageList!= null){localMessageList.size}else{0})
                 refreshLayout.finishRefresh()
                 refreshLayout.resetNoMoreData()//setNoMoreData(false);
             }, 2000)*/

            var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
            pullNewMessageList(localMessageList.size)

        }
        refreshLayout.setOnLoadMoreListener { refreshLayout ->
            refreshLayout.layout.postDelayed({
                /*if (mAdapter.getItemCount() > 30) {
                    Toast.makeText(AppConfig.instance, "数据全部加载完毕", Toast.LENGTH_SHORT).show()
                    refreshLayout.finishLoadMoreWithNoMoreData()//将不会再次触发加载更多事件
                } else {
                    pullMoreMessageList()
                    refreshLayout.finishLoadMore()
                }*/

            }, 2000)
            var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
            pullMoreMessageList(if (localMessageList!= null){localMessageList.size}else{0})
        }

        //触发自动刷新
        //refreshLayout.autoRefresh()
        EventBus.getDefault().register(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changEmailMessage(changEmailMessage: ChangEmailMessage) {
        if(changEmailMessage.type == 0)
        {
            emaiMessageChooseAdapter!!.notifyItemChanged(changEmailMessage.positon)
        }else{
            emaiMessageChooseAdapter!!.remove(changEmailMessage.positon)
            emaiMessageChooseAdapter!!.notifyDataSetChanged()
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
                    var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
                    if (localMessageList== null || localMessageList.size == 0)
                    {
                        showProgressDialog()
                        pullMoreMessageList(0)
                    }else{
                        showProgressDialog()
                        pullNewMessageList(localMessageList.size)
                    }
                }else{
                    var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
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
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDrawerOpened(onDrawerOpened: OnDrawerOpened) {
        var localMessageList = mutableListOf<EmailMessageEntity>()
        runOnUiThread {
            emaiMessageChooseAdapter!!.setNewData(localMessageList);
        }
        if(menu.equals("node") || menu.equals("star")|| menu.equals(""))
        {

            //toast(R.string.No_mail)
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
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
                        if (localMessageList== null || localMessageList.size == 0)
                        {
                            showProgressDialog()
                            pullMoreMessageList(0)
                        }else{
                            showProgressDialog()
                            pullNewMessageList(localMessageList.size)
                        }
                    }else{
                        var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
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
                var localMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
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
    fun pullNewMessageList(localSize:Int) {
        var account= AppConfig.instance.emailConfig().account
        var smtpHost = AppConfig.instance.emailConfig().smtpHost
        Log.i("pullMoreMessageList",account +":"+smtpHost)

        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
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
                                    override fun gainSuccess(messageList: List<EmailMessage>, totalCount: Int, totalUnreadCount: Int, noMoreData:Boolean) {
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
                                        if(messageList.size > 0)
                                        {
                                            var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                                            if(emailConfigEntityChoose.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
                                                when(menu)
                                                {
                                                    emailConfigEntity.inboxMenu->
                                                    {
                                                        emailConfigEntity.totalCount += messageList.size
                                                        emailConfigEntity.inboxMenuRefresh = false
                                                    }
                                                    emailConfigEntity.drafMenu->
                                                    {
                                                        emailConfigEntity.drafTotalCount += messageList.size
                                                        emailConfigEntity.drafMenuRefresh = false
                                                    }
                                                    emailConfigEntity.sendMenu->
                                                    {
                                                        emailConfigEntity.sendTotalCount += messageList.size
                                                        emailConfigEntity.sendMenuRefresh = false
                                                    }
                                                    emailConfigEntity.garbageMenu->
                                                    {
                                                        emailConfigEntity.garbageCount += messageList.size
                                                        emailConfigEntity.garbageMenuRefresh = false
                                                    }
                                                    emailConfigEntity.deleteMenu->
                                                    {
                                                        emailConfigEntity.deleteTotalCount += messageList.size
                                                        emailConfigEntity.deleteMenuRefresh = false
                                                    }
                                                }
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                            }
                                        }
                                        var list = messageList;
                                        for (item in messageList)
                                        {
                                            var eamilMessage = EmailMessageEntity()
                                            eamilMessage.account = AppConfig.instance.emailConfig().account
                                            eamilMessage.msgId = item.id
                                            eamilMessage.menu = menu
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
                                            eamilMessage.content= item.content
                                            eamilMessage.contentText= item.contentText
                                            eamilMessage.originalText = getOriginalText(eamilMessage).get("originalText")
                                            eamilMessage.aesKey  = getOriginalText(eamilMessage).get("aesKey")
                                            eamilMessage.date = item.date
                                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.insert(eamilMessage)
                                            var mailAttachmentList: List<MailAttachment> = item.mailAttachmentList
                                            for (attachItem in mailAttachmentList)
                                            {
                                                var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(item.id),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachList == null || attachList.size == 0)
                                                {
                                                    var eamilAttach = EmailAttachEntity()
                                                    eamilAttach.account = AppConfig.instance.emailConfig().account
                                                    eamilAttach.msgId = item.id
                                                    eamilAttach.name = attachItem.name
                                                    eamilAttach.data = attachItem.byt
                                                    eamilAttach.hasData = true
                                                    eamilAttach.isCanDelete = false
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)
                                                }
                                            }

                                            var name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("<"))
                                            var account= eamilMessage.from.substring(eamilMessage.from.indexOf("<")+1,eamilMessage.from.length -1)
                                            account = account.toLowerCase()
                                            var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                                            if(localEmailContacts.size == 0)
                                            {
                                                var emailContactsEntity= EmailContactsEntity();
                                                emailContactsEntity.name = name
                                                emailContactsEntity.account = account
                                                AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                                            }

                                        }
                                        //var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                        var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
                                        runOnUiThread {
                                            emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
                                            progressDialog.dismiss()
                                        }

                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()

                                    }
                                },menu,beginIndex,5,lastTotalCount)
                    }
        }else{
            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).list()
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
            }
        }

    }
    fun pullMoreMessageList(localSize:Int) {
        var account= AppConfig.instance.emailConfig().account
        var smtpHost = AppConfig.instance.emailConfig().smtpHost
        Log.i("pullMoreMessageList",account +":"+smtpHost)

        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
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
                                .imapReceiveMoreAsyn(this.activity, object : GetReceiveCallback {
                                    override fun gainSuccess(messageList: List<EmailMessage>, totalCount: Int, totalUnreadCount: Int, noMoreData:Boolean) {
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
                                        var list = messageList;
                                        for (item in messageList)
                                        {
                                            var eamilMessage = EmailMessageEntity()
                                            eamilMessage.account = AppConfig.instance.emailConfig().account
                                            eamilMessage.msgId = item.id
                                            eamilMessage.menu = menu
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
                                            eamilMessage.content= item.content
                                            eamilMessage.contentText= item.contentText
                                            eamilMessage.originalText = getOriginalText(eamilMessage).get("originalText")
                                            eamilMessage.aesKey  = getOriginalText(eamilMessage).get("aesKey")
                                            eamilMessage.date = item.date
                                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.insert(eamilMessage)
                                            var mailAttachmentList: List<MailAttachment> = item.mailAttachmentList
                                            for (attachItem in mailAttachmentList)
                                            {
                                                var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(item.id),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachList == null || attachList.size == 0)
                                                {
                                                    var eamilAttach = EmailAttachEntity()
                                                    eamilAttach.account = AppConfig.instance.emailConfig().account
                                                    eamilAttach.msgId = item.id
                                                    eamilAttach.name = attachItem.name
                                                    eamilAttach.data = attachItem.byt
                                                    eamilAttach.hasData = true
                                                    eamilAttach.isCanDelete = false
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)
                                                }
                                            }

                                            var name  = eamilMessage.from.substring(0,eamilMessage.from.indexOf("<"))
                                            var account= eamilMessage.from.substring(eamilMessage.from.indexOf("<")+1,eamilMessage.from.length -1)
                                            account = account.toLowerCase()
                                            var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(account)).list()
                                            if(localEmailContacts.size == 0)
                                            {
                                                var emailContactsEntity= EmailContactsEntity();
                                                emailContactsEntity.name = name
                                                emailContactsEntity.account = account
                                                AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.insert(emailContactsEntity)
                                            }

                                        }
                                        //var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                        var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
                                        runOnUiThread {
                                            emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
                                            progressDialog.dismiss()
                                        }

                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()

                                    }
                                },menu,beginIndex,5,lastTotalCount)
                    }
        }else{
            var localEmailMessage = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(account),EmailMessageEntityDao.Properties.Menu.eq(menu)).orderDesc(EmailMessageEntityDao.Properties.Date).list()
            runOnUiThread {
                emaiMessageChooseAdapter!!.setNewData(localEmailMessage);
            }
        }

    }
    fun getOriginalText(emailMeaasgeData:EmailMessageEntity): HashMap<String, String>
    {
        var contactMapList = HashMap<String, String>()
        if(emailMeaasgeData!!.content.contains("confidantKey") || emailMeaasgeData!!.content.contains("confidantkey"))
        {

            var endStr = ""
            if(emailMeaasgeData!!.content.contains("MyConfidantBegin"))
            {
                endStr =  "<div MyConfidantBegin=''>"+
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
            var beginIndex = emailMeaasgeData!!.content.indexOf("confidantkey='")
            if(beginIndex == -1)
            {
                beginIndex = emailMeaasgeData!!.content.indexOf("confidantKey='")
            }
            var miContentSoucreBase64 = emailMeaasgeData!!.content.substring(miContentSoucreBgeinIndex,miContentSoucreEndIndex)
            var confidantkeyBefore = emailMeaasgeData!!.content.substring(beginIndex,emailMeaasgeData!!.content.length)
            var endIndex = confidantkeyBefore.indexOf("'></span>")
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
                    confidantkeyArr = item.split("&&")
                    accountMi = confidantkeyArr.get(0)
                    shareMiKey = confidantkeyArr.get(1)
                    account =  String(RxEncodeTool.base64Decode(accountMi))
                    if(account != "" && account.contains(AppConfig.instance.emailConfig().account))
                    {
                        break;
                    }
                }

            }else{
                confidantkeyArr = confidantkey.split("&&")
                accountMi = confidantkeyArr.get(0)
                shareMiKey = confidantkeyArr.get(1)
            }
            var aesKey = LibsodiumUtil.DecryptShareKey(shareMiKey);
            var miContentSoucreBase = RxEncodeTool.base64Decode(miContentSoucreBase64)
            val miContent = AESCipher.aesDecryptBytes(miContentSoucreBase, aesKey.toByteArray())
            var sourceContent = ""
            try{
                sourceContent = String(miContent)
            }catch (e:Exception)
            {

            }

            contactMapList.put("originalText",sourceContent + endStr)
            if(sourceContent != "")
            {
                contactMapList.put("aesKey",aesKey)
            }else{
                contactMapList.put("aesKey","")
            }

            return contactMapList
        }else{
            contactMapList.put("originalText",emailMeaasgeData!!.content)
            contactMapList.put("aesKey","")
           return contactMapList
        }
    }
    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        if(progressDialog!= null)
            progressDialog.hide()
    }
}