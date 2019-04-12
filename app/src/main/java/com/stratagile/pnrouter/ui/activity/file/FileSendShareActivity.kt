package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileSendShareComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileSendShareContract
import com.stratagile.pnrouter.ui.activity.file.module.FileSendShareModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileSendSharePresenter
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_file_sendshare.*
import java.io.File
import java.lang.Exception
import java.util.HashMap

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/04/12 15:17:33
 */

class FileSendShareActivity : BaseActivity(), FileSendShareContract.View {

    @Inject
    internal lateinit var mPresenter: FileSendSharePresenter
    var payLoad:JPullFileListRsp.ParamsBean.PayloadBean? = null
    var fileLocalPath:String = ""
    var filePath:String = ""
    var receiveFileDataMap = HashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun initView() {
        setContentView(R.layout.activity_file_sendshare)
        title.text = "FilePreview"
    }
    override fun initData() {
        //EventBus.getDefault().register(this)
        fileLocalPath = intent.getStringExtra("fileLocalPath")
        var file = File(fileLocalPath)
        var fileName = fileLocalPath.substring(fileLocalPath.lastIndexOf("/")+1,fileLocalPath.length)
        tvFileName.text = file.name
        if (fileName.contains("jpg")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.picture_large))
        } else if (fileName.contains("pdf")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.pdf_large))
        } else if (fileName.contains("mp4")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.video_large))
        } else if (fileName.contains("png")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.picture_large))
        } else if (fileName.contains("txt")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.txt_large))
        } else if (fileName.contains("ppt")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.ppt_large))
        } else if (fileName.contains("xls")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.xls_large))
        } else if (fileName.contains("doc")) {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.doc_large))
        } else {
            ivFileType.setImageDrawable(resources.getDrawable(R.mipmap.other_large))
        }
        if (file.exists()) {
            progressBar.visibility = View.GONE
            if (fileName.contains(".pdf")) {
                pdfView.visibility = View.VISIBLE
                pdfView.fromFile(file).load()
            } else if(fileName.contains(".txt")){
                scrollView.visibility = View.VISIBLE
                tvText.visibility = View.VISIBLE
                tvText.text = FileUtil.getDataFromFile(file)
            } else if (fileName.contains(".jpg")) {
                scrollView.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
                Glide.with(this)
                        .load(file)
                        .into(imageView)
            } else if (isOfficeFile(fileName) != null) {
                llNoFile.visibility = View.VISIBLE
                tvFileName.text = file.name
                tvFileSie.text = "100KB"
                tvFileOpreate.text = "Open with other applications"
                tvFileOpreate.setOnClickListener {
                    try {
                        startActivity(isOfficeFile(fileName))
                    } catch (ex : Exception) {
                        ex.printStackTrace()
                    }
                }
            }
            tvFileOpreate.text = "Open with other applications"
            tvFileOpreate.setOnClickListener {
                openFile(filePath)
            }
        } else {
            llNoFile.visibility = View.VISIBLE

            //tvFileSie.text = "100KB"
            tvFileOpreate.setOnClickListener {
                //downLoadFile()
            }
        }
        send.setOnClickListener {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            val intent = Intent(this, selectFriendActivity::class.java)
            var MsgType = fileLocalPath.substring(fileLocalPath.lastIndexOf(".",fileLocalPath.length))
            var fileName = fileLocalPath.substring(fileLocalPath.lastIndexOf("/",fileLocalPath.length))
            var message = EMMessage.createImageSendMessage(fileLocalPath, true, "")
            when (MsgType) {
                "png", "jpg", "jpeg", "webp" -> message = EMMessage.createImageSendMessage(fileLocalPath, true, userId)
                "mp4" -> {
                    val thumbPath = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                    val bitmap = EaseImageUtils.getVideoPhoto(fileLocalPath)
                    message = EMMessage.createVideoSendMessage(fileLocalPath, thumbPath, 1000, userId)
                }
                else ->  message = EMMessage.createFileSendMessage(fileLocalPath, userId)
            }

            intent.putExtra("fromId", userId)
            intent.putExtra("message", message)
            startActivity(intent)
        }
        upload.setOnClickListener {
            var MsgType = fileLocalPath.substring(fileLocalPath.lastIndexOf(".",fileLocalPath.length))
            var list = ArrayList<LocalMedia>()
            var localMedia = LocalMedia()
            localMedia.path = fileLocalPath
            when (MsgType) {
                "png", "jpg", "jpeg", "webp" -> localMedia.pictureType = "image/jpeg"
                "mp4" -> localMedia.pictureType = "video/mp4"
                else -> localMedia.pictureType = ""
            }
            list.add(localMedia)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
            finish()
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
       DaggerFileSendShareComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .fileSendShareModule(FileSendShareModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FileSendShareContract.FileSendShareContractPresenter) {
            mPresenter = presenter as FileSendSharePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}