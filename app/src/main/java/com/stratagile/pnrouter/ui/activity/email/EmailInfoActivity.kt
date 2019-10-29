package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.webkit.*
import android.widget.PopupWindow
import android.widget.TextView
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
import com.pawegio.kandroid.toast
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import com.smailnet.eamil.Callback.GetAttachCallback
import com.smailnet.eamil.Callback.MarkCallback
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.MailAttachment
import com.smailnet.eamil.Utils.AESCipher
import com.smailnet.eamil.Utils.AESToolsCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ChangEmailMessage
import com.stratagile.pnrouter.entity.events.ChangEmailStar
import com.stratagile.pnrouter.entity.events.ChangeEmailConfig
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.gmail.GmailQuickstart
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailInfoComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailInfoContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailInfoModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailInfoPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiAttachAdapter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiInfoAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.email_info_view.*
import kotlinx.android.synthetic.main.emailpassword_bar3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

class EmailInfoActivity : BaseActivity(), EmailInfoContract.View , PNRouterServiceMessageReceiver.BakupEmailCallback,PNRouterServiceMessageReceiver.BakMailsCheckCallback,PNRouterServiceMessageReceiver.DelEmailCallback{
    override fun DelEmailBack(JDelEmailRsp: JDelEmailRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        if(JDelEmailRsp.params.retCode == 0)
        {
            EventBus.getDefault().post(ChangEmailMessage(positionIndex,1))
            runOnUiThread {
                finish()
            }
        }else{

            toast(R.string.fail)
        }

    }

    override fun BakMailsCheckBack(JBakMailsCheckRsp: JBakMailsCheckRsp) {
        if(JBakMailsCheckRsp.params.retCode == 0)
        {
            isBackEd = JBakMailsCheckRsp.params.result
            runOnUiThread {
                var drawable = getResources().getDrawable(R.mipmap.statusbar_download_node_backups)
                drawable.setBounds(0, 0, 72, 72);
                backMenu.setCompoundDrawables(drawable, null, null,null);
            }
        }
    }


