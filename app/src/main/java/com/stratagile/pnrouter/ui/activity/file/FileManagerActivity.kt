package com.stratagile.pnrouter.ui.activity.file

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.easeui.ui.EaseShowBigImageActivity
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RecentFile
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileManagerComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileManagerContract
import com.stratagile.pnrouter.ui.activity.file.module.FileManagerModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileManagerPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.FileListChooseAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.ease_search_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.io.File
import java.util.HashMap

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/23 14:15:29
 */

class FileManagerActivity : BaseActivity(), FileManagerContract.View, PNRouterServiceMessageReceiver.FileManageBack {
    override fun pullFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp) {
        if (jJToxPullFileRsp.params.retCode != 0) {
            runOnUiThread {
                toast(R.string.fail)
            }

        } else {
            runOnUiThread {
                toast(R.string.success)
            }

        }
    }

    override fun deleFileRsp(jDelFileRsp: JDelFileRsp) {

        when (jDelFileRsp.params.retCode) {
            0 -> {
                var recentFile = RecentFile()
                recentFile.fileName = String(Base58.decode(waitDeleteData?.fileName!!.substring(waitDeleteData?.fileName!!.lastIndexOf("/") + 1)))
                recentFile.fileType = 1
                recentFile.friendName = ""
                recentFile.opreateType = 2
                recentFile.timeStamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().recentFileDao.insert(recentFile)
                EventBus.getDefault().post(recentFile)
                runOnUiThread {
                    fileListChooseAdapter!!.data.remove(waitDeleteData!!)
                    fileListChooseAdapter!!.notifyDataSetChanged()
                    closeProgressDialog()
                    toast(R.string.deletsuccess)
                }
            }
            1 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.File_does_not_exist)
                }
            }
            2 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.No_authority)
                }
            }
        }
    }

    override fun pullFileListRsp(pullFileListRsp: JPullFileListRsp) {
        KLog.i("页面收到了文件列表拉取的返回了。")
        runOnUiThread {
            when (SpUtil.getInt(this, ConstantValue.currentArrangeType, 0)) {
                0 -> {
                    fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { it.fileName }?.toMutableList())
                }
                1 -> {
                    fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { it.timestamp }?.toMutableList())
                }
                2 -> {
                    fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { it.fileSize }?.toMutableList())
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: FileManagerPresenter

    var fileListChooseAdapter: FileListChooseAdapter? = null

    //类型，0 = My Files 1 = sent 2 = Documents received
    var fileType = 0

    var waitDeleteData: JPullFileListRsp.ParamsBean.PayloadBean? = null

    var receiveFileDataMap = HashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var receiveToxFileDataMap = HashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var wantOpen = false

    var beforeList = mutableListOf<JPullFileListRsp.ParamsBean.PayloadBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_manager)
        sort.setOnClickListener {
            PopWindowUtil.showFileSortWindow(this, sort, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    SpUtil.putInt(this@FileManagerActivity, ConstantValue.currentArrangeType, position)
                    when (position) {
                        0 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.fileName }.toMutableList())
                        }
                        1 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.timestamp }.toMutableList())
                        }
                        2 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.fileSize }.toMutableList())
                        }
                    }
                }

            })
        }
        query.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                KLog.i("beforeTextChanged " + charSequence.toString())
                if ("".equals(charSequence.toString())) {
                    beforeList = fileListChooseAdapter!!.data
                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                KLog.i("onTextChanged " + charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {
                KLog.i("afterTextChanged " + editable.toString())
                if ("".equals(editable.toString())) {
                    fileListChooseAdapter!!.setNewData(beforeList)
                } else {
                    fileListChooseAdapter!!.setNewData(searchListByName(editable.toString()))
                }
            }
        })
    }

    fun searchListByName(name: String): MutableList<JPullFileListRsp.ParamsBean.PayloadBean> {
        var retList = mutableListOf<JPullFileListRsp.ParamsBean.PayloadBean>()
        beforeList.forEach {
            var fileName = String(Base58.decode(it.fileName.substring(it.fileName.lastIndexOf("/") + 1)))
            if (fileName.contains(name)) {
                retList.add(it)
            }
        }
        return retList
    }


    override fun initData() {
        EventBus.getDefault().register(this)
        AppConfig.instance.messageReceiver?.fileManageBack = this
        fileType = intent.getIntExtra("fileType", 0)
        when (fileType) {
            0 -> {
                title.text = resources.getText(R.string.all_Files)
            }
            1 -> {
                title.text = resources.getText(R.string.files_sent)
            }
            2 -> {
                title.text = resources.getText(R.string.file_received)
            }
        }

        pullFileList()

        fileListChooseAdapter = FileListChooseAdapter(arrayListOf())
        recyclerView.adapter = fileListChooseAdapter
        fileListChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = fileListChooseAdapter!!.getItem(position)
            /*var filePath = "" + Environment.getExternalStorageDirectory() + "/1/接口介绍.pdf"
            if (position == 0) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/test.txt"
            } else if (position == 1) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/tupian.jpg"
            } else if (position == 2) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/xxx.jpg"
            } else if (position == 3) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/vpn.xlsx"
            }*/
            startActivity(Intent(this, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }

        fileListChooseAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.fileOpreate -> {
                    PopWindowUtil.showFileOpreatePopWindow(this@FileManagerActivity, recyclerView, fileListChooseAdapter!!.data[position], object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            var data = obj as JPullFileListRsp.ParamsBean.PayloadBean
                            when (position) {
                                0 -> {

                                }
                                1 -> {
                                    var recentFile = RecentFile()
                                    recentFile.fileName = String(Base58.decode(data.fileName!!.substring(data.fileName!!.lastIndexOf("/") + 1)))
                                    recentFile.fileType = 1
                                    recentFile.friendName = ""
                                    recentFile.opreateType = 1
                                    recentFile.timeStamp = Calendar.getInstance().timeInMillis
                                    AppConfig.instance.mDaoMaster!!.newSession().recentFileDao.insert(recentFile)
                                    EventBus.getDefault().post(recentFile)

                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                                    var fileOrginName = String(Base58.decode(fileMiName))
                                    var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileOrginName
                                    var fileMiPath = PathUtils.getInstance().tempPath.toString() + "/" + fileOrginName
                                    var file = File(filePath)
                                    if(file.exists())
                                    {
                                        DeleteUtils.deleteFile(filePath)
                                    }
                                    var fileMi = File(fileMiPath)
                                    if(fileMi.exists())
                                    {
                                        DeleteUtils.deleteFile(fileMiPath)
                                    }
                                    if (false) {
                                        runOnUiThread {
                                            toast(R.string.no_download_is_required)
                                        }
                                    } else {
                                        var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port + data.fileName
                                        var files_dir = PathUtils.getInstance().filePath.toString() + "/"
                                        if (ConstantValue.isWebsocketConnected) {
                                            receiveFileDataMap.put(data.msgId.toString(), data)

                                            Thread(Runnable() {
                                                run() {
                                                    FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, data.msgId, handler, data.userKey,data.fileFrom)
                                                }
                                            }).start()

                                        } else {
                                            receiveToxFileDataMap.put(fileOrginName,data)
                                            ConstantValue.receiveToxFileGlobalDataMap.put(fileMiName,data.userKey)
                                            val uploadFile = UpLoadFile(fileMiName, filledUri,0, true, false, false, 0, 1, 0, false,data.userKey,data.fileFrom)
                                            val myRouter = MyFile()
                                            myRouter.type = 0
                                            myRouter.userSn = ConstantValue.currentRouterSN
                                            myRouter.upLoadFile = uploadFile
                                            LocalFileUtils.insertLocalAssets(myRouter)
                                            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                            var msgData = PullFileReq(selfUserId!!, selfUserId!!, fileMiName, data.msgId, data.fileFrom, 2)
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

                                }
                                2 -> {
                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                                    var fileOrginName = String(Base58.decode(fileMiName))
                                    var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileOrginName
                                    var file = File(filePath)
                                    if (!file.exists()) {
                                        runOnUiThread {
                                            toast(R.string.You_need_to_download)
                                        }
                                        wantOpen = true
                                        var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port + data.fileName
                                        var files_dir = PathUtils.getInstance().filePath.toString() + "/"
                                        if (ConstantValue.isWebsocketConnected) {
                                            receiveFileDataMap.put(data.msgId.toString(), data)
                                            FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, data.msgId, handler, data.userKey,data.fileFrom)
                                        } else {
                                            receiveToxFileDataMap.put(fileOrginName,data)
                                            ConstantValue.receiveToxFileGlobalDataMap.put(fileMiName,data.userKey)
                                            val uploadFile = UpLoadFile(fileMiName,filledUri, 0, true, false, false, 0, 1, 0, false,data.userKey, data.fileFrom)
                                            val myRouter = MyFile()
                                            myRouter.type = 0
                                            myRouter.userSn = ConstantValue.currentRouterSN
                                            myRouter.upLoadFile = uploadFile
                                            LocalFileUtils.insertLocalAssets(myRouter)

                                            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                            var msgData = PullFileReq(selfUserId!!, selfUserId!!, fileMiName, data.msgId, data.fileFrom, 2)
                                            var baseData = BaseData(msgData)
                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                            if (ConstantValue.isAntox) {
                                                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                            } else {
                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                            }
                                        }
                                    } else {
                                        openFile(filePath)

                                    }
                                }
                                3 -> {
                                    startActivity(Intent(this@FileManagerActivity, FileDetailInformationActivity::class.java).putExtra("file", data))
                                }
                                4 -> {

                                }
                                5 -> {
                                    showProgressDialog("wait…")
                                    waitDeleteData = data
                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                    var delFileReq = DelFileReq(selfUserId!!, data.fileName)
                                    var sendData = BaseData(2, delFileReq)
                                    if (ConstantValue.isWebsocketConnected) {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                    } else if (ConstantValue.isToxConnected) {
                                        var baseData = sendData
                                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                        if (ConstantValue.isAntox) {
                                            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                        } else {
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                    }

                                }
                            }

                        }
                    })
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            if(fileStatus.downLoad == true)
            {
                toast(R.string.Download_failed)
            }else{
                toast(R.string.upload_failed)
            }

        } else if (fileStatus.result == 2) {
            toast(R.string.Files_100M)
        }else {
            if(fileStatus.complete)
            {
                if(fileStatus.downLoad == true)
                {
                    toast(R.string.Download_success)
                }else{
                    toast(R.string.Upload_success)
                }

            }
        }
    }
    fun pullFileList() {
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullFileListReq = PullFileListReq(selfUserId!!, 0, 30, fileType, 0)
        var sendData = BaseData(2, pullFileListReq)
        if (ConstantValue.isWebsocketConnected) {
            Log.i("pullFriendList", "webosocket" + AppConfig.instance.getPNRouterServiceMessageSender())
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        } else if (ConstantValue.isToxConnected) {
            Log.i("pullFriendList", "tox")
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    fun openFile(filePath: String) {
        var fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
        var file = File(filePath)
        if (file.exists())
            if (fileName.indexOf("jpg") > -1 || fileName.indexOf("jpeg") > -1 || fileName.indexOf("png") > -1) {
                val intent = Intent(AppConfig.instance, EaseShowBigImageActivity::class.java)
                val file = File(filePath)
                val uri = Uri.fromFile(file)
                intent.putExtra("uri", uri)
                startActivity(intent)
            } else if (fileName.indexOf("mp4") > -1) {
                val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                intent.putExtra("path", filePath)
                startActivity(intent)
            } else {
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

    override fun setupActivityComponent() {
        DaggerFileManagerComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .fileManagerModule(FileManagerModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileManagerContract.FileManagerContractPresenter) {
        mPresenter = presenter as FileManagerPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    var SELECT_PHOTO = 2
    var SELECT_VIDEO = 3
    var SELECT_DEOCUMENT = 4

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.upLoadFile) {
            PopWindowUtil.showFileUploadPopWindow(this@FileManagerActivity, recyclerView, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    when (position) {
                        0 -> {
//                            startActivityForResult(Intent(this@MainActivity, FileChooseActivity::class.java).putExtra("fileType", 1), 0)
                            PictureSelector.create(this@FileManagerActivity)
                                    .openGallery(PictureMimeType.ofImage())
//                                    .theme()
                                    .maxSelectNum(100)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
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
                                    .forResult(SELECT_PHOTO)
                        }
                        1 -> {
                            PictureSelector.create(this@FileManagerActivity)
                                    .openGallery(PictureMimeType.ofVideo())
//                                    .theme()
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
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
                                    .forResult(SELECT_VIDEO)
                        }
                        2 -> {
                            startActivityForResult(Intent(this@FileManagerActivity, FileChooseActivity::class.java).putExtra("fileType", 2), SELECT_DEOCUMENT)
                        }
                    }
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            return
        } else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_DEOCUMENT && resultCode == Activity.RESULT_OK) {
            var list = ArrayList<LocalMedia>()
            var localMedia = LocalMedia()
            localMedia.path = data!!.getStringExtra("path")
            list.add(localMedia)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        }
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            hideSoftKeyboard()
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.upload_file, menu)
        return super.onCreateOptionsMenu(menu)
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
                    /* if(wantOpen)
                     {
                         var fromData = receiveFileDataMap.get(msgId.toString())
                         var fileMiName =fromData!!.fileName.substring(fromData!!.fileName.lastIndexOf("/")+1,fromData!!.fileName.length)
                         var base58Name =  String(Base58.decode(fileMiName))
                         var filePath = PathUtils.getInstance().filePath.toString()+"/"+base58Name
                         openFile(filePath)
                         wantOpen = false
                     }*/
                    receiveFileDataMap.remove(msgId.toString())
                }
            }//goMain();
            //goMain();
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
