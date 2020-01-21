package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
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
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FileUploadItem
import com.stratagile.pnrouter.db.FileUploadItemDao
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.events.UpdateAlbumEncryptionItemEvent
import com.stratagile.pnrouter.entity.events.UpdateAlbumNodeEncryptionItemEvent
import com.stratagile.pnrouter.entity.events.UpdateAlbumNodeSuccessEncryptionItemEvent
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionNodelListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionNodelListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionNodelListPresenter
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.file.MiFileViewActivity
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.encryption_nodefile_list.*
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
 * @date 2019/12/23 16:07:44
 */

class PicEncryptionNodelListActivity : BaseActivity(), PicEncryptionNodelListContract.View , PNRouterServiceMessageReceiver.NodeFilesListPullCallback{
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
                var localFileItem:LocalFileItem = LocalFileItem();
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
                        R.id.itemTypeIcon,R.id.itemInfo ->
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
                            PopWindowUtil.showPopMenuWindow(this@PicEncryptionNodelListActivity, opMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                                override fun onSelect(position: Int, obj: Any) {
                                    KLog.i("" + position)
                                    var data = obj as FileOpreateType
                                    when (data.icon) {
                                        "sheet_rename" -> {
                                            var oldName = chooseFileData!!.fileName.substring(0,chooseFileData!!.fileName.lastIndexOf("."));
                                            var oldExit = chooseFileData!!.fileName.substring(chooseFileData!!.fileName.lastIndexOf("."),chooseFileData!!.fileName.length);
                                            PopWindowUtil.showRenameFolderWindow(_this as Activity,  opMenu,oldName, object : PopWindowUtil.OnSelectListener {
                                                override fun onSelect(position: Int, obj: Any) {
                                                    var map = obj as HashMap<String,String>
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
                                                    var filePathsPullReq = FileActionReq( selfUserId!!, 1,1,1,chooseFileData!!.fileId,pathId,base58NewName,base58Name)
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
                                        "statusbar_delete" -> {
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
                                                        var filePathsPullReq = FileActionReq( selfUserId!!, 1,1,2,data!!.fileId,pathId,base58Name,"")
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
    internal lateinit var mPresenter: PicEncryptionNodelListPresenter
    var _this:Activity?= null
    var picItemEncryptionAdapter: PicItemEncryptionAdapter? = null
    var folderInfo: LocalFileMenu? = null
    protected val REQUEST_CODE_MENU = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    var localMediaUpdate: LocalMedia? = null
    var pathId:Long = 0
    var chooseFileData: LocalFileItem? = null;
    var chooseFolderData: LocalFileMenu? = null;

    var clickTimeMap = ConcurrentHashMap<String, Long>()

    var receiveFileDataMap = ConcurrentHashMap<String, UpLoadFile>()
    var deleteData:LocalFileItem ? = null
    var deletePositon = -1;

    var renamePositon = -1;
    var renameOldPath = ""
    var renameNewPath = "";
    var renameNewFilePath = "";
    var folderNewname = "";
    var widthAndHeight:String? = null;
    var isClick = false;
    var isShowOld = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encryption_nodefile_list)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateAlbumNodeSuccessEncryptionItemEvent(statusChange: UpdateAlbumNodeSuccessEncryptionItemEvent) {
        getData()
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = this
        _this = this;
        isClick = false;
        EventBus.getDefault().register(this)
        folderInfo = intent.getParcelableExtra("folderInfo")
        titleShow.text = folderInfo!!.fileName
        initPicPlug()
        chooseFolderData = folderInfo
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
        actionButton.setOnClickListener {
            selectPicFromLocal()
        }
    }
    fun getData()
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var base58Name = Base58.encode(folderInfo!!.fileName.toByteArray())
        var filesListPullReq = FilesListPullReq( selfUserId!!, 1,folderInfo!!.nodeId,base58Name,1,0,0)
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
    override fun setupActivityComponent() {
        DaggerPicEncryptionNodelListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .picEncryptionNodelListModule(PicEncryptionNodelListModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionNodelListContract.PicEncryptionNodelListContractPresenter) {
        mPresenter = presenter as PicEncryptionNodelListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddLocalEncryptionItemEvent(statusChange: UpdateAlbumEncryptionItemEvent) {
        runOnUiThread {
            showProgressDialog()
        }
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var base58Name = Base58.encode(folderInfo!!.fileName.toByteArray())
        var filesListPullReq = FilesListPullReq( selfUserId!!, 1,folderInfo!!.nodeId,base58Name,1,0,0)
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
                fileUploadItem.depens = 1;
                fileUploadItem.userId = selfUserId;
                fileUploadItem.fileId = fileId
                fileUploadItem.type = chooseFileData!!.fileType
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
            if (requestCode == REQUEST_CODE_MENU) { //
                chooseFolderData = data!!.getParcelableExtra<LocalFileMenu>("folderInfo")
                var file = File(chooseFileData!!.filePath)
                if (file.exists()) {
                    var fileName = chooseFileData!!.filePath.substring(chooseFileData!!.filePath.lastIndexOf("/") + 1)
                    var aesKey = LibsodiumUtil.DecryptShareKey(chooseFileData!!.srcKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                    var fileTempPath  = PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ "upload"
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
                        val MsgType = fileTempPath.substring(fileTempPath.lastIndexOf(".") + 1).toLowerCase()
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
                        if (file.exists()) {

                            chooseFileData = LocalFileItem();
                            localMediaUpdate = LocalMedia()
                            localMediaUpdate!!.path = list.get(i).path
                            val MsgType = list.get(i).path.substring(list.get(i).path.lastIndexOf(".") + 1).toLowerCase()
                            localMediaUpdate!!.pictureType = "file"
                            when (MsgType) {
                                "png", "jpg", "jpeg", "webp" -> {
                                    localMediaUpdate!!.pictureType = "image/jpeg"
                                    // 配置压缩的参数
                                    var  bitmap = BitmapFactory.decodeFile(list.get(i).path);
                                    widthAndHeight = "" + bitmap.getWidth() + ".0000000" + "*" + bitmap.getHeight() + ".0000000";
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
                            var base58NameR = list.get(i).path.substring(list.get(i).path.lastIndexOf("/")+1,list.get(i).path.length)
                            if(  localMediaUpdate!!.pictureType == "image/jpeg")
                            {
                                val options = BitmapFactory.Options()
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = 16
                                val bmNew = BitmapFactory.decodeFile(list.get(i).path, options) // 解码文件
                                val thumbPath = fileLocalPath +"/th"+base58NameR
                                FileUtil.saveBitmpToFileNoThread(bmNew, thumbPath,50)
                            }else if(  localMediaUpdate!!.pictureType == "video/mp4")
                            {
                                val thumbPath = fileLocalPath +"/thbig"+base58NameR.replace("mp4","jpg")
                                val bitmap = EaseImageUtils.getVideoPhoto(list.get(i).path)
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
                            var aesKey = RxEncryptTool.generateAESKey()
                            var SrcKey = ByteArray(256)
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, ConstantValue.libsodiumpublicMiKey!!))
                            chooseFileData!!.srcKey = String(SrcKey);
                            chooseFileData!!.fileSize = file.length()
                            chooseFileData!!.filePath = list.get(i).path
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
                    /* SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
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
                             .show()*/
                } else {
                    Toast.makeText(this, getString(R.string.select_resource_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = null
        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}