package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskInformationComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskInformationContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskInformationModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskInformationPresenter
import kotlinx.android.synthetic.main.activity_disk_information.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 15:21:12
 */

class DiskInformationActivity : BaseActivity(), DiskInformationContract.View {

    @Inject
    internal lateinit var mPresenter: DiskInformationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_information)
    }
    override fun initData() {
        title.text = "Disk A"
        configurate_disk.setOnClickListener {
            startActivity(Intent(this, DiskConfigureActivity::class.java))
        }
    }

    override fun setupActivityComponent() {
       DaggerDiskInformationComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskInformationModule(DiskInformationModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskInformationContract.DiskInformationContractPresenter) {
            mPresenter = presenter as DiskInformationPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}