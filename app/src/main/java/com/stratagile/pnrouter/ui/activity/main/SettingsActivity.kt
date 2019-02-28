package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSettingsComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SettingsContract
import com.stratagile.pnrouter.ui.activity.main.module.SettingsModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SettingsPresenter
import com.stratagile.pnrouter.utils.SystemUtil
import com.stratagile.pnrouter.utils.VersionUtil
import kotlinx.android.synthetic.main.activity_settings.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/02/28 14:55:22
 */

class SettingsActivity : BaseActivity(), SettingsContract.View {

    @Inject
    internal lateinit var mPresenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_settings)
        title.text = getString(R.string.setting_)
    }
    override fun initData() {
        versionNumber.text =  VersionUtil.getAppVersionName(this)
        llLogout.setOnClickListener {

        }
    }

    fun logout() {

    }

    override fun setupActivityComponent() {
       DaggerSettingsComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .settingsModule(SettingsModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SettingsContract.SettingsContractPresenter) {
            mPresenter = presenter as SettingsPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}