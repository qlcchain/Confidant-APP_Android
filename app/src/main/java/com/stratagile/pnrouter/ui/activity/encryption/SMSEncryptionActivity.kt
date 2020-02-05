package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionPresenter
import com.stratagile.pnrouter.utils.FileUtil
import kotlinx.android.synthetic.main.picencry_sms_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/17 14:47:42
 */

class SMSEncryptionActivity : BaseActivity(), SMSEncryptionContract.View {
    override fun getScanPermissionSuccess() {
        var count = FileUtil.getAllSmsCount(this@SMSEncryptionActivity)
        runOnUiThread {
            localContacts.text = count.toString();
        }
    }

    @Inject
    internal lateinit var mPresenter: SMSEncryptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_sms_list)
    }
    override fun initData() {
        title.text = getString(R.string.SMS)
    }

    override fun setupActivityComponent() {
       DaggerSMSEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionModule(SMSEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionContract.SMSEncryptionContractPresenter) {
            mPresenter = presenter as SMSEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}