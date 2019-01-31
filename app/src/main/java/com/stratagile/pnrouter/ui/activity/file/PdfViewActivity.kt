package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.bumptech.glide.Glide
import com.hyphenate.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.PullFileReq
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.file.component.DaggerPdfViewComponent
import com.stratagile.pnrouter.ui.activity.file.contract.PdfViewContract
import com.stratagile.pnrouter.ui.activity.file.module.PdfViewModule
import com.stratagile.pnrouter.ui.activity.file.presenter.PdfViewPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_pdf_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    var payLoad:JPullFileListRsp.ParamsBean.PayloadBean? = null
    var fileMiPath:String = ""
    var filePath:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_pdf_view)
        title.text = "FilePreview"
    }
    override fun initData() {
        EventBus.getDefault().register(this)
        fileMiPath = intent.getStringExtra("fileMiPath")
        payLoad = intent.getParcelableExtra<JPullFileListRsp.ParamsBean.PayloadBean>("file")
        var fileMiName = fileMiPath.substring(fileMiPath.lastIndexOf("/")+1,fileMiPath.length)
        var base58Name =  String(Base58.decode(fileMiName))
        filePath = PathUtils.getInstance().filePath.toString()+"/"+base58Name
        var file = File(filePath)
        var fileName = String(Base58.decode(payLoad!!.fileName.substring(payLoad!!.fileName.lastIndexOf("/")+1,payLoad!!.fileName.length)))
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
                pdfView.fromFile(file)
                        .load()
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
                downLoadFile()
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            toast(R.string.Download_failed)
        }else if (fileStatus.result == 2) {
            toast(R.string.Files_100M)
        } else {
            runOnUiThread {
                var fileMiName = payLoad!!.fileName.substring(payLoad!!.fileName.lastIndexOf("/") + 1, payLoad!!.fileName.length)
                if(fileStatus.fileKey.equals(fileMiName))
                {
                    progressBar.progress = fileStatus.segSeqResult * 100 / fileStatus.segSeqTotal
                    if(fileStatus.segSeqResult >= fileStatus.segSeqTotal)
                    {
                        tvFileOpreate.text = "Open with other applications"
                        tvFileOpreate.setTextColor(resources.getColor(R.color.white))
                        tvFileOpreate.background = resources.getDrawable(R.drawable.filepreview_bg)
                        tvFileOpreate.setOnClickListener {
                            openFile(filePath)
                        }
                    }
                    if (progressBar.progress == 100) {
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
    fun downLoadFile() {
        tvFileOpreate.text = "Downloading"
        tvFileOpreate.setOnClickListener {
        }
        tvFileOpreate.setTextColor(resources.getColor(R.color.mainColor))
        tvFileOpreate.background = resources.getDrawable(R.drawable.filedownload_bg)
        progressBar.visibility = View.VISIBLE
        /*Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(101)
                .map {
                    KLog.i("" + it)
                    progressBar.progress = it.toInt()
                }
                .doOnSubscribe {

                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }*/
        var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port + payLoad!!.fileName
        var files_dir = PathUtils.getInstance().filePath.toString() + "/"
        var fileMiName = payLoad!!.fileName.substring(payLoad!!.fileName.lastIndexOf("/") + 1, payLoad!!.fileName.length)
        if (ConstantValue.isWebsocketConnected) {
            //receiveFileDataMap.put(data.msgId.toString(), data)
            var msgId = (System.currentTimeMillis() / 1000).toInt()
            FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, msgId, handler, payLoad!!.userKey,payLoad!!.fileFrom)
        } else {
            //receiveToxFileDataMap.put(fileOrginName,data)
            ConstantValue.receiveToxFileGlobalDataMap.put(fileMiName,payLoad!!.userKey)
            val uploadFile = UpLoadFile(fileMiName,filledUri, 0, true, false, false, 0, 1, 0, false,payLoad!!.userKey, payLoad!!.fileFrom)
            val myRouter = MyFile()
            myRouter.type = 0
            myRouter.userSn = ConstantValue.currentRouterSN
            myRouter.upLoadFile = uploadFile
            LocalFileUtils.insertLocalAssets(myRouter)
            var msgId = (System.currentTimeMillis() / 1000).toInt()
            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgData = PullFileReq(selfUserId!!, selfUserId!!, fileMiName, msgId, payLoad!!.fileFrom, 2)
            var baseData = BaseData(msgData)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
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
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    runOnUiThread {
                        closeProgressDialog()
                        toast(R.string.Download_failed)
                    }
                }
                0x55 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    runOnUiThread {
                        closeProgressDialog()
                        toast(R.string.Download_success)
                    }
                }
            }//goMain();
            //goMain();
        }
    }
    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}