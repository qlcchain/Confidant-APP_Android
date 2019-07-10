package com.stratagile.pnrouter.ui.activity.email

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailChooseComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailChooseContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailChooseModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailChoosePresenter

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
//        setContentView(R.layout.activity_emailChoose)
    }
    override fun initData() {

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