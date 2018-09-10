package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.SpUtil

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        SpUtil.putString(this, ConstantValue.testValue, "test")
    }

    override fun setupActivityComponent() {
       DaggerSplashComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .splashModule(SplashModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SplashContract.SplashContractPresenter) {
            mPresenter = presenter as SplashPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}