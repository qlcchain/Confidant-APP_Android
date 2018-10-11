package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.file.component.DaggerPdfViewComponent
import com.stratagile.pnrouter.ui.activity.file.contract.PdfViewContract
import com.stratagile.pnrouter.ui.activity.file.module.PdfViewModule
import com.stratagile.pnrouter.ui.activity.file.presenter.PdfViewPresenter
import com.stratagile.pnrouter.utils.FileUtil
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File
import java.lang.Exception
import javax.inject.Inject

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
        title.text = "FilePreview"
    }
    override fun initData() {
        var file = File(intent.getStringExtra("filePath"))
        if (file.exists()) {
            if (intent.getStringExtra("filePath").contains(".pdf")) {
                pdfView.visibility = View.VISIBLE
                pdfView.fromFile(file)
                        .load()
            } else if(intent.getStringExtra("filePath").contains(".txt")){
                scrollView.visibility = View.VISIBLE
                tvText.visibility = View.VISIBLE
                tvText.text = FileUtil.getDataFromFile(file)
            } else if (intent.getStringExtra("filePath").contains(".jpg")) {
                scrollView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                Glide.with(this)
                        .load(file)
                        .into(imageView)
            } else if (isOfficeFile(intent.getStringExtra("filePath")) != null) {
                llNoFile.visibility = View.VISIBLE
                tvFileName.text = file.name
                tvFileSie.text = "100KB"
                tvFileOpreate.text = "Open with other applications"
                tvFileOpreate.setOnClickListener {
                    try {
                        startActivity(isOfficeFile(intent.getStringExtra("filePath")))
                    } catch (ex : Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        } else {
            llNoFile.visibility = View.VISIBLE
            tvFileName.text = file.name
            tvFileSie.text = "100KB"
        }
    }

    fun isOfficeFile(fileName : String) : Intent?{
        if (fileName.contains(".xls") || fileName.contains(".word") || fileName.contains(".ppt")) {
            if (fileName.contains(".xls")) {
                return FileUtil.getExcelFileIntent(fileName)
            } else if (fileName.contains(".word")) {
                return FileUtil.getWordFileIntent(fileName)
            } else if (fileName.contains(".ppt")) {
                return FileUtil.getPPTFileIntent(fileName)
            } else return null
        } else return null
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