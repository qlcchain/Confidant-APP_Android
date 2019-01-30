package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.service.FileDownloadUploadService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RecentFile
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.TaskFile
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileTaskListComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileTaskListContract
import com.stratagile.pnrouter.ui.activity.file.module.FileTaskListModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileTaskListPresenter
import com.stratagile.pnrouter.ui.adapter.file.FileTaskLisytAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_file_task_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

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
        when(jUploadFileRsp.params.retCode)
        {
            0-> {
                runOnUiThread {
                    toast(getString(R.string.Start_uploading))
                }
                var fileName = localMedia!!.path.substring(localMedia!!.path.lastIndexOf("/")+1)
                var file = File(localMedia!!.path)
                if(file.exists())
                {

                    when(localMedia!!.pictureType)
                    {
                        "image/jpeg"-> {
                            FileMangerUtil.sendImageFile(localMedia!!.path,false)
                        }
                        "image/png"-> {
                            FileMangerUtil.sendImageFile(localMedia!!.path,false)
                        }
                        "video/mp4"-> {
                            FileMangerUtil.sendVideoFile(localMedia!!.path)
                        }
                        else -> {
                            FileMangerUtil.sendOtherFile(localMedia!!.path)
                        }
                    }


                }
            }
            1-> {
                runOnUiThread {
                    toast(getString(R.string.Documents_already_exist))
                }

            }
            2-> {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_task_list)
    }

    lateinit var ongoingTaskHead : TaskFile
    lateinit var completeTaskHead : TaskFile
    var localMedia:LocalMedia? = null
    var listGoing = mutableListOf<TaskFile>()
    var listComplete = mutableListOf<TaskFile>()

    override fun initData() {
        EventBus.getDefault().register(this)
        var startFileDownloadUploadService = Intent(this, FileDownloadUploadService::class.java)
        startService(startFileDownloadUploadService)
        title.text = "Task List"
        listGoing = mutableListOf<TaskFile>()
        listComplete  = mutableListOf<TaskFile>()
        ongoingTaskHead = TaskFile(true, "111")
        completeTaskHead = TaskFile(true, "222")

        AppConfig.instance.messageReceiver?.fileTaskBack = this
        var listData = intent.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
        if(listData != null && listData.size > 0)
        {
            for (i in listData) {
                var file = File(i.path)
                if(file.exists())
                {
                    localMedia = i
                    var fileName = localMedia!!.path.substring(localMedia!!.path.lastIndexOf("/")+1)
                    val fileNameBase58 = Base58.encode(fileName.toByteArray())
                    var fileSize = file.length()
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var fileType = 1
                    when(localMedia!!.pictureType)
                    {
                        "image/jpeg"-> {
                            fileType = 1
                        }
                        "image/png"-> {
                            fileType = 1
                        }
                        "video/mp4"-> {
                            fileType = 4
                        }
                        else -> {
                            fileType = 6
                        }
                    }
                    runOnUiThread {
                        showProgressDialog(getString(R.string.waiting))
                    }
                    var msgData = UploadFileReq(userId!!,fileNameBase58,fileSize,fileType)
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                    }else if (ConstantValue.isToxConnected) {
                        var baseData = BaseData(2,msgData)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        if (ConstantValue.isAntox) {
                            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }else{
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                        }
                    }
                }

            }

        }
        updataUI()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if(fileStatus.result == 1)
        {
            toast(R.string.File_does_not_exist)
        }
        updataUI()
    }
    fun updataUI()
    {
        listGoing = mutableListOf<TaskFile>()
        listComplete  = mutableListOf<TaskFile>()
        var localFilesList = LocalFileUtils.localFilesList
        listGoing.add(ongoingTaskHead)
        listComplete.add(completeTaskHead)
        for (myFie in localFilesList)
        {

            if(myFie.upLoadFile.isComplete == false)
            {
                listGoing.add(TaskFile(UpLoadFile(myFie.upLoadFile.path,myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, myFie.upLoadFile.isComplete, myFie.upLoadFile.isStop, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, myFie.upLoadFile.speed, myFie.upLoadFile.SendGgain)))
            }else{

                listComplete.add(TaskFile(UpLoadFile(myFie.upLoadFile.path,myFie.upLoadFile.fileSize, myFie.upLoadFile.isDownLoad, true, false, myFie.upLoadFile.segSeqResult, myFie.upLoadFile.segSeqTotal, 0,false)))
            }
        }
        fileGoingTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
        fileGoingTaskLisytAdapter.setOnItemChildClickListener { adapter, view, position ->
            var taskFile = fileGoingTaskLisytAdapter!!.getItem(position)
            var localMedia = taskFile!!.t
            var file = File(localMedia!!.path)
            if(file.exists())
            {
                if(localMedia!!.path.indexOf("jpg") > -1 || localMedia!!.path.indexOf("jpeg") > -1 || localMedia!!.path.indexOf("png") > -1 )
                {
                    FileMangerUtil.sendImageFile(localMedia!!.path,false)
                }else if(localMedia!!.path.indexOf("mp4") > -1 )
                {
                    FileMangerUtil.sendVideoFile(localMedia!!.path)
                }else{
                    FileMangerUtil.sendOtherFile(localMedia!!.path)
                }
            }
        }
        recyclerView.adapter = fileGoingTaskLisytAdapter
        recyclerView.setNestedScrollingEnabled(false)
        recyclerView.setHasFixedSize(true)
        fileCompleteTaskLisytAdapter = FileTaskLisytAdapter(listComplete)
        reSetHeadTitle()
        recyclerView2.adapter = fileCompleteTaskLisytAdapter
        recyclerView2.setNestedScrollingEnabled(false)
        recyclerView2.setHasFixedSize(true)
    }
    fun reSetHeadTitle() {
        var ongoing = fileGoingTaskLisytAdapter.data.size -1
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
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.fileTaskBack = null
    }
}