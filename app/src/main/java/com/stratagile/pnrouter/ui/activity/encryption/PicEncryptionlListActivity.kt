package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.observable.ImagesObservable
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.Sceen
import com.stratagile.pnrouter.entity.events.UpdateAlbumEncryptionItemEvent
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.events.SanXingEvent
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionlListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionlListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionlListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionlListPresenter
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.encryption_file_list.*
import kotlinx.android.synthetic.main.layout_encryption_file_list_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:22
 */

class PicEncryptionlListActivity : BaseActivity(), PicEncryptionlListContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionlListPresenter
    var picItemEncryptionAdapter: PicItemEncryptionAdapter? = null
    var folderInfo:LocalFileMenu? = null
    protected val REQUEST_CODE_MENU = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    var localMediaUpdate: LocalMedia? = null

    var chooseFileData:LocalFileItem? = null;
    var chooseFolderData:LocalFileMenu? = null;

    var clickTimeMap = ConcurrentHashMap<String, Long>()

    var receiveFileDataMap = ConcurrentHashMap<String, UpLoadFile>()
    var _this:Activity?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }
    override fun initView() {
        setContentView(R.layout.encryption_file_list)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddLocalEncryptionItemEvent(statusChange: UpdateAlbumEncryptionItemEvent) {
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).orderDesc(LocalFileItemDao.Properties.CreatTime).list()
        picItemEncryptionAdapter!!.setNewData(picMenuList)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSanXingEvent(sanXingEvent: SanXingEvent) {
        //selectPicFromLocal()
    }
    override fun initData() {
        _this = this;
        EventBus.getDefault().register(this)
        folderInfo = intent.getParcelableExtra("folderInfo")
        titleShow.text = folderInfo!!.fileName
        initPicPlug()
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).orderDesc(LocalFileItemDao.Properties.CreatTime).list()
        picItemEncryptionAdapter = PicItemEncryptionAdapter(picMenuList)
        recyclerView.adapter = picItemEncryptionAdapter
        /*picItemEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picItemEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }*/
        picItemEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.itemTypeIcon,R.id.itemInfo ->
                {
                    var emaiAttach = picItemEncryptionAdapter!!.getItem(position)
                    var fileName = emaiAttach!!.fileName
                    var filePath= emaiAttach.filePath
                    var fileTempPath  = PathUtils.getInstance().getEncryptionAlbumPath().toString() +"/"+ "temp"
                    var fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    fileTempPath += "/"+fileName;
                    var aesKey = LibsodiumUtil.DecryptShareKey(emaiAttach.srcKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                    var code = FileUtil.copySdcardToxFileAndDecrypt(filePath,fileTempPath,aesKey)
                    if(code == 1)
                    {
                        if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                            showImagList(fileTempPath)
                        }else if(fileName.contains("mp4"))
                        {
                            val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                            intent.putExtra("path", fileTempPath)
                            startActivity(intent)
                        }else{
                            OpenFileUtil.getInstance(AppConfig.instance)
                            val intent = OpenFileUtil.openFile(fileTempPath)
                            startActivity(intent)
                        }
                    }

                }
                R.id.opMenu ->
                {
                    chooseFileData = picItemEncryptionAdapter!!.getItem(position)
                    var menuArray = arrayListOf<String>()
                    var iconArray = arrayListOf<String>()
                    menuArray = arrayListOf<String>(getString(R.string.Node_back_up),getString(R.string.rename),getString(R.string.Delete))
                    iconArray = arrayListOf<String>("statusbar_download_node","sheet_rename","statusbar_delete")
                    var chooseItemPosition = position
                    PopWindowUtil.showPopMenuWindow(this@PicEncryptionlListActivity, opMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            var data = obj as FileOpreateType
                            when (data.name) {
                                "Node back up" -> {
                                    val intent = Intent(AppConfig.instance, SelectNodeMenuActivity::class.java)
                                    intent.putExtra("fromType",1)
                                    startActivityForResult(intent,REQUEST_CODE_MENU)
                                }
                                "Rename" -> {
                                    var oldName = chooseFileData!!.fileName.substring(0,chooseFileData!!.fileName.lastIndexOf("."));
                                    var oldExit = chooseFileData!!.fileName.substring(chooseFileData!!.fileName.lastIndexOf("."),chooseFileData!!.fileName.length);
                                    PopWindowUtil.showRenameFolderWindow(_this as Activity,  opMenu,oldName, object : PopWindowUtil.OnSelectListener {
                                        override fun onSelect(position: Int, obj: Any) {
                                            var map = obj as HashMap<String,String>
                                            var folderNewname = map.get("foldername") as String
                                            folderNewname += oldExit
                                            if(folderNewname.equals(""))
                                            {
                                                toast(R.string.Name_cannot_be_empty)
                                                return;
                                            }
                                            var newPath = folderInfo!!.path +"/"+folderNewname
                                            var newFile = File(newPath)
                                            if(newFile.exists())
                                            {
                                                toast(R.string.This_name_folder_already_exists)
                                                return;
                                            }

                                            var thumPathPre = chooseFileData!!.filePath.substring(0,chooseFileData!!.filePath.lastIndexOf("/")+1)
                                            var thumName = chooseFileData!!.filePath.substring(chooseFileData!!.filePath.lastIndexOf("/")+1,chooseFileData!!.filePath.length).replace("mp4","jpg")
                                            var thumPathPreNew = newPath.substring(0,newPath.lastIndexOf("/")+1)
                                            var thumNameNew = newPath.substring(newPath.lastIndexOf("/")+1,newPath.length).replace("mp4","jpg")
                                            var oldThumFile = File(thumPathPre +"th"+thumName)
                                            if(oldThumFile.exists())
                                            {
                                                oldThumFile.renameTo(File(thumPathPreNew +"th"+thumNameNew))
                                            }

                                            var oldFile = File(chooseFileData!!.filePath)
                                            if(oldFile.exists())
                                            {
                                                oldFile.renameTo(File(newPath))
                                                chooseFileData!!.filePath = newPath
                                                chooseFileData!!.fileName = folderNewname
                                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.update(chooseFileData!!)
                                                picItemEncryptionAdapter!!.notifyItemChanged(chooseItemPosition)
                                            }
                                        }
                                    })
                                }
                                "Delete" -> {
                                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_file))
                                            .setConfirmClickListener {
                                                var data = picItemEncryptionAdapter!!.getItem(chooseItemPosition)
                                                var filePath = data!!.filePath;
                                                var thumPre = filePath.substring(0,filePath.lastIndexOf("/")+1)
                                                var thumEnd = "th"+filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
                                                DeleteUtils.deleteFile(filePath)
                                                DeleteUtils.deleteFile(thumPre + thumEnd)
                                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.delete(data)
                                                picItemEncryptionAdapter!!.remove(chooseItemPosition)
                                                picItemEncryptionAdapter!!.notifyDataSetChanged()
                                                var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(folderInfo!!.id)).list()
                                                if(picMenuList != null && picMenuList.size > 0)
                                                {
                                                    var picMenuItem = picMenuList.get(0)
                                                    picMenuItem.fileNum -= 1;
                                                    if(picMenuItem.fileNum < 0)
                                                        picMenuItem.fileNum = 0
                                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                                                }
                                                EventBus.getDefault().post(UpdateAlbumEncryptionItemEvent())
                                            }
                                            .show()
                                }

                            }
                        }

                    })
                }

            }
        }
        allMenu.setOnClickListener()
        {

        }
        addMenu.setOnClickListener()
        {
            selectPicFromLocal()
        }
        backBtn.setOnClickListener {
            onBackPressed()
        }
        actionButton.setOnClickListener {
            selectPicFromLocal()
        }
       /* actionButton.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                        selectPicFromLocal()
                        return false
                    }
                })*/
    }
    fun showImagList(localPath:String)
    {
        previewImages = ArrayList()
        val selectedImages = ArrayList<LocalMedia>()

        val localMedia = LocalMedia()
        localMedia.isCompressed = false
        localMedia.duration = 0
        localMedia.height = 100
        localMedia.width = 100
        localMedia.isChecked = false
        localMedia.isCut = false
        localMedia.mimeType = 0
        localMedia.num = 0
        localMedia.path = localPath
        localMedia.pictureType = "image/jpeg"
        localMedia.setPosition(0)
        localMedia.sortIndex = 0
        previewImages.add(localMedia)
        ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")

        val previewImages = ImagesObservable.getInstance().readLocalMedias("chat")
        if (previewImages != null && previewImages.size > 0) {
            val intentPicturePreviewActivity = Intent(this, PicturePreviewActivity::class.java)
            val bundle = Bundle()
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages as Serializable)
            bundle.putInt(PictureConfig.EXTRA_POSITION, 0)
            bundle.putString("from", "chat")
            intentPicturePreviewActivity.putExtras(bundle)
            startActivity(intentPicturePreviewActivity)
        }
    }
    /**
     * select local image
     * //todo
     */
    protected fun initPicPlug() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
    }
    /**
     * select local image
     * //todo
     */
    protected fun selectPicFromLocal() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .forResult(REQUEST_CODE_LOCAL)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            toast(R.string.File_does_not_exist)
        } else if (fileStatus.result == 2) {
            toast(R.string.Files_100M)
        } else if (fileStatus.result == 3) {
            toast(R.string.Files_0M)
        }else {

            var  fileKey = fileStatus.fileKey;
            var fileId = fileKey.substring(fileKey.indexOf("__")+2,fileKey.length);
            var picItemList = AppConfig.instance.mDaoMaster!!.newSession().fileUploadItemDao.queryBuilder().where(FileUploadItemDao.Properties.FileId.eq(fileId)).list()
            if(picItemList ==null || picItemList.size == 0)
            {
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var fileUploadItem = FileUploadItem();
                fileUploadItem.localFileItemId = chooseFileData!!.id;
                fileUploadItem.depens = 1;
                fileUploadItem.userId = selfUserId;
                fileUploadItem.type = chooseFileData!!.fileType
                fileUploadItem.fileId = fileId
                fileUploadItem.size = chooseFileData!!.fileSize
                val fileMD5 = FileUtil.getFileMD5(File(chooseFileData!!.filePath))
                fileUploadItem.md5 = fileMD5;
                val fileNameBase58 = Base58.encode(chooseFileData!!.fileName.toByteArray())
                fileUploadItem.setfName(fileNameBase58);
                fileUploadItem.setfKey(chooseFileData!!.srcKey);
                fileUploadItem.setfInfo(chooseFileData!!.fileInfo);
                fileUploadItem.pathId = chooseFolderData!!.nodeId;
                val folderNameBase58 = Base58.encode(chooseFolderData!!.fileName.toByteArray())
                fileUploadItem.pathName =folderNameBase58;
                AppConfig.instance.mDaoMaster!!.newSession().fileUploadItemDao.insert(fileUploadItem)
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_MENU) { //
                chooseFolderData = data!!.getParcelableExtra<LocalFileMenu>("folderInfo")
                var file = File(chooseFileData!!.filePath)
                if (file.exists()) {
                    var fileName = chooseFileData!!.filePath.substring(chooseFileData!!.filePath.lastIndexOf("/") + 1)
                    var aesKey = LibsodiumUtil.DecryptShareKey(chooseFileData!!.srcKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                    var fileTempPath  = PathUtils.getInstance().getEncryptionAlbumPath().toString() +"/"+ "upload"
                    var fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    fileTempPath += "/"+chooseFileData!!.fileName;
                    var code = FileUtil.copySdcardToxFileAndDecrypt(chooseFileData!!.filePath,fileTempPath,aesKey)
                    if(code == 1)
                    {
                        localMediaUpdate = LocalMedia()
                        localMediaUpdate!!.path = fileTempPath
                        val MsgType = fileTempPath.substring(fileTempPath.lastIndexOf(".") + 1)
                        localMediaUpdate!!.pictureType = "file"
                        when (MsgType) {
                            "png", "jpg", "jpeg", "webp" -> {
                                localMediaUpdate!!.pictureType = "image/jpeg"
                            }
                            "mp4" -> {

                                localMediaUpdate!!.pictureType = "video/mp4"
                            }
                        }
                        // 配置压缩的参数
                        var fileLocalPath = chooseFolderData!!.path ;
                        var base58NameR = chooseFileData!!.fileName
                        if(  localMediaUpdate!!.pictureType == "image/jpeg")
                        {
                            val options = BitmapFactory.Options()
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = 16
                            val bmNew = BitmapFactory.decodeFile(fileTempPath, options) // 解码文件
                            val thumbPath = fileLocalPath +"/th"+base58NameR
                            FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath,50)
                        }else if(  localMediaUpdate!!.pictureType == "video/mp4")
                        {
                            val thumbPath = fileLocalPath +"/thbig"+base58NameR.replace("mp4","jpg")
                            val bitmap = EaseImageUtils.getVideoPhoto(fileTempPath)
                            FileUtil.saveBitmpToFileNoThread(bitmap, thumbPath,100)

                            val thumbPath2 =fileLocalPath +"/th"+base58NameR.replace("mp4","jpg")
                            val options = BitmapFactory.Options()
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = 16
                            val bmNew = BitmapFactory.decodeFile(thumbPath, options) // 解码文件
                            FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath2,50)
                            DeleteUtils.deleteFile(thumbPath)
                        }
                        var list = arrayListOf<LocalMedia>()
                        list.add(localMediaUpdate!!)
                        var startIntent = Intent(this, FileTaskListActivity::class.java)
                        startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
                        startIntent.putExtra("fromPorperty",3)
                        var aesKey = LibsodiumUtil.DecryptShareKey(chooseFileData!!.srcKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                        startIntent.putExtra("aesKey",aesKey)
                        startActivity(startIntent)
                    }

                }

            }
            else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                KLog.i("选照片或者视频返回。。。")
                val list = data!!.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
                KLog.i(list)
                if (list != null && list.size > 0) {
                    var len = list.size
                    for (i in 0 until len) {
                        var file = File(list.get(i).path);
                        var isHas = file.exists();
                        if (isHas) {
                            var filePath = list.get(i).path
                            val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                            val fileMD5 = FileUtil.getFileMD5(File(filePath))
                            var picItemList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileMD5.eq(fileMD5),LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).list()
                            if(picItemList != null && picItemList.size > 0)
                            {
                                toast(imgeSouceName+" "+getString( R.string.file_already_exists))
                                continue;
                            }

                            var fileSize = file.length();
                            val fileKey = RxEncryptTool.generateAESKey()
                            var SrcKey = ByteArray(256)
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))

                            val base58files_dir = folderInfo!!.path +"/"+imgeSouceName
                            val code = FileUtil.copySdcardToxFileAndEncrypt(list.get(i).path, base58files_dir, fileKey.substring(0, 16))

                            if (code == 1) {

                                var localFileItem = LocalFileItem();
                                localFileItem.filePath = base58files_dir;
                                localFileItem.fileName = imgeSouceName;
                                localFileItem.fileSize = fileSize;
                                localFileItem.creatTime = System.currentTimeMillis()
                                localFileItem.fileMD5 = fileMD5;
                                localFileItem.upLoad = false;
                                val MsgType = imgeSouceName.substring(imgeSouceName.lastIndexOf(".") + 1)
                                try {
                                    when (MsgType) {
                                        "png", "jpg", "jpeg", "webp" ->
                                        {
                                            // 配置压缩的参数
                                            var  bitmap = BitmapFactory.decodeFile(list.get(i).path);

                                            var widthAndHeight = "" + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
                                            localFileItem.fileType = 1
                                            localFileItem.fileInfo = widthAndHeight;

                                            val options = BitmapFactory.Options()
                                            options.inJustDecodeBounds = false;
                                            options.inSampleSize = 16
                                            val bmNew = BitmapFactory.decodeFile(list.get(i).path, options) // 解码文件
                                            val thumbPath = folderInfo!!.path +"/th"+imgeSouceName.substring(0,imgeSouceName.lastIndexOf("."))+".jpg"
                                            FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath,50)
                                        }
                                        "amr" ->  localFileItem.fileType = 2
                                        "mp4" ->
                                        {

                                            localFileItem.fileType = 4
                                            localFileItem.fileInfo = "";
                                            val thumbPath = folderInfo!!.path +"/thbig"+imgeSouceName.substring(0,imgeSouceName.lastIndexOf("."))+".jpg"
                                            val bitmap = EaseImageUtils.getVideoPhoto(list.get(i).path)
                                            FileUtil.saveBitmpToFileNoThread(bitmap, thumbPath,100)

                                            val thumbPath2 = folderInfo!!.path +"/th"+imgeSouceName.substring(0,imgeSouceName.lastIndexOf("."))+".jpg"
                                            val options = BitmapFactory.Options()
                                            options.inJustDecodeBounds = false;
                                            options.inSampleSize = 16
                                            val bmNew = BitmapFactory.decodeFile(thumbPath, options) // 解码文件
                                            FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath2,50)
                                            DeleteUtils.deleteFile(thumbPath)
                                        }
                                        else ->  localFileItem.fileType = 5
                                    }
                                }catch (e:Exception)
                                {

                                }
                                localFileItem.fileFrom = 0;
                                localFileItem.autor = "";
                                localFileItem.fileId = folderInfo!!.id;
                                localFileItem.srcKey = String(SrcKey)
                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.insert(localFileItem)
                                var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(folderInfo!!.id)).list()
                                if(picMenuList != null && picMenuList.size > 0)
                                {
                                    var picMenuItem = picMenuList.get(0)
                                    picMenuItem.fileNum += 1;
                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                                }
                                picItemEncryptionAdapter!!.addData(0,localFileItem)
                                picItemEncryptionAdapter!!.notifyItemChanged(0)
                                toast(imgeSouceName+" "+getString( R.string.Encryption_succeeded))
                                EventBus.getDefault().post(UpdateAlbumEncryptionItemEvent())




                            }
                        }

                    }
                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                            .setContentText(getString(R.string.Delete_original_file))
                            .setConfirmClickListener {
                                for (i in 0 until len) {
                                    var file = File(list.get(i).path);
                                    var isHas = file.exists();
                                    if (isHas) {
                                        var filePath = list.get(i).path
                                        val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                                        val MsgType = imgeSouceName.substring(imgeSouceName.lastIndexOf(".") + 1)
                                        when (MsgType) {
                                            "png", "jpg", "jpeg", "webp" ->
                                            {
                                                AlbumNotifyHelper.deleteImagesInAlbumDB(AppConfig.instance, list.get(i).path)
                                            }
                                            "amr" ->  {

                                            }
                                            "mp4" ->
                                            {
                                                AlbumNotifyHelper.deleteVideoInAlbumDB(AppConfig.instance, list.get(i).path)
                                            }
                                            else -> {

                                            }
                                        }
                                    }
                                }

                            }
                            .show()
                } else {
                    Toast.makeText(this, getString(R.string.select_resource_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun setupActivityComponent() {
        DaggerPicEncryptionlListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .picEncryptionlListModule(PicEncryptionlListModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionlListContract.PicEncryptionlListContractPresenter) {
        mPresenter = presenter as PicEncryptionlListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openSceen(screen: Sceen) {
        var screenshotsSettingFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
        if (screenshotsSettingFlag.equals("1")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    override fun onResume() {
        super.onResume()
    }
    override fun onDestroy() {
        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionAlbumPath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}