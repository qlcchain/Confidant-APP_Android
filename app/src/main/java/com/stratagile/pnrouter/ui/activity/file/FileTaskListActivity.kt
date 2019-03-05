package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.AllFileStatus
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.events.PullFileList
import com.stratagile.pnrouter.entity.file.TaskFile
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileTaskListComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileTaskListContract
import com.stratagile.pnrouter.ui.activity.file.module.FileTaskListModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileTaskListPresenter
import com.stratagile.pnrouter.ui.adapter.file.FileTaskLisytAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_file_task_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.HashMap

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: $description
 * @date 2019/01/25 16:21:04
 */

class FileTaskListActivity : BaseActivity(), FileTaskListContract.View, PNRouterServiceMessageReceiver.FileTaskBack {
    override fun UploadFileRsp(jUploadFileRsp: JUploadFileRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        when (jUploadFileRsp.params.retCode) {
            0,1 -> {

                var fileName = localMedia!!.path.substring(localMedia!!.path.lastIndexOf("/") + 1)
                var file = File(localMedia!!.path)
                if (file.exists()) {

                    when (localMedia!!.pictureType) {
                        "image/jpeg" -> {
                            var result =  FileMangerUtil.sendImageFile(localMedia!!.path, false)
                            if(result  == 1)
                            {
                                runOnUiThread {
                                    toast(getString(R.string.Start_uploading))
                                }
                            }
                        }
                        "image/png" -> {
                            var result =  FileMangerUtil.sendImageFile(localMedia!!.path, false)
                            if(result  == 1)
                            {
                                runOnUiThread {
                                    toast(getString(R.string.Start_uploading))
                                }
                            }else{
                                runOnUiThread {
                                    toast(getString(R.string.Already_on_the_list))
                                }
                            }
                        }
                        "video/mp4" -> {
                            var result =  FileMangerUtil.sendVideoFile(localMedia!!.path)
                            if(result  == 1)
                            {
                                runOnUiThread {
                                    toast(getString(R.string.Start_uploading))
                                }
                            }else{
                                runOnUiThread {
                                    toast(getString(R.string.Already_on_the_list))
                                }
                            }
                        }
                        else -> {
                            var result =  FileMangerUtil.sendOtherFile(localMedia!!.path)
                            if(result  == 1)
                            {
                                runOnUiThread {
                                    toast(getString(R.string.Start_uploading))
                                }
                            }else{
                                runOnUiThread {
                                    toast(getString(R.string.Already_on_the_list))
                                }
                            }
                        }
                    }

                }
            }
            2 -> {
                runOnUiThread {
                    toast(getString(R.string.not_enough_space))
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: FileTaskListPresenter

    lateinit var fileGoingTaskLisytAdapter: FileTaskLisytAdapter

    lateinit var fileCompleteTaskLisytAdapter: FileTaskLisytAdapter

    var receiveFileDataMap = HashMap<String, UpLoadFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_task_list)
    }

    lateinit var ongoingTaskHead: TaskFile
    lateinit var completeTaskHead: TaskFile
    var localMedia: LocalMedia? = null
    var listGoing = mutableListOf<TaskFile>()
    var listComplete = mutableListOf<TaskFile>()

    override fun initData() {
        EventBus.getDefault().register(this)
        title.text = "Task List"
        listGoing = mutableListOf<TaskFile>()
        listComplete = mutableListOf<TaskFile>()
        ongoingTaskHead = TaskFile(true, "111")
        completeTaskHead = TaskFile(true, "222")

        AppConfig.instance.messageReceiver?.fileTaskBack = this
        var listData = intent.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
        if (listData != null && listData.size > 0) {
            for (i in listData) {
                var file = File(i.path)
                if (file.exists()) {
                    localMedia = i
                    var fileName = localMedia!!.path.substring(localMedia!!.path.lastIndexOf("/") + 1)
                    val fileNameBase58 = Base58.encode(fileName.toByteArray())
                    var fileSize = file.length()
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var fileType = 1
                    when (localMedia!!.pictureType) {
                        "image/jpeg" -> {
                            fileType = 1
                        }
                        "image/png" -> {
                            fileType = 1
                        }
                        "video/mp4" -> {
                            fileType = 4
                        }
                        else -> {
                            fileType = 6
                        }
                    }
                    runOnUiThread {
                        showProgressDialog(getString(R.string.waiting))
                    }
                    var msgData = UploadFileReq(userId!!, fileNameBase58, fileSize, fileType)
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData))
                    } else if (ConstantValue.isToxConnected) {
                        var baseData = BaseData(2, msgData)
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

        initUI()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tasklist_file, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.optaskList)
        {
            tvDelete.visibility = View.VISIBLE
            fileGoingTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 1
                    fileGoingTaskLisytAdapter.notifyItemChanged(index)
                }
            }
            fileCompleteTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 1
                    fileCompleteTaskLisytAdapter.notifyItemChanged(index)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态FileTaskListActivity:"+connectStatus.status)
        if(connectStatus.status != 0)
        {
            resetUnCompleteFileRecode()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        KLog.i("tox状态FileTaskListActivity:"+toxStatusEvent.status)
        if(toxStatusEvent.status != 0)
        {
            resetUnCompleteFileRecode()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {
        KLog.i("tox好友状态FileTaskListActivity:"+toxFriendStatusEvent.status)
        if(toxFriendStatusEvent.status == 0)
        {
            resetUnCompleteFileRecode()
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAllFileStatusChange(allFileStatus: AllFileStatus) {
        initUI()
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
            kotlin.run {
                KLog.i(fileStatus.fileKey)
                var localFilesList = LocalFileUtils.localFilesList
                listGoing.forEachIndexed { index, it ->
                    it.takeUnless { it.isHeader }?.let {
                        if (it.t.fileKey.equals(fileStatus.fileKey)) {
                            for (myFie in localFilesList) {
                                if (myFie.upLoadFile.fileKey.equals(fileStatus.fileKey)) {
                                    if (myFie.upLoadFile.isComplete == false) {
                                        it.t.segSeqResult = myFie.upLoadFile.segSeqResult
                                        it.t.segSeqTotal = myFie.upLoadFile.segSeqTotal
                                        it.t.fileSize = myFie.upLoadFile.fileSize
                                        fileGoingTaskLisytAdapter.notifyItemChanged(index)
                                        KLog.i("没有下载完")
                                    } else {
                                        listGoing.removeAt(index)
                                        listComplete.add(1,TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, true, false, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, 0, false,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom)))
                                        reSetHeadTitle()
                                        fileGoingTaskLisytAdapter.notifyDataSetChanged()
                                        KLog.i("下载完1")
                                        fileCompleteTaskLisytAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            return
                        }
                    }
                }
                for (myFie in localFilesList) {
                    if (myFie.upLoadFile.isComplete == false && fileStatus.fileKey.equals(myFie.upLoadFile.fileKey)) {
                        listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, myFie.upLoadFile.isStop, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom)))
                        reSetHeadTitle()
                        fileGoingTaskLisytAdapter.notifyDataSetChanged()
                        KLog.i("新下载")
                    }
                }
            }

        }
    }

    fun initUI() {
        listGoing = mutableListOf<TaskFile>()
        listComplete = mutableListOf<TaskFile>()
        var localFilesList = LocalFileUtils.localFilesList
        listGoing.add(ongoingTaskHead)
        listComplete.add(completeTaskHead)
        for (myFie in localFilesList) {

            if (myFie.upLoadFile.isComplete == false) {
                listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.fileKey, myFie.upLoadFile.path,myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, myFie.upLoadFile.isStop, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom)))
            } else {

                listComplete.add(1, TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, true, false, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, 0, false,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom)))
            }
        }
        fileGoingTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
        fileGoingTaskLisytAdapter.setOnItemChildClickListener { adapter, view, position ->
            var taskFile = fileGoingTaskLisytAdapter!!.getItem(position)
            var localMedia = taskFile!!.t
            var file = File(localMedia!!.path)
            fileGoingTaskLisytAdapter!!.getItem(position)!!.t.SendGgain = false
            if(!localMedia!!.isDownLoad)
            {
                if (file.exists()) {
                    if (localMedia!!.path.indexOf("jpg") > -1 || localMedia!!.path.indexOf("jpeg") > -1 || localMedia!!.path.indexOf("png") > -1) {
                        var result =    FileMangerUtil.sendImageFile(localMedia!!.path, false)
                        if(result  == 1)
                        {
                            runOnUiThread {
                                toast(getString(R.string.Start_uploading))
                            }
                        }else{
                            runOnUiThread {
                                toast(getString(R.string.Already_on_the_list))
                            }
                        }
                    } else if (localMedia!!.path.indexOf("mp4") > -1) {
                        var result =   FileMangerUtil.sendVideoFile(localMedia!!.path)
                        if(result  == 1)
                        {
                            runOnUiThread {
                                toast(getString(R.string.Start_uploading))
                            }
                        }else{
                            runOnUiThread {
                                toast(getString(R.string.Already_on_the_list))
                            }
                        }
                    } else {
                        var result =  FileMangerUtil.sendOtherFile(localMedia!!.path)
                        if(result  == 1)
                        {
                            runOnUiThread {
                                toast(getString(R.string.Start_uploading))
                            }
                        }else{
                            runOnUiThread {
                                toast(getString(R.string.Already_on_the_list))
                            }
                        }
                    }
                }
            }else{

                var filledUri = localMedia!!.path
                if(localMedia!!.path.indexOf("https://") < 0 )
                {
                    filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port + localMedia!!.path
                }
                var files_dir = PathUtils.getInstance().filePath.toString() + "/"

                var fileMiName = localMedia!!.fileKey
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

                if (ConstantValue.isWebsocketConnected) {
                    var msgId =  (System.currentTimeMillis() / 1000).toInt()
                    receiveFileDataMap.put(msgId.toString(),localMedia)
                    Thread(Runnable() {
                        run() {
                            val uploadFile = UpLoadFile(localMedia!!.fileKey, filledUri,0, true, false, false, 0, 1, 0, false, localMedia!!.userKey, localMedia!!.fileFrom)
                            val myRouter = MyFile()
                            myRouter.type = 0
                            myRouter.userSn = ConstantValue.currentRouterSN
                            myRouter.upLoadFile = uploadFile
                            LocalFileUtils.updateLocalAssets(myRouter)
                            FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, msgId, handler, localMedia!!.userKey,localMedia!!.fileFrom)
                        }
                    }).start()

                } else {
                    var msgId =  (System.currentTimeMillis() / 1000).toInt()
                    ConstantValue.receiveToxFileGlobalDataMap.put(localMedia!!.fileKey,localMedia!!.userKey)
                    val uploadFile = UpLoadFile(localMedia!!.fileKey, filledUri,0, true, false, false, 0, 1, 0, false, localMedia!!.userKey, localMedia!!.fileFrom)
                    val myRouter = MyFile()
                    myRouter.type = 0
                    myRouter.userSn = ConstantValue.currentRouterSN
                    myRouter.upLoadFile = uploadFile
                    LocalFileUtils.updateLocalAssets(myRouter)

                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var msgData = PullFileReq(selfUserId!!, selfUserId!!, localMedia!!.fileKey, msgId, localMedia!!.fileFrom, 2)
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
        recyclerView.adapter = fileGoingTaskLisytAdapter
        recyclerView.setNestedScrollingEnabled(false)
        recyclerView.setHasFixedSize(true)
        fileCompleteTaskLisytAdapter = FileTaskLisytAdapter(listComplete)
        recyclerView2.adapter = fileCompleteTaskLisytAdapter
        recyclerView2.setNestedScrollingEnabled(false)
        recyclerView2.setHasFixedSize(true)
        reSetHeadTitle()
    }

    fun reSetHeadTitle() {
        var ongoing = fileGoingTaskLisytAdapter.data.size - 1
        var complete = fileCompleteTaskLisytAdapter.data.size - 1

        ongoingTaskHead.header = "Ongoing (" + ongoing + ")"
        completeTaskHead.header = "Completed (" + complete + ")"
        fileGoingTaskLisytAdapter.notifyDataSetChanged()
        if (ongoing == 0) {
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
        }
        if (complete == 0) {
            recyclerView2.visibility = View.GONE
        } else {
            recyclerView2.visibility = View.VISIBLE
        }
    }

    override fun setupActivityComponent() {
        DaggerFileTaskListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .fileTaskListModule(FileTaskListModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileTaskListContract.FileTaskListContractPresenter) {
        mPresenter = presenter as FileTaskListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    var fileData = receiveFileDataMap.get(msgId.toString())
                    if(fileData != null)
                    {
                        LocalFileUtils.deleteLocalAssets(fileData!!.fileKey)
                        EventBus.getDefault().post(AllFileStatus())
                    }

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
    fun resetUnCompleteFileRecode()
    {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList)
        {
            if(myFie.upLoadFile.isComplete == false)
            {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
        initUI()

    }
    override fun onDestroy() {
        EventBus.getDefault().post(PullFileList())
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.fileTaskBack = null
        super.onDestroy()
    }
}