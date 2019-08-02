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
    var emailType: String? = null       //邮件类型  //1：qq企业邮箱   //2：qq邮箱   //3：163邮箱   //4：gmail邮箱

    override fun saveEmailConf(jSaveEmailConfRsp: JSaveEmailConfRsp) {
        if(jSaveEmailConfRsp.params.retCode == 0)
        {
            runOnUiThread {
                sycDataCountIMAP()
            }
        }else{
            runOnUiThread {
                closeProgressDialog()
                sycDataCountIMAP()
                //toast(R.string.Over_configure)
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
        when(emailType)
        {
            "1"->
            {

            }
            "2"->
            {

            }
        }
        title.text = getString(R.string.NewAccount)
        login.setOnClickListener {
            Islands
                    .circularProgress(this)
                    .setMessage(getString(R.string.loading))
                    .setCancelable(false)
                    .show()
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
        //配置发件服务器
        if(account_editText.getText().toString().equals(""))
        {
            toast(R.string.Account)
            return;
        }
        if(password_editText.getText().toString().equals("")  )
        {
            toast(R.string.Password)
            return;
        }
        AppConfig.instance.emailConfig()
                .setAccount(account_editText.getText().toString())
                .setPassword(password_editText.getText().toString())

        val emailExamine = EmailExamine(AppConfig.instance.emailConfig())
        emailExamine.connectServer(this, object : GetConnectCallback {
            override fun loginSuccess() {
                progressDialog.dismiss()

                showProgressDialog(getString(R.string.waiting))
                var pulicSignKey = ConstantValue.libsodiumpublicSignKey!!
                var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                var saveEmailConf = SaveEmailConf(1,1,accountBase64 ,"", pulicSignKey)
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
                //sycDataCountIMAP()
                /* startActivity(Intent(this@EmailLoginActivity, EmailMainActivity::class.java))
                 finish()*/
            }

            override fun loginFailure(errorMsg: String) {
                progressDialog.dismiss()
                Islands.ordinaryDialog(this@EmailLoginActivity)
                        .setText(null, getString(R.string.fail))
                        .setButton( getString(R.string.close), null, null)
                        .click()
                        .show()
            }
        })

    }
    private fun sycDataCountIMAP()
    {
        var account =  account_editText.getText().toString()
        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(account)).list()
        var hasVerify = false
        if(emailConfigEntityList.size > 0)
        {
            var localemailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
            for (j in localemailConfigEntityList) {
                j.choose = false
                AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(j)
            }
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

                }
                "4"->
                {

                }
            }
            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.insert(emailConfigEntity)
        }
        EventBus.getDefault().post(ChangeEmailConfig())
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
                                    closeProgressDialog()
                                    if(messageList.size >0)
                                    {
                                        var emailMessage = messageList.get(0)
                                        var account =  account_editText.getText().toString()
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
                                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                                        }
                                    }

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