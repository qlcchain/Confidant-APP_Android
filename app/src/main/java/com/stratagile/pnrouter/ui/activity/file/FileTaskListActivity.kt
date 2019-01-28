package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.service.FileDownloadUploadService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
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
                var fileName = localMedia!!.path.substring(localMedia!!.path.lastIndexOf("/")+1)
                var file = File(localMedia!!.path)
                if(file.exists())
                {
                    var fileSize = file.length()
                    var uploadFile = UpLoadFile(localMedia!!.path, false, false, false,0,fileSize,0)
                    val myRouter = MyFile()
                    myRouter.setType(0)
                    myRouter.setUpLoadFile(uploadFile)
                    LocalFileUtils.insertLocalAssets(myRouter)


                    var abc = LocalFileUtils.localAssetsList


                    FileMangerUtil.sendImageFile(localMedia!!.path,false)
                }
                runOnUiThread {
                    toast(getString(R.string.Start_uploading))
                    fileGoingTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
                    reSetHeadTitle()
                    recyclerView.adapter = fileGoingTaskLisytAdapter
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
        var intent = Intent(this, FileDownloadUploadService::class.java)
        startService(intent)
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
        listGoing.add(ongoingTaskHead)
        listGoing.add(TaskFile(UpLoadFile("ccc", false, true, true, 10, 100, 0)))
        listGoing.add(TaskFile(UpLoadFile("ccc", false, true, true, 10, 100, 0)))
        listGoing.add(TaskFile(UpLoadFile("ccc", false, true, true, 10, 100, 0)))
        fileGoingTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
        //fileGoingTaskLisytAdapter.notifyItemChanged()
        recyclerView.adapter = fileGoingTaskLisytAdapter

        listComplete.add(completeTaskHead)
        listComplete.add(TaskFile(UpLoadFile("ccc", false, true, true, 0, 0, 0)))
        listComplete.add(TaskFile(UpLoadFile("ccc", false, true, true, 0, 0, 0)))
        listComplete.add(TaskFile(UpLoadFile("ccc", false, true, true, 0, 0, 0)))

        fileCompleteTaskLisytAdapter = FileTaskLisytAdapter(listGoing)
        //fileGoingTaskLisytAdapter.notifyItemChanged()
        reSetHeadTitle()
        recyclerView2.adapter = fileCompleteTaskLisytAdapter
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {


    }
    fun reSetHeadTitle() {
        var ongoing = fileGoingTaskLisytAdapter.data.size
        var complete = fileCompleteTaskLisytAdapter.data.size

        ongoingTaskHead.header = "Ongoing (" + ongoing + ")"
        completeTaskHead.header = "Completed (" + complete + ")"
        fileGoingTaskLisytAdapter.notifyDataSetChanged()
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