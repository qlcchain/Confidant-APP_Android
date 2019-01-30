package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hyphenate.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.file.component.DaggerPdfViewComponent
import com.stratagile.pnrouter.ui.activity.file.contract.PdfViewContract
import com.stratagile.pnrouter.ui.activity.file.module.PdfViewModule
import com.stratagile.pnrouter.ui.activity.file.presenter.PdfViewPresenter
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.FileUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_pdf_view.*
import rx.lang.scala.schedulers.AndroidMainThreadScheduler
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit
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
        var fileMiPath = intent.getStringExtra("fileMiPath")
        var fileMiName = fileMiPath.substring(fileMiPath.lastIndexOf("/")+1,fileMiPath.length)
        var base58Name =  String(Base58.decode(fileMiName))
        var filePath = PathUtils.getInstance().filePath.toString()+"/"+base58Name
        var file = File(filePath)
        if (file.exists()) {
            progressBar.visibility = View.GONE
           /* if (intent.getStringExtra("filePath").contains(".pdf")) {
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
            }*/
            tvFileOpreate.text = "Open with other applications"
            tvFileOpreate.setOnClickListener {
                openFile(filePath)
            }
        } else {
            llNoFile.visibility = View.VISIBLE
            tvFileName.text = file.name
            //tvFileSie.text = "100KB"
            tvFileOpreate.setOnClickListener {
                downLoadFile()
            }
        }
    }

    fun downLoadFile() {
        tvFileOpreate.text = "Cancel Download"
        tvFileOpreate.setTextColor(resources.getColor(R.color.mainColor))
        tvFileOpreate.background = resources.getDrawable(R.drawable.filedownload_bg)
        progressBar.visibility = View.VISIBLE
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(101)
                .map {
                    KLog.i("" + it)
                    progressBar.progress = it.toInt()
                }
                .doOnSubscribe {

                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }

    }
    fun openFile(filePath:String)
    {
        var fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
        var file = File(filePath)
        if (file.exists())
            if(fileName.indexOf("jpg") > -1 || fileName.indexOf("jpeg") > -1 || fileName.indexOf("png") > -1 )
            {
                val intent = Intent(AppConfig.instance, EaseShowBigImageActivity::class.java)
                val file = File(filePath)
                val uri = Uri.fromFile(file)
                intent.putExtra("uri", uri)
                startActivity(intent)
            }else if(fileName.indexOf("mp4") > -1 )
            {
                val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                intent.putExtra("path", filePath)
                startActivity(intent)
            }else{
                run {
                    val newFilePath = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/temp/" + file.name
                    val result = FileUtil.copyAppFileToSdcard(filePath, newFilePath)
                    if (result == 1) {
                        try {
                            OpenFileUtil.getInstance(AppConfig.instance)
                            val intent = OpenFileUtil.openFile(newFilePath)
                            startActivity(intent)
                            //FileUtils.openFile(file, (Activity) getContext());
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        Toast.makeText(AppConfig.instance, R.string.open_error, Toast.LENGTH_SHORT).show()
                    }

                }
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