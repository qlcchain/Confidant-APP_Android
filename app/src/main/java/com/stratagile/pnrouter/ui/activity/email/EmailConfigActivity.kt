package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.InflateException
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetConnectCallback
import com.smailnet.eamil.Callback.GetCountCallback
import com.smailnet.eamil.EmailConfig
import com.smailnet.eamil.EmailCount
import com.smailnet.eamil.EmailExamine
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.islands.Islands
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.EmailConfigEntity
import com.stratagile.pnrouter.db.EmailConfigEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JSaveEmailConfRsp
import com.stratagile.pnrouter.entity.OtherEmailConfig
import com.stratagile.pnrouter.entity.SaveEmailConf
import com.stratagile.pnrouter.entity.events.ChangeEmailConfig
import com.stratagile.pnrouter.gmail.GmailQuickstart
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailConfigComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailConfigContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailConfigModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailConfigPresenter
import com.stratagile.pnrouter.ui.activity.email.view.ContactSortModel
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.StringUitl
import kotlinx.android.synthetic.main.email_otherconfig_activity.*
import org.greenrobot.eventbus.EventBus

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/08/20 16:58:53
 */

class EmailConfigActivity : BaseActivity(), EmailConfigContract.View , PNRouterServiceMessageReceiver.SaveEmailConfCallback {
    override fun saveEmailConf(jSaveEmailConfRsp: JSaveEmailConfRsp) {
        if(jSaveEmailConfRsp.params.retCode == 0)
        {
            runOnUiThread {
                sycDataCountIMAP()
            }
        }else if(jSaveEmailConfRsp.params.retCode == 1)
        {
            runOnUiThread {
                closeProgressDialog()
                //sycDataCountIMAP()
                toast(R.string.Over_configure)
            }
        }else{
            if(BuildConfig.DEBUG)
            {
                sycDataCountIMAP()
                //AppConfig.instance.emailConfig().setAccount(accountOld).setPassword(passwordOld).setEmailType(emailTypeOld)
                runOnUiThread {
                    closeProgressDialog()
                    //sycDataCountIMAP()
                    toast(R.string.The_mailbox_has_been_configured)
                }
            }else{
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.The_mailbox_has_been_configured)

                }
            }

        }
    }

    @Inject
    internal lateinit var mPresenter: EmailConfigPresenter
    protected val REQUEST_CODE_IN = 101
    protected val REQUEST_CODE_TO = 102
    private  var otherEmailConfig: OtherEmailConfig?= null
    var settings = 0;
    var emailConfig:EmailConfig? = null
    var isShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.mainColor))
                        view.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_otherconfig_activity)
    }
    override fun initData() {
        isShow = false
        otherEmailConfig  = OtherEmailConfig();
        AppConfig.instance.messageReceiver!!.saveEmailConfCallback = this
        emailConfig = AppConfig.instance.emailConfig().clone()
        if(BuildConfig.DEBUG)
        {
            account.setText("emaildev@qlink.mobi")
            password.setText("Qlcchain@123")
            hostname.setText("imap.exmail.qq.com")
            userName.setText("ak47")
            outhostname.setText("smtp.exmail.qq.com")
        }
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showandhide.setImageResource(R.mipmap.tabbar_shut)
        if(intent.hasExtra("settings"))
        {
            settings = intent.getIntExtra("settings",0)
        }
        if(settings == 1)
        {
            account.setText(AppConfig.instance.emailConfig().account)
            account.isEnabled = false
            password.setText("")
            hostname.setText(AppConfig.instance.emailConfig().imapHost)
            inPort.setText(AppConfig.instance.emailConfig().imapPort.toString())

            if(AppConfig.instance.emailConfig().name == null || AppConfig.instance.emailConfig().name == "")
            {
                var name = AppConfig.instance.emailConfig().account.substring(0,AppConfig.instance.emailConfig().account.indexOf("@"))
                userName.setText(name)
            }else{
                userName.setText(AppConfig.instance.emailConfig().name)
            }

            outhostname.setText(AppConfig.instance.emailConfig().smtpHost)
            outPort.setText(AppConfig.instance.emailConfig().smtpPort.toString())

            if(AppConfig.instance.emailConfig().imapEncrypted == null || AppConfig.instance.emailConfig().imapEncrypted == "")
            {
                inEncrypted.setText("None")
                otherEmailConfig!!.imapEncrypted ="None"
            }else{
                otherEmailConfig!!.imapEncrypted = AppConfig.instance.emailConfig().imapEncrypted
                inEncrypted.setText(AppConfig.instance.emailConfig().imapEncrypted)
            }
            if(AppConfig.instance.emailConfig().smtpEncrypted == null || AppConfig.instance.emailConfig().smtpEncrypted == "")
            {
                outEncrypted.setText("None")
                otherEmailConfig!!.smtpEncrypted ="None"
            }else{
                otherEmailConfig!!.smtpEncrypted = AppConfig.instance.emailConfig().smtpEncrypted
                outEncrypted.setText(AppConfig.instance.emailConfig().smtpEncrypted)
            }

        }

        title.text = getString(R.string.IMAP_Email_Settings)
        inEncrypted.setOnClickListener {
            var Intent = Intent(this, EmailConfigEncryptedActivity::class.java)
            Intent.putExtra("encryptedTypeChoose",inEncrypted.text.toString())
            startActivityForResult(Intent,REQUEST_CODE_IN)
        }
        outEncrypted.setOnClickListener {
            var Intent = Intent(this, EmailConfigEncryptedActivity::class.java)
            Intent.putExtra("encryptedTypeChoose",outEncrypted.text.toString())
            startActivityForResult(Intent,REQUEST_CODE_TO)
        }
        showandhide.setOnClickListener {
            isShow = !isShow
            if (isShow) {
                //如果选中，显示密码
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_open)
            } else {
                //否则隐藏密码
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_shut)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IN) {
                if (data!!.hasExtra("encryptedType")) {
                    var encryptedType = data!!.getStringExtra("encryptedType");
                    otherEmailConfig!!.imapEncrypted = data!!.getStringExtra("encryptedType")
                    inEncrypted.setText(encryptedType)
                    when(encryptedType)
                    {
                        "None" ->
                        {
                            inPort.setText("143")
                        }
                        "SSL/TLS" ->
                        {
                            inPort.setText("993")
                        }
                        "STARTTLS" ->
                        {
                            inPort.setText("993")
                        }
                    }
                }
            }else if (requestCode == REQUEST_CODE_TO) {
                if (data!!.hasExtra("encryptedType")) {
                    var encryptedType = data!!.getStringExtra("encryptedType");
                    otherEmailConfig!!.smtpEncrypted = data!!.getStringExtra("encryptedType")
                    outEncrypted.setText(encryptedType)
                    when(encryptedType)
                    {
                        "None" ->
                        {
                            outPort.setText("25")
                        }
                        "SSL/TLS" ->
                        {
                            outPort.setText("465")
                        }
                        "STARTTLS" ->
                        {
                            outPort.setText("587")
                        }
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.next, menu)
        //val itemSubmit = menu.findItem(R.id.nextBtn)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nextBtn) {
            var account = account.text.toString()
            var hostname = hostname.text.toString()
            var userName = userName.text.toString()
            var password = password.text.toString()
            if(settings == 1)
            {
                if(password== "")
                {
                    password = AppConfig.instance.emailConfig().password
                }
            }
            var inPort = inPort.text.toString()
            var outhostname = outhostname.text.toString()
            var outPort = outPort.text.toString()
            if(account == "" || hostname == "" || userName == ""|| password == ""|| inPort == ""|| outhostname == ""|| outPort == "")
            {

                toast(R.string.Some_are_empty)
            }
            var isEmail = StringUitl.isEmail(account)
            if(!isEmail)
            {
                toast(account +" "+ getString(R.string.Some_addresses_are_illegal))
            }

            /*  private String account;         //邮箱帐号
              private String imapHost;        //IMAP的Host
              private String name;            //昵称
              private String password;        //邮箱密码
              private int imapPort;           //IMAP端口
              private String imapEncrypted;
              private String popHost;         //POP的Host
              private int popPort;            //POP端口
              private String smtpHost;        //SMTP的Host
              private int smtpPort;           //SMTP端口
              private String smtpEncrypted;
              private String emailType;       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱*/
            otherEmailConfig!!.account = account
            otherEmailConfig!!.imapHost = hostname
            otherEmailConfig!!.name = userName
            otherEmailConfig!!.password = password
            otherEmailConfig!!.imapPort = inPort.toInt()
            otherEmailConfig!!.smtpHost = outhostname
            otherEmailConfig!!.smtpPort = outPort.toInt()
            otherEmailConfig!!.emailType = "255"
            if(otherEmailConfig!!.smtpEncrypted == null)
            {
                otherEmailConfig!!.smtpEncrypted = "None"
            }
            if(otherEmailConfig!!.imapEncrypted == null)
            {
                otherEmailConfig!!.imapEncrypted = "None"
            }
            AppConfig.instance.emailConfig()
                    .setSmtpHost(otherEmailConfig!!.smtpHost )
                    .setSmtpPort( otherEmailConfig!!.smtpPort)
                    .setPopHost("")
                    .setPopPort(0)
                    .setImapHost(otherEmailConfig!!.imapHost)
                    .setImapPort(otherEmailConfig!!.imapPort)
                    .setAccount(otherEmailConfig!!.account)
                    .setPassword(otherEmailConfig!!.password )
                    .setName( otherEmailConfig!!.name)
                    .setEmailType("255")
                    .setImapEncrypted(otherEmailConfig!!.imapEncrypted)
                    .setSmtpEncrypted(otherEmailConfig!!.smtpEncrypted)
            runOnUiThread {
                showProgressDialog()
            }
            Islands.circularProgress(this)
                    .setMessage(getString(R.string.loading))
                    .setCancelable(false)
                    .run { progressDialog -> login(progressDialog) }
            /*  var intent = Intent()
              intent.putExtra("selectAdressStr", selectAdressStr)
              intent.putExtra("nameAdressStr", nameAdressStr)
              setResult(Activity.RESULT_OK, intent)
              finish()*/
        }
        return super.onOptionsItemSelected(item)
    }
    private fun login(progressDialog: ProgressDialog) {

        val emailExamine = EmailExamine(AppConfig.instance.emailConfig())
        emailExamine.connectServer(this, object : GetConnectCallback {
            override fun loginSuccess() {
                //progressDialog.dismiss()
                //showProgressDialog(getString(R.string.waiting))
                if(settings == 1)
                {
                    var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account)).list()
                    if(emailConfigEntityList.size > 0) {
                        var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                        emailConfigEntity.password =  AppConfig.instance.emailConfig().password
                        emailConfigEntity.imapHost= hostname.text.toString()
                        emailConfigEntity.imapPort= inPort.text.toString().toInt()
                        emailConfigEntity.imapEncrypted= inEncrypted.text.toString()
                        emailConfigEntity.name= userName.text.toString()
                        emailConfigEntity.smtpHost= outhostname.text.toString()
                        emailConfigEntity.smtpPort= outPort.text.toString().toInt()
                        emailConfigEntity.smtpEncrypted= outEncrypted.text.toString()
                        AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                    }
                    toast(R.string.success)
                    finish()
                }else{
                    var pulicSignKey = ConstantValue.libsodiumpublicSignKey!!
                    var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                    var saveEmailConf = SaveEmailConf(1,1,accountBase64 ,"", pulicSignKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
                }



                //sycDataCountIMAP()
                /* startActivity(Intent(this@EmailLoginActivity, EmailMainActivity::class.java))
                 finish()*/
            }

            override fun loginFailure(errorMsg: String) {
                //progressDialog.dismiss()
                runOnUiThread {
                    closeProgressDialog()
                    //toast(R.string.Over_configure)
                }
                AppConfig.instance.emailConfig()
                        .setSmtpHost(emailConfig!!.smtpHost)
                        .setSmtpPort(emailConfig!!.smtpPort)
                        .setPopHost(emailConfig!!.popHost)
                        .setPopPort(emailConfig!!.popPort)
                        .setImapHost(emailConfig!!.imapHost)
                        .setImapPort(emailConfig!!.imapPort)
                        .setAccount(emailConfig!!.account)
                        .setPassword(emailConfig!!.password)
                        .setName(emailConfig!!.name)
                        .setEmailType(emailConfig!!.emailType)
                        .setImapEncrypted(emailConfig!!.imapEncrypted)
                        .setSmtpEncrypted(emailConfig!!.smtpEncrypted)
                try {
                    Islands.ordinaryDialog(this@EmailConfigActivity)
                            .setText(null, getString(R.string.fail)+":"+errorMsg)
                            .setButton( getString(R.string.close), null, null)
                            .click()
                            .show()
                }catch (e:Exception)
                {

                }

            }
        })

    }
    private fun sycDataCountIMAP()
    {
        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(AppConfig.instance.emailConfig().account)).list()
        var hasVerify = false
        var localemailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
        for (j in localemailConfigEntityList) {
            j.choose = false
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(j)
        }
        if(emailConfigEntityList.size > 0)
        {
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
            emailConfigEntity.account = AppConfig.instance.emailConfig().account
            emailConfigEntity.emailType = AppConfig.instance.emailConfig().emailType
            emailConfigEntity.password =  AppConfig.instance.emailConfig().password
            emailConfigEntity.smtpHost =  AppConfig.instance.emailConfig().smtpHost
            emailConfigEntity.smtpPort =  AppConfig.instance.emailConfig().smtpPort
            emailConfigEntity.popHost =  AppConfig.instance.emailConfig().popHost
            emailConfigEntity.popPort =  AppConfig.instance.emailConfig().popPort
            emailConfigEntity.imapHost =  AppConfig.instance.emailConfig().imapHost
            emailConfigEntity.imapPort =  AppConfig.instance.emailConfig().imapPort
            emailConfigEntity.imapEncrypted =  AppConfig.instance.emailConfig().imapEncrypted
            emailConfigEntity.smtpEncrypted =  AppConfig.instance.emailConfig().smtpEncrypted
            emailConfigEntity.inboxMenuRefresh = false
            emailConfigEntity.nodeMenuRefresh = false
            emailConfigEntity.starMenuRefresh = false
            emailConfigEntity.drafMenuRefresh = false
            emailConfigEntity.sendMenuRefresh = false
            emailConfigEntity.garbageMenuRefresh = false
            emailConfigEntity.deleteMenuRefresh = false
            emailConfigEntity.choose = true
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
        }else{
            var emailConfigEntity: EmailConfigEntity = EmailConfigEntity()
            emailConfigEntity.account = AppConfig.instance.emailConfig().account
            emailConfigEntity.emailType = AppConfig.instance.emailConfig().emailType
            emailConfigEntity.password =  AppConfig.instance.emailConfig().password
            emailConfigEntity.smtpHost =  AppConfig.instance.emailConfig().smtpHost
            emailConfigEntity.smtpPort =  AppConfig.instance.emailConfig().smtpPort
            emailConfigEntity.popHost =  AppConfig.instance.emailConfig().popHost
            emailConfigEntity.popPort =  AppConfig.instance.emailConfig().popPort
            emailConfigEntity.imapHost =  AppConfig.instance.emailConfig().imapHost
            emailConfigEntity.imapPort =  AppConfig.instance.emailConfig().imapPort
            emailConfigEntity.imapEncrypted =  AppConfig.instance.emailConfig().imapEncrypted
            emailConfigEntity.smtpEncrypted =  AppConfig.instance.emailConfig().smtpEncrypted
            emailConfigEntity.inboxMenuRefresh = false
            emailConfigEntity.nodeMenuRefresh = false
            emailConfigEntity.starMenuRefresh = false
            emailConfigEntity.drafMenuRefresh = false
            emailConfigEntity.sendMenuRefresh = false
            emailConfigEntity.garbageMenuRefresh = false
            emailConfigEntity.deleteMenuRefresh = false
            emailConfigEntity.choose = true
            when(emailConfigEntity.emailType)
            {
                "1"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = "Drafts"
                    emailConfigEntity.sendMenu = "Sent Messages"
                    emailConfigEntity.garbageMenu = "Junk"
                    emailConfigEntity.deleteMenu = "Deleted Messages"
                }
                "2"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = "Drafts"
                    emailConfigEntity.sendMenu = "Sent Messages"
                    emailConfigEntity.garbageMenu = "Junk"
                    emailConfigEntity.deleteMenu = "Deleted Messages"
                }
                "3"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = "草稿箱"
                    emailConfigEntity.sendMenu = "已发送"
                    emailConfigEntity.garbageMenu = "垃圾邮件"
                    emailConfigEntity.deleteMenu = "已删除"
                }
                "4"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = "[Gmail]/草稿"
                    emailConfigEntity.sendMenu = "[Gmail]/已发邮件"
                    emailConfigEntity.garbageMenu = "[Gmail]/垃圾邮件"
                    emailConfigEntity.deleteMenu = "[Gmail]/已删除邮件"
                }
                "5"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = "Drafts"
                    emailConfigEntity.sendMenu = "Sent"
                    emailConfigEntity.garbageMenu = "Junk"
                    emailConfigEntity.deleteMenu = "Deleted"
                }
                "6"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = ""
                    emailConfigEntity.sendMenu = ""
                    emailConfigEntity.garbageMenu = ""
                    emailConfigEntity.deleteMenu = ""
                }
                "255"->
                {
                    //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
                    emailConfigEntity.inboxMenu = "INBOX"
                    emailConfigEntity.nodeMenu = "node"
                    emailConfigEntity.starMenu = "star"
                    emailConfigEntity.drafMenu = ""
                    emailConfigEntity.sendMenu = ""
                    emailConfigEntity.garbageMenu = ""
                    emailConfigEntity.deleteMenu = ""
                }
            }
            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.insert(emailConfigEntity)
        }
        var menuList = arrayListOf<String>( ConstantValue.currentEmailConfigEntity!!.inboxMenu,ConstantValue.currentEmailConfigEntity!!.drafMenu,ConstantValue.currentEmailConfigEntity!!.sendMenu,ConstantValue.currentEmailConfigEntity!!.garbageMenu,ConstantValue.currentEmailConfigEntity!!.deleteMenu)
        if(ConstantValue.currentEmailConfigEntity!!.userId == null || ConstantValue.currentEmailConfigEntity!!.userId == "")
        {
            Islands.circularProgress(this)
                    .setCancelable(false)
                    .setMessage(getString(R.string.waiting))
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .imapReceiveAsynCount(this, object : GetCountCallback {
                                    override fun gainSuccess(messageList: List<EmailCount>, count: Int) {
                                        //progressDialog.dismiss()
                                        runOnUiThread {
                                            closeProgressDialog()
                                            //toast(R.string.Over_configure)
                                        }
                                        if(messageList.size >0)
                                        {
                                            var emailMessage = messageList.get(0)
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(account)).list()
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

                                                emailConfigEntity.inboxMaxMessageId = emailMessage.inboxMaxMessageId
                                                emailConfigEntity.inboxMinMessageId = emailMessage.inboxMinMessageId
                                                emailConfigEntity.nodeMaxMessageId = emailMessage.nodeMaxMessageId
                                                emailConfigEntity.nodeMinMessageId = emailMessage.nodeMinMessageId

                                                emailConfigEntity.starMaxMessageId = emailMessage.starMaxMessageId
                                                emailConfigEntity.starMinMessageId = emailMessage.starMinMessageId
                                                emailConfigEntity.drafMaxMessageId = emailMessage.drafMaxMessageId
                                                emailConfigEntity.drafMinMessageId = emailMessage.drafMinMessageId
                                                emailConfigEntity.sendMaxMessageId = emailMessage.sendMaxMessageId
                                                emailConfigEntity.sendMinMessageId = emailMessage.sendMinMessageId
                                                emailConfigEntity.garbageMaxMessageId = emailMessage.garbageMaxMessageId
                                                emailConfigEntity.garbageMinMessageId = emailMessage.garbageMinMessageId
                                                emailConfigEntity.deleteMaxMessageId = emailMessage.deleteMaxMessageId
                                                emailConfigEntity.deleteMinMessageId = emailMessage.deleteMinMessageId

                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                            }
                                        }
                                        EventBus.getDefault().post(ChangeEmailConfig())
                                        finish()
                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        //Toast.makeText(AppConfig.instance, "IMAP邮件收取失败", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                },menuList)
                    }
        }else{
            var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,AppConfig.instance.emailConfig().account);
            Islands.circularProgress(this)
                    .setCancelable(false)
                    .setMessage(getString(R.string.waiting))
                    .run { progressDialog ->
                        val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                        emailReceiveClient
                                .gmaiApiAsynCount(this, object : GetCountCallback {
                                    override fun gainSuccess(messageList: List<EmailCount>, count: Int) {
                                        //progressDialog.dismiss()
                                        runOnUiThread {
                                            closeProgressDialog()
                                            //toast(R.string.Over_configure)
                                        }
                                        if(messageList.size >0)
                                        {
                                            var emailMessage = messageList.get(0)
                                            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(account)).list()
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

                                                emailConfigEntity.inboxMaxMessageId = emailMessage.inboxMaxMessageId
                                                emailConfigEntity.inboxMinMessageId = emailMessage.inboxMinMessageId
                                                emailConfigEntity.nodeMaxMessageId = emailMessage.nodeMaxMessageId
                                                emailConfigEntity.nodeMinMessageId = emailMessage.nodeMinMessageId

                                                emailConfigEntity.starMaxMessageId = emailMessage.starMaxMessageId
                                                emailConfigEntity.starMinMessageId = emailMessage.starMinMessageId
                                                emailConfigEntity.drafMaxMessageId = emailMessage.drafMaxMessageId
                                                emailConfigEntity.drafMinMessageId = emailMessage.drafMinMessageId
                                                emailConfigEntity.sendMaxMessageId = emailMessage.sendMaxMessageId
                                                emailConfigEntity.sendMinMessageId = emailMessage.sendMinMessageId
                                                emailConfigEntity.garbageMaxMessageId = emailMessage.garbageMaxMessageId
                                                emailConfigEntity.garbageMinMessageId = emailMessage.garbageMinMessageId
                                                emailConfigEntity.deleteMaxMessageId = emailMessage.deleteMaxMessageId
                                                emailConfigEntity.deleteMinMessageId = emailMessage.deleteMinMessageId

                                                ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                                                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                            }
                                            EventBus.getDefault().post(ChangeEmailConfig())
                                            finish()
                                        }else{
                                            progressDialog.dismiss()
                                        }

                                    }

                                    override fun gainFailure(errorMsg: String) {
                                        progressDialog.dismiss()
                                        //Toast.makeText(AppConfig.instance, "IMAP邮件收取失败", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                },menuList,gmailService,"me")
                    }
        }

    }
    override fun setupActivityComponent() {
        DaggerEmailConfigComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailConfigModule(EmailConfigModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EmailConfigContract.EmailConfigContractPresenter) {
        mPresenter = presenter as EmailConfigPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.saveEmailConfCallback = null
        super.onDestroy()
    }
}