package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import android.os.Handler
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
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
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_file_task_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

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
                            var result =  FileMangerUtil.sendImageFile(localMedia!!.path,"", false)
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
                            var result =  FileMangerUtil.sendImageFile(localMedia!!.path,"", false)
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
                            var result =  FileMangerUtil.sendVideoFile(localMedia!!.path,"")
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
                            var result =  FileMangerUtil.sendOtherFile(localMedia!!.path,"")
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
    var clickTimeMap = ConcurrentHashMap<String, Long>()

    var receiveFileDataMap = ConcurrentHashMap<String, UpLoadFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.mainColor))
                        view.isAllCaps = false
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_task_list)
        tvDelete.setOnClickListener {
            onDeleteClick()
        }
    }

    fun onDeleteClick() {
        var deleteStr = ""
        if(fileGoingTaskLisytAdapter.data!= null && fileGoingTaskLisytAdapter.data.size >0)
        {
            var index = 0;

            fileGoingTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    var checkBox =  fileGoingTaskLisytAdapter!!.getViewByPosition(recyclerView,index,R.id.checkBox) as CheckBox
                    if(checkBox.isChecked)
                    {
                        deleteStr += index.toString() +","
                        //fileGoingTaskLisytAdapter.remove(index)
                        var localMedia = it!!.t
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

                        LocalFileUtils.deleteLocalAssetsByMsgId(localMedia.msgId)
                    }


                }
            }
            var deleteArray = deleteStr.split(",")
            var len = deleteArray.size
            for (i in len -1 downTo 0){
                var index = deleteArray[i]
                if(!index.equals(""))
                {
                    fileGoingTaskLisytAdapter.remove(index.toInt())
                }
            }
        }
        deleteStr = ""
        if(fileCompleteTaskLisytAdapter.data!= null && fileCompleteTaskLisytAdapter.data.size >0)
        {
            var index = 0;
            fileCompleteTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    var checkBox =  fileGoingTaskLisytAdapter!!.getViewByPosition(recyclerView2,index,R.id.checkBox) as CheckBox
                    if(checkBox.isChecked)
                    {
                        deleteStr += index.toString() +","
                        //fileCompleteTaskLisytAdapter.remove(index)
                        var localMedia = it!!.t
                        LocalFileUtils.deleteLocalAssetsByMsgId(localMedia.msgId)
                    }
                }
            }
            var deleteArray = deleteStr.split(",")
            var len = deleteArray.size
            for (i in len -1 downTo 0){
                var index = deleteArray[i]
                if(!index.equals(""))
                {
                    fileCompleteTaskLisytAdapter.remove(index.toInt())
                }
            }

        }

        mMenu?.getItem(0)?.setVisible(true)
        mMenu?.getItem(1)?.setVisible(false)
        tvDelete.visibility = View.GONE

        if(fileGoingTaskLisytAdapter.data!= null && fileGoingTaskLisytAdapter.data.size >0)
        {
            var ongoing = fileGoingTaskLisytAdapter.data.size -1
            ongoingTaskHead.header = "Ongoing (" + ongoing + ")"
            fileGoingTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 0
                    //fileGoingTaskLisytAdapter.notifyItemChanged(index)
                }
            }
            if(fileGoingTaskLisytAdapter.data.size == 1)
            {
                fileGoingTaskLisytAdapter.remove(0)
                recyclerView.visibility= View.GONE
            }else{
                recyclerView.visibility= View.VISIBLE
            }
            fileGoingTaskLisytAdapter.notifyDataSetChanged()
        }
        if(fileCompleteTaskLisytAdapter.data!= null && fileCompleteTaskLisytAdapter.data.size >0)
        {
            var complete = fileCompleteTaskLisytAdapter.data.size -1
            completeTaskHead.header = "Completed (" + complete + ")"
            fileCompleteTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 0
                    //fileCompleteTaskLisytAdapter.notifyItemChanged(index)
                }
            }
            if(fileCompleteTaskLisytAdapter.data.size == 1)
            {
                fileCompleteTaskLisytAdapter.remove(0)
                recyclerView2.visibility= View.GONE
            }else{
                recyclerView2.visibility= View.VISIBLE
            }
            fileCompleteTaskLisytAdapter.notifyDataSetChanged()
        }
        var ongoingTotal = fileGoingTaskLisytAdapter.data.size
        var completeTotal = fileCompleteTaskLisytAdapter.data.size
        if (ongoingTotal == 0 && completeTotal == 0) {
            flEmpty.visibility = View.VISIBLE
        } else {
            flEmpty.visibility = View.GONE
        }
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
    var mMenu : Menu? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tasklist_file, menu)
        mMenu = menu
        mMenu?.getItem(1)?.setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(fileGoingTaskLisytAdapter.data.size == 1 && fileCompleteTaskLisytAdapter.data.size == 1)
        {
            return super.onOptionsItemSelected(item)
        }
        if (item.itemId == R.id.optaskList)
        {
            mMenu?.getItem(0)?.setVisible(false)
            mMenu?.getItem(1)?.setVisible(true)
            tvDelete.visibility = View.VISIBLE
            tvDelete.setText(R.string.delete)
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
        } else if (item.itemId == R.id.cacanlTaskList) {
            mMenu?.getItem(0)?.setVisible(true)
            mMenu?.getItem(1)?.setVisible(false)
            tvDelete.visibility = View.GONE
            fileGoingTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 0
                    fileGoingTaskLisytAdapter.notifyItemChanged(index)
                }
            }
            fileCompleteTaskLisytAdapter.data.forEachIndexed { index, it ->
                it.takeUnless { it.isHeader }?.let {
                    it.t.status = 0
                    fileCompleteTaskLisytAdapter.notifyItemChanged(index)
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
        if(count > 0)
        {
            tvDelete.setText(getString(R.string.delete)+"("+count+")")
        }else{
            tvDelete.setText(getString(R.string.delete))
        }

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
                                        KLog.i("没有下载完:"+myFie.upLoadFile.fileKey+"###"+myFie.upLoadFile.isStop)
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
                                    FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, localMedia!!.msgId.toInt(), handler, localMedia!!.userKey,localMedia!!.fileFrom)
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
                                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
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
                if(tvDelete.visibility == View.VISIBLE)
                {
                    status = 1
                }
                var taskFile =  fileCompleteTaskLisytAdapter!!.getItem(position)
                taskFile!!.t.status = status
                taskFile!!.t.isCheck = isCheck
                fileCompleteTaskLisytAdapter.notifyItemChanged(position)
                updataCount()
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
    override fun onDestroy() {
        EventBus.getDefault().post(PullFileList())
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.fileTaskBack = null
        super.onDestroy()
    }
}