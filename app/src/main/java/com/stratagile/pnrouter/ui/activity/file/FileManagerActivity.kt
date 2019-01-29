package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
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
                                    var filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port +data.fileName
                                    var files_dir = PathUtils.getInstance().filePath.toString()+"/"
                                    if (ConstantValue.isWebsocketConnected) {
                                        receiveFileDataMap.put(data.msgId.toString(),data)
                                        FileDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance,data.msgId, handler,data.userKey)
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
                                2 -> {

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

                }
                0x55 -> {
                    var data:Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    var jPushFileMsgRsp:JPullFileListRsp.ParamsBean.PayloadBean = receiveFileDataMap.get(msgId.toString())!!
                   /* var fileName:String = jPushFileMsgRsp.params.fileName;
                    var fromId = jPushFileMsgRsp.params.fromId;
                    var toId = jPushFileMsgRsp.params.toId
                    var FileType = jPushFileMsgRsp.params.fileType*/
                    receiveFileDataMap.remove(msgId.toString())
                }
            }//goMain();
            //goMain();
        }
    }
}