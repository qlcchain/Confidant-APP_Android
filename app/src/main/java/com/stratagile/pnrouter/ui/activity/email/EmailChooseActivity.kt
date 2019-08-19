package com.stratagile.pnrouter.ui.activity.email

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailChooseComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailChooseContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailChooseModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailChoosePresenter
import kotlinx.android.synthetic.main.email_choose_activity.*
import kotlinx.android.synthetic.main.email_login_activity.*
import kotlinx.android.synthetic.main.emailname_bar.*
import kotlinx.android.synthetic.main.emailpassword_bar.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/10 17:41:08
 */

class EmailChooseActivity : BaseActivity(), EmailChooseContract.View {

    @Inject
    internal lateinit var mPresenter: EmailChoosePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_choose_activity)
    }
    override fun initData() {
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
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","4")
            startActivity(Intent)
            finish()
        }
        outlook.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.office365.com")
                    .setSmtpPort(587)
                    .setPopHost("outlook.office365.com")
                    .setPopPort(995)
                    .setImapHost("outlook.office365.com")
                    .setImapPort(993)
            var Intent = Intent(this, EmailLoginActivity::class.java)
            Intent.putExtra("emailType","5")
            startActivity(Intent)
            finish()
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

}