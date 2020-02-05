package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionListPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/05 14:48:11
 */

class SMSEncryptionListActivity : BaseActivity(), SMSEncryptionListContract.View {

    @Inject
    internal lateinit var mPresenter: SMSEncryptionListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_sms_list)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerSMSEncryptionListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionListModule(SMSEncryptionListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionListContract.SMSEncryptionListContractPresenter) {
            mPresenter = presenter as SMSEncryptionListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}