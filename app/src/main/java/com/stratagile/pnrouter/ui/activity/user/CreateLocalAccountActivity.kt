package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerCreateLocalAccountComponent
import com.stratagile.pnrouter.ui.activity.user.contract.CreateLocalAccountContract
import com.stratagile.pnrouter.ui.activity.user.module.CreateLocalAccountModule
import com.stratagile.pnrouter.ui.activity.user.presenter.CreateLocalAccountPresenter
import kotlinx.android.synthetic.main.activity_create_local_account.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/02/20 14:14:35
 */

class CreateLocalAccountActivity : BaseActivity(), CreateLocalAccountContract.View {

    @Inject
    internal lateinit var mPresenter: CreateLocalAccountPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_create_local_account)
    }
    override fun initData() {
        title.text = getString(R.string.create_an_account)
        importOtherAccount.setOnClickListener {
            startActivity(Intent(this, ImportAccountActivity::class.java))
        }
    }

    override fun setupActivityComponent() {
       DaggerCreateLocalAccountComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .createLocalAccountModule(CreateLocalAccountModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: CreateLocalAccountContract.CreateLocalAccountContractPresenter) {
            mPresenter = presenter as CreateLocalAccountPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}