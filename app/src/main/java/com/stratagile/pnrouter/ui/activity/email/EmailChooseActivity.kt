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
        qq.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.qq.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.qq.com")
                    .setPopPort(995)
                    .setImapHost("imap.qq.com")
                    .setImapPort(993)
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
        }
        sina.setOnClickListener {
            AppConfig.instance.emailConfig()
                    .setSmtpHost("smtp.sina.com")
                    .setSmtpPort(465)
                    .setPopHost("pop.sina.com")
                    .setPopPort(995)
                    .setImapHost("imap.sina.com")
                    .setImapPort(993)
            startActivity(Intent(this, EmailLoginActivity::class.java))
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