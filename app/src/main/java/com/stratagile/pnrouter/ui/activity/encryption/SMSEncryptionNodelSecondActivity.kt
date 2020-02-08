package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionNodelSecondComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelSecondContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelSecondModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelSecondPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/07 23:33:10
 */

class SMSEncryptionNodelSecondActivity : BaseActivity(), SMSEncryptionNodelSecondContract.View {

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelSecondPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_node_sms_list)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerSMSEncryptionNodelSecondComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionNodelSecondModule(SMSEncryptionNodelSecondModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionNodelSecondContract.SMSEncryptionNodelSecondContractPresenter) {
            mPresenter = presenter as SMSEncryptionNodelSecondPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}