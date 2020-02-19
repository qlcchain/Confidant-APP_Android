package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEncryptMsgTypeComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgTypeContract
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgTypeModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgTypePresenter

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2020/02/19 16:13:36
 */

class EncryptMsgTypeActivity : BaseActivity(), EncryptMsgTypeContract.View {

    @Inject
    internal lateinit var mPresenter: EncryptMsgTypePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
//        setContentView(R.layout.activity_encryptMsgType)
    }
    override fun initData() {

    }

    override fun setupActivityComponent() {
       DaggerEncryptMsgTypeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .encryptMsgTypeModule(EncryptMsgTypeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: EncryptMsgTypeContract.EncryptMsgTypeContractPresenter) {
            mPresenter = presenter as EncryptMsgTypePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}