package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.view.InflateException
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.entity.OtherEmailConfig
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailConfigComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailConfigContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailConfigModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailConfigPresenter
import com.stratagile.pnrouter.ui.activity.email.view.ContactSortModel
import kotlinx.android.synthetic.main.email_otherconfig_activity.*
import kotlinx.android.synthetic.main.email_selectfriend.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/08/20 16:58:53
 */

class EmailConfigActivity : BaseActivity(), EmailConfigContract.View {

    @Inject
    internal lateinit var mPresenter: EmailConfigPresenter
    protected val REQUEST_CODE_IN = 101
    protected val REQUEST_CODE_TO = 102
    private  var otherEmailConfig: OtherEmailConfig?= null

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
        title.text = getString(R.string.IMAP_Email_Settings)
        inEncryptedRoot.setOnClickListener {
            var Intent = Intent(this, EmailConfigEncryptedActivity::class.java)
            startActivityForResult(Intent,REQUEST_CODE_IN)
        }
        outEncryptedRoot.setOnClickListener {
            var Intent = Intent(this, EmailConfigEncryptedActivity::class.java)
            startActivityForResult(Intent,REQUEST_CODE_TO)
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
                            inPort.setText("465")
                        }
                        "STARTTLS" ->
                        {
                            inPort.setText("587")
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
                            inPort.setText("143")
                        }
                        "SSL/TLS" ->
                        {
                            inPort.setText("465")
                        }
                        "STARTTLS" ->
                        {
                            inPort.setText("587")
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
            /*  var intent = Intent()
              intent.putExtra("selectAdressStr", selectAdressStr)
              intent.putExtra("nameAdressStr", nameAdressStr)
              setResult(Activity.RESULT_OK, intent)
              finish()*/
        }
        return super.onOptionsItemSelected(item)
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

}