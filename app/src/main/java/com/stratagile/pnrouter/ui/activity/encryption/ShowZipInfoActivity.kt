package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.observable.ImagesObservable
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.EmailAttachEntity
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerShowZipInfoComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.ShowZipInfoContract
import com.stratagile.pnrouter.ui.activity.encryption.module.ShowZipInfoModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ShowZipInfoPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiAttachAdapter
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.ZipUnTask
import kotlinx.android.synthetic.main.zip_info_view.*
import java.io.File
import java.io.Serializable
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/30 10:42:26
 */

class ShowZipInfoActivity : BaseActivity(), ShowZipInfoContract.View {

    @Inject
    internal lateinit var mPresenter: ShowZipInfoPresenter
    var zipUnTask: ZipUnTask? = null
    var zipPath:String? = null
    var fileToPath:String? = null
    var id :String? = null
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    var attachListEntityNode =  arrayListOf<EmailAttachEntity>()
    var emaiAttachAdapter : EmaiAttachAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.zip_info_view)
    }
    override fun initData() {
        previewImages = ArrayList()
        zipPath = intent.getStringExtra("path")
        id = intent.getStringExtra("id")
        fileToPath =   PathUtils.generateWechatMessagePath("zipshow"+id)
        var zipFile = File(zipPath)
        if(zipFile.exists())
        {
            showProgressDialog()
            var fileName = zipPath!!.substring(zipPath!!.lastIndexOf("/")+1,zipPath!!.lastIndexOf("."))
            inboxTitle.text = fileName
            zipUnTask = ZipUnTask(zipPath, fileToPath, AppConfig.instance, false, handlerUnZip,true)
            zipUnTask!!.execute()
        }
    }
    internal var handlerUnZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    toast(R.string.Failure_of_decompression)
                }
                0x56 -> {
                    updateUIByZipData()
                }
            }//goMain();
            //goMain();
        }
    }
    fun updateUIByZipData()
    {
        var folderFile = File(fileToPath)
        var subFile = folderFile.listFiles()
        var contentPath = ""
        var attachListEntity =  arrayListOf<EmailAttachEntity>()
        var picIndex = 0
        for(file in subFile)
        {
            var name = file.name
            var path = file.path
            var newFile = path
            if(name == "htmlContent.txt")
            {
                contentPath = newFile;
            }else{
                var emailAttachEntity = EmailAttachEntity()
                emailAttachEntity.isHasData = true
                emailAttachEntity.localPath = newFile
                emailAttachEntity.name = name
                emailAttachEntity.isCanDelete = false
                attachListEntity.add(emailAttachEntity)
                if (name.contains("jpg") || name.contains("JPG")  || name.contains("png")) {
                    val localMedia = LocalMedia()
                    localMedia.isCompressed = false
                    localMedia.duration = 0
                    localMedia.height = 100
                    localMedia.width = 100
                    localMedia.isChecked = false
                    localMedia.isCut = false
                    localMedia.mimeType = 0
                    localMedia.num = 0
                    localMedia.path = newFile
                    localMedia.pictureType = "image/jpeg"
                    localMedia.setPosition(picIndex)
                    localMedia.sortIndex = picIndex
                    previewImages.add(localMedia)
                    ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")
                    picIndex ++;
                }
            }
        }
        if(contentPath != "")
        {
            closeProgressDialog()
            var contentPathFile = File(contentPath)
            var contentHtml = FileUtil.readTxtFile(contentPathFile);
            var headStr = "<head><style>body {font-family: Helvetica;font-size: 16px;word-wrap: break-word;-webkit-text-size-adjust:none;-webkit-nbsp-mode: space;white-space: pre-line;}</style></head>"
            var URLText = "<html>"+headStr+contentHtml+"</div></body>"+"</html>";
            webView.loadDataWithBaseURL(null,URLText,"text/html","utf-8",null);
        }
        if(attachListEntity.size >0)
        {
            attchtitle.visibility = View.VISIBLE
            attachListEntityNode = attachListEntity
            runOnUiThread {
                emaiAttachAdapter = EmaiAttachAdapter(attachListEntity)
                emaiAttachAdapter!!.setOnItemLongClickListener { adapter, view, position ->

                    true
                }
                recyclerViewAttach.setLayoutManager(GridLayoutManager(AppConfig.instance, 2));
                recyclerViewAttach.adapter = emaiAttachAdapter
                emaiAttachAdapter!!.setOnItemClickListener { adapter, view, position ->
                    var emaiAttach = emaiAttachAdapter!!.getItem(position)
                    var fileName = emaiAttach!!.name
                    if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                        showImagList(position)
                    }else if(fileName.contains("mp4"))
                    {
                        val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                        intent.putExtra("path", emaiAttach.localPath)
                        startActivity(intent)
                    }else{
                        OpenFileUtil.getInstance(AppConfig.instance)
                        val intent = OpenFileUtil.openFile(emaiAttach.localPath)
                        startActivity(intent)
                    }
                }
            }
        }
    }
    fun showImagList(showIndex:Int)
    {
        val selectedImages = ArrayList<LocalMedia>()
        val previewImages = ImagesObservable.getInstance().readLocalMedias("chat")
        if (previewImages != null && previewImages.size > 0) {

            val intentPicturePreviewActivity = Intent(this, PicturePreviewActivity::class.java)
            val bundle = Bundle()
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages as Serializable)
            bundle.putInt(PictureConfig.EXTRA_POSITION, showIndex)
            bundle.putString("from", "chat")
            intentPicturePreviewActivity.putExtras(bundle)
            startActivity(intentPicturePreviewActivity)
        }
    }
    override fun setupActivityComponent() {
       DaggerShowZipInfoComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .showZipInfoModule(ShowZipInfoModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ShowZipInfoContract.ShowZipInfoContractPresenter) {
            mPresenter = presenter as ShowZipInfoPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}