package com.stratagile.pnrouter.ui.activity.email

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.hyphenate.easeui.utils.PathUtils
import com.smailnet.eamil.Callback.GetAttachCallback
import com.smailnet.eamil.Callback.MarkCallback
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.EmailAttachEntity
import com.stratagile.pnrouter.db.EmailAttachEntityDao
import com.stratagile.pnrouter.db.EmailMessageEntity
import com.stratagile.pnrouter.entity.EmailInfoData
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailInfoComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailInfoContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailInfoModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailInfoPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiAttachAdapter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiInfoAdapter
import com.stratagile.pnrouter.utils.DateUtil
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.email_info_view.*
import java.io.File
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/15 15:18:54
 */

class EmailInfoActivity : BaseActivity(), EmailInfoContract.View {

    @Inject
    internal lateinit var mPresenter: EmailInfoPresenter
    var emailMeaasgeData:EmailMessageEntity? = null
    var menu:String= "INBOX"
    var emaiInfoAdapter : EmaiInfoAdapter? = null
    var emaiAttachAdapter : EmaiAttachAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_info_view)
    }

    override fun initData() {
        emailMeaasgeData = intent.getParcelableExtra("emailMeaasgeData")
        menu = intent.getStringExtra("menu")
        var msgId = emailMeaasgeData!!.msgId
        var to = emailMeaasgeData!!.to
        var cc = emailMeaasgeData!!.cc
        var bcc = emailMeaasgeData!!.bcc
        var attachCount = emailMeaasgeData!!.attachmentCount
        if(emailMeaasgeData!!.isStar())
        {
            inboxStar.visibility =View.VISIBLE
        }else{
            inboxStar.visibility =View.GONE
        }
        if(attachCount > 0)
        {
            val save_dir = PathUtils.getInstance().filePath.toString() + "/"
            var  attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

            var isDownload = true
            var listAccath:ArrayList<MailAttachment>  = ArrayList<MailAttachment>()
            var i = 0;
            for (attach in attachList)
            {
                var file = File(save_dir+attach.account+"_"+attach.name)
                if(!file.exists())
                {
                    isDownload = false
                }
                attach.localPath = save_dir+attach.account+"_"+attach.name
                AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.update(attach)
                /*var inputStream = ByteArrayInputStream(accach.data);
                var mailAttachment = MailAttachment(accach.name,inputStream,accach.data,accach.msgId,accach.account);
                listAccath.add(mailAttachment)*/
            }
            //MailUtil.saveFile(listAccath)
          /*  val tipDialog: QMUITipDialog
            tipDialog = QMUITipDialog.Builder(AppConfig.instance)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在加载")
                    .create()*/
            if(!isDownload)
            {
                showProgressDialog(getString(R.string.Attachmentdownloading))
                /*tipDialog.show()*/
                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                emailReceiveClient
                        .imapDownloadEmailAttach(this@EmailInfoActivity, object : GetAttachCallback {
                            override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                //tipDialog.dismiss()
                                closeProgressDialog()
                                runOnUiThread {
                                    attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                                    emaiAttachAdapter = EmaiAttachAdapter(attachList)
                                    emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

                                        true
                                    }
                                    recyclerViewAttach.setLayoutManager(GridLayoutManager(AppConfig.instance, 2));
                                    recyclerViewAttach.adapter = emaiAttachAdapter
                                    emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
                                        /* var intent = Intent(activity!!, ConversationActivity::class.java)
                                         intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
                                         startActivity(intent)*/
                                    }
                                }
                            }
                            override fun gainFailure(errorMsg: String) {
                                //tipDialog.dismiss()
                                closeProgressDialog()
                                Toast.makeText(this@EmailInfoActivity, getString(R.string.Attachment_download_failed), Toast.LENGTH_SHORT).show()
                            }
                        },menu,msgId,save_dir)
            }else{
                attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                emaiAttachAdapter = EmaiAttachAdapter(attachList)
                emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

                    true
                }
                recyclerViewAttach.setLayoutManager(GridLayoutManager(this, 2));
                recyclerViewAttach.adapter = emaiAttachAdapter
                emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
                    /* var intent = Intent(activity!!, ConversationActivity::class.java)
                     intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
                     startActivity(intent)*/
                }
            }


        }

        var titleStr = intent.getStringExtra("title")
        tvTitle.text = getString(R.string.Inbox)
        attach_info.text = getString(R.string.details)
        details.visibility = View.GONE
        inboxTitle.text = emailMeaasgeData!!.subject
        var fromName = emailMeaasgeData!!.from.substring(0,emailMeaasgeData!!.from.indexOf("<"))
        var fromAdress = emailMeaasgeData!!.from.substring(emailMeaasgeData!!.from.indexOf("<"),emailMeaasgeData!!.from.length)
        title_info.text = fromName
        avatar_info.setText(fromName)
        time_info.text = DateUtil.getTimestampString(DateUtil.getDate(emailMeaasgeData!!.date), AppConfig.instance)
        fromName_Time.text = emailMeaasgeData!!.date
        attach_info.setOnClickListener {
            if(attach_info.text == getString(R.string.details))
            {
                attach_info.text = getString(R.string.Hide)
                details.visibility = View.VISIBLE
            }else{
                attach_info.text = getString(R.string.details)
                details.visibility = View.GONE
            }
        }
        fromName_From.text = fromName
        fromEmailAdress_From.text = fromAdress
        var emailConfigEntityList = ArrayList<EmailInfoData>()
        //emailConfigEntityList.add(EmailInfoData("From",fromName,fromAdress))
        if(cc!= null && cc != "" )
        {
            var ccList  =cc.split(",")
            for(ccItem in ccList)
            {
                var ccName = ccItem.substring(0,ccItem.indexOf("<"))
                var ccAdress = ccItem.substring(ccItem.indexOf("<"),ccItem.length)
                emailConfigEntityList.add(EmailInfoData("Cc",ccName,ccAdress))
            }
        }
        if(bcc!= null && bcc != "" )
        {
            var bccList  =bcc.split(",")
            for(bccItem in bccList)
            {
                var ccName = bccItem.substring(0,bccItem.indexOf("<"))
                var ccAdress = bccItem.substring(bccItem.indexOf("<"),bccItem.length)
                emailConfigEntityList.add(EmailInfoData("Bcc",ccName,ccAdress))
            }
        }
        emaiInfoAdapter = EmaiInfoAdapter(emailConfigEntityList)
        emaiInfoAdapter!!.setOnItemLongClickListener { adapter, view, position ->

            true
        }
        recyclerViewleft.adapter = emaiInfoAdapter
        emaiInfoAdapter!!.setOnItemClickListener { adapter, view, position ->
            /* var intent = Intent(activity!!, ConversationActivity::class.java)
             intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
             startActivity(intent)*/
        }




        backBtn.setOnClickListener {

            onBackPressed()
        }
        backMenu.setOnClickListener {


        }
        deleteMenu.setOnClickListener {

        }
        tvRefuse.setOnClickListener {
            var intent = Intent(this, EmailSendActivity::class.java)
            startActivity(intent)
        }
        forWardbtn.setOnClickListener {
            var intent = Intent(this, SelectEmailFriendActivity::class.java)
            startActivity(intent)
        }

        if(emailMeaasgeData!!.isSeen())
        {
            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
            emailReceiveClient
                    .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                        override fun gainSuccess(result: Boolean) {
                            //tipDialog.dismiss()
                            closeProgressDialog()
                            emailMeaasgeData!!.setIsSeen(false)
                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                        }
                        override fun gainFailure(errorMsg: String) {
                            //tipDialog.dismiss()
                            closeProgressDialog()
                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                        }
                    },menu,msgId,32,true,"")
        }

        moreMenu.setOnClickListener {

            /*list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
            list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
            list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
            var starIcon = "tabbar_attach_selected"
            var starFlag = false;
            if(emailMeaasgeData!!.isStar())
            {
                starIcon = "tabbar_stars_selected"
                starFlag = true
            }
            var menuArray = arrayListOf<String>(getString(R.string.Mark_Unread),getString(R.string.Star),getString(R.string.Node_back_up),getString(R.string.Move_to),getString(R.string.Delete))
            var iconArray = arrayListOf<String>("sheet_mark",starIcon,"statusbar_download_node","sheet_move","tabbar_deleted")
            PopWindowUtil.showPopMenuWindow(this@EmailInfoActivity, moreMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    when (position) {
                        0 -> {
                            showProgressDialog(getString(R.string.loading))
                            /*tipDialog.show()*/
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                            emailReceiveClient
                                    .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                        override fun gainSuccess(result: Boolean) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            emailMeaasgeData!!.setIsSeen(false)
                                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                                        }
                                        override fun gainFailure(errorMsg: String) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                        }
                                    },menu,msgId,32,false,"")
                        }
                        1 -> {
                            showProgressDialog(getString(R.string.loading))
                            /*tipDialog.show()*/
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                            emailReceiveClient
                                    .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                        override fun gainSuccess(result: Boolean) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            emailMeaasgeData!!.setIsStar(!starFlag)
                                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                                            if(emailMeaasgeData!!.isStar())
                                            {
                                                inboxStar.visibility =View.VISIBLE
                                            }else{
                                                inboxStar.visibility =View.GONE
                                            }
                                        }
                                        override fun gainFailure(errorMsg: String) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                        }
                                    },menu,msgId,8,!starFlag,"")
                        }
                        2 -> {

                        }
                        3 -> {
                            showMovePop()
                        }
                        4 -> {
                            showProgressDialog(getString(R.string.loading))
                            /*tipDialog.show()*/
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                            emailReceiveClient
                                    .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                        override fun gainSuccess(result: Boolean) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                        }
                                        override fun gainFailure(errorMsg: String) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                        }
                                    },menu,msgId,2,true,ConstantValue.currentEmailConfigEntity!!.deleteMenu)
                        }
                    }
                }

            })
        }
        val webSettings = webView.getSettings()
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)//加载缓存否则网络
        }

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true)//图片自动缩放 打开
        } else {
            webSettings.setLoadsImagesAutomatically(false)//图片自动缩放 关闭
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)//软件解码
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)//硬件解码
        webSettings.javaScriptEnabled = true // 设置支持javascript脚本
        webSettings.setTextSize(WebSettings.TextSize.LARGER)
