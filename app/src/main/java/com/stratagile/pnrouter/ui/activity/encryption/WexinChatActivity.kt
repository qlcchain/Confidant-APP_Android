package com.stratagile.pnrouter.ui.activity.encryption

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.widget.Toast
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileItemDao
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.entity.events.UpdateAlbumEncryptionItemEvent
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWexinChatComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WexinChatContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WexinChatModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WexinChatPresenter
import com.stratagile.pnrouter.ui.activity.main.SplashActivity
import com.stratagile.pnrouter.ui.adapter.conversation.WechatMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.picencry_wechat_list.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/27 16:17:50
 */

class WexinChatActivity : BaseActivity(), WexinChatContract.View {

    @Inject
    internal lateinit var mPresenter: WexinChatPresenter
    var picMenuEncryptionAdapter: WechatMenuEncryptionAdapter? = null
    var sharedTextContent:String? = ""
    var sharedText:String? = ""
    var imageUris:ArrayList<Uri>? = null
    var zipFileSoucePath:MutableList<String> = java.util.ArrayList()
    var zipCompressTask: ZipCompressTask? = null
    var zipSavePath =""
    var zipSavePathTemp =""
    var zipUnTask:ZipUnTask? = null
    var chooseMenuData: LocalFileMenu?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        if(AppConfig.instance.sharedText.equals(""))
        {
            if(intent != null)
            {
                KLog.i("微信调试：11"+this)
                val action = intent.action
                val type = intent.type
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if(sharedText != null && sharedText!!.contains("\n"))
                {
                    sharedTextContent = sharedText!!.substring(0,sharedText!!.indexOf("\n"))
                    if(sharedTextContent!!.contains("如下"))
                    {
                        sharedTextContent = sharedTextContent!!.substring(0,sharedTextContent!!.indexOf("如下"))
                    }
                }
                imageUris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                var size = 0;
                if(imageUris != null)
                {
                    size = imageUris!!.size
                }
                KLog.i("微信,EmailLoginActivity" + action+"#####"+type+"#####"+sharedText +"#####"+ size)
            }
        }else{
            sharedTextContent =  AppConfig.instance.sharedText
            imageUris = AppConfig.instance.imageUris
        }

        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_wechat_list)
    }
    override fun initData() {
        if(!AppConfig.instance.isOpenSplashActivity)
        {
            AppConfig.instance.sharedText=  sharedTextContent
            AppConfig.instance.imageUris=  imageUris
            val intent = Intent(AppConfig.instance, SplashActivity::class.java)
            startActivity(intent)
            return;
        }
        chatName.text = sharedTextContent
        title.text = getString(R.string.Wechat_Records)
        var  path = PathUtils.generateWechatMessagePath("temp")+"htmlContent.txt";
        var  result = FileUtil.writeStr_to_txt(path,sharedText)
        if(result)
        {
            zipFileSoucePath.add(path)
            for (item in imageUris!!)
            {
                //var path: String = getPathByUri(item)
                var realPath = RxFileTool.getFPUriToPath(this,item)
                var fileTemp = File(realPath)
                if(fileTemp.exists())
                {
                    zipFileSoucePath.add(realPath)
                }
            }
            zipSavePath = PathUtils.generateWechatMessagePath("temp")+sharedTextContent+".zip";
            zipCompressTask = ZipCompressTask(zipFileSoucePath!!, zipSavePath, this, false, handlerCompressZip!!)
            zipCompressTask!!.execute()
        }
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("1")).list()
        picMenuEncryptionAdapter = WechatMenuEncryptionAdapter(picMenuList)
        recyclerViewNodeMenu.adapter = picMenuEncryptionAdapter
        picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem -> {
                    var dataList = picMenuEncryptionAdapter!!.data;
                    for(item in dataList)
                    {
                        item.isChoose = false;
                    }
                    var data = picMenuEncryptionAdapter!!.getItem(position)
                    data!!.isChoose = true;
                    picMenuEncryptionAdapter!!.notifyDataSetChanged()
                    chooseMenuData = data
                }
            }
        }
        selectbtn.setOnClickListener()
        {
            //val result = FileUtil.copyAppFileToSdcard(zipSavePath, toFileUrl)

            if(chooseMenuData == null)
            {
                toast(getString(R.string.Please_select_a_folder))
                return@setOnClickListener
            }
            var file = File(zipSavePath);
            var isHas = file.exists();
            if (isHas) {
                var filePath = zipSavePath
                val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                val fileMD5 = FileUtil.getFileMD5(File(filePath))
                var picItemList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileMD5.eq(fileMD5), LocalFileItemDao.Properties.FileId.eq(chooseMenuData!!.id)).list()
                if (picItemList != null && picItemList.size > 0) {
                    toast(imgeSouceName + " " + getString(R.string.file_already_exists))
                    return@setOnClickListener
                }

                var fileSize = file.length();
                val fileKey = RxEncryptTool.generateAESKey()
                var SrcKey = ByteArray(256)
                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val date = simpleDateFormat.format(Date())
                var imgeSouceNamePre = imgeSouceName.substring(0,imgeSouceName.indexOf("."))
                var imgeSouceNameEnd = imgeSouceName.substring(imgeSouceName.indexOf("."),imgeSouceName.length)
                val base58files_dir = chooseMenuData!!.path + "/" + imgeSouceNamePre+"_"+date+imgeSouceNameEnd;
                val code = FileUtil.copySdcardToxFileAndEncrypt(zipSavePath, base58files_dir, fileKey.substring(0, 16))

                if (code == 1) {

                    var localFileItem = LocalFileItem();
                    localFileItem.filePath = base58files_dir;
                    localFileItem.fileName = imgeSouceNamePre+"_"+date+imgeSouceNameEnd;
                    localFileItem.fileSize = fileSize;
                    localFileItem.creatTime = System.currentTimeMillis()
                    localFileItem.fileMD5 = fileMD5;
                    localFileItem.upLoad = false;
                    localFileItem.fileType = 5
                    localFileItem.fileFrom = 0;
                    localFileItem.autor = "";
                    localFileItem.fileId = chooseMenuData!!.id;
                    localFileItem.srcKey = String(SrcKey)
                    AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.insert(localFileItem)
                    var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(chooseMenuData!!.id)).list()
                    if (picMenuList != null && picMenuList.size > 0) {
                        var picMenuItem = picMenuList.get(0)
                        picMenuItem.fileNum += 1;
                        AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                    }
                    toast(imgeSouceName + " " + getString(R.string.Encryption_succeeded))
                    EventBus.getDefault().post(UpdateAlbumEncryptionItemEvent())
                    finish();
                }
            }

        }
    }
    internal var handlerCompressZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                    toast(R.string.Compression_failure)
                }
                0x56 -> {
                    /* var zipFile = File(zipSavePath)
                     if(zipFile.exists())
                     {
                         zipUnTask = ZipUnTask(zipSavePath, PathUtils.generateWechatMessagePath("temp"), AppConfig.instance, false, handlerUnZip,false)
                         zipUnTask!!.execute()
                     }*/
                }
            }//goMain();
            //goMain();
        }
    }
    internal var handlerUnZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    toast(R.string.Failure_of_decompression)
                }
                0x56 -> {

                }
            }//goMain();
            //goMain();
        }
    }
    fun getPathByUri(selectedImage:Uri ):String
    {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor!!.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor!!.getString(columnIndex)
            cursor!!.close()
            cursor = null
            if (picturePath != null &&  picturePath != "null") {
                return picturePath
            }

            //sendImageMessage(picturePath);
        } else {
            val file = File(selectedImage.getPath())
            if (file.exists()) {
                return file.getAbsolutePath()
            }
        }
        return ""
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
    fun getRealPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (null != cursor && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }
    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)
        return null
    }
    override fun setupActivityComponent() {
        DaggerWexinChatComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .wexinChatModule(WexinChatModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: WexinChatContract.WexinChatContractPresenter) {
        mPresenter = presenter as WexinChatPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}