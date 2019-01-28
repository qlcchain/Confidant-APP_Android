package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskManagementComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskManagementContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskManagementModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskManagementPresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 11:29:37
 */

class DiskManagementActivity : BaseActivity(), DiskManagementContract.View {

    @Inject
    internal lateinit var mPresenter: DiskManagementPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_management)
    }
    override fun initData() {
        title.text = resources.getText(R.string.disk_management)
    }

    override fun setupActivityComponent() {
       DaggerDiskManagementComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskManagementModule(DiskManagementModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskManagementContract.DIsManagementContractPresenter) {
            mPresenter = presenter as DiskManagementPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}