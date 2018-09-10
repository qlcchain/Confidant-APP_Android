package com.stratagile.pnrouter.ui.activity.login

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.login.component.DaggerLoginActivityComponent
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/10 15:05:29
 */

class LoginActivityActivity : BaseActivity(), LoginActivityContract.View {

    @Inject
    internal lateinit var mPresenter: LoginActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_loginActivity)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerLoginActivityComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .loginActivityModule(LoginActivityModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: LoginActivityContract.LoginActivityContractPresenter) {
            mPresenter = presenter as LoginActivityPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}