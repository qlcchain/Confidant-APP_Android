package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.observable.ImagesObservable
import com.pawegio.kandroid.startActivityForResult
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
import com.stratagile.pnrouter.entity.file.TaskFile
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionlListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionlListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionlListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionlListPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.ui.adapter.file.FileTaskLisytAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.encryption_file_list.*
import kotlinx.android.synthetic.main.layout_encryption_file_list_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.Serializable
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:22
 */

class PicEncryptionlListActivity : BaseActivity(), PicEncryptionlListContract.View, PNRouterServiceMessageReceiver.FileTaskBack {
    override fun UploadFileRsp(jUploadFileRsp: JUploadFileRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        when (jUploadFileRsp.params.retCode) {
            0,1 -> {
                var fileName = localMediaUpdate!!.path.substring(localMediaUpdate!!.path.lastIndexOf("/") + 1)
                var file = File(localMediaUpdate!!.path)
                if (file.exists()) {
                    FileMangerUtil.setPorperty(3);
                    when (localMediaUpdate!!.pictureType) {
                        "image/jpeg" -> {
                            var result =  FileMangerUtil.sendImageFile(localMediaUpdate!!.path,"", false)
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
                        "image/jpeg" -> {
                            var result =  FileMangerUtil.sendImageFile(localMediaUpdate!!.path,"", false)
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
                            var result =  FileMangerUtil.sendVideoFile(localMediaUpdate!!.path,"")
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
                            var result =  FileMangerUtil.sendOtherFile(localMediaUpdate!!.path,"")
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
    var listGoing = mutableListOf<TaskFile>()
    var listComplete = mutableListOf<TaskFile>()
    var chooseFileData:LocalFileItem? = null;
    lateinit var ongoingTaskHead: TaskFile
    lateinit var completeTaskHead: TaskFile
    lateinit var fileGoingTaskLisytAdapter: FileTaskLisytAdapter

    lateinit var fileCompleteTaskLisytAdapter: FileTaskLisytAdapter
    var clickTimeMap = ConcurrentHashMap<String, Long>()

    var receiveFileDataMap = ConcurrentHashMap<String, UpLoadFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
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
    fun initUI() {
        listGoing = mutableListOf<TaskFile>()
        listComplete = mutableListOf<TaskFile>()
        var localFilesList = LocalFileUtils.localFilesList
        listGoing.add(ongoingTaskHead)
        listComplete.add(completeTaskHead)
        for (myFie in localFilesList) {

            if (myFie.upLoadFile.isComplete == false) {
                if(myFie.upLoadFile.isStop.equals("1"))
                {
                    listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.fileKey, myFie.upLoadFile.path,myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, "1", myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom,0,myFie.upLoadFile.msgId,false)))
                }
                else{
                    listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.fileKey, myFie.upLoadFile.path,myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, "0", myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom,0,myFie.upLoadFile.msgId,false)))
                }
            } else {

                listComplete.add(1, TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, true, "0", myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, 0, false,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom,0,myFie.upLoadFile.msgId,false)))
            }
        }
        fileGoingTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
        fileGoingTaskLisytAdapter!!.setOnItemClickListener { adapter, view, position ->
            var checkBox =  fileGoingTaskLisytAdapter!!.getViewByPosition(recyclerView,position,R.id.checkBox) as CheckBox
            if(checkBox.visibility ==View.VISIBLE)
            {
                checkBox.setChecked(!checkBox.isChecked)
                var status = 0;
                var isCheck = checkBox.isChecked
                if(tvDelete.visibility == View.VISIBLE)
                {
                    status = 1
                }
                var taskFile =  fileGoingTaskLisytAdapter!!.getItem(position)
                taskFile!!.t.status = status
                taskFile!!.t.isCheck = isCheck
                fileGoingTaskLisytAdapter.notifyItemChanged(position)
                updataCount()
            }

        }
        fileGoingTaskLisytAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.status ->
                {
                    var taskFile = fileGoingTaskLisytAdapter!!.getItem(position)
                    var localMedia = taskFile!!.t
                    var lastClickTime = clickTimeMap.get(taskFile.t.msgId)
                    if(lastClickTime == null)
                    {
                        lastClickTime = 0;
                    }
                    if(System.currentTimeMillis() - lastClickTime < 1000)
                    {
                        clickTimeMap.put(taskFile.t.msgId,System.currentTimeMillis())
                        return@setOnItemChildClickListener
                    }
                    var file = File(localMedia!!.path)
                    if(!localMedia!!.isDownLoad)
                    {
                        if (file.exists())
                        {
                            if (localMedia!!.path.indexOf("jpg") > -1 || localMedia!!.path.indexOf("jpeg") > -1 || localMedia!!.path.indexOf("png") > -1) {

                                runOnUiThread {
                                    fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                    fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                }
                                Thread(Runnable() {
                                    run() {

                                        var result =    FileMangerUtil.sendImageFile(localMedia!!.path,taskFile.t.msgId, false)
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
                                        runOnUiThread {
                                            fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                            fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                        }
                                    }
                                }).start()

                            } else if (localMedia!!.path.indexOf("mp4") > -1) {
                                runOnUiThread {
                                    fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                    fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                }

                                Thread(Runnable() {
                                    run() {

                                        var result =   FileMangerUtil.sendVideoFile(localMedia!!.path,taskFile.t.msgId)
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
                                        runOnUiThread {
                                            fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                            fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                        }
                                    }
                                }).start()

                            } else {
                                runOnUiThread {
                                    fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                    fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                }
                                Thread(Runnable() {
                                    run() {

                                        var result =  FileMangerUtil.sendOtherFile(localMedia!!.path,taskFile.t.msgId)
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
                                        runOnUiThread {
                                            fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                            fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                        }
                                    }
                                }).start()


                            }
                        }else{
                            LocalFileUtils.deleteLocalAssets(taskFile.t.msgId)
                            EventBus.getDefault().post(AllFileStatus())
                            runOnUiThread {
                                toast(getString(R.string.Local_file_does_not_exist))
                            }
                        }
                    }else{

                        var filledUri = localMedia!!.path
                        if(localMedia!!.path.indexOf("https://") < 0 )
                        {
                            filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + localMedia!!.path
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
                            receiveFileDataMap.put(localMedia!!.msgId,localMedia)
                            Thread(Runnable() {
                                run() {
                                    val uploadFile = UpLoadFile(localMedia!!.fileKey, filledUri,0, true, false, "2", 0, 1, 0, false, localMedia!!.userKey, localMedia!!.fileFrom,0,localMedia!!.msgId,false)
                                    val myRouter = MyFile()
                                    myRouter.type = 0
                                    myRouter.userSn = ConstantValue.currentRouterSN
                                    myRouter.upLoadFile = uploadFile
                                    LocalFileUtils.updateLocalAssets(myRouter)
                                    fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                                    fileGoingTaskLisytAdapter.notifyItemChanged(position)
                                    FileMangerDownloadUtils.doDownLoadWork(filledUri,localMedia!!.fileKey, files_dir, AppConfig.instance, localMedia!!.msgId.toInt(), handler, localMedia!!.userKey,localMedia!!.fileFrom)
                                }
                            }).start()

                        } else {
                            ConstantValue.receiveToxFileGlobalDataMap.put(localMedia!!.fileKey,localMedia!!.userKey)
                            val uploadFile = UpLoadFile(localMedia!!.fileKey, filledUri,0, true, false, "2", 0, 1, 0, false, localMedia!!.userKey, localMedia!!.fileFrom,0,localMedia!!.msgId,false)
                            val myRouter = MyFile()
                            myRouter.type = 0
                            myRouter.userSn = ConstantValue.currentRouterSN
                            myRouter.upLoadFile = uploadFile
                            LocalFileUtils.updateLocalAssets(myRouter)

                            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                            var msgData = PullFileReq(selfUserId!!, selfUserId!!, localMedia!!.fileKey, localMedia!!.msgId.toInt(), localMedia!!.fileFrom, 2)
                            var baseData = BaseData(msgData)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            fileGoingTaskLisytAdapter!!.getItem(position)!!.t.isStop = "2"
                            fileGoingTaskLisytAdapter.notifyItemChanged(position)
                            if (ConstantValue.isAntox) {
                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            } else {
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            }
                        }
                    }
                }
                R.id.stopBtn ->
                {
                    var taskFile = fileGoingTaskLisytAdapter!!.getItem(position)
                    var localMedia = taskFile!!.t
                    localMedia.isStop = "1"
                    localMedia.segSeqResult = 0
                    localMedia.segSeqTotal = 1
                    fileGoingTaskLisytAdapter.notifyItemChanged(position)

                    var isDown = true
                    if(!localMedia.isDownLoad && !localMedia.isComplete)
                    {
                        isDown = false
                    }
                    val uploadFile = UpLoadFile(localMedia!!.fileKey, localMedia!!.path,0, isDown, false, localMedia.isStop, 0, 1, 0, true, localMedia!!.userKey, localMedia!!.fileFrom,0,localMedia!!.msgId,localMedia!!.isCheck)
                    val myRouter = MyFile()
                    myRouter.type = 0
                    myRouter.userSn = ConstantValue.currentRouterSN
                    myRouter.upLoadFile = uploadFile
                    LocalFileUtils.updateLocalAssets(myRouter)
                    if (ConstantValue.isWebsocketConnected) {
                        if(!localMedia.isDownLoad && !localMedia.isComplete)
                        {
                            FileMangerUtil.cancelWebSocketWork(localMedia.msgId)
                        }
                        else if(localMedia.isDownLoad && !localMedia.isComplete)
                        {
                            FileMangerDownloadUtils.cancelWork(localMedia.msgId.toInt())
                        }
                    }else if (ConstantValue.isToxConnected) {
                        if(!localMedia.isDownLoad && !localMedia.isComplete)
                        {
                            FileMangerUtil.cancelFileSend(localMedia.msgId)
                        }
                        else if(localMedia.isDownLoad && !localMedia.isComplete)
                        {
                            FileMangerUtil.cancelFileReceive(localMedia.msgId)
                        }
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
        fileCompleteTaskLisytAdapter!!.setOnItemClickListener { adapter, view, position ->
            var checkBox =  fileCompleteTaskLisytAdapter!!.getViewByPosition(recyclerView2,position,R.id.checkBox) as CheckBox
            if(checkBox.visibility ==View.VISIBLE)
            {
                checkBox.setChecked(!checkBox.isChecked)
                var status = 0;
                var isCheck = checkBox.isChecked
              /*  if(tvDelete.visibility == View.VISIBLE)
                {
                    status = 1
                }*/
                var taskFile =  fileCompleteTaskLisytAdapter!!.getItem(position)
                taskFile!!.t.status = status
                taskFile!!.t.isCheck = isCheck
                fileCompleteTaskLisytAdapter.notifyItemChanged(position)
                updataCount()
            }

        }
    }
    fun updataCount()
    {
        var count = 0;
        fileGoingTaskLisytAdapter.data.forEachIndexed { index, it ->
            it.takeUnless { it.isHeader }?.let {
                if(it.t.isCheck)
                {
                    count ++;
                }
            }
        }
        fileCompleteTaskLisytAdapter.data.forEachIndexed { index, it ->
            it.takeUnless { it.isHeader }?.let {
                it.t.status = 1
                if(it.t.isCheck)
                {
                    count ++;
                }
            }
        }
        /* if(count > 0)
         {
             tvDelete.setText(getString(R.string.delete)+"("+count+")")
         }else{
             tvDelete.setText(getString(R.string.delete))
         }*/

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
                        LocalFileUtils.deleteLocalAssets(fileData!!.msgId)
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
                        var msgIdLeft = fileStatus.fileKey.substring(fileStatus.fileKey.indexOf("__")+2,fileStatus.fileKey.length)
                        if (msgIdLeft.equals(it.t.msgId)) {
                            for (myFie in localFilesList) {

                                if (msgIdLeft.equals(myFie.upLoadFile.msgId)) {
                                    if (myFie.upLoadFile.isComplete == false) {
                                        it.t.segSeqResult = myFie.upLoadFile.segSeqResult
                                        it.t.segSeqTotal = myFie.upLoadFile.segSeqTotal
                                        it.t.fileSize = myFie.upLoadFile.fileSize
                                        it.t.isStop = myFie.upLoadFile.isStop
                                        if(tvDelete.visibility == View.VISIBLE)
                                        {
                                            it.t.status = 1
                                        }else{
                                            it.t.status = 0
                                        }
                                        if (it.t.segSeqResult != 0) {
                                            var count  = it.t.segSeqTotal / it.t.segSeqResult
                                            KLog.i("count= " + count)
                                            if (count >= ToxCoreJni.progressBarMaxSeg - 1) {
                                                fileGoingTaskLisytAdapter.notifyItemChanged(index)
                                            } else {
                                                fileGoingTaskLisytAdapter.notifyItemChanged(index, "")
                                            }
                                        } else {
                                            fileGoingTaskLisytAdapter.notifyItemChanged(index)
                                        }
                                        KLog.i("上传中:"+myFie.upLoadFile.fileKey+"###"+myFie.upLoadFile.isStop)
                                    } else {
                                        listGoing.removeAt(index)
                                        var status  = 0
                                        if(tvDelete.visibility == View.VISIBLE)
                                        {
                                            status = 1
                                        }
                                        listComplete.add(1,TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, true, "0", myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, 0, false,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom,status,myFie.upLoadFile.msgId,it.t.isCheck)))
                                        reSetHeadTitle()
                                        fileGoingTaskLisytAdapter.notifyDataSetChanged()
                                        KLog.i("上传成功")
                                        fileCompleteTaskLisytAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                            return
                        }
                    }
                }
                for (myFie in localFilesList) {
                    var msgIdLeft = fileStatus.fileKey.substring(fileStatus.fileKey.indexOf("__")+2,fileStatus.fileKey.length)
                    if (myFie.upLoadFile.isComplete == false && msgIdLeft.equals(myFie.upLoadFile.msgId)) {
                        listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.fileKey,myFie.upLoadFile.path, myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, "0", myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain,myFie.upLoadFile.userKey,myFie.upLoadFile.fileFrom,0,myFie.upLoadFile.msgId,false)))
                        reSetHeadTitle()
                        fileGoingTaskLisytAdapter.notifyDataSetChanged()
                        KLog.i("新下载")
                    }
                }
            }

        }
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
        if (ongoing == 0 && complete == 0) {
            flEmpty.visibility = View.VISIBLE
        } else {
            flEmpty.visibility = View.GONE
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
                myFie.upLoadFile.isStop = "1"
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
    override fun initView() {
        setContentView(R.layout.encryption_file_list)

    }
    override fun initData() {
        var _this = this;
        EventBus.getDefault().register(this)
        AppConfig.instance.messageReceiver?.fileTaskBack = this
        listGoing = mutableListOf<TaskFile>()
        listComplete = mutableListOf<TaskFile>()
        ongoingTaskHead = TaskFile(true, "111")
        completeTaskHead = TaskFile(true, "222")
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
                    var fileTempPath  = PathUtils.getInstance().getEncryptionLocalPath().toString() +"/"+ "temp"
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
                    menuArray = arrayListOf<String>(getString(R.string.Node_back_up),getString(R.string.Delete))
                    iconArray = arrayListOf<String>("statusbar_download_node","statusbar_delete")
                    PopWindowUtil.showPopMenuWindow(this@PicEncryptionlListActivity, opMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            var data = obj as FileOpreateType
                            when (data.name) {
                                "Node back up" -> {
                                    val intent = Intent(AppConfig.instance, SelectNodeMenuActivity::class.java)

                                    startActivityForResult(intent,REQUEST_CODE_MENU)
                                }
                                "Delete" -> {
                                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_file))
                                            .setConfirmClickListener {
                                                var data = picItemEncryptionAdapter!!.getItem(position)
                                                var filePath = data!!.filePath;
                                                DeleteUtils.deleteFile(filePath)
                                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.delete(data)
                                                picItemEncryptionAdapter!!.remove(position)
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
                                                EventBus.getDefault().post(AddLocalEncryptionItemEvent())
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_MENU) { //
                var folderInfo = data!!.getParcelableExtra<LocalFileMenu>("folderInfo")
                var file = File(chooseFileData!!.filePath)
                if (file.exists()) {


                    var fileName = chooseFileData!!.filePath.substring(chooseFileData!!.filePath.lastIndexOf("/") + 1)

                    var aesKey = LibsodiumUtil.DecryptShareKey(chooseFileData!!.srcKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                    var fileTempPath  = PathUtils.getInstance().getEncryptionLocalPath().toString() +"/"+ "temp"
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
                        localMediaUpdate!!.pictureType = "image/jpeg"
                        val fileNameBase58 = Base58.encode(fileName.toByteArray())
                        var fileSize = file.length()
                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var fileType = 1
                        when (localMediaUpdate!!.pictureType) {
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
                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            } else {
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            }
                        }
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
                                val MsgType = imgeSouceName.substring(imgeSouceName.lastIndexOf(".") + 1)
                                when (MsgType) {
                                    "png", "jpg", "jpeg", "webp" ->  localFileItem.fileType = 1
                                    "amr" ->  localFileItem.fileType = 2
                                    "mp4" ->  localFileItem.fileType = 3
                                    else ->  localFileItem.fileType = 4
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
                                EventBus.getDefault().post(AddLocalEncryptionItemEvent())
                                AlbumNotifyHelper.deleteImagesInAlbumDB(AppConfig.instance, list.get(i).path)
                            }
                        }

                    }

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
        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionLocalPath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.fileTaskBack = null
        super.onDestroy()
    }
}