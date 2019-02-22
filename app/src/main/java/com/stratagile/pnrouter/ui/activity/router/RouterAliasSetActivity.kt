package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.JResetRouterNameRsp
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginSuccessActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterAliasSetComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAliasSetContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterAliasSetModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAliasSetPresenter
import kotlinx.android.synthetic.main.activity_routeraliasset.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/02/21 11:00:31
 */

class RouterAliasSetActivity : BaseActivity(), RouterAliasSetContract.View, PNRouterServiceMessageReceiver.ResetRouterNameCallBack {
    override fun ResetRouterName(jResetRouterNameRsp: JResetRouterNameRsp) {

    }

    @Inject
    internal lateinit var mPresenter: RouterAliasSetPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_routeraliasset)
    }
    override fun initData() {
        LoginInBtn.setOnClickListener {
            var intent = Intent(this, AdminLoginSuccessActivity::class.java)
            intent.putExtra("adminRouterId",intent.getStringExtra("adminRouterId"))
            intent.putExtra("adminUserSn",intent.getStringExtra("adminUserSn"))
            intent.putExtra("adminIdentifyCode",intent.getStringExtra("adminIdentifyCode"))
            intent.putExtra("adminQrcode",intent.getStringExtra("adminQrcode"))
            intent.putExtra("routerName",intent.getStringExtra("routerName"))
            startActivity(intent)
        }
    }

    override fun setupActivityComponent() {
       DaggerRouterAliasSetComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerAliasSetModule(RouterAliasSetModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterAliasSetContract.RouterAliasSetContractPresenter) {
            mPresenter = presenter as RouterAliasSetPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}