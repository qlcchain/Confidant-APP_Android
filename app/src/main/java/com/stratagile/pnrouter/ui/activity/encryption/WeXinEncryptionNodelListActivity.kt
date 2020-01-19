package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import com.floatball.FloatPermissionManager
import com.huxq17.floatball.libarary.FloatBallManager
import com.huxq17.floatball.libarary.floatball.FloatBallCfg
import com.huxq17.floatball.libarary.menu.FloatMenuCfg
import com.huxq17.floatball.libarary.menu.MenuItem
import com.huxq17.floatball.libarary.utils.BackGroudSeletor
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.hyphenate.util.DensityUtil
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
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWeXinEncryptionNodelListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WeXinEncryptionNodelListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeXinEncryptionNodelListPresenter
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.file.MiFileViewActivity
import com.stratagile.pnrouter.ui.activity.user.MyDetailActivity
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.encryption_nodefile_list.*
import kotlinx.android.synthetic.main.layout_encryption_file_list_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.*
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/26 10:33:40
 */

class WeXinEncryptionNodelListActivity : BaseActivity(), WeXinEncryptionNodelListContract.View , PNRouterServiceMessageReceiver.NodeFilesListPullCallback{
    override fun fileAction(jFileActionRsp: JFileActionRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        if (jFileActionRsp.params.retCode== 0)
        {
            if(jFileActionRsp.params.react == 1)//重命名
            {
                var thumPathPre = renameOldPath.substring(0,renameOldPath.lastIndexOf("/")+1)
                var thumName = renameOldPath.substring(renameOldPath.lastIndexOf("/")+1,renameOldPath.length).replace("mp4","jpg")
                var thumPathPreNew = renameNewPath.substring(0,renameNewPath.lastIndexOf("/")+1)
                var thumNameNew = renameNewPath.substring(renameNewPath.lastIndexOf("/")+1,renameNewPath.length).replace("mp4","jpg")
                var oldThumFile = File(thumPathPre +"th"+thumName)
                if(oldThumFile.exists())
                {
                    oldThumFile.renameTo(File(thumPathPreNew +"th"+thumNameNew))
                }
                var oldFile = File(renameOldPath)
                if(oldFile.exists())
                {
                    oldFile.renameTo(File(renameNewPath))
                }
                //chooseFileData!!.filePath = renameNewPath
                chooseFileData!!.fileName = folderNewname
                runOnUiThread {
                    picItemEncryptionAdapter!!.notifyItemChanged(renamePositon)
                }
            }
            else if(jFileActionRsp.params.react == 2)//删除
            {
                /*var deleteData:LocalFileItem ? = null
                           var deletePositon = -1;*/
                var filePath = PathUtils.getInstance().filePath.toString()+"/"+deleteData!!.fileName;
                var thumPre = filePath.substring(0,filePath.lastIndexOf("/")+1)
                var thumEnd = "th"+filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
                DeleteUtils.deleteFile(filePath)
                DeleteUtils.deleteFile(thumPre + thumEnd)
                runOnUiThread {
                    picItemEncryptionAdapter!!.remove(deletePositon)
                    picItemEncryptionAdapter!!.notifyDataSetChanged()
                }
                EventBus.getDefault().post(UpdateAlbumNodeEncryptionItemEvent())
                /*var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).orderDesc(LocalFileItemDao.Properties.CreatTime).list()
                val fileItemList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.NodeId.eq(jFileActionRsp.params.fileId)).list()
                if (fileItemList != null && fileItemList.size != 0)
                //加密相册上传
                {
                    val fileItem = fileItemList[0]
                    fileItem.upLoad = false
                    fileItem.nodeId = jFileActionRsp.params.fileId
                    AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.update(fileItem)
                    EventBus.getDefault().post(UpdateAlbumEncryptionItemEvent())
                }*/
            }
        }else{
            com.pawegio.kandroid.runOnUiThread {
                toast(R.string.fail)
            }
        }
    }

