package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileShareSetComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileShareSetContract
import com.stratagile.pnrouter.ui.activity.file.module.FileShareSetModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileShareSetPresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/24 10:26:38
 */

class FileShareSetActivity : BaseActivity(), FileShareSetContract.View {

    @Inject
    internal lateinit var mPresenter: FileShareSetPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_share_set)
    }
    override fun initData() {
        title.text = "Shared Settings"
    }

    override fun setupActivityComponent() {
       DaggerFileShareSetComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .fileShareSetModule(FileShareSetModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FileShareSetContract.FileShareSetContractPresenter) {
            mPresenter = presenter as FileShareSetPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}