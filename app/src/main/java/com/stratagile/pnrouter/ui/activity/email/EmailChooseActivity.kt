package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetCountCallback
import com.smailnet.eamil.EmailCount
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.islands.Islands
import com.socks.library.KLog
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
import com.stratagile.pnrouter.entity.SaveEmailConf
import com.stratagile.pnrouter.entity.events.ChangeEmailConfig
import com.stratagile.pnrouter.gmail.GmailHelper
import com.stratagile.pnrouter.gmail.GmailQuickstart
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailChooseComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailChooseContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailChooseModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailChoosePresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import kotlinx.android.synthetic.main.email_choose_activity.*
import kotlinx.android.synthetic.main.email_login_activity.*
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.emailpassword_bar.*
import org.greenrobot.eventbus.EventBus

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/10 17:41:08
 */

class EmailChooseActivity : BaseActivity(), EmailChooseContract.View ,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PNRouterServiceMessageReceiver.SaveEmailConfCallback {
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

                //AppConfig.instance.emailConfig().setAccount(accountOld).setPassword(passwordOld).setEmailType(emailTypeOld)
                runOnUiThread {
                    sycDataCountIMAP()
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
    override fun onConnected(p0: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Inject
    internal lateinit var mPresenter: EmailChoosePresenter
    var RC_SIGN_IN= 10001
    var mGoogleApiClient: GoogleApiClient? = null;
    var account =""
    var userId = "";
    var password =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_choose_activity)
    }
    override fun initData() {
        var gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)//* FragmentActivity *//**//* OnConnectionFailedListener *//*
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        title.text = getString(R.string.NewAccount)
        //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱
        qqCompany.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.exmail.qq.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.exmail.qq.com")
                    .setPopPort(995)
                    .setImapHost("imap.exmail.qq.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("SSL/TLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","1")
            startActivity(Intent)
            finish()
        }
        qq.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.qq.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.qq.com")
                    .setPopPort(995)
                    .setImapHost("imap.qq.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("SSL/TLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","2")
            startActivity(Intent)
            finish()
        }
        wangyi.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.163.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.163.com")
                    .setPopPort(995)
                    .setImapHost("imap.163.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("SSL/TLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","3")
            startActivity(Intent)
            finish()
        }
        gmail.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.gmail.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.gmail.com")
                    .setPopPort(995)
                    .setImapHost("imap.gmail.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("SSL/TLS")
            if(ConstantValue.isGooglePlayServicesAvailable)
            {

                var signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }else{
                var Intent = Intent(this, EmailLoginActivity::class.java)
                Intent.putExtra("emailType","4")
                startActivity(Intent)
                finish()
            }
        }
        outlook.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.office365.com")
                    .setSmtpPort(587)
                    .setPopHost("outlook.office365.com")
                    .setPopPort(995)
                    .setImapHost("outlook.office365.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("STARTTLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","5")
            startActivity(Intent)
            finish()
        }
        icloud.visibility = View.GONE
        otherEmail.visibility = View.VISIBLE
        icloud.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.mail.me.com")
                    .setSmtpPort(587)
                    .setPopHost("pop.mail.me.com")
                    .setPopPort(995)
                    .setImapHost("imap.mail.me.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("STARTTLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","6")
            startActivity(Intent)
            finish()
        }
        exchange.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.office365.com")
                    .setSmtpPort(587)
                    .setPopHost("outlook.office365.com")
                    .setPopPort(995)
                    .setImapHost("outlook.office365.com")
                    .setImapPort(993)
                    .setImapEncrypted("SSL/TLS")
                    .setSmtpEncrypted("STARTTLS")
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","7")
            startActivity(Intent)
            finish()
        }
        otherEmail.setOnClickListener {
            var Intent = Intent(this, EmailConfigActivity::class.java)
            Intent.putExtra("emailType","255")
            startActivity(Intent)
            finish()
        }
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private fun handleSignInResult(result: GoogleSignInResult){
        KLog.i("robin"+ "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            toast("成功")
            KLog.i("robin"+ "成功");
            var acct = result.getSignInAccount();
            if(acct!=null){
                KLog.i("robin"+"用户名是:" + acct.getDisplayName());
                toast("用户email是:" + acct.getEmail())
                KLog.i("robin"+"用户email是:" + acct.getEmail());
                KLog.i("robin"+ "用户头像是:" + acct.getPhotoUrl());
                KLog.i("robin"+ "用户Id是:" + acct.getId());//之后就可以更新UI了
                toast("用户Id是:" + acct.getId())
                KLog.i("robin"+ "用户IdToken是:" + acct.getIdToken());
                account = acct!!.getEmail()!!
                userId = acct!!.getId()!!
                var pulicSignKey = ConstantValue.libsodiumpublicSignKey!!
                var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                var saveEmailConf = SaveEmailConf(1,1,accountBase64 ,"", pulicSignKey)
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
                /*Thread(Runnable() {
                    run() {
                        val settings = getPreferences(Context.MODE_PRIVATE)
                        var gmailService = GmailQuickstart.getGmailService(AppConfig.instance);
                        var mailList = GmailHelper.listMessagesMatchingQuery(gmailService,"me","")
                        var cc = ""
                    }}
                ).start()*/

            }
        }else{
            toast("失败")
            KLog.i("robin"+ "没有成功"+result.getStatus());
        }
    }
    override fun setupActivityComponent() {
        DaggerEmailChooseComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailChooseModule(EmailChooseModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EmailChooseContract.EmailChooseContractPresenter) {
        mPresenter = presenter as EmailChoosePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    private fun sycDataCountIMAP()
    {
        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(account)).list()
        var hasVerify = false
        var localemailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
        for (j in localemailConfigEntityList) {
            j.choose = false
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(j)
        }
        if(emailConfigEntityList.size > 0)
        {
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
            emailConfigEntity.account = account
            emailConfigEntity.userId = userId;
            emailConfigEntity.emailType = "4"
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
            emailConfigEntity.account = account
            emailConfigEntity.userId = userId;
            emailConfigEntity.emailType = "4"
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
            //arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
            emailConfigEntity.inboxMenu = "INBOX"
            emailConfigEntity.nodeMenu = "node"
            emailConfigEntity.starMenu = "star"
            emailConfigEntity.drafMenu = "DRAFT"
            emailConfigEntity.sendMenu = "SENT"
            emailConfigEntity.garbageMenu = "SPAM"
            emailConfigEntity.deleteMenu = "TRASH"

            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.insert(emailConfigEntity)
        }
        var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,account);
        var menuList = arrayListOf<String>( ConstantValue.currentEmailConfigEntity!!.inboxMenu,ConstantValue.currentEmailConfigEntity!!.drafMenu,ConstantValue.currentEmailConfigEntity!!.sendMenu,ConstantValue.currentEmailConfigEntity!!.garbageMenu,ConstantValue.currentEmailConfigEntity!!.deleteMenu)
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
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.saveEmailConfCallback = null
        super.onDestroy()
    }
}