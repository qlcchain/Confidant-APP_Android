package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerImportAccountComponent
import com.stratagile.pnrouter.ui.activity.user.contract.ImportAccountContract
import com.stratagile.pnrouter.ui.activity.user.module.ImportAccountModule
import com.stratagile.pnrouter.ui.activity.user.presenter.ImportAccountPresenter
import kotlinx.android.synthetic.main.activity_import_account.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/02/20 14:43:29
 */

class ImportAccountActivity : BaseActivity(), ImportAccountContract.View {

    @Inject
    internal lateinit var mPresenter: ImportAccountPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_import_account)
    }
    override fun initData() {
        title.text = "Log in"
        ivScan.setOnClickListener {

        }
    }

    override fun setupActivityComponent() {
       DaggerImportAccountComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .importAccountModule(ImportAccountModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ImportAccountContract.ImportAccountContractPresenter) {
            mPresenter = presenter as ImportAccountPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}