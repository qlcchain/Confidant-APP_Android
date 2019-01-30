package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
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
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
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
    override fun deleFileRsp(jDelFileRsp: JDelFileRsp) {

        when(jDelFileRsp.params.retCode)
        {
            0 ->{

                runOnUiThread {
                    fileListChooseAdapter!!.data.remove(waitDeleteData!!)
                    fileListChooseAdapter!!.notifyDataSetChanged()
                    closeProgressDialog()
                    toast(R.string.deletsuccess)
                }
            }
            1 ->{
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.File_does_not_exist)
                }
            }
            2 ->{
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
            fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload)
        }
    }

    @Inject
    internal lateinit var mPresenter: FileManagerPresenter

    var fileListChooseAdapter : FileListChooseAdapter? = null

    //类型，0 = My Files 1 = sent 2 = Documents received
    var fileType = 0

    var waitDeleteData:JPullFileListRsp.ParamsBean.PayloadBean? = null

    var receiveFileDataMap = HashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var receiveToxFileDataMap = HashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var wantOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_manager)
        sort.setOnClickListener {
            PopWindowUtil.showFileSortWindow(this, sort, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    ConstantValue.currentArrangeType = position
                }

            })
        }
    }


    override fun initData() {
        AppConfig.instance.messageReceiver?.fileManageBack = this
        fileType = intent.getIntExtra("fileType", 0)
        when(fileType) {
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
            var filePath = "" + Environment.getExternalStorageDirectory() + "/1/接口介绍.pdf"
            if (position == 0) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/test.txt"
            } else if (position == 1) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/tupian.jpg"
            } else if (position == 2) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/xxx.jpg"
            } else if (position == 3) {
                filePath = "" + Environment.getExternalStorageDirectory() + "/1/vpn.xlsx"
            }
            startActivity(Intent(this, PdfViewActivity::class.java).putExtra("filePath", filePath))
        }

        fileListChooseAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.fileOpreate -> {
                    PopWindowUtil.showFileOpreatePopWindow(this@FileManagerActivity, recyclerView, fileListChooseAdapter!!.data[position], object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj : Any) {
                            KLog.i("" + position)
                            var data = obj as JPullFileListRsp.ParamsBean.PayloadBean
                            when(position) {
                                0 -> {

                                }
                                1 -> {
                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/")+1,data.fileName.length)
                                    var base58Name =  String(Base58.decode(fileMiName))
                                    var filePath = PathUtils.getInstance().filePath.toString()+"/"+base58Name
                                    var file = File(filePath)
                                    if(file.exists())
                                    {
                                        runOnUiThread {
                                            toast(R.string.no_download_is_required)
                                        }
                                    }else{
                                        runOnUiThread {
                                            showProgressDialog("wait…")
                                        }
                                        var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port +data.fileName
                                        var files_dir = PathUtils.getInstance().filePath.toString()+"/"
                                        if (ConstantValue.isWebsocketConnected) {
                                            receiveFileDataMap.put(data.msgId.toString(),data)
                                            val uploadFile = UpLoadFile(filledUri, 0, true, false, false, 0, 1, 0, false)
                                            val myRouter = MyFile()
                                            myRouter.type = 0
                                            myRouter.userSn = ConstantValue.currentRouterSN
                                            myRouter.upLoadFile = uploadFile
                                            LocalFileUtils.insertLocalAssets(myRouter)

                                            FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance,data.msgId, handler,data.userKey)

                                        }else{
                                            /*var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/")+1,data.fileName.length)
                                            var base58Name =  Base58.encode(fileMiName.toByteArray())
                                            receiveToxFileDataMap.put(base58Name,data)
                                            var msgData = PullFileReq(jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,base58Name,data.msgId,2)
                                            var baseData = BaseData(msgData)
                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                            if (ConstantValue.isAntox) {
                                                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                            }else{
                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                            }*/
                                        }
                                    }

                                }
                                2 -> {
                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/")+1,data.fileName.length)
                                    var base58Name =  String(Base58.decode(fileMiName))
                                    var filePath = PathUtils.getInstance().filePath.toString()+"/"+base58Name
                                    var file = File(filePath)
                                    if(!file.exists())
                                    {
                                        wantOpen = true
                                        runOnUiThread {
                                            toast(R.string.You_need_to_download)
                                            showProgressDialog("wait…")
                                        }
                                        var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port +data.fileName
                                        var files_dir = PathUtils.getInstance().filePath.toString()+"/"
                                        if (ConstantValue.isWebsocketConnected) {
                                            receiveFileDataMap.put(data.msgId.toString(),data)
                                            val uploadFile = UpLoadFile(filledUri, 0, true, false, false, 0, 1, 0, false)
                                            val myRouter = MyFile()
                                            myRouter.type = 0
                                            myRouter.userSn = ConstantValue.currentRouterSN
                                            myRouter.upLoadFile = uploadFile
                                            LocalFileUtils.insertLocalAssets(myRouter)
                                            FileMangerDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance,data.msgId, handler,data.userKey)
                                        }else{

                                            /*var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/")+1,data.fileName.length)
                                            var base58Name =  Base58.encode(fileMiName.toByteArray())
                                            receiveToxFileDataMap.put(base58Name,data)
                                            var msgData = PullFileReq(jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,base58Name,data.msgId,2)
                                            var baseData = BaseData(msgData)
                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                            if (ConstantValue.isAntox) {
                                                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                            }else{
                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                            }*/
                                        }
                                    }else{
                                        openFile(filePath)

                                    }
                                }
                                3 -> {
                                    startActivity(Intent(this@FileManagerActivity, FileDetailInformationActivity::class.java))
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
                                    }else if (ConstantValue.isToxConnected) {
                                        var baseData = sendData
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

                    })
                }
            }
        }
    }

    fun pullFileList() {
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullFileListReq = PullFileListReq(selfUserId!!, 0, 10, fileType, 0)
        var sendData = BaseData(2, pullFileListReq)
        if (ConstantValue.isWebsocketConnected) {
            Log.i("pullFriendList", "webosocket" + AppConfig.instance.getPNRouterServiceMessageSender())
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            Log.i("pullFriendList", "tox")
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    fun openFile(filePath:String)
    {
        var fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length)
        var file = File(filePath)
        if (file.exists())
            if(fileName.indexOf("jpg") > -1 || fileName.indexOf("jpeg") > -1 || fileName.indexOf("png") > -1 )
            {
                val intent = Intent(AppConfig.instance, EaseShowBigImageActivity::class.java)
                val file = File(filePath)
                val uri = Uri.fromFile(file)
                intent.putExtra("uri", uri)
                startActivity(intent)
            }else if(fileName.indexOf("mp4") > -1 )
            {
                val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                intent.putExtra("path", filePath)
                startActivity(intent)
            }else{
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.chooseFile) {
            fileListChooseAdapter!!.isChooseMode = !fileListChooseAdapter!!.isChooseMode
            fileListChooseAdapter!!.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.choose_file, menu)
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
                    var data:Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                }
                0x55 -> {
                    var data:Bundle = msg.data;
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
}