//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportZoom(true)// 设置可以支持缩放
        webSettings.builtInZoomControls = true// 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false

        webSettings.displayZoomControls = false//隐藏缩放工具
        webSettings.useWideViewPort = true// 扩大比例的缩放

        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN//自适应屏幕
        webSettings.loadWithOverviewMode = true

        webSettings.databaseEnabled = true//
        webSettings.savePassword = true//保存密码
        webSettings.domStorageEnabled = true//是否开启本地DOM存储  鉴于它的安全特性（任何人都能读取到它，尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。

        webView.setSaveEnabled(true)
        webView.setKeepScreenOn(true)


        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title1: String?) {
                super.onReceivedTitle(view, title1)
                if (title1 != null) {
                    //title.text = title1
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    KLog.i("进度：" + newProgress)
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

        }
        webView.webViewClient = object  : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //view.loadUrl(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val url = Uri.parse(url)
                intent.data = url
                startActivity(intent)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                KLog.i("ddddddd")
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                KLog.i("ddddddd")
                super.onReceivedError(view, request, error)
            }
        }
        var URLText = "<html><body>"+emailMeaasgeData!!.content+"</body></html>";
        webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
    }
    fun showMovePop()
    {
        moreMenu.performClick();
    }
    fun initAttachUI()
    {

    }
    override fun setupActivityComponent() {
        DaggerEmailInfoComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailInfoModule(EmailInfoModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: EmailInfoContract.EmailInfoContractPresenter) {
        mPresenter = presenter as EmailInfoPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}