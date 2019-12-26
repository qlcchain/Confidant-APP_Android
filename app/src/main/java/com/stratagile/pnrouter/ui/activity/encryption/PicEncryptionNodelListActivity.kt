package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
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
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.UpdateAlbumNodeEncryptionItemEvent
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionNodelListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionNodelListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionNodelListPresenter
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
                DeleteUtils.deleteFile(filePath)
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
                                    when (data.name) {
                                        "Rename" -> {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encryption_nodefile_list)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = this
        _this = this;
        EventBus.getDefault().register(this)
        folderInfo = intent.getParcelableExtra("folderInfo")
        titleShow.text = folderInfo!!.fileName
        initPicPlug()

        showProgressDialog()
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

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.nodeFilesListPullCallback = null
        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}