    @Inject
    internal lateinit var mPresenter: EmailInfoPresenter
    var isBackEd = 0
    var emailMeaasgeData:EmailMessageEntity? = null
    var positionIndex = 0;
    var menu:String= "INBOX"
    var msgId = "";
    var emaiInfoAdapter : EmaiInfoAdapter? = null
    var emaiAttachAdapter : EmaiAttachAdapter? = null
    var emailConfigEntityChoose:EmailConfigEntity? = null
    var emailConfigEntityChooseList= mutableListOf<EmailConfigEntity>()
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    var isScaleInit = false
    var newScaleInit = 0f
    var webViewScroll = false;
    var contentHtml = "";
    var zipSavePath =""
    var zipSavePathTemp =""
    var zipFileSoucePath:MutableList<String> = ArrayList()
    var zipCompressTask: ZipCompressTask? = null
    var zipUnTask:ZipUnTask? = null
    var needWaitAttach =false
    var needWaitCid =false
    var fileAESKey = ""
    var mailInfo = EmailInfo()
    var attachListEntityNode =  arrayListOf<EmailAttachEntity>()
    var msgID = 0
    var needOp = false
    var this_:Activity? = null;
    var webviewContentWidth = 0
    var hasPassWord = false;//是否已经获取密码
    var newconfidantpassOp = true;
    var userPassWord ="";
    var clickDecryptBtn = false
    var isShow = false
    private var mNormalPopup: QMUIPopup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        this_ = this
        hasPassWord = false
        clickDecryptBtn = false
        super.onCreate(savedInstanceState)
    }
    override fun BakupEmailBack(jBakupEmailRsp: JBakupEmailRsp) {

        if(jBakupEmailRsp.params.retCode == 0)
        {
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.success)
            }

        }else if(jBakupEmailRsp.params.retCode == 1)
        {
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.It_already_exists)
            }
        }else{
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.fail)
            }
        }
    }
    override fun initView() {
        setContentView(R.layout.email_info_view)
    }

    override fun initData() {
        if(!clickDecryptBtn)
        {
            AppConfig.instance.messageReceiver!!.bakupEmailCallback = this
            AppConfig.instance.messageReceiver!!.bakMailsCheckCallback = this
            AppConfig.instance.messageReceiver!!.dlEmailCallback = this
            EventBus.getDefault().register(this)
            initPicPlug()
            emailMeaasgeData = intent.getParcelableExtra("emailMeaasgeData")
        }
        isScaleInit = false
        webViewScroll = false
        previewImages = ArrayList()
        zipFileSoucePath = ArrayList()
        if(userPassWord != "")
        {
            var fileKey = userPassWord
            if(userPassWord != "")
            {
                var len = userPassWord.length
                if(len > 16)
                {
                    userPassWord = userPassWord.substring(0,16)
                }else if(len < 16)
                {
                    var need = 16- len;
                    for (index in 1..need){
                        userPassWord += "0"
                    }
                }
                fileKey = userPassWord;
            }

            var miTxtEndAllIndex  = emailMeaasgeData!!.content.indexOf("newconfidantpass")
            var miTxtEndAllTxt = emailMeaasgeData!!.content.substring(0,miTxtEndAllIndex)
            var miTxtEndIndex = miTxtEndAllTxt.indexOf("<")
            var miTxtEnd = miTxtEndAllTxt.substring(0,miTxtEndIndex)
            var miContentSoucreBase = RxEncodeTool.base64Decode(miTxtEnd)
            val miContent = AESCipher.aesDecryptBytes(miContentSoucreBase, fileKey.toByteArray())
            var sourceContent = ""
            try{
                sourceContent = String(miContent)
                emailMeaasgeData!!.aesKey = fileKey
                emailMeaasgeData!!.originalText = sourceContent
            }catch (e:Exception)
            {
                inputPassWordParent.visibility = View.VISIBLE
                toast(R.string.Decryption_failed)
               return
            }
        }
        if(emailMeaasgeData!!.content.contains("newconfidantpass") && !hasPassWord)
        {

            var miTxtEndAllIndex  = emailMeaasgeData!!.content.indexOf("newconfidantpass")
            var newconfidantpassAllText = emailMeaasgeData!!.content.substring(miTxtEndAllIndex,emailMeaasgeData!!.content.length)
            var newconfidantpassTextEndIndex = newconfidantpassAllText.indexOf("\"");
            var newconfidantpassText = newconfidantpassAllText.substring("newconfidantpass".length,newconfidantpassTextEndIndex);
            if(newconfidantpassText !="")
            {
                passwordTips.text = getString(R.string.PasswordHint)+newconfidantpassText;
                passwordTips.visibility = View.VISIBLE;
            }else{
                passwordTips.visibility = View.GONE;
            }
            backMenu.visibility = View.GONE
            moreMenu.visibility = View.GONE
            jiemiRoot.visibility = View.VISIBLE
            inputPassWordParent.visibility = View.GONE
            webViewParent.visibility = View.GONE
            llOperate.visibility = View.GONE
            attachListParent.visibility = View.GONE
            newconfidantpassOp = false;
        }else{
            backMenu.visibility = View.VISIBLE
            moreMenu.visibility = View.VISIBLE
            webViewParent.visibility = View.VISIBLE
            llOperate.visibility = View.VISIBLE
            jiemiRoot.visibility = View.GONE
            inputPassWordParent.visibility = View.GONE
            hasPassWord = true;
        }
        var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
        var uuid = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId
        var saveEmailConf = BakMailsCheck(accountBase64,uuid)
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
        positionIndex = intent.getIntExtra("positionIndex",0)
        menu = intent.getStringExtra("menu")
        if(menu == "node")
        {
            moreMenu.visibility = View.GONE
            backMenu.visibility =  View.GONE
        }else{
            if(hasPassWord)
            {
                moreMenu.visibility = View.VISIBLE
                backMenu.visibility =  View.VISIBLE
            }
        }
        zipSavePathTemp = emailMeaasgeData!!.account+"_"+ menu + "_"+ emailMeaasgeData!!.msgId
        msgId = emailMeaasgeData!!.msgId
        var to = emailMeaasgeData!!.to
        var cc = emailMeaasgeData!!.cc
        var bcc = emailMeaasgeData!!.bcc
        var isContainerAttachment = emailMeaasgeData!!.isContainerAttachment()
        var isContainerCid = false
        if(emailMeaasgeData!!.originalText!= null && emailMeaasgeData!!.originalText!="")
        {
            isContainerCid = isCidImgAndReplace(emailMeaasgeData!!.originalText)
        }else{
            if(emailMeaasgeData!!.content!= null && emailMeaasgeData!!.content!="")
            {
                isContainerCid = isCidImgAndReplace(emailMeaasgeData!!.content)
            }
        }
        mailInfo.attchCount = emailMeaasgeData!!.attachmentCount
        mailInfo.subTitle = emailMeaasgeData!!.subject
        emailConfigEntityChooseList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChooseList.size > 0)
        {
            emailConfigEntityChoose = emailConfigEntityChooseList.get(0)
        }
        var account = AppConfig.instance.emailConfig().account


        if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
        {
            lockTips.visibility = View.VISIBLE
        }else{
            lockTips.visibility = View.GONE
        }
        when(menu)
        {
            ConstantValue.currentEmailConfigEntity!!.inboxMenu->
            {
                if(hasPassWord)
                {
                    moreMenu.visibility = View.VISIBLE
                }


            }
            ConstantValue.currentEmailConfigEntity!!.starMenu->
            {
                if(hasPassWord)
                {
                    moreMenu.visibility = View.VISIBLE
                }

            }
            ConstantValue.currentEmailConfigEntity!!.drafMenu->
            {
                moreMenu.visibility = View.GONE
            }
            ConstantValue.currentEmailConfigEntity!!.sendMenu->
            {
                if(hasPassWord)
                {
                    moreMenu.visibility = View.VISIBLE
                }

            }
            ConstantValue.currentEmailConfigEntity!!.garbageMenu->
            {
                if(hasPassWord)
                {
                    moreMenu.visibility = View.VISIBLE
                }


            }
            ConstantValue.currentEmailConfigEntity!!.deleteMenu->
            {
                if(hasPassWord)
                {
                    moreMenu.visibility = View.VISIBLE
                }

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
        if(menu != "node")
        {
            if(isContainerAttachment)
            {
                if(hasPassWord)
                {
                    attachListParent.visibility =View.VISIBLE
                }
                val save_dir = PathUtils.getInstance().filePath.toString() + "/"
                var addMenu = false
                var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                if(attachList.size == 0)
                {
                    addMenu = true
                    attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                }
                var isDownload = true
                var listAccath:ArrayList<MailAttachment>  = ArrayList<MailAttachment>()
                var i = 0;
                if(attachList.size > 0)
                {
                    for (attach in attachList)
                    {
                        var savePath = save_dir+attach.account+"_"+attach.msgId+"_"+attach.name
                        if(addMenu)
                        {
                            savePath = save_dir+attach.account+"_"+emailMeaasgeData!!.menu+"_"+attach.msgId+"_"+attach.name
                        }
                        var file = File(savePath)
                        if(!file.exists())
                        {
                            isDownload = false
                            needWaitAttach = true
                        }
                        attach.localPath = savePath
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
                }else{
                    isDownload = false
                    needWaitAttach = true
                }
                //MailUtil.saveFile(listAccath)
                /*  val tipDialog: QMUITipDialog
                  tipDialog = QMUITipDialog.Builder(AppConfig.instance)
                          .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                          .setTipWord("正在加载")
                          .create()*/
                if(hasPassWord)
                {
                    if(!isDownload)
                    {
                        loadingBar.visibility = View.VISIBLE
                        loadingTips.visibility = View.VISIBLE
                        //showProgressDialog(getString(R.string.Attachmentdownloading))
                        /*tipDialog.show()*/
                        if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
                        {
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                            emailReceiveClient
                                    .imapDownloadEmailAttach(this@EmailInfoActivity, object : GetAttachCallback {
                                        override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                            //tipDialog.dismiss()
                                            loadingBar.visibility = View.GONE
                                            loadingTips.visibility = View.GONE
                                            needWaitAttach = false
                                            runOnUiThread {
                                                var iFlag = 0;
                                                for (attachItem in messageList)
                                                {
                                                    var attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                    if(attachListTemp.size == 0)
                                                    {
                                                        attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

                                                    }
                                                    if(attachListTemp == null || attachListTemp.size == 0)
                                                    {
                                                        var eamilAttach = EmailAttachEntity()
                                                        eamilAttach.account = AppConfig.instance.emailConfig().account
                                                        eamilAttach.msgId = emailMeaasgeData!!.menu+"_"+msgId
                                                        eamilAttach.name = attachItem.name
                                                        eamilAttach.data = attachItem.byt
                                                        eamilAttach.hasData = true
                                                        eamilAttach.isCanDelete = false
                                                        var savePath = save_dir+eamilAttach.account+"_"+eamilAttach.msgId+"_"+eamilAttach.name
                                                        eamilAttach.localPath = savePath
                                                        AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)

                                                        var fileName =  eamilAttach.name
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
                                                            localMedia.path = eamilAttach.localPath
                                                            localMedia.pictureType = "image/jpeg"
                                                            localMedia.setPosition(iFlag)
                                                            localMedia.sortIndex = iFlag
                                                            previewImages.add(localMedia)
                                                            ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                                                        }

                                                        iFlag++
                                                    }
                                                }
                                                attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                                                if(attachList.size == 0)
                                                {
                                                    attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

                                                }
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
                            var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
                            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                            emailReceiveClient
                                    .gmailDownloadEmailAttach(this@EmailInfoActivity, object : GetAttachCallback {
                                        override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                            //tipDialog.dismiss()
                                            loadingBar.visibility = View.GONE
                                            loadingTips.visibility = View.GONE
                                            needWaitAttach = false
                                            runOnUiThread {
                                                var iFlag = 0;
                                                for (attachItem in messageList)
                                                {
                                                    var attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId),EmailAttachEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                    if(attachListTemp.size == 0)
                                                    {
                                                        attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

                                                    }
                                                    if(attachListTemp == null || attachListTemp.size == 0)
                                                    {
                                                        var eamilAttach = EmailAttachEntity()
                                                        eamilAttach.account = AppConfig.instance.emailConfig().account
                                                        eamilAttach.msgId = emailMeaasgeData!!.menu+"_"+msgId
                                                        eamilAttach.name = attachItem.name
                                                        eamilAttach.data = attachItem.byt
                                                        eamilAttach.hasData = true
                                                        eamilAttach.isCanDelete = false
                                                        var savePath = save_dir+eamilAttach.account+"_"+eamilAttach.msgId+"_"+eamilAttach.name
                                                        eamilAttach.localPath = savePath
                                                        AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.insert(eamilAttach)

                                                        var fileName =  eamilAttach.name
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
                                                            localMedia.path = eamilAttach.localPath
                                                            localMedia.pictureType = "image/jpeg"
                                                            localMedia.setPosition(iFlag)
                                                            localMedia.sortIndex = iFlag
                                                            previewImages.add(localMedia)
                                                            ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                                                        }

                                                        iFlag++
                                                    }
                                                }
                                                attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                                                if(attachList.size == 0)
                                                {
                                                    attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

                                                }
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
                                    },menu,msgId,save_dir,emailMeaasgeData!!.aesKey,gmailService,"me")
                        }

                    }else{
                        attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                        if(attachList.size == 0)
                        {
                            attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()
                        }
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



            }
            if(isContainerCid && hasPassWord)
            {
                val save_dir = PathUtils.getInstance().filePath.toString() + "/"
                var addMenu = false
                var citList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                if(citList.size == 0)
                {
                    addMenu = true
                    citList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()
                }
                var isDownload = true
                if(citList.size > 0)
                {
                    for (attach in citList)
                    {
                        var savePath = save_dir+attach.account+"_"+attach.msgId+"_"+attach.name
                        if(addMenu)
                        {
                            savePath = save_dir+attach.account+"_"+emailMeaasgeData!!.menu+"_"+attach.msgId+"_"+attach.name
                        }
                        var file = File(savePath)
                        if(!file.exists())
                        {
                            isDownload = false
                            needWaitCid = true
                        }
                        attach.localPath = savePath
                        AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.update(attach)
                    }
                }else{
                    isDownload = false
                    needWaitCid = true
                }
                if(!isDownload)
                {
                    if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
                    {
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .imapDownloadEmailCid(this@EmailInfoActivity, object : GetAttachCallback {
                                    override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                        needWaitCid = false
                                        runOnUiThread {
                                            var iFlag = 0;
                                            for (attachItem in messageList)
                                            {
                                                var attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId),EmailCidEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachListTemp.size == 0)
                                                {
                                                    attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()

                                                }
                                                if(attachListTemp == null || attachListTemp.size == 0)
                                                {
                                                    var eamilCid = EmailCidEntity()
                                                    eamilCid.account = AppConfig.instance.emailConfig().account
                                                    eamilCid.msgId = emailMeaasgeData!!.menu+"_"+msgId
                                                    eamilCid.name = attachItem.name
                                                    eamilCid.cid = attachItem.cid
                                                    eamilCid.data = attachItem.byt
                                                    eamilCid.hasData = true
                                                    eamilCid.isCanDelete = false
                                                    var savePath = save_dir+eamilCid.account+"_"+eamilCid.msgId+"_"+eamilCid.name
                                                    eamilCid.localPath = savePath
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.insert(eamilCid)
                                                }
                                            }
                                            var cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                                            if(cidList.size == 0)
                                            {
                                                cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()

                                            }
                                            for (cidItem in cidList)
                                            {
                                                val save_dir = PathUtils.getInstance().filePath.toString() + "/"
                                                var savePath = save_dir+ AppConfig.instance.emailConfig().account+"_"+emailMeaasgeData!!.menu+"_"+msgId+"_"+cidItem.name
                                                if(cidItem.cid == null && cidItem.cid =="")
                                                {
                                                    cidItem.cid = cidItem.name
                                                }
                                                if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
                                                {
                                                    emailMeaasgeData!!.originalText = replaceLocalPathByImgCid(emailMeaasgeData!!.originalText ,cidItem.cid ,savePath )
                                                }else{
                                                    emailMeaasgeData!!.content = replaceLocalPathByImgCid(emailMeaasgeData!!.content ,cidItem.cid ,savePath )
                                                }

                                            }
                                            if(menu != "node")
                                            {
                                                var headStr = "<head><style>body {font-family: Helvetica;font-size: 16px;word-wrap: break-word;-webkit-text-size-adjust:none;-webkit-nbsp-mode: space;}pre {white-space: pre-wrap;}</style></head>"
                                                var iframeStr = "<iframe src='x-mailcore-msgviewloaded:' style='width: 0px; height: 0px; border: none;'></iframe>"
                                                iframeStr = ""
                                                var URLText = "";
                                                if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
                                                {
                                                    var originalTextCun = StringUitl.StripHT(emailMeaasgeData!!.originalText)
                                                    if(originalTextCun.length > 50)
                                                    {
                                                        originalTextCun = originalTextCun.substring(0,50)
                                                    }
                                                    mailInfo.content = originalTextCun
                                                    URLText = "<html>"+headStr+emailMeaasgeData!!.originalText+"</body>"+iframeStr+"</html>";
                                                    contentHtml = URLText
                                                    webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
                                                }else{
                                                    var contentText =  emailMeaasgeData!!.contentText
                                                    if(contentText.length > 50)
                                                    {
                                                        contentText = contentText.substring(0,50)
                                                    }
                                                    mailInfo.content = contentText
                                                    URLText = "<html>"+headStr+emailMeaasgeData!!.content+"</div></body>"+iframeStr+"</html>";
                                                    contentHtml = URLText
                                                    webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
                                                }
                                            }
                                        }
                                    }
                                    override fun gainFailure(errorMsg: String) {
                                        //tipDialog.dismiss()
                                        //closeProgressDialog()
                                        Toast.makeText(this@EmailInfoActivity, getString(R.string.picture_download_failed), Toast.LENGTH_SHORT).show()
                                    }
                                },menu,msgId,save_dir,emailMeaasgeData!!.aesKey)
                    }else{
                        var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .gmailDownloadEmailCid(this@EmailInfoActivity, object : GetAttachCallback {
                                    override fun gainSuccess(messageList: List<MailAttachment>, count: Int) {
                                        needWaitCid = false
                                        runOnUiThread {
                                            var iFlag = 0;
                                            for (attachItem in messageList)
                                            {
                                                var attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId),EmailCidEntityDao.Properties.Name.eq(attachItem.name)).list()
                                                if(attachListTemp.size == 0)
                                                {
                                                    attachListTemp =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()

                                                }
                                                if(attachListTemp == null || attachListTemp.size == 0)
                                                {
                                                    var eamilCid = EmailCidEntity()
                                                    eamilCid.account = AppConfig.instance.emailConfig().account
                                                    eamilCid.msgId = emailMeaasgeData!!.menu+"_"+msgId
                                                    eamilCid.name = attachItem.name
                                                    eamilCid.cid = attachItem.cid
                                                    eamilCid.data = attachItem.byt
                                                    eamilCid.hasData = true
                                                    eamilCid.isCanDelete = false
                                                    var savePath = save_dir+eamilCid.account+"_"+eamilCid.msgId+"_"+eamilCid.name
                                                    eamilCid.localPath = savePath
                                                    AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.insert(eamilCid)
                                                }
                                            }
                                            var cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                                            if(cidList.size == 0)
                                            {
                                                cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()

                                            }
                                            for (cidItem in cidList)
                                            {
                                                val save_dir = PathUtils.getInstance().filePath.toString() + "/"
                                                var savePath = save_dir+ AppConfig.instance.emailConfig().account+"_"+emailMeaasgeData!!.menu+"_"+msgId+"_"+cidItem.name
                                                if(cidItem.cid == null && cidItem.cid =="")
                                                {
                                                    cidItem.cid = cidItem.name
                                                }
                                                if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
                                                {
                                                    emailMeaasgeData!!.originalText = replaceLocalPathByImgCid(emailMeaasgeData!!.originalText ,cidItem.cid ,savePath )
                                                }else{
                                                    emailMeaasgeData!!.content = replaceLocalPathByImgCid(emailMeaasgeData!!.content ,cidItem.cid ,savePath )
                                                }

                                            }
                                            if(menu != "node")
                                            {
                                                var headStr = "<head><style>body {font-family: Helvetica;font-size: 16px;word-wrap: break-word;-webkit-text-size-adjust:none;-webkit-nbsp-mode: space;}pre {white-space: pre-wrap;}</style></head>"
                                                var iframeStr = "<iframe src='x-mailcore-msgviewloaded:' style='width: 0px; height: 0px; border: none;'></iframe>"
                                                iframeStr = ""
                                                var URLText = "";
                                                if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
                                                {
                                                    var originalTextCun = StringUitl.StripHT(emailMeaasgeData!!.originalText)
                                                    if(originalTextCun.length > 50)
                                                    {
                                                        originalTextCun = originalTextCun.substring(0,50)
                                                    }
                                                    mailInfo.content = originalTextCun
                                                    URLText = "<html>"+headStr+emailMeaasgeData!!.originalText+"</body>"+iframeStr+"</html>";
                                                    contentHtml = URLText
                                                    webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
                                                }else{
                                                    var contentText =  emailMeaasgeData!!.contentText
                                                    if(contentText.length > 50)
                                                    {
                                                        contentText = contentText.substring(0,50)
                                                    }
                                                    mailInfo.content = contentText
                                                    URLText = "<html>"+headStr+emailMeaasgeData!!.content+"</div></body>"+iframeStr+"</html>";
                                                    contentHtml = URLText
                                                    webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
                                                }
                                            }
                                        }
                                    }
                                    override fun gainFailure(errorMsg: String) {
                                        //tipDialog.dismiss()
                                        //closeProgressDialog()
                                        Toast.makeText(this@EmailInfoActivity, getString(R.string.picture_download_failed), Toast.LENGTH_SHORT).show()
                                    }
                                },menu,msgId,save_dir,emailMeaasgeData!!.aesKey,gmailService,"me")
                    }

                }else{
                    var cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
                    if(cidList.size == 0)
                    {
                        cidList =  AppConfig.instance.mDaoMaster!!.newSession().emailCidEntityDao.queryBuilder().where(EmailCidEntityDao.Properties.MsgId.eq(msgId)).list()
                    }
                    for (cidItem in cidList)
                    {
                        val save_dir = PathUtils.getInstance().filePath.toString() + "/"
                        var savePath = save_dir+ AppConfig.instance.emailConfig().account+"_"+emailMeaasgeData!!.menu+"_"+msgId+"_"+cidItem.name
                        if(cidItem.cid == null && cidItem.cid =="")
                        {
                            cidItem.cid = cidItem.name
                        }
                        if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
                        {
                            emailMeaasgeData!!.originalText = replaceLocalPathByImgCid(emailMeaasgeData!!.originalText ,cidItem.cid ,savePath )
                        }else{
                            emailMeaasgeData!!.content = replaceLocalPathByImgCid(emailMeaasgeData!!.content ,cidItem.cid ,savePath )
                        }

                    }

                }


            }
        }else{
            if(isContainerAttachment) {
                attachListParent.visibility = View.VISIBLE
            }
            var folderName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId
            var fileSoucePath =   PathUtils.generateEmailMessagePath(folderName) +"/htmlContent.txt"
            var txtFile = File(fileSoucePath)
            if(txtFile.exists())
            {
                updateUIByLocalZipData()
            }else{
                showProgressDialog(getString(R.string.waiting))
                var folderName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId+"_downzip"
                var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + emailMeaasgeData!!.emailAttachPath
                var fileSavePath =   PathUtils.generateEmailMessagePath(folderName)
                var fileName = "htmlContent.zip"
                var fileNameBase58 = Base58.encode(fileName.toByteArray())
                FileDownloadUtils.doDownLoadWork(filledUri,fileNameBase58, fileSavePath, this, emailMeaasgeData!!.msgId.toInt(), handlerDownLoad, "","3")
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
            var itemEndIndex = emailMeaasgeData!!.from.indexOf("@")
            if(itemEndIndex < 0)
            {
                itemEndIndex = 0;
            }
            fromName = emailMeaasgeData!!.from.substring(0,itemEndIndex)
            fromAdress = emailMeaasgeData!!.from.substring(0,emailMeaasgeData!!.from.length)
        }
        var toName = ""
        var toAdress = ""
        if(emailMeaasgeData!!.to.contains(","))
        {
            var toList = emailMeaasgeData!!.to.split(",")
            for(item in toList)
            {
                if(item.indexOf("<") >-1)
                {
                    toName += item.substring(0,item.indexOf("<"))+","
                    toAdress += item.substring(item.indexOf("<"),item.length)+","
                }else{
                    var itemEndIndex = item.indexOf("@")
                    if(itemEndIndex < 0)
                    {
                        itemEndIndex = 0;
                    }
                    toName += item.substring(0,itemEndIndex)+","
                    toAdress += item.substring(0,item.length)+","
                }
            }
            if(toName.contains(","))
            {
                toName.substring(0,toName.length -1)
            }
            if(toAdress.contains(","))
            {
                toAdress.substring(0,toAdress.length -1)
            }
        }else{
            if(emailMeaasgeData!!.to.indexOf("<") >-1)
            {
                toName = emailMeaasgeData!!.to.substring(0,emailMeaasgeData!!.to.indexOf("<"))
                toAdress = emailMeaasgeData!!.to.substring(emailMeaasgeData!!.to.indexOf("<"),emailMeaasgeData!!.to.length)
            }else{
                var itemEndIndex = emailMeaasgeData!!.to.indexOf("@")
                if(itemEndIndex < 0)
                {
                    itemEndIndex = 0;
                }
                toName = emailMeaasgeData!!.to.substring(0,itemEndIndex)
                toAdress = emailMeaasgeData!!.to.substring(0,emailMeaasgeData!!.to.length)
            }
        }

        title_info.text = fromName
        avatar_info.setText(fromName)
        time_info.text = DateUtil.getTimestampString(DateUtil.getDate(emailMeaasgeData!!.date), AppConfig.instance)
        mailInfo.revDate = (DateUtil.getDate(emailMeaasgeData!!.date).time / 1000).toInt()
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
        showandhide.setOnClickListener {

            isShow = !isShow
            if (isShow) {
                //如果选中，显示密码
                password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_open)
            } else {
                //否则隐藏密码
                password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_shut)
            }
        }
        passwordHelp.setOnClickListener {
           /* initNormalPopupIfNeed();
            mNormalPopup!!.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
            mNormalPopup!!.setPreferredDirection(QMUIPopup.DIRECTION_TOP)
            mNormalPopup!!.show(it)*/
        }
        var menuFrom = emailMeaasgeData!!.menu
        if(menuFrom.contains("Sent") || menuFrom.contains("已发") || menuFrom.contains("Drafts")|| menuFrom.contains("草稿"))
        {
            draft_info.text = getString(R.string.From_me)
            detail_from_From.text = getString(R.string.To)
            fromName_From.text = toName
            fromEmailAdress_From.text = toAdress

        }else{
            draft_info.text = getString(R.string.To_me)
            detail_from_From.text = getString(R.string.From)
            fromName_From.text = fromName
            fromEmailAdress_From.text = fromAdress
        }
        mailInfo.fromName = fromName
        mailInfo.fromEmailBox = fromAdress
        formEmailAdress.text = fromAdress.replace("<","").replace(">","")
        var emailConfigEntityList = ArrayList<EmailInfoData>()
        //emailConfigEntityList.add(EmailInfoData("From",fromName,fromAdress))
        var emailContactList = mutableListOf<EmailContact>()
        var toNameStr = ""
        var toAdressStr = ""
        if(to!= null && to != "" )
        {
            var toList  = to.split(",")
            for(toItem in toList)
            {
                var toName = ""
                var toAdress = ""
                if(toItem.indexOf("<") > -1)
                {
                    toName = toItem.substring(0,toItem.indexOf("<"))
                    toAdress = toItem.substring(toItem.indexOf("<"),toItem.length)
                }else{
                    var itemEndIndex = toItem.indexOf("@")
                    if(itemEndIndex < 0)
                    {
                        itemEndIndex = 0;
                    }
                    toName = toItem.substring(0,itemEndIndex)
                    toAdress = toItem.substring(0,toItem.length)
                }
                /* toName =toName.replace("\"","")
                 toName =toName.replace("\"","")*/
                if(toName != "")
                {
                    toNameStr += toName+","
                    toAdressStr += toAdress+","
                }
                emailConfigEntityList.add(EmailInfoData("To",toName,toAdress))
                var emailContact = EmailContact(toName,toAdress)
                emailContactList.add(emailContact)
            }
            if(toNameStr != "")
            {
                toNameStr = toNameStr.substring(0,toNameStr.length -1)
                toAdressStr = toAdressStr.substring(0,toAdressStr.length -1)
                toRoot.visibility = View.VISIBLE
                fromName_to.text = toNameStr;
                fromEmailAdress_to.text = toAdressStr;
            }
            if(emailContactList.size > 0)
            {
                mailInfo.toUserJosn = emailContactList.baseDataToJson()
            }else{
                mailInfo.toUserJosn = ""
            }
        }
        emailContactList = mutableListOf<EmailContact>()
        toNameStr = ""
        toAdressStr = ""
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
                    var itemEndIndex = ccItem.indexOf("@")
                    if(itemEndIndex < 0)
                    {
                        itemEndIndex = 0;
                    }
                    ccName = ccItem.substring(0,itemEndIndex)
                    ccAdress = ccItem.substring(0,ccItem.length)
                }
                /* ccName =ccName.replace("\"","")
                 ccName =ccName.replace("\"","")*/
                if(ccName != "")
                {
                    toNameStr += ccName+","
                    toAdressStr += ccAdress+","
                }
                emailConfigEntityList.add(EmailInfoData("Cc",ccName,ccAdress))
                var emailContact = EmailContact(ccName,ccAdress)
                emailContactList.add(emailContact)
            }
            if(toNameStr != "")
            {
                toNameStr = toNameStr.substring(0,toNameStr.length -1)
                toAdressStr = toAdressStr.substring(0,toAdressStr.length -1)
                ccRoot.visibility = View.VISIBLE
                fromName_cc.text = toNameStr;
                fromEmailAdress_cc.text = toAdressStr;
            }
            if(emailContactList.size > 0)
            {
                mailInfo.ccUserJosn = emailContactList.baseDataToJson()
            }else{
                mailInfo.ccUserJosn = ""
            }
        }
        emailContactList = mutableListOf<EmailContact>()
        toNameStr = ""
        toAdressStr = ""
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
                    var itemEndIndex = bccItem.indexOf("@")
                    if(itemEndIndex < 0)
                    {
                        itemEndIndex = 0;
                    }
                    ccName = bccItem.substring(0,itemEndIndex)
                    ccAdress = bccItem.substring(0,bccItem.length)
                }
                /*ccName =ccName.replace("\"","")
                ccName =ccName.replace("\"","")*/
                if(ccName != "")
                {
                    toNameStr += ccName+","
                    toAdressStr += ccAdress+","
                }
                emailConfigEntityList.add(EmailInfoData("Bcc",ccName,ccAdress))
                var emailContact = EmailContact(ccName,ccAdress)
                emailContactList.add(emailContact)
            }
            if(toNameStr != "")
            {
                toNameStr = toNameStr.substring(0,toNameStr.length -1)
                toAdressStr = toAdressStr.substring(0,toAdressStr.length -1)
                bccRoot.visibility = View.VISIBLE
                fromName_bcc.text = toNameStr;
                fromEmailAdress_bcc.text = toAdressStr;
            }
            if(emailContactList.size > 0)
            {
                mailInfo.bccUserJosn = emailContactList.baseDataToJson()
            }else{
                mailInfo.bccUserJosn = ""
            }
        }
        emaiInfoAdapter = EmaiInfoAdapter(emailConfigEntityList)
        emaiInfoAdapter!!.setOnItemLongClickListener { adapter, view, position ->

            true
        }
        //recyclerViewleft.adapter = emaiInfoAdapter
        emaiInfoAdapter!!.setOnItemClickListener { adapter, view, position ->
            /* var intent = Intent(activity!!, ConversationActivity::class.java)
             intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
             startActivity(intent)*/
        }




        backBtn.setOnClickListener {

            onBackPressed()
        }
        backMenu.setOnClickListener {

            doBackUp()

        }
        deleteMenu.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .setContentText(getString(R.string.askdelete))
                    .setConfirmClickListener {
                        showProgressDialog(getString(R.string.waiting))
                        if(menu == "node")
                        {
                            var delEmail = DelEmail(AppConfig.instance.emailConfig().emailType.toInt(),emailMeaasgeData!!.msgId.toInt())
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,delEmail))
                        }else{
                            deleteEmailOp(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2,true)
                        }
                    }.setCancelClickListener {

                    }
                    .show()


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
            if(emailMeaasgeData!!.isContainerAttachment())
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
        ViewEncryptedMessage.setOnClickListener {

            inputPassWordParent.visibility = View.VISIBLE
            jiemiRoot.visibility = View.GONE
        }
        DecryptBtn.setOnClickListener {
            userPassWord = password_editText.text.toString()
            if(userPassWord == "")
            {
                toast(R.string.Password_cannot_be_empty)
               return@setOnClickListener
            }
            hasPassWord = true
            clickDecryptBtn = true;
            inputPassWordParent.visibility = View.GONE
            initData()
        }
        if(menu != "node")
        {
            if(!emailMeaasgeData!!.isSeen())
            {
                if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
                {
                    val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                    emailReceiveClient
                            .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                override fun gainSuccess(result: Boolean) {

                                    when(menu)
                                    {
                                        ConstantValue.currentEmailConfigEntity!!.inboxMenu ->
                                        {
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                            var EmailMessage = false
                                            if(emailConfigEntityList.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                emailConfigEntity.unReadCount -= 1   //Inbox未读数量
                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                EventBus.getDefault().post(ChangeEmailConfig())
                                            }
                                        }
                                        ConstantValue.currentEmailConfigEntity!!.garbageMenu ->
                                        {
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                            var EmailMessage = false
                                            if(emailConfigEntityList.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                emailConfigEntity.garbageCount -= 1   //Inbox未读数量
                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                EventBus.getDefault().post(ChangeEmailConfig())
                                            }
                                        }

                                    }
                                }
                                override fun gainFailure(errorMsg: String) {

                                }
                            },menu,msgId,32,true,"")
                }else{
                    var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
                    var labelsToAdd = listOf<String>()
                    var labelsToRemove = listOf<String>("UNREAD")
                    val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                    emailReceiveClient
                            .gmailMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                override fun gainSuccess(result: Boolean) {

                                    when(menu)
                                    {
                                        ConstantValue.currentEmailConfigEntity!!.inboxMenu ->
                                        {
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                            var EmailMessage = false
                                            if(emailConfigEntityList.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                emailConfigEntity.unReadCount -= 1   //Inbox未读数量
                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                EventBus.getDefault().post(ChangeEmailConfig())
                                            }
                                        }
                                        ConstantValue.currentEmailConfigEntity!!.garbageMenu ->
                                        {
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                            var EmailMessage = false
                                            if(emailConfigEntityList.size > 0)
                                            {
                                                var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                emailConfigEntity.garbageCount -= 1   //Inbox未读数量
                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                EventBus.getDefault().post(ChangeEmailConfig())
                                            }
                                        }

                                    }
                                }
                                override fun gainFailure(errorMsg: String) {

                                }
                            },gmailService,"me",msgId,labelsToAdd,labelsToRemove)
                }

            }
        }

        moreMenu.setOnClickListener {

            /*list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
            list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
            list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
            var starIcon = "tabbar_stars_unselected"
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
                            if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
                            {
                                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())

                                emailReceiveClient
                                        .imapMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                            override fun gainSuccess(result: Boolean) {
                                                //tipDialog.dismiss()
                                                closeProgressDialog()
                                                emailMeaasgeData!!.setIsSeen(false)
                                                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                                                when(menu)
                                                {
                                                    ConstantValue.currentEmailConfigEntity!!.inboxMenu ->
                                                    {
                                                        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                                        if(emailConfigEntityList.size > 0)
                                                        {
                                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                            emailConfigEntity.unReadCount += 1   //Inbox未读数量
                                                            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                            EventBus.getDefault().post(ChangeEmailConfig())
                                                        }
                                                    }
                                                    ConstantValue.currentEmailConfigEntity!!.garbageMenu ->
                                                    {
                                                        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                                        if(emailConfigEntityList.size > 0)
                                                        {
                                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                            emailConfigEntity.garbageCount += 1   //Inbox未读数量
                                                            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                            EventBus.getDefault().post(ChangeEmailConfig())
                                                        }
                                                    }

                                                }
                                                EventBus.getDefault().post(ChangEmailMessage(positionIndex,0))
                                            }
                                            override fun gainFailure(errorMsg: String) {
                                                //tipDialog.dismiss()
                                                closeProgressDialog()
                                                Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                            }
                                        },menu,msgId,32,false,"")
                            }else{
                                var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
                                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                                var labelsToAdd = listOf<String>("UNREAD")
                                var labelsToRemove = listOf<String>()
                                emailReceiveClient
                                        .gmailMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                            override fun gainSuccess(result: Boolean) {
                                                //tipDialog.dismiss()
                                                closeProgressDialog()
                                                emailMeaasgeData!!.setIsSeen(false)
                                                AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.update(emailMeaasgeData)
                                                when(menu)
                                                {
                                                    ConstantValue.currentEmailConfigEntity!!.inboxMenu ->
                                                    {
                                                        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                                        if(emailConfigEntityList.size > 0)
                                                        {
                                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                            emailConfigEntity.unReadCount += 1   //Inbox未读数量
                                                            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                            EventBus.getDefault().post(ChangeEmailConfig())
                                                        }
                                                    }
                                                    ConstantValue.currentEmailConfigEntity!!.garbageMenu ->
                                                    {
                                                        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(ConstantValue.currentEmailConfigEntity!!.account)).list()
                                                        if(emailConfigEntityList.size > 0)
                                                        {
                                                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                                                            emailConfigEntity.garbageCount += 1   //Inbox未读数量
                                                            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                                            EventBus.getDefault().post(ChangeEmailConfig())
                                                        }
                                                    }

                                                }
                                                EventBus.getDefault().post(ChangEmailMessage(positionIndex,0))
                                            }
                                            override fun gainFailure(errorMsg: String) {
                                                //tipDialog.dismiss()
                                                closeProgressDialog()
                                                Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                                            }
                                        },gmailService,"me",msgId,labelsToAdd,labelsToRemove)
                            }

                        }
                        "Star", "Starred" -> {
                            showProgressDialog(getString(R.string.waiting))
                            /*tipDialog.show()*/
                            if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
                            {
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
                            }else{
                                var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
                                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                                var labelsToAdd = listOf<String>("STARRED")
                                var labelsToRemove = listOf<String>()
                                if(starFlag)
                                {
                                    labelsToAdd = listOf<String>()
                                    labelsToRemove = listOf<String>("STARRED")
                                }else{
                                    labelsToAdd = listOf<String>("STARRED")
                                    labelsToRemove = listOf<String>()
                                }
                                emailReceiveClient
                                        .gmailMarkEmail(this@EmailInfoActivity, object : MarkCallback {
                                            override fun gainSuccess(result: Boolean) {
                                                //tipDialog.dismiss()
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
                                        },gmailService,"me",msgId,labelsToAdd,labelsToRemove)
                            }

                        }
                        "Node back up" -> {
                            doBackUp()
                        }
                        "Move to" -> {
                            showMovePop()
                        }
                        "Delete" -> {
                            deleteMenu.performClick()
                        }
                    }
                }

            })
        }
        needOp = false
        if( emailMeaasgeData!!.content != null)
        {
            var exitName = emailMeaasgeData!!.content.toLowerCase()
            if(emailMeaasgeData!!.content.contains("<img"))
            {
                if(exitName.contains(".jpg") || exitName.contains(".jpeg")|| exitName.contains(".png"))
                {
                    needOp = true
                }
            }
            if(emailMeaasgeData!!.content.contains("cid:") || emailMeaasgeData!!.content.contains("file:///"))
            {
                needOp = true
            }

        }
        if(emailMeaasgeData!!.originalText!= null)
        {
            if(emailMeaasgeData!!.originalText.contains("<img"))
            {
                var exitName = emailMeaasgeData!!.originalText.toLowerCase()
                if(exitName.contains(".jpg") || exitName.contains(".jpeg")|| exitName.contains(".png"))
                {
                    needOp = true
                }
            }

            if(emailMeaasgeData!!.originalText.contains("cid:") || emailMeaasgeData!!.originalText.contains("file:///"))
            {
                needOp = true
            }
        }
        NestedScrollViewParent.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                isScaleInit = true
                return false
            }
        })
        val webSettings = webView.getSettings()
        //允许webview对文件的操作
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        if(needOp)
        {

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
        webView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                isScaleInit = true
                if(!needOp)
                {
                    webViewScroll = false
                }
                webView.requestDisallowInterceptTouchEvent(webViewScroll)
                return false
            }
        })
        webView.webViewClient = object  : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.loadUrl("javascript:window.HTMLOUT.getContentWidth(document.getElementsByTagName('html')[0].scrollWidth);");
            }
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //view.loadUrl(url)
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val url = Uri.parse(url)
                intent.data = url
                startActivity(intent)
                return true
            }

            override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                var saleOld = oldScale
                var sscaleNew = newScale
                if(!isScaleInit)
                {
                    newScaleInit = newScale
                }
                if(newScaleInit == newScale)
                {
                    webViewScroll = false
                }else{
                    webViewScroll = true
                }
                Log.i("onScaleChanged",saleOld.toString()+"##"+sscaleNew.toString() +"##"+(saleOld -sscaleNew))
                super.onScaleChanged(view, oldScale, newScale)
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
        if(menu != "node")
        {
            var headStr = "<head><style>body {font-family: Helvetica;font-size: 16px;word-wrap: break-word;-webkit-text-size-adjust:none;-webkit-nbsp-mode: space;}pre {white-space: pre-wrap;}</style></head>"
            var iframeStr = "<iframe src='x-mailcore-msgviewloaded:' style='width: 0px; height: 0px; border: none;'></iframe>"
            iframeStr = ""
            if(emailMeaasgeData!!.originalText != null && emailMeaasgeData!!.originalText != "")
            {
                var originalTextCun = StringUitl.StripHT(emailMeaasgeData!!.originalText)
                if(originalTextCun.length > 50)
                {
                    originalTextCun = originalTextCun.substring(0,50)
                }
                mailInfo.content = originalTextCun
                URLText = "<html>"+headStr+emailMeaasgeData!!.originalText+"</body>"+iframeStr+"</html>";
                contentHtml = URLText
                webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
            }else{
                var contentText =  emailMeaasgeData!!.contentText
                if(contentText.length > 50)
                {
                    contentText = contentText.substring(0,50)
                }
                mailInfo.content = contentText
                //URLText = "<html><body style ='font-size:16px!important;'><div style ='overflow-wrap: break-word;width: 100%;'>"+emailMeaasgeData!!.content+"</div></body></html>";
                URLText = "<html>"+headStr+emailMeaasgeData!!.content+"</body>"+iframeStr+"</html>";
                contentHtml = URLText
                //contentHtml = StringUitl.addHrefToURL(contentHtml);
                webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
            }
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
    fun doBackUp()
    {
        if(isBackEd == 1)
        {
            toast(R.string.It_already_exists)
            return
        }
        zipFileSoucePath = ArrayList()
        if(needWaitAttach)
        {
            toast(R.string.Waiting_for_attachments)
            return
        }
        showProgressDialog(getString(R.string.waiting))
        fileAESKey = RxEncryptTool.generateAESKey()
        var base58files_dir =  PathUtils.getInstance().tempPath.toString() + "/"
        var  path = PathUtils.generateEmailMessagePath("temp")+"htmlContent.txt";
        var  result = FileUtil.writeStr_to_txt(path,contentHtml)
        if(result)
        {
            var miPath = base58files_dir +"htmlContent.txt";
            val code = FileUtil.copySdcardToxFileAndEncrypt(path, miPath, fileAESKey.substring(0, 16))
            zipFileSoucePath.add(miPath)
        }
        var  attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(emailMeaasgeData!!.menu+"_"+msgId)).list()
        if(attachList.size == 0)
        {
            attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.MsgId.eq(msgId)).list()

        }
        val save_dir = PathUtils.getInstance().filePath.toString() + "/"


        for (attach in attachList) {
            var fromPath = save_dir + attach.account + "_" + attach.msgId + "_" + attach.name
            var fileSouceName = attach.account+"_"+attach.msgId+"_"+attach.name
            var base58Name = Base58.encode(fileSouceName.toByteArray())
            var miPath = base58files_dir + base58Name

            val code = FileUtil.copySdcardToxFileAndEncrypt(fromPath, miPath, fileAESKey.substring(0, 16))
            zipFileSoucePath.add(miPath)
        }
        zipSavePath = PathUtils.generateEmailMessagePath("temp")+"htmlContent.zip";
        zipCompressTask = ZipCompressTask(zipFileSoucePath!!, zipSavePath, this, false, handlerCompressZip!!)
        zipCompressTask!!.execute()
    }
    internal var handlerCompressZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                    toast(R.string.Compression_failure)
                }
                0x56 -> {
                    var zipSavePathaa = zipSavePath
                    msgID = (System.currentTimeMillis() / 1000).toInt()
                    FileMangerUtil.sendEmailFile(zipSavePath,msgID, false)
                }
            }//goMain();
            //goMain();
        }
    }
    internal var handlerDownLoad: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    runOnUiThread {
                        toast(getString(R.string.Download_failure))
                    }
                }
                0x55 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    var folderName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId+"_downzip"
                    var fileFromPath =   PathUtils.generateEmailMessagePath(folderName)
                    var zipPath = fileFromPath +"/htmlContent.zip"
                    var zipFile = File(zipPath)
                    if(zipFile.exists())
                    {
                        zipUnTask = ZipUnTask(zipPath, fileFromPath, AppConfig.instance, false, handlerUnZip)
                        zipUnTask!!.execute()
                    }
                }
            }//goMain();
            //goMain();
        }
    }
    internal var handlerUnZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    toast(R.string.Failure_of_decompression)
                }
                0x56 -> {
                    var zipSavePathaa = zipSavePath
                    val msgID = (System.currentTimeMillis() / 1000).toInt()
                    updateUIByZipData()


                }
            }//goMain();
            //goMain();
        }
    }
    fun updateUIByLocalZipData()
    {
        var folderNewName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId
        var fileSavePath =   PathUtils.generateEmailMessagePath(folderNewName)
        var folderFile = File(fileSavePath)
        var subFile = folderFile.listFiles()
        var contentPath = ""
        var attachListEntity =  arrayListOf<EmailAttachEntity>()
        var picIndex = 0
        for(file in subFile)
        {
            var name = file.name
            var path = file.path
            if(name == "htmlContent.txt")
            {
                contentPath = path;
            }else{
                var emailAttachEntity = EmailAttachEntity()
                emailAttachEntity.isHasData = true
                emailAttachEntity.localPath = path
                emailAttachEntity.name = name
                emailAttachEntity.isCanDelete = false
                attachListEntity.add(emailAttachEntity)

                if (name.contains("jpg") || name.contains("JPG")  || name.contains("png")) {
                    val localMedia = LocalMedia()
                    localMedia.isCompressed = false
                    localMedia.duration = 0
                    localMedia.height = 100
                    localMedia.width = 100
                    localMedia.isChecked = false
                    localMedia.isCut = false
                    localMedia.mimeType = 0
                    localMedia.num = 0
                    localMedia.path = path
                    localMedia.pictureType = "image/jpeg"
                    localMedia.setPosition(picIndex)
                    localMedia.sortIndex = picIndex
                    previewImages.add(localMedia)
                    ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                    picIndex ++;
                }
            }
        }
        if(contentPath != "")
        {
            var contentPathFile = File(contentPath)
            var contentHtml = FileUtil.readTxtFile(contentPathFile);
            emailMeaasgeData!!.content = contentHtml
            webView.loadDataWithBaseURL(null,contentHtml,"text/html","utf-8",null);
        }
        if(attachListEntity.size >0)
        {
            attachListEntityNode = attachListEntity
            runOnUiThread {
                emaiAttachAdapter = EmaiAttachAdapter(attachListEntity)
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
    }
    fun updateUIByZipData()
    {
        closeProgressDialog()
        var folderNewName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId
        var fileSavePath =   PathUtils.generateEmailMessagePath(folderNewName)
        var folderName = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId+"_downzip"
        var fileSoucePath =   PathUtils.generateEmailMessagePath(folderName)
        var folderFile = File(fileSoucePath)
        var subFile = folderFile.listFiles()
        var contentPath = ""
        var attachListEntity =  arrayListOf<EmailAttachEntity>()
        var picIndex = 0
        for(file in subFile)
        {
            var name = file.name
            var path = file.path
            if(name != "htmlContent.txt")
            {
                name = String(Base58.decode(name));
            }
            var newFile = fileSavePath+"/"+name
            val result = FileUtil.copyTempFiletoFileAndDecrypt(path, newFile, emailMeaasgeData!!.aesKey)
            if(result == 1)
            {
                if(name == "htmlContent.txt")
                {
                    contentPath = newFile;
                }else{
                    var emailAttachEntity = EmailAttachEntity()
                    emailAttachEntity.isHasData = true
                    emailAttachEntity.localPath = newFile
                    emailAttachEntity.name = name
                    emailAttachEntity.isCanDelete = false
                    attachListEntity.add(emailAttachEntity)

                    if (name.contains("jpg") || name.contains("JPG")  || name.contains("png")) {
                        val localMedia = LocalMedia()
                        localMedia.isCompressed = false
                        localMedia.duration = 0
                        localMedia.height = 100
                        localMedia.width = 100
                        localMedia.isChecked = false
                        localMedia.isCut = false
                        localMedia.mimeType = 0
                        localMedia.num = 0
                        localMedia.path = newFile
                        localMedia.pictureType = "image/jpeg"
                        localMedia.setPosition(picIndex)
                        localMedia.sortIndex = picIndex
                        previewImages.add(localMedia)
                        ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                        picIndex ++;
                    }
                }
            }
        }
        if(contentPath != "")
        {
            var contentPathFile = File(contentPath)
            var contentHtml = FileUtil.readTxtFile(contentPathFile);
            emailMeaasgeData!!.content = contentHtml
            webView.loadDataWithBaseURL(null,contentHtml,"text/html","utf-8",null);
        }
        if(attachListEntity.size >0)
        {
            attachListEntityNode = attachListEntity
            runOnUiThread {
                emaiAttachAdapter = EmaiAttachAdapter(attachListEntity)
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
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            toast(R.string.Download_failed)
        }else if (fileStatus.result == 2) {
            toast(R.string.Files_100M)
        } else if (fileStatus.result == 3) {
            toast(R.string.Files_0M)
        }else {

            var fileID = fileStatus.fileKey.substring(fileStatus.fileKey.indexOf("##")+2,fileStatus.fileKey.indexOf("__"))
            var file = File(zipSavePath)
            var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
            var type = AppConfig.instance.emailConfig().emailType.toInt()
            var fileSize = file.length().toInt()
            var fileMD5 = FileUtil.getFileMD5(file);
            var uuid = AppConfig.instance.emailConfig().account+"_"+ConstantValue.chooseEmailMenuName +"_"+emailMeaasgeData!!.msgId
            var pulicSignKey = String(RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileAESKey, ConstantValue.libsodiumpublicMiKey!!)))
            mailInfo.dsKey = pulicSignKey
            mailInfo.flags = 1;
            var mailInfoJson = mailInfo.baseDataToJson()
            val contentBuffer = mailInfoJson.toByteArray()
            var fileKey16 = fileAESKey.substring(0,16)
            var mailInfoMiStr = RxEncodeTool.base64Encode2String(AESToolsCipher.aesEncryptBytes(contentBuffer, fileKey16!!.toByteArray(charset("UTF-8"))))
            var saveEmailConf = BakupEmail(type,fileID.toInt(),fileSize,fileMD5 ,accountBase64,uuid, pulicSignKey,mailInfoMiStr)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
        }
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
                    intent.putExtra("attachListEntityNode",attachListEntityNode)

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
    fun deleteEmailOp(menuTo:String,flag:Int,delete:Boolean)
    {
        /*tipDialog.show()*/
        if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
        {
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
        }else{
            var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
            emailReceiveClient
                    .gmailDeleteEmail(gmailService,"me",this@EmailInfoActivity, object : MarkCallback {
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
                    },msgId,delete)
        }

    }
    fun moveEmailOp(menuTo:String,flag:Int,from:String)
    {
        /*tipDialog.show()*/
        if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
        {
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
        }else{
            var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,ConstantValue.currentEmailConfigEntity!!.account);
            var labelsToAdd = listOf<String>(menuTo.toUpperCase())
            var labelsToRemove = listOf<String>(from.toUpperCase())
            val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
            emailReceiveClient
                    .gmailMarkEmail(this@EmailInfoActivity, object : MarkCallback {
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
                            closeProgressDialog()
                            Toast.makeText(this@EmailInfoActivity, getString(R.string.fail), Toast.LENGTH_SHORT).show()
                        }
                    },gmailService,"me",msgId,labelsToAdd,labelsToRemove)
        }

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
                        moveEmailOp(ConstantValue.currentEmailConfigEntity!!.inboxMenu,2,menu)
                    }
                    "Spam" -> {
                        showProgressDialog(getString(R.string.waiting))
                        moveEmailOp(ConstantValue.currentEmailConfigEntity!!.garbageMenu,2,menu)
                    }
                    "Trash" -> {
                        showProgressDialog(getString(R.string.waiting))
                        moveEmailOp(ConstantValue.currentEmailConfigEntity!!.deleteMenu,2,menu)
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
    fun  isCidImgAndReplace(text:String ):Boolean {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return text.contains("cid:");
    }
    fun  replaceLocalPathByImgCid(content:String ,fileName:String ,filePath:String ):String {
        return content.replace("cid:" + fileName + "\"","file://" + filePath+"\"").toString();
    }

    private fun initNormalPopupIfNeed() {
        if (mNormalPopup == null) {
            mNormalPopup = QMUIPopup(applicationContext, QMUIPopup.DIRECTION_NONE)
            val textView = TextView(applicationContext)
            textView.layoutParams = mNormalPopup!!.generateLayoutParam(QMUIDisplayHelper.dp2px(applicationContext, 250),WRAP_CONTENT)
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(applicationContext, 4).toFloat(), 1.0f)
            val padding = QMUIDisplayHelper.dp2px(applicationContext, 20)
            textView.setPadding(padding, padding, padding, padding)
            //textView.text = getString(R.string.passwordSetTips)
            textView.text = "test"
            //textView.setTextColor(resources.getColor(R.color.app_color_description))
            mNormalPopup!!.setContentView(textView)
            mNormalPopup!!.setOnDismissListener(PopupWindow.OnDismissListener {

            })
        }
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.bakupEmailCallback = null
        AppConfig.instance.messageReceiver!!.bakMailsCheckCallback = null
        AppConfig.instance.messageReceiver!!.dlEmailCallback = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    internal inner class JavaScriptInterface {
        fun getContentWidth(value: String?) {
            if (value != null) {
                webviewContentWidth = Integer.parseInt(value)
                Log.d("EmailInfoActivity", "Result from javascript: $webviewContentWidth")

            }
        }
    }
}