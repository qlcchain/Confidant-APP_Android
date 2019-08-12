package com.stratagile.pnrouter.ui.activity.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.observable.ImagesObservable
import com.smailnet.eamil.Callback.GetAttachCallback
import com.smailnet.eamil.Callback.MarkCallback
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.EmailAttachEntityDao
import com.stratagile.pnrouter.db.EmailConfigEntity
import com.stratagile.pnrouter.db.EmailConfigEntityDao
import com.stratagile.pnrouter.db.EmailMessageEntity
import com.stratagile.pnrouter.entity.EmailInfoData
import com.stratagile.pnrouter.entity.events.ChangEmailMessage
import com.stratagile.pnrouter.entity.events.ChangEmailStar
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailInfoComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailInfoContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailInfoModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailInfoPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiAttachAdapter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiInfoAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.email_info_view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.Serializable
import java.util.*
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
    var positionIndex = 0;
    var menu:String= "INBOX"
    var msgId = "";
    var emaiInfoAdapter : EmaiInfoAdapter? = null
    var emaiAttachAdapter : EmaiAttachAdapter? = null
    var emailConfigEntityChoose:EmailConfigEntity? = null
    var emailConfigEntityChooseList= mutableListOf<EmailConfigEntity>()
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_info_view)
    }

    override fun initData() {
        initPicPlug()
        previewImages = ArrayList()

        emailMeaasgeData = intent.getParcelableExtra("emailMeaasgeData")
        positionIndex = intent.getIntExtra("positionIndex",0)
        menu = intent.getStringExtra("menu")
        msgId = emailMeaasgeData!!.msgId
        var to = emailMeaasgeData!!.to
        var cc = emailMeaasgeData!!.cc
        var bcc = emailMeaasgeData!!.bcc
        var attachCount = emailMeaasgeData!!.attachmentCount
        emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChooseList.size > 0)
        {
            emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
        }
        var account = AppConfig.instance.emailConfig().account
        if(to.contains(account))
        {
            draft_info.text = getString(R.string.To_me)
            detail_from_From.text = getString(R.string.From)
        }else{
            draft_info.text = getString(R.string.From_me)
            detail_from_From.text = getString(R.string.To)
        }
        if(emailMeaasgeData!!.content!= "" && emailMeaasgeData!!.content.contains("confidantkey"))
        {
            lockTips.visibility = View.VISIBLE
        }else{
            lockTips.visibility = View.GONE
        }
        when(menu)
        {
            ConstantValue.currentEmailConfigEntity!!.inboxMenu->
            {
                moreMenu.visibility = View.VISIBLE

            }
            ConstantValue.currentEmailConfigEntity!!.starMenu->
            {
                moreMenu.visibility = View.VISIBLE
            }
            ConstantValue.currentEmailConfigEntity!!.drafMenu->
            {
                moreMenu.visibility = View.GONE
            }
            ConstantValue.currentEmailConfigEntity!!.sendMenu->
            {
                moreMenu.visibility = View.VISIBLE
            }
            ConstantValue.currentEmailConfigEntity!!.garbageMenu->
            {
                moreMenu.visibility = View.VISIBLE

            }
            ConstantValue.currentEmailConfigEntity!!.deleteMenu->
            {
                moreMenu.visibility = View.VISIBLE
            }
        }
        if(emailMeaasgeData!!.isStar())
        {
            inboxStar.visibility =View.VISIBLE
        }else{
            inboxStar.visibility =View.INVISIBLE
        }
        attachListParent.visibility =View.GONE
        loadingBar.visibility = View.GONE
        loadingTips.visibility = View.GONE
        if(attachCount > 0)
        {
            attachListParent.visibility =View.VISIBLE
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

                var fileName =  attach.name
                if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                    val localMedia = LocalMedia()
                    localMedia.isCompressed = false
                    localMedia.duration = 0
                    localMedia.height = 100
                    localMedia.width = 100
                    localMedia.isChecked = false
                    localMedia.isCut = false
                    localMedia.mimeType = 0
                    localMedia.num = 0
                    localMedia.path = attach.localPath
                    localMedia.pictureType = "image/jpeg"
                    localMedia.setPosition(i)
                    localMedia.sortIndex = i
                    previewImages.add(localMedia)
                    ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                }

                i++
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
                loadingBar.visibility = View.VISIBLE
                loadingTips.visibility = View.VISIBLE
                //showProgressDialog(getString(R.string.Attachmentdownloading))
                /*tipDialog.show()*/
                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                emailReceiveClient
                        .imapDownloadEmailAttach(this@EmailInfoActivity, object : GetAttachCallback {
                            override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                //tipDialog.dismiss()
                                loadingBar.visibility = View.GONE
                                loadingTips.visibility = View.GONE
                                runOnUiThread {
                                    attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                                    emaiAttachAdapter = EmaiAttachAdapter(attachList)
                                    emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

                                        true
                                    }
                                    recyclerViewAttach.setLayoutManager(GridLayoutManager(AppConfig.instance, 2));
                                    recyclerViewAttach.adapter = emaiAttachAdapter
                                    emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
                                        var emaiAttach = emaiAttachAdapter!!.getItem(position)
                                        var fileName = emaiAttach!!.name
                                        if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                                            showImagList(position)
                                        }else if(fileName.contains("mp4"))
                                        {
                                            val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                                            intent.putExtra("path", emaiAttach.localPath)
                                            startActivity(intent)
                                        }else{
                                            OpenFileUtil.getInstance(AppConfig.instance)
                                            val intent = OpenFileUtil.openFile(emaiAttach.localPath)
                                            startActivity(intent)
                                        }
                                    }
                                }
                            }
                            override fun gainFailure(errorMsg: String) {
                                //tipDialog.dismiss()
                                //closeProgressDialog()
                                loadingBar.visibility = View.GONE
                                loadingTips.visibility = View.GONE
                                Toast.makeText(this@EmailInfoActivity, getString(R.string.Attachment_download_failed), Toast.LENGTH_SHORT).show()
                            }
                        },menu,msgId,save_dir,emailMeaasgeData!!.aesKey)
            }else{
                attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                emaiAttachAdapter = EmaiAttachAdapter(attachList)
                emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

                    true
                }
                recyclerViewAttach.setLayoutManager(GridLayoutManager(this, 2));
                recyclerViewAttach.adapter = emaiAttachAdapter
                emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
                    var emaiAttach = emaiAttachAdapter!!.getItem(position)
                    var fileName = emaiAttach!!.name
                    if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                        showImagList(position)
                    }else if(fileName.contains("mp4"))
                    {
                        val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                        intent.putExtra("path", emaiAttach.localPath)
                        startActivity(intent)
                    }else{
                        OpenFileUtil.getInstance(AppConfig.instance)
                        val intent = OpenFileUtil.openFile(emaiAttach.localPath)
                        startActivity(intent)
                    }
                }
            }


        }

        var titleStr = intent.getStringExtra("title")

        when(menu)
        {
            ConstantValue.currentEmailConfigEntity!!.inboxMenu->
            {
                tvTitle.text = getString(R.string.Inbox)

            }
            ConstantValue.currentEmailConfigEntity!!.starMenu->
            {
                tvTitle.text = getString(R.string.Starred)
            }
            ConstantValue.currentEmailConfigEntity!!.drafMenu->
            {
                tvTitle.text = getString(R.string.Drafts)
            }
            ConstantValue.currentEmailConfigEntity!!.sendMenu->
            {
                tvTitle.text = getString(R.string.Sent)
            }
            ConstantValue.currentEmailConfigEntity!!.garbageMenu->
            {
                tvTitle.text = getString(R.string.Spam)
            }
            ConstantValue.currentEmailConfigEntity!!.deleteMenu->
            {
                tvTitle.text = getString(R.string.Trash)
            }
        }
        attach_info.text = getString(R.string.details)
        details.visibility = View.GONE
        inboxTitle.text = emailMeaasgeData!!.subject
        var fromName = ""
        var fromAdress = ""
        if(emailMeaasgeData!!.from.indexOf("<") >-1)
        {
            fromName = emailMeaasgeData!!.from.substring(0,emailMeaasgeData!!.from.indexOf("<"))
            fromAdress = emailMeaasgeData!!.from.substring(emailMeaasgeData!!.from.indexOf("<"),emailMeaasgeData!!.from.length)
        }else{
            fromName = emailMeaasgeData!!.from.substring(0,emailMeaasgeData!!.from.indexOf("@"))
            fromAdress = emailMeaasgeData!!.from.substring(0,emailMeaasgeData!!.from.length)
        }
        var toName = ""
        var toAdress = ""
        if(emailMeaasgeData!!.to.indexOf("<") >-1)
        {
            toName = emailMeaasgeData!!.to.substring(0,emailMeaasgeData!!.to.indexOf("<"))
            toAdress = emailMeaasgeData!!.to.substring(emailMeaasgeData!!.to.indexOf("<"),emailMeaasgeData!!.to.length)
        }else{
            toName = emailMeaasgeData!!.to.substring(0,emailMeaasgeData!!.to.indexOf("@"))
            toAdress = emailMeaasgeData!!.to.substring(0,emailMeaasgeData!!.to.length)
        }
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
        if(to.contains(account))
        {
            fromName_From.text = fromName
            fromEmailAdress_From.text = fromAdress
        }else{
            fromName_From.text = toName
            fromEmailAdress_From.text = toAdress
        }
        var emailConfigEntityList = ArrayList<EmailInfoData>()
        //emailConfigEntityList.add(EmailInfoData("From",fromName,fromAdress))
        if(cc!= null && cc != "" )
        {
            var ccList  = cc.split(",")
            for(ccItem in ccList)
            {
                var ccName = ""
                var ccAdress = ""
                if(ccItem.indexOf("<") > -1)
                {
                    ccName = ccItem.substring(0,ccItem.indexOf("<"))
                    ccAdress = ccItem.substring(ccItem.indexOf("<"),ccItem.length)
                }else{
                    ccName = ccItem.substring(0,ccItem.indexOf("@"))
                    ccAdress = ccItem.substring(0,ccItem.length)
                }
                emailConfigEntityList.add(EmailInfoData("Cc",ccName,ccAdress))
            }
        }
        if(bcc!= null && bcc != "" )
        {
            var bccList  =bcc.split(",")
            for(bccItem in bccList)
            {
                var ccName = ""
                var ccAdress = ""
                if(bccItem.indexOf("<") > -1)
                {
                    ccName = bccItem.substring(0,bccItem.indexOf("<"))
                    ccAdress = bccItem.substring(bccItem.indexOf("<"),bccItem.length)
                }else{
                    ccName = bccItem.substring(0,bccItem.indexOf("@"))
                    ccAdress = bccItem.substring(0,bccItem.length)
                }
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
            showProgressDialog(getString(R.string.waiting))
            deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2)
        }
        tvRefuse.setOnClickListener {
            var intent = Intent(this, EmailSendActivity::class.java)
            intent.putExtra("flag",1)
            intent.putExtra("attach",0)
            intent.putExtra("menu",menu)
            intent.putExtra("emailMeaasgeInfoData", emailMeaasgeData)
            startActivity(intent)
        }
        forWardbtn.setOnClickListener {
            if(emailMeaasgeData!!.attachmentCount >0)
            {
                showDialog()
            }else{
                var intent = Intent(this, EmailSendActivity::class.java)
                intent.putExtra("flag",1)
                intent.putExtra("foward",1)
                intent.putExtra("menu",menu)
                intent.putExtra("emailMeaasgeInfoData", emailMeaasgeData)
                startActivity(intent)
            }

        }

        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
        emailReceiveClient
                .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                    override fun gainSuccess(result: Boolean) {

                    }
                    override fun gainFailure(errorMsg: String) {

                    }
                },menu,msgId,32,true,"")


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
            var menuArray = arrayListOf<String>()
            var iconArray = arrayListOf<String>()
            when(menu)
            {
                ConstantValue.currentEmailConfigEntity!!.inboxMenu->
                {
                    menuArray = arrayListOf<String>(getString(R.string.Mark_Unread),getString(R.string.Star),getString(R.string.Node_back_up),getString(R.string.Move_to),getString(R.string.Delete))
                    iconArray = arrayListOf<String>("sheet_mark",starIcon,"statusbar_download_node","sheet_move","statusbar_delete")

                }
                ConstantValue.currentEmailConfigEntity!!.starMenu->
                {
                    menuArray = arrayListOf<String>(getString(R.string.Star))
                    iconArray = arrayListOf<String>(starIcon)
                }
                ConstantValue.currentEmailConfigEntity!!.sendMenu->
                {
                    menuArray = arrayListOf<String>(getString(R.string.Star),getString(R.string.Delete))
                    iconArray = arrayListOf<String>(starIcon,"statusbar_delete")
                }
                ConstantValue.currentEmailConfigEntity!!.garbageMenu->
                {
                    menuArray = arrayListOf<String>(getString(R.string.Mark_Unread),getString(R.string.Star),getString(R.string.Node_back_up),getString(R.string.Move_to),getString(R.string.Delete))
                    iconArray = arrayListOf<String>("sheet_mark",starIcon,"statusbar_download_node","sheet_move","statusbar_delete")
                }
                ConstantValue.currentEmailConfigEntity!!.deleteMenu->
                {
                    menuArray = arrayListOf<String>(getString(R.string.Mark_Unread),getString(R.string.Star),getString(R.string.Node_back_up),getString(R.string.Move_to))
                    iconArray = arrayListOf<String>("sheet_mark",starIcon,"statusbar_download_node","sheet_move")
                }
            }
            PopWindowUtil.showPopMenuWindow(this@EmailInfoActivity, moreMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.name) {
                        "Mark Unread" -> {
                            showProgressDialog(getString(R.string.waiting))
                            /*tipDialog.show()*/
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                            emailReceiveClient
                                    .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                        override fun gainSuccess(result: Boolean) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            emailMeaasgeData!!.setIsSeen(false)
                                            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                                            var test = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                            EventBus.getDefault().post(ChangEmailMessage(positionIndex,0))
                                        }
                                        override fun gainFailure(errorMsg: String) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                        }
                                    },menu,msgId,32,false,"")
                        }
                        "Star" -> {
                            showProgressDialog(getString(R.string.waiting))
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
                                                EventBus.getDefault().post(ChangEmailStar(positionIndex,1))
                                            }else{
                                                inboxStar.visibility =View.INVISIBLE
                                                EventBus.getDefault().post(ChangEmailStar(positionIndex,0))
                                            }

                                        }
                                        override fun gainFailure(errorMsg: String) {
                                            //tipDialog.dismiss()
                                            closeProgressDialog()
                                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                        }
                                    },menu,msgId,8,!starFlag,"")
                        }
                        "" -> {

                        }
                        "Move to" -> {
                            showMovePop()
                        }
                        "Delete" -> {
                            showProgressDialog(getString(R.string.waiting))
                            deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2)
                        }
                    }
                }

            })
        }
        var needOp = false
        if( emailMeaasgeData!!.content != null && emailMeaasgeData!!.content.contains("<img"))
        {
            needOp = true
        }
        if(emailMeaasgeData!!.originalText!= null && emailMeaasgeData!!.originalText.contains("<img"))
        {
            needOp = true;
        }
        if(needOp)
        {
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
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
            webSettings.javaScriptEnabled = true // 设置支持javascript脚本
            //webSettings.setTextSize(WebSettings.TextSize.LARGEST)
//        webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setSupportZoom(true)// 设置可以支持缩放
            webSettings.builtInZoomControls = true// 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false

            webSettings.displayZoomControls = false//隐藏缩放工具
            webSettings.useWideViewPort = true// 扩大比例的缩放

            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN//自适应屏幕
            webSettings.loadWithOverviewMode = true

            /* webSettings.databaseEnabled = true//
             webSettings.savePassword = true//保存密码
             webSettings.domStorageEnabled = true//是否开启本地DOM存储  鉴于它的安全特性（任何人都能读取到它，尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。

             webView.setSaveEnabled(true)
             webView.setKeepScreenOn(true)*/
        }


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
        var URLText = "";
        Log.i("URLText",emailMeaasgeData!!.content)
        if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
        {
            URLText = "<html><body style ='font-size:16px!important;'>"+emailMeaasgeData!!.originalText+"</body></html>";
            webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
        }else{
            URLText = "<html><body style ='font-size:16px!important;'><div style ='overflow-wrap: break-word;width: 100%;'>"+emailMeaasgeData!!.content+"</div></body></html>";
            webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
        }
        /* try {
             if(emailMeaasgeData!!.content.contains("confidantKey") || emailMeaasgeData!!.content.contains("confidantkey"))
             {

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
                 val sourceContent = String(miContent)

                 emailMeaasgeData!!.content = sourceContent;
                 URLText = "<html><body>"+sourceContent+"</body></html>";
                 webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
             }else{
                 webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
             }
         }catch (e:Exception)
         {
             e.printStackTrace();
             webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
         }*/




    }
    fun showDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setCancelText(getString(R.string.no))
                .setConfirmText(getString(R.string.yes))
                .setContentText(getString(R.string.Send_attachments))
                .setConfirmClickListener {
                    var intent = Intent(this, EmailSendActivity::class.java)
                    intent.putExtra("flag",1)
                    intent.putExtra("foward",1)
                    intent.putExtra("attach",1)
                    intent.putExtra("menu",menu)
                    intent.putExtra("emailMeaasgeInfoData", emailMeaasgeData)
                    startActivity(intent)
                }.setCancelClickListener {
                    var intent = Intent(this, EmailSendActivity::class.java)
                    intent.putExtra("flag",1)
                    intent.putExtra("foward",1)
                    intent.putExtra("menu",menu)
                    intent.putExtra("emailMeaasgeInfoData", emailMeaasgeData)
                    startActivity(intent)
                }
                .show()

    }
    /**
     * select local image
     * //todo
     */
    protected fun initPicPlug() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
    }
    fun showImagList(showIndex:Int)
    {
        val selectedImages = ArrayList<LocalMedia>()
        val previewImages = ImagesObservable.getInstance().readLocalMedias("chat")
        if (previewImages != null && previewImages.size > 0) {

            val intentPicturePreviewActivity = Intent(this, PicturePreviewActivity::class.java)
            val bundle = Bundle()
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages as Serializable)
            bundle.putInt(PictureConfig.EXTRA_POSITION, showIndex)
            bundle.putString("from", "chat")
            intentPicturePreviewActivity.putExtras(bundle)
            startActivity(intentPicturePreviewActivity)
        }
    }
    fun deleteAndMoveEmailSend(menuTo:String,flag:Int)
    {
        /*tipDialog.show()*/
        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
        emailReceiveClient
                .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                    override fun gainSuccess(result: Boolean) {
                        //tipDialog.dismiss()
                        closeProgressDialog()
                        if(result)
                        {
                            deleteEmail()
                            finish()
                        }else{
                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun gainFailure(errorMsg: String) {
                        //tipDialog.dismiss()
                        closeProgressDialog()
                        Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                    }
                },menu,msgId,flag,true,menuTo)
    }
    fun deleteEmail()
    {
        AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.delete(emailMeaasgeData)
        EventBus.getDefault().post(ChangEmailMessage(positionIndex,1))
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
    fun showMovePop()
    {
        var title = getString(R.string.Move_to)
        var starIcon = "tabbar_attach_selected"
        var menuArray = arrayListOf<String>()
        var iconArray = arrayListOf<String>()

        when(menu)
        {
            ConstantValue.currentEmailConfigEntity!!.inboxMenu->
            {
                menuArray = arrayListOf<String>(getString(R.string.Spam),getString(R.string.Trash))
                iconArray = arrayListOf<String>("tabbar_trash","tabbar_deleted")
            }
            ConstantValue.currentEmailConfigEntity!!.garbageMenu->
            {
                menuArray = arrayListOf<String>(getString(R.string.Inbox),getString(R.string.Trash))
                iconArray = arrayListOf<String>("tabbar_inbox","tabbar_deleted")
            }
            ConstantValue.currentEmailConfigEntity!!.deleteMenu->
            {
                menuArray = arrayListOf<String>(getString(R.string.Inbox),getString(R.string.Spam))
                iconArray = arrayListOf<String>("tabbar_inbox","tabbar_trash")
            }
        }
        PopWindowUtil.showPopMoveMenuWindow(this@EmailInfoActivity, moreMenu,title,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
            override fun onSelect(position: Int, obj: Any) {
                KLog.i("" + position)
                var data = obj as FileOpreateType
                when (data.name) {
                    "Inbox" -> {
                        showProgressDialog(getString(R.string.waiting))
                        deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.inboxMenu,2)
                    }
                    "Spam" -> {
                        showProgressDialog(getString(R.string.waiting))
                        deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.garbageMenu,2)
                    }
                    "Trash" -> {
                        showProgressDialog(getString(R.string.waiting))
                        deleteAndMoveEmailSend(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2)
                    }


                }
            }

        })
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
    fun loadJS(){
        webView.loadUrl("javascript:(function(){"
                //将DIV元素中的外边距和内边距设置为零，防止网页左右有空隙
                + " var divs = document.getElementsByTagName(\"div\");"
                + " for(var j=0;j<divs.length;j++){"
                + "   divs[j].style.margin=\"0px\";"
                + "   divs[j].style.padding=\"0px\";"
                + "   divs[j].style.width=document.body.clientWidth-10;"
                + " }"

                + " var imgs = document.getElementsByTagName(\"img\"); "
                + "   for(var i=0;i<imgs.length;i++)  "
                + "       {"
                //过滤掉GIF图片，防止过度放大后，GIF失真
                + "    var vkeyWords=/.gif$/;"
                + "        if(!vkeyWords.test(imgs[i].src)){"
                + "         var hRatio="+getScreenWidthPX()+"/objs[i].width;"
                + "         objs[i].height= objs[i].height*hRatio;"//通过缩放比例来设置图片的高度
                + "         objs[i].width="+getScreenWidthPX()+";"//设置图片的宽度
                + "        }"
                +    "}"
                +   "})()");
    }
    /**
     * WebView Setting
     */
    fun initWebSettings(){
        var webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

    }
    /**
     * 获取屏幕的宽度（单位：像素PX）
     * @return
     */
    fun getScreenWidthPX(): Int{
        var wm =  this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var dm = DisplayMetrics();
        if (wm != null && wm.getDefaultDisplay() != null){
            wm.getDefaultDisplay().getMetrics(dm);
            return px2dip(dm.widthPixels.toFloat());
        }else {
            return 0;
        }
    }
    /**
     * 像素转DP
     * @param pxValue
     * @return
     */
    fun px2dip(pxValue: Float): Int {
        val scale = this.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}