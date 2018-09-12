package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.startActivity
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.SpUtil

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    override fun loginSuccees() {
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToLogin() {
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToGuest() {
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        FileUtil.init()
        AppConfig.instance.getPNRouterServiceMessageReceiver()
        SpUtil.putString(this, ConstantValue.testValue, "test")
        mPresenter.getLastVersion()
        mPresenter.getPermission()
        mPresenter.observeJump()
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