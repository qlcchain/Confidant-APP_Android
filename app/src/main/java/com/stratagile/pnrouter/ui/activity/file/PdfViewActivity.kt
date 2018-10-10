package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import android.os.Environment
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.file.component.DaggerPdfViewComponent
import com.stratagile.pnrouter.ui.activity.file.contract.PdfViewContract
import com.stratagile.pnrouter.ui.activity.file.module.PdfViewModule
import com.stratagile.pnrouter.ui.activity.file.presenter.PdfViewPresenter
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2018/10/09 16:03:36
 */

class PdfViewActivity : BaseActivity(), PdfViewContract.View {

    @Inject
    internal lateinit var mPresenter: PdfViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_pdf_view)
    }
    override fun initData() {
        var filePath = "" + Environment.getExternalStorageDirectory() + "/1/接口介绍.pdf"
        var file = File(filePath)
        pdfView.fromFile(file)
                .load()
    }

    override fun setupActivityComponent() {
       DaggerPdfViewComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .pdfViewModule(PdfViewModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PdfViewContract.PdfViewContractPresenter) {
            mPresenter = presenter as PdfViewPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}