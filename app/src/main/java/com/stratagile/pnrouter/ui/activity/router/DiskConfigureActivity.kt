package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskConfigureComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskConfigureContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskConfigureModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskConfigurePresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 14:51:22
 */

class DiskConfigureActivity : BaseActivity(), DiskConfigureContract.View {

    @Inject
    internal lateinit var mPresenter: DiskConfigurePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_configure)
    }
    override fun initData() {
        title.text = "Configuration Disk"
    }

    override fun setupActivityComponent() {
       DaggerDiskConfigureComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskConfigureModule(DiskConfigureModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskConfigureContract.DiskConfigureContractPresenter) {
            mPresenter = presenter as DiskConfigurePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}