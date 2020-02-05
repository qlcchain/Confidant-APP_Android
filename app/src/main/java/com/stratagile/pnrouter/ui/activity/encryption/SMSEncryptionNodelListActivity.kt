package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSMSEncryptionNodelListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelListPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/02/05 14:49:08
 */

class SMSEncryptionNodelListActivity : BaseActivity(), SMSEncryptionNodelListContract.View {

    @Inject
    internal lateinit var mPresenter: SMSEncryptionNodelListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_sMSEncryptionNodelList)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerSMSEncryptionNodelListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .sMSEncryptionNodelListModule(SMSEncryptionNodelListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SMSEncryptionNodelListContract.SMSEncryptionNodelListContractPresenter) {
            mPresenter = presenter as SMSEncryptionNodelListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}