    override fun filesListPull(jFilesListPullRsp: JFilesListPullRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        if(jFilesListPullRsp.params.retCode == 0)
        {
            var fileItemList = mutableListOf<LocalFileItem>()
            pathId = jFilesListPullRsp.params.pathId.toLong();
            for (item in jFilesListPullRsp.params.payload)
            {
                var localFileItem: LocalFileItem = LocalFileItem();
                localFileItem.fileId = item.id.toLong()
                localFileItem.filePath = item.paths;
                localFileItem.fileLocalPath = folderInfo!!.path
                localFileItem.fileType = item.type;
                var souceName = String(Base58.decode(item.fname))
                localFileItem.fileName = souceName
                localFileItem.fileFrom = 1
                localFileItem.fileSize = item.size.toLong()
                localFileItem.creatTime = item.lastModify.toLong()
                localFileItem.fileMD5 = item.md5
                localFileItem.srcKey = item.fKey;
                localFileItem.fileInfo = item.finfo
                localFileItem.upLoad = false
                fileItemList.add(localFileItem)
            }
            runOnUiThread {
                picItemEncryptionAdapter = PicItemEncryptionAdapter(fileItemList)
                recyclerView.adapter = picItemEncryptionAdapter
                /*picItemEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
                    var taskFile = picItemEncryptionAdapter!!.getItem(position)
                    //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
                }*/
                picItemEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.itemTypeIcon, R.id.itemInfo ->
                        {
                            var localFileItem = picItemEncryptionAdapter!!.getItem(position)


                            var filePathLocal = folderInfo!!.path+"/"+localFileItem!!.fileName
                            var file = File(filePathLocal)
                            if(file.exists())
                            {
                                var fileName = localFileItem!!.fileName
                                var fileTempPath  = filePathLocal
                                if (fileName.toLowerCase().contains("jpg") || fileName.toLowerCase().contains("png")|| fileName.toLowerCase().contains("jpeg")) {
                                    showImagList(fileTempPath)
                                }else if(fileName.contains("mp4"))
                                {
                                    val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                                    intent.putExtra("path", fileTempPath)
                                    startActivity(intent)
                                }else if(fileName.contains("zip"))
                                {
                                    isClick = true;
                                    val intent = Intent(AppConfig.instance, ShowZipInfoActivity::class.java)
                                    intent.putExtra("path", fileTempPath)
                                    var id = localFileItem!!.fileId;
                                    intent.putExtra("id", id.toString())
                                    startActivity(intent)
                                }else{
                                    OpenFileUtil.getInstance(AppConfig.instance)
                                    val intent = OpenFileUtil.openFile(fileTempPath)
                                    startActivity(intent)
                                }
                            }else{
                                isClick = true;
                                startActivity(Intent(this, MiFileViewActivity::class.java).putExtra("file", localFileItem).putExtra("folderPath", folderInfo!!.path))
                            }
                        }
                        R.id.opMenu ->
                        {
                            chooseFileData = picItemEncryptionAdapter!!.getItem(position)
                            var menuArray = arrayListOf<String>()
                            var iconArray = arrayListOf<String>()
                            menuArray = arrayListOf<String>(getString(R.string.rename),getString(R.string.Delete))
                            iconArray = arrayListOf<String>("sheet_rename","statusbar_delete")
                            var chooseItemPosition = position
                            PopWindowUtil.showPopMenuWindow(this@WeXinEncryptionNodelListActivity, opMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                                override fun onSelect(position: Int, obj: Any) {
                                    KLog.i("" + position)
                                    var data = obj as FileOpreateType
                                    when (data.name) {
                                        "Rename" -> {
                                            var oldName = chooseFileData!!.fileName.substring(0,chooseFileData!!.fileName.lastIndexOf("."));
                                            var oldExit = chooseFileData!!.fileName.substring(chooseFileData!!.fileName.lastIndexOf("."),chooseFileData!!.fileName.length);
                                            PopWindowUtil.showRenameFolderWindow(_this as Activity,  opMenu,oldName, object : PopWindowUtil.OnSelectListener {
                                                override fun onSelect(position: Int, obj: Any) {
                                                    var map = obj as HashMap<String, String>
                                                    folderNewname = map.get("foldername") as String
                                                    folderNewname+= oldExit
                                                    if(folderNewname.equals(""))
                                                    {
                                                        toast(R.string.Name_cannot_be_empty)
                                                        return;
                                                    }
                                                    renamePositon = chooseItemPosition;


                                                    var newPath = folderInfo!!.path +"/"+folderNewname
                                                    var newFile = File(newPath)
                                                    renameOldPath = folderInfo!!.path +"/"+chooseFileData!!.fileName
                                                    renameNewPath = newPath;
                                                    renameNewFilePath = chooseFileData!!.filePath
                                                    if(newFile.exists())
                                                    {
                                                        toast(R.string.This_name_folder_already_exists)
                                                        return;
                                                    }
                                                    runOnUiThread {
                                                        showProgressDialog()
                                                    }
                                                    var base58Name = Base58.encode(chooseFileData!!.fileName.toByteArray())
                                                    var base58NewName = Base58.encode(folderNewname.toByteArray())
                                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                    var filePathsPullReq = FileActionReq( selfUserId!!, 3,1,1,chooseFileData!!.fileId,pathId,base58NewName,base58Name)
                                                    var sendData = BaseData(6, filePathsPullReq);
                                                    if (ConstantValue.isWebsocketConnected) {
                                                        AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                                    }else if (ConstantValue.isToxConnected) {
                                                        var baseData = sendData
                                                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                                        if (ConstantValue.isAntox) {
                                                            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                                        }else{
                                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                        }
                                                    }


                                                }
                                            })
                                        }
                                        "Delete" -> {
                                            SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                                    .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_file))
                                                    .setConfirmClickListener {
                                                        var data = picItemEncryptionAdapter!!.getItem(chooseItemPosition)

                                                        deleteData= data
                                                        deletePositon = chooseItemPosition;
                                                        runOnUiThread {
                                                            showProgressDialog()
                                                        }
                                                        var foldername = data!!.fileName
                                                        var base58Name = Base58.encode(foldername.toByteArray())
                                                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                        var filePathsPullReq = FileActionReq( selfUserId!!, 3,1,2,data!!.fileId,pathId,base58Name,"")
                                                        var sendData = BaseData(6, filePathsPullReq);
                                                        if (ConstantValue.isWebsocketConnected) {
                                                            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                                        }else if (ConstantValue.isToxConnected) {
                                                            var baseData = sendData
                                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                                            if (ConstantValue.isAntox) {
                                                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                                            }else{
                                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                            }
                                                        }
                                                    }
                                                    .show()
                                        }

                                    }
                                }

                            })
                        }

                    }
                }
            }
        }
    }
    @Inject
    internal lateinit var mPresenter: WeXinEncryptionNodelListPresenter
    var _this:Activity?= null
    var picItemEncryptionAdapter: PicItemEncryptionAdapter? = null
    var folderInfo: LocalFileMenu? = null
    protected val REQUEST_CODE_MENU = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6
    var REQUEST_MEDIA_PROJECTION = 1008
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    var localMediaUpdate: LocalMedia? = null
    var pathId:Long = 0
    var chooseFileData: LocalFileItem? = null;
    var chooseFolderData: LocalFileMenu? = null;
    private var mFloatballManager: FloatBallManager? = null
    private var mFloatPermissionManager: FloatPermissionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mImageReader: ImageReader? = null
    private var mWindowManager: WindowManager? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mGestureDetector: GestureDetector? = null
    private var resumed: Int = 0
    private var mResultData: Intent? = null
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mScreenDensity: Int = 0
    var isClick = false;
    var isShowOld = false;

    var clickTimeMap = ConcurrentHashMap<String, Long>()

    var receiveFileDataMap = ConcurrentHashMap<String, UpLoadFile>()
    var deleteData:LocalFileItem ? = null
    var deletePositon = -1;

    var renamePositon = -1;
    var renameOldPath = ""
    var renameNewPath = "";
    var renameNewFilePath = "";
    var folderNewname = "";
    
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encryption_nodefile_list)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateAlbumNodeSuccessEncryptionItemEvent(statusChange: UpdateWxNodeSuccessEncryptionItemEvent) {
        getData()
    }
    fun getData()
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var base58Name = Base58.encode(folderInfo!!.fileName.toByteArray())
        var filesListPullReq = FilesListPullReq( selfUserId!!, 3,folderInfo!!.nodeId,base58Name,1,0,0)
        var sendData = BaseData(6, filesListPullReq);
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = this
        _this = this;
        isClick = false;
        EventBus.getDefault().register(this)
        folderInfo = intent.getParcelableExtra("folderInfo")
        chooseFolderData = folderInfo
        titleShow.text = folderInfo!!.fileName
        initPicPlug()
        initFlatBall()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.showIFHasPermission()
            if(mFloatballManager!!.isShow)
            {
                actionButton.visibility = View.GONE
            }
        }
        showProgressDialog()
        getData()
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
        actionButton.setOnClickListener()
        {
            setFloatballVisible(true)
        }
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
                fileUploadItem.localFileItemId = 0;
                fileUploadItem.depens = 3;
                fileUploadItem.userId = selfUserId;
                fileUploadItem.type = chooseFileData!!.fileType
                fileUploadItem.fileId = fileId
                fileUploadItem.size = chooseFileData!!.fileSize
                fileUploadItem.md5 = chooseFileData!!.fileMD5;
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
            if (requestCode == REQUEST_MEDIA_PROJECTION) {
                mResultData = data;

            }
        }
    }
    fun initFlatBall()
    {
        //1 初始化悬浮球配置，定义好悬浮球大小和icon的drawable
        val ballSize = DensityUtil.dip2px(this, 45f)
        val ballIcon = BackGroudSeletor.getdrawble("ic_floatball", this)
        //可以尝试使用以下几种不同的config。
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_CENTER,false);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_BOTTOM, -100);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_TOP, 100);
        val ballCfg = FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER)
        //设置悬浮球不半隐藏
        ballCfg.setHideHalfLater(false)
        //2 需要显示悬浮菜单
        //2.1 初始化悬浮菜单配置，有菜单item的大小和菜单item的个数
        val menuSize = DensityUtil.dip2px(this, 180f)
        val menuItemSize = DensityUtil.dip2px(this, 40f)
        val menuCfg = FloatMenuCfg(menuSize, menuItemSize)
        //3 生成floatballManager
        mFloatballManager = FloatBallManager(applicationContext, ballCfg, menuCfg)
        addFloatMenuItem()
        setFloatPermission()

        //5 如果没有添加菜单，可以设置悬浮球点击事件
        if (mFloatballManager!!.getMenuItemSize() == 0) {
            mFloatballManager!!.setOnFloatBallClickListener(FloatBallManager.OnFloatBallClickListener { toast("点击了悬浮球") })
        }
        //6 如果想做成应用内悬浮球，可以添加以下代码。
        //application.registerActivityLifecycleCallbacks(mActivityLifeCycleListener)
        createFloatView()
        createImageReader()
        requestCapturePermission()
    }

    private fun createFloatView() {
        mGestureDetector = GestureDetector(applicationContext, FloatGestrueTouchListener())
        mLayoutParams = WindowManager.LayoutParams()
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        mWindowManager!!.getDefaultDisplay().getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels

        mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        mLayoutParams!!.format = PixelFormat.RGBA_8888
        // 设置Window flag
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        mLayoutParams!!.x = mScreenWidth
        mLayoutParams!!.y = 100
        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT


        //startScreenShot();
    }

    private inner class FloatGestrueTouchListener : GestureDetector.OnGestureListener {
        internal var lastX: Int = 0
        internal var lastY: Int = 0
        internal var paramX: Int = 0
        internal var paramY: Int = 0

        override fun onDown(event: MotionEvent): Boolean {
            lastX = event.rawX.toInt()
            lastY = event.rawY.toInt()
            paramX = mLayoutParams!!.x
            paramY = mLayoutParams!!.y
            return true
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            startScreenShot()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val dx = e2.rawX.toInt() - lastX
            val dy = e2.rawY.toInt() - lastY
            mLayoutParams!!.x = paramX + dx
            mLayoutParams!!.y = paramY + dy
            // 更新悬浮窗位置
            // 更新悬浮窗位置

            return true
        }

        override fun onLongPress(e: MotionEvent) {

        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }
    private fun createImageReader() {

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1)

    }

    fun startScreenShot() {
        /*val handler1 = Handler()
        handler1.postDelayed({
            //start virtual
            startVirtual()
        }, 5)

        handler1.postDelayed({
            //capture the screen
            startCapture()
        }, 30)*/
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
        val handler1 = Handler()
        handler1.postDelayed({
            startVirtual()
            startCapture()
        }, 5)

    }

    fun startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay()
        } else {
            setUpMediaProjection()
            virtualDisplay()
        }
    }
    private fun addFloatMenuItem() {
        var _this = this;
        val personItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_desktop", this)) {
            override fun action() {
                goToDesktop(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        val walletItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_screenshot", this)) {
            override fun action() {
                if(mResultData == null)
                {
                    val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    startActivityForResult(
                            mediaProjectionManager.createScreenCaptureIntent(),
                            REQUEST_MEDIA_PROJECTION)
                    toast(R.string.Please_click_again)
                    return
                }
                if(!AppConfig.instance.isBackGroud)//截应用内屏
                {
                    var screenshotsFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
                    if(screenshotsFlag.equals("1"))
                    {
                        runOnUiThread {
                            SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.screen_security))
                                    .setConfirmClickListener {
                                        var intent= Intent(_this, MyDetailActivity::class.java)
                                        intent.putExtra("flag",1)
                                        startActivity(intent)
                                    }
                                    .show()
                        }
                    }else{
                        startScreenShot()
                    }
                }else{
                    startScreenShot()
                }
                mFloatballManager!!.closeMenu()
            }
        }
        val settingItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_return", this)) {
            override fun action() {
                goToApp(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        val closeItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_close", this)) {
            override fun action() {
                mFloatballManager!!.hide()
                actionButton.visibility = View.VISIBLE
                /* runOnUiThread {
                     SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                             .setContentText(getString(R.string.Are_you_colse))
                             .setConfirmClickListener {

                             }
                             .show()
                 }*/

            }
        }
        mFloatballManager!!.addMenuItem(personItem)
                .addMenuItem(settingItem)
                .addMenuItem(walletItem)
                .addMenuItem(closeItem)
                .buildMenu()
    }

    fun requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setContentText(getString(R.string.not_support))
                    .setConfirmClickListener {
                        finish()
                    }
                    .show()
            return
        }

        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION)
    }
    private fun setFloatballVisible(visible: Boolean) {
        if(mFloatballManager != null)
        {
            if (visible) {
                mFloatballManager!!.show()
                if(mFloatballManager!!.isShow)
                {
                    actionButton.visibility = View.GONE
                }
            } else {
                mFloatballManager!!.hide()
            }
        }

    }

    fun isApplicationInForeground(): Boolean {
        return resumed > 0
    }
    fun setUpMediaProjection() {
        if (mResultData == null) {
            val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION)
            toast(R.string.Please_click_again)
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData)
        }
    }
    private fun getMediaProjectionManager(): MediaProjectionManager {

        return getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.showIFHasPermission()
            if(mFloatballManager!!.isShow)
            {
                actionButton.visibility = View.GONE
            }
        }
        //mFloatballManager!!.onFloatBallClick()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
    inner class ActivityLifeCycleListener : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            ++resumed
            setFloatballVisible(true)
        }

        override fun onActivityPaused(activity: Activity) {
            --resumed
            if (!isApplicationInForeground()) {
                //setFloatballVisible(false);
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }
    private fun setFloatPermission() {
        // 设置悬浮球权限，用于申请悬浮球权限的，这里用的是别人写好的库，可以自己选择
        //如果不设置permission，则不会弹出悬浮球
        mFloatPermissionManager = FloatPermissionManager()
        mFloatballManager!!.setPermission(object : FloatBallManager.IFloatBallPermission {
            override fun onRequestFloatBallPermission(): Boolean {
                requestFloatBallPermission(this@WeXinEncryptionNodelListActivity)
                return true
            }

            override fun hasFloatBallPermission(context: Context): Boolean {
                return mFloatPermissionManager!!.checkPermission(context)
            }

            override fun requestFloatBallPermission(activity: Activity) {
                mFloatPermissionManager!!.applyPermission(activity)
            }

        })
    }
    private fun startCapture() {

        val image = mImageReader!!.acquireLatestImage()

        if (image == null) {
            startScreenShot()
        } else {
            val mSaveTask = SaveTask()
            mSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image)
            //AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    inner class SaveTask : AsyncTask<Image, Void, Bitmap>() {

        override fun doInBackground(vararg params: Image): Bitmap? {

            if (params == null || params.size < 1 || params[0] == null) {

                return null
            }

            val image = params[0]

            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer = planes[0].buffer
            //每个像素的间距
            val pixelStride = planes[0].pixelStride
            //总的间距
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            var bitmap: Bitmap? = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
            bitmap!!.copyPixelsFromBuffer(buffer)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            image.close()
            var fileTempPathFile:File? = null
            var filePic:File? = null
            var fileScreenShotName = com.stratagile.pnrouter.screencapture.FileUtil.getOnlyScreenShotsName();
            var fileTempPath  = PathUtils.getInstance().getEncryptionWeChatNodePath().toString() +"/"+ "sreenshots"
            if (bitmap != null) {
                try {
                    fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    filePic = File(fileTempPath +"/"+fileScreenShotName)
                    val out = FileOutputStream(filePic)
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                        /*val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val contentUri = Uri.fromFile(filePic)
                        media.data = contentUri
                        sendBroadcast(media)*/
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    filePic = null
                } catch (e: IOException) {
                    e.printStackTrace()
                    filePic = null
                }
                var needPicPath = fileTempPath +"/"+fileScreenShotName
                var file = File(needPicPath);
                var isHas = file.exists();
                if (isHas) {
                    var filePath = needPicPath
                    val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                    val fileMD5 = FileUtil.getFileMD5(File(filePath))
                    chooseFileData = LocalFileItem();
                    localMediaUpdate = LocalMedia()
                    localMediaUpdate!!.path = filePath
                    val MsgType = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase()
                    localMediaUpdate!!.pictureType = "file"
                    when (MsgType) {
                        "png", "jpg", "jpeg", "webp" -> {
                            localMediaUpdate!!.pictureType = "image/jpeg"
                            // 配置压缩的参数
                            var  bitmap = BitmapFactory.decodeFile(filePath);
                            var widthAndHeight = "" + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
                            chooseFileData!!.fileType = 1
                            chooseFileData!!.fileInfo = widthAndHeight
                        }
                        "mp4" -> {

                            localMediaUpdate!!.pictureType = "video/mp4"
                            chooseFileData!!.fileType = 4
                        }
                    }
                    // 配置压缩的参数
                    var fileLocalPath = chooseFolderData!!.path ;
                    var base58NameR = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
                    if(  localMediaUpdate!!.pictureType == "image/jpeg")
                    {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = 16
                        val bmNew = BitmapFactory.decodeFile(filePath, options) // 解码文件
                        val thumbPath = fileLocalPath +"/th"+base58NameR
                        FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath,50)
                    }else if(  localMediaUpdate!!.pictureType == "video/mp4")
                    {
                        val thumbPath = fileLocalPath +"/thbig"+base58NameR.replace("mp4","jpg")
                        val bitmap = EaseImageUtils.getVideoPhoto(filePath)
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
                    var startIntent = Intent(this@WeXinEncryptionNodelListActivity, FileTaskListActivity::class.java)
                    startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
                    startIntent.putExtra("fromPorperty",5)
                    var aesKey = RxEncryptTool.generateAESKey()
                    var SrcKey = ByteArray(256)
                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, ConstantValue.libsodiumpublicMiKey!!))
                    chooseFileData!!.srcKey = String(SrcKey);
                    chooseFileData!!.fileSize = file.length()
                    chooseFileData!!.filePath = filePath
                    var fileTempPath  = PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ "upload"
                    var fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    fileTempPath += "/tmep"+base58NameR;
                    var code = FileUtil.copySdcardToxFileAndEncrypt(chooseFileData!!.filePath,fileTempPath,aesKey.substring(0, 16))
                    if(code == 1)
                    {
                        val fileMD511 = FileUtil.getFileMD5(File(chooseFileData!!.filePath))
                        val fileMD5 = FileUtil.getFileMD5(File(fileTempPath))
                        chooseFileData!!.fileMD5 = fileMD5;
                        DeleteUtils.deleteFile(fileTempPath)
                    }
                    chooseFileData!!.fileName = base58NameR;
                    startIntent.putExtra("aesKey",aesKey)
                    startActivity(startIntent)
                }
            }

            return if (filePic != null) {
                bitmap
            } else null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            //预览图片
            if (bitmap != null) {

                (application as AppConfig).setmScreenCaptureBitmap(bitmap)
                Log.e("ryze", "获取图片成功")
                if(mFloatballManager != null)
                {
                    mFloatballManager!!.show()
                }
                //toast(R.string.screenshots_success)
                //startActivity(PreviewPictureActivity.newIntent(applicationContext))
            }

        }
    }

    private fun virtualDisplay() {
        if (mMediaProjection != null) {
            mVirtualDisplay = mMediaProjection!!.createVirtualDisplay("screen-mirror",
                    mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader!!.getSurface(), null, null)
        }
    }
    override fun setupActivityComponent() {
       DaggerWeXinEncryptionNodelListComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .weXinEncryptionNodelListModule(WeXinEncryptionNodelListModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: WeXinEncryptionNodelListContract.WeXinEncryptionNodelListContractPresenter) {
            mPresenter = presenter as WeXinEncryptionNodelListPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    /**
     * 跳转到桌面
     */
    fun goToDesktop(context: Context) {

        try {
            var intentWX = Intent(Intent.ACTION_MAIN);
            var cmp =  ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intentWX.addCategory(Intent.CATEGORY_LAUNCHER);
            intentWX.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentWX.setComponent(cmp);
            context.startActivity(intentWX)
        }catch (e:Exception)
        {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }

    }
    /**
     * 跳转到桌面
     */
    fun goToApp(context: Context) {
        /*val intent = Intent("android.intent.action.MAIN")
        var currentActivity =  AppConfig.instance.mAppActivityManager.currentActivity() as Activity
        intent.component = ComponentName(applicationContext.packageName, MainActivity::class.java.name)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)*/
        var launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.stratagile.pnrouter");
        //launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(launchIntent);
    }
    override fun onPause() {
        if(mFloatballManager != null)
        {
            if(mFloatballManager!!.isShow)
            {
                isShowOld = true;
            }else{
                isShowOld = false;
            }
            if(isClick)
            {
                mFloatballManager!!.hide()
                isClick = false;
            }

        }
        super.onPause()
    }

    override fun onResume() {
        if(mFloatballManager != null)
        {
            if(isShowOld)
            {
                mFloatballManager!!.show()
            }
        }
        super.onResume()
    }
    override fun onDestroy() {
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = null
        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}