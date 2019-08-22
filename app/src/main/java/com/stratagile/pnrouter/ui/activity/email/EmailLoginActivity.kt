package com.stratagile.pnrouter.ui.activity.email

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GetConnectCallback
import com.smailnet.eamil.Callback.GetCountCallback
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
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JSaveEmailConfRsp
import com.stratagile.pnrouter.entity.LoginReq_V4
import com.stratagile.pnrouter.entity.SaveEmailConf
import com.stratagile.pnrouter.entity.events.ChangeEmailConfig
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailLoginComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailLoginContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailLoginModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailLoginPresenter
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import kotlinx.android.synthetic.main.email_login_activity.*
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.emailpassword_bar.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/02 15:20:41
 */

class EmailLoginActivity : BaseActivity(), EmailLoginContract.View, PNRouterServiceMessageReceiver.SaveEmailConfCallback  {

    @Inject
    internal lateinit var mPresenter: EmailLoginPresenter
    var emailTypeOld: String? = null       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱
    var accountOld =""
    var passwordOld =""
    var emailType: String? = null       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱
    var account =""
    var password =""
    var settings = 0;

    override fun saveEmailConf(jSaveEmailConfRsp: JSaveEmailConfRsp) {
        if(jSaveEmailConfRsp.params.retCode == 0)
        {
            runOnUiThread {
                sycDataCountIMAP()
            }
        }else if(jSaveEmailConfRsp.params.retCode == 1)
        {
            AppConfig.instance.emailConfig().setAccount(accountOld).setPassword(passwordOld).setEmailType(emailTypeOld)
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
                AppConfig.instance.emailConfig().setAccount(accountOld).setPassword(passwordOld).setEmailType(emailTypeOld)
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.The_mailbox_has_been_configured)

                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_login_activity)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.saveEmailConfCallback = this
        emailType = intent.getStringExtra("emailType")
        emailTypeOld = intent.getStringExtra("emailType")
        if(AppConfig.instance.emailConfig().account != null)
        {
            accountOld = AppConfig.instance.emailConfig().account
            passwordOld = AppConfig.instance.emailConfig().password
        }
       if(intent.hasExtra("settings"))
       {
           settings = intent.getIntExtra("settings",0)
       }

        if(settings == 1)
        {
            account_editText.setText(accountOld)
            account_editText.isEnabled = false
        }else{
            account_editText.setText("")
            account_editText.isEnabled = true
        }
        when(emailType)
        {
            "1"->
            {
                emailHelper.setText(getString(R.string.qqCompany_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_qqmailbox_n))
            }
            "2"->
            {
                emailHelper.setText(getString(R.string.qq_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_qq_n))
            }
            "3"->
            {
                emailHelper.setText(getString(R.string.wangyi_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_163_n))
            }
            "4"->
            {
                emailHelper.setText(getString(R.string.gmail_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_google_n))
            }
            "5"->
            {
                emailHelper.setText(getString(R.string.hotlook_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_outlook_n))
            }
            "6"->
            {
                emailHelper.setText(getString(R.string.icloud_Guides))
                emailLogo.setImageDrawable(resources.getDrawable(R.mipmap.email_icon_icloud_n))
            }
        }
        if(BuildConfig.DEBUG)
        {
            when(emailType)
            {
                "1"->
                {
                    account_editText.setText("emaildev@qlink.mobi")
                    password_editText.setText("Qlcchain@123")
                }
                "2"->
                {
                    account_editText.setText("283619512@qq.com")
                    password_editText.setText("kpagrlcmliolbjii")
                }
                "3"->
                {
                    account_editText.setText("bitcoin108@163.com")
                    password_editText.setText("lang108")
                }
                "4"->
                {
                    account_editText.setText("bitcoin108@163.com")
                    password_editText.setText("lang108")
                }
                "5"->
                {
                    account_editText.setText("zhanglang108@hotmail.com")
                    password_editText.setText("langlang_108")
                }
                "6"->
                {
                    account_editText.setText("kzh1989@icloud.com")
                    password_editText.setText("ewfj-edtl-ydhs-ehml")
                }
            }
        }
        title.text = getString(R.string.NewAccount)
        login.setOnClickListener {
            //配置发件服务器
            account = account_editText.getText().toString().trim()
            password = password_editText.getText().toString().trim()
            account = account.trim().toLowerCase()
            password = password.trim()
            if(account.equals(""))
            {
                toast(R.string.NeedAccount)
                return@setOnClickListener
            }
            if(password.equals("")  )
            {
                toast(R.string.NeedPassword)
                return@setOnClickListener
            }
            showProgressDialog()
            Islands.circularProgress(this)
                    .setMessage(getString(R.string.loading))
                    .setCancelable(false)
                    .run { progressDialog -> login(progressDialog) }
        }
    }

    override fun setupActivityComponent() {
        DaggerEmailLoginComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailLoginModule(EmailLoginModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EmailLoginContract.EmailLoginContractPresenter) {
        mPresenter = presenter as EmailLoginPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    /**
     * 登录邮箱
     *
     * @param progressDialog
     */
    private fun login(progressDialog: ProgressDialog) {

        AppConfig.instance.emailConfig()
                .setAccount(account)
                .setPassword(password)
                .setEmailType(emailType)

        val emailExamine = EmailExamine(AppConfig.instance.emailConfig())
        emailExamine.connectServer(this, object : GetConnectCallback {
            override fun loginSuccess() {
                //progressDialog.dismiss()
                //showProgressDialog(getString(R.string.waiting))
                if(settings == 1)
                {
                    var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(account)).list()
                    if(emailConfigEntityList.size > 0) {
                        var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                        emailConfigEntity.password = AppConfig.instance.emailConfig().password
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
                AppConfig.instance.emailConfig().setAccount(accountOld).setPassword(passwordOld).setEmailType(emailTypeOld)
                try {
                    Islands.ordinaryDialog(this@EmailLoginActivity)
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
            emailConfigEntity.emailType = emailType
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
            emailConfigEntity.emailType = emailType
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
            when(emailType)
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
            }
            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.insert(emailConfigEntity)
        }
        var menuList = arrayListOf<String>( ConstantValue.currentEmailConfigEntity!!.inboxMenu,ConstantValue.currentEmailConfigEntity!!.drafMenu,ConstantValue.currentEmailConfigEntity!!.sendMenu,ConstantValue.currentEmailConfigEntity!!.garbageMenu,ConstantValue.currentEmailConfigEntity!!.deleteMenu)
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
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.saveEmailConfCallback = null
        super.onDestroy()
    }
}