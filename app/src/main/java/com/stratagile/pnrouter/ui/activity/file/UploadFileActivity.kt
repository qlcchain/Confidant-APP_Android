package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.file.component.DaggerUploadFileComponent
import com.stratagile.pnrouter.ui.activity.file.contract.UploadFileContract
import com.stratagile.pnrouter.ui.activity.file.module.UploadFileModule
import com.stratagile.pnrouter.ui.activity.file.presenter.UploadFilePresenter

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/25 14:59:07
 */

class UploadFileActivity : BaseActivity(), UploadFileContract.View {

    @Inject
    internal lateinit var mPresenter: UploadFilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_upload_file)
    }
    override fun initData() {
        title.text = "Upload Files"
    }

    override fun setupActivityComponent() {
       DaggerUploadFileComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .uploadFileModule(UploadFileModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: UploadFileContract.UploadFileContractPresenter) {
            mPresenter = presenter as UploadFilePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}