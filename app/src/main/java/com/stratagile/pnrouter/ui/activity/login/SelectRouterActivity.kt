package com.stratagile.pnrouter.ui.activity.login

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.login.component.DaggerSelectRouterComponent
import com.stratagile.pnrouter.ui.activity.login.contract.SelectRouterContract
import com.stratagile.pnrouter.ui.activity.login.module.SelectRouterModule
import com.stratagile.pnrouter.ui.activity.login.presenter.SelectRouterPresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2018/09/12 13:59:14
 */

class SelectRouterActivity : BaseActivity(), SelectRouterContract.View {

    @Inject
    internal lateinit var mPresenter: SelectRouterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_select_router)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerSelectRouterComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectRouterModule(SelectRouterModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectRouterContract.SelectRouterContractPresenter) {
            mPresenter = presenter as SelectRouterPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}