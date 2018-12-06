package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterCreateUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterCreateUserContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterCreateUserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterCreateUserPresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 17:59:39
 */

class RouterCreateUserActivity : BaseActivity(), RouterCreateUserContract.View {

    @Inject
    internal lateinit var mPresenter: RouterCreateUserPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_adduser)
    }
    override fun initData() {
        title.text = getString(R.string.Createuseraccounts)
    }

    override fun setupActivityComponent() {
       DaggerRouterCreateUserComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerCreateUserModule(RouterCreateUserModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterCreateUserContract.RouterCreateUserContractPresenter) {
            mPresenter = presenter as RouterCreateUserPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}