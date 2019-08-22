package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailConfigEncryptedComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailConfigEncryptedContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailConfigEncryptedModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailConfigEncryptedPresenter
import kotlinx.android.synthetic.main.email_otherencrypted_activity.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/08/20 17:26:16
 */

class EmailConfigEncryptedActivity : BaseActivity(), EmailConfigEncryptedContract.View {

    @Inject
    internal lateinit var mPresenter: EmailConfigEncryptedPresenter
    var encryptedTypeChoose = "None"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_otherencrypted_activity)
    }
    override fun initData() {
        title.text = getString(R.string.Encrypted)
        encryptedTypeChoose = intent.getStringExtra("encryptedTypeChoose")
        when (encryptedTypeChoose)
        {
            "None" ->
            {
                noneChoose.visibility = View.VISIBLE
            }
            "SSL/TLS" ->
            {
                sslChoose.visibility = View.VISIBLE
            }
            "STARTTLS" ->
            {
                starttlsChoose.visibility = View.VISIBLE
            }
        }
        noneParent.setOnClickListener {
            noneChoose.visibility = View.VISIBLE
            sslChoose.visibility = View.GONE
            starttlsChoose.visibility = View.GONE
            var intent = Intent()
            intent.putExtra("encryptedType", "None")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        sslParent.setOnClickListener {
            noneChoose.visibility = View.GONE
            sslChoose.visibility = View.VISIBLE
            starttlsChoose.visibility = View.GONE
            var intent = Intent()
            intent.putExtra("encryptedType", "SSL/TLS")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        starttlsParent.setOnClickListener {
            noneChoose.visibility = View.GONE
            sslChoose.visibility = View.GONE
            starttlsChoose.visibility = View.VISIBLE
            var intent = Intent()
            intent.putExtra("encryptedType", "STARTTLS")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun setupActivityComponent() {
       DaggerEmailConfigEncryptedComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .emailConfigEncryptedModule(EmailConfigEncryptedModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: EmailConfigEncryptedContract.EmailConfigEncryptedContractPresenter) {
            mPresenter = presenter as EmailConfigEncryptedPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}