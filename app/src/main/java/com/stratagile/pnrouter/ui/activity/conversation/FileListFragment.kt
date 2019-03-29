package com.stratagile.pnrouter.ui.activity.conversation

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.inflateLayout
import com.pawegio.kandroid.inputManager
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.AllFileStatus
import com.stratagile.pnrouter.entity.events.PullFileList
import com.stratagile.pnrouter.entity.events.ResetAvatar
import com.stratagile.pnrouter.entity.file.UpLoadFile
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerFileListComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileListContract
import com.stratagile.pnrouter.ui.activity.conversation.module.FileListModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileListPresenter
import com.stratagile.pnrouter.ui.activity.file.FileDetailInformationActivity
import com.stratagile.pnrouter.ui.activity.file.PdfViewActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendSendFileActivity
import com.stratagile.pnrouter.ui.adapter.conversation.FileListChooseAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.ease_search_bar.*
import kotlinx.android.synthetic.main.fragment_file_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2018/09/13 15:32:14
 */

class FileListFragment : BaseFragment(), FileListContract.View,PNRouterServiceMessageReceiver.FileMainManageBack {
    override fun fileRenameReq(jFileRenameRsp: JFileRenameRsp) {
        when(jFileRenameRsp.params.retCode)
        {
            0 ->{
                runOnUiThread {
                    toast(R.string.success)
                    fileListChooseAdapter!!.data.forEachIndexed { index, it ->
                        if(index == flagIndex)
                        {
                            fileListChooseAdapter!!.data[index].fileName = reNameTempName + reName
                            fileListChooseAdapter!!.notifyItemChanged(flagIndex)
                        }

                    }
                }
                //waitRenameData!!.fileName = reName


            }
            else ->{
                runOnUiThread {
                    toast(R.string.fail)
                }
            }
        }

    }

    var waitDeleteData: JPullFileListRsp.ParamsBean.PayloadBean? = null
    var flagIndex = 0;
    var reName = ""
    var reNameTempName = ""
    var reNamePos = -1
    var waitRenameData:JPullFileListRsp.ParamsBean.PayloadBean? = null
    var receiveFileDataMap = ConcurrentHashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var receiveToxFileDataMap = ConcurrentHashMap<String, JPullFileListRsp.ParamsBean.PayloadBean>()
    var currentPage = 0

    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    var fileData = receiveFileDataMap.get(msgId.toString())
                    if(fileData != null)
                    {
                        var fileMiName = fileData!!.fileName.substring(fileData!!.fileName.lastIndexOf("/") + 1, fileData!!.fileName.length)
                        LocalFileUtils.deleteLocalAssets(fileData!!.msgId.toString())
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

    override fun pullFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp) {
        if (jJToxPullFileRsp.params.retCode != 0) {
            runOnUiThread {
                toast(R.string.Download_failed)
            }

        } else {
            runOnUiThread {
                //toast(R.string.success)
            }

        }
    }

    override fun deleFileRsp(jDelFileRsp: JDelFileRsp) {

        when (jDelFileRsp.params.retCode) {
            0 -> {
                FileUtil.recordRecentFile(String(Base58.decode(waitDeleteData?.fileName!!.substring(waitDeleteData?.fileName!!.lastIndexOf("/") + 1))), 2, 1, "")
                runOnUiThread {
                    fileListChooseAdapter!!.data.removeAt(flagIndex)
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
        if (currentPage == 0) {
            runOnUiThread {
                when (SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1)) {
                    0 -> {
                        fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { String(Base58.decode(it.fileName.substring(it.fileName.lastIndexOf("/") + 1))) }?.toMutableList())
                    }
                    1 -> {
                        fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { it.timestamp }?.toMutableList())
                    }
                    2 -> {
                        fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedByDescending { it.fileSize }?.toMutableList())
                    }
                    3 -> {
                        fileListChooseAdapter?.setNewData(pullFileListRsp.params.payload?.sortedBy { String(RxEncodeTool.base64Decode(it.sender)) }?.toMutableList())
                    }
                }
            }
        } else {
            runOnUiThread {
                fileListChooseAdapter?.loadMoreComplete()
                if (pullFileListRsp.params.fileNum == 0) {
                    KLog.i("全部数据加载完成。。")
                    fileListChooseAdapter?.loadMoreEnd(true)
                } else {
                    KLog.i("还有数据需要加载。。")
                    fileListChooseAdapter!!.addData(pullFileListRsp.params!!.payload)
                    when (SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1)) {
                        0 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { String(Base58.decode(it.fileName.substring(it.fileName.lastIndexOf("/") + 1))) }?.toMutableList())
                        }
                        1 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.timestamp }?.toMutableList())
                        }
                        2 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.fileSize }?.toMutableList())
                        }
                        3 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedBy { String(RxEncodeTool.base64Decode(it.sender)) }?.toMutableList())
                        }
                    }
                }
            }
        }
    }
    @Inject
    lateinit internal var mPresenter: FileListPresenter

    var fileListChooseAdapter : FileListChooseAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file_list, null);
        return view
    }

    fun pullFileList(startId : Int) {
        currentPage = startId
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var pullFileListReq = PullFileListReq(selfUserId!!, startId, 5, 0, 0)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reSetAvatar(resetAvatar: ResetAvatar) {
        pullFileList(0)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        AppConfig.instance.messageReceiver?.fileMainManageBack = this
        pullFileList(0)
        fileListChooseAdapter = FileListChooseAdapter(arrayListOf())
        recyclerView.adapter = fileListChooseAdapter
        fileListChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = fileListChooseAdapter!!.getItem(position)
            startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }

        refreshLayout.setOnRefreshListener {
            pullFileList(0)
            refreshLayout.isRefreshing = false
        }

        fileListChooseAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.fileOpreate -> {
                    flagIndex = position
                    PopWindowUtil.showFileOpreatePopWindow(activity!!, recyclerView, fileListChooseAdapter!!.data[position], object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            var data = obj as JPullFileListRsp.ParamsBean.PayloadBean
                            when (position) {
                                0 -> {
                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                                    var fileOrginName = String(Base58.decode(fileMiName))
                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                    var intent =  Intent(activity!!, selectFriendSendFileActivity::class.java)
                                    intent.putExtra("fromId", selfUserId);
                                    intent.putExtra("msgId",data.msgId);
                                    intent.putExtra("fileName",fileOrginName)
                                    intent.putExtra("fileInfo",data.fileInfo)
                                    intent.putExtra("fileKey",data.userKey)
                                    startActivity(intent);
                                }
                                1 -> {
                                    FileUtil.recordRecentFile(String(Base58.decode(data.fileName!!.substring(data.fileName!!.lastIndexOf("/") + 1))), 1, 1, "")
                                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                                    var msgId = data.msgId
                                    var fileOrginName = String(Base58.decode(fileMiName))
                                    var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileOrginName
                                    var fileMiPath = PathUtils.getInstance().tempPath.toString() + "/" + fileOrginName
                                    var file = File(filePath)
                                    if(file.exists())
                                    {
                                        val fileMD5 = FileUtil.getFileMD5(file)
                                        if(fileMD5.equals(data.fileMD5))
                                        {
                                            runOnUiThread {
                                                toast(R.string.no_download_is_required)
                                            }
                                            return
                                        }

                                    }
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
                                        var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + data.fileName
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
                                            val uploadFile = UpLoadFile(fileMiName, filledUri,0, true, false, "0", 0, 1, 0, false,data.userKey,data.fileFrom,0,msgId.toString(),false)
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
                                    startActivity(Intent(activity!!, FileDetailInformationActivity::class.java).putExtra("file", data))
                                }
                                3 -> {
                                    waitRenameData = data
                                    reNameTempName = data.fileName.substring(0 , data.fileName.lastIndexOf("/") + 1)
                                    showRenameDialog(data, position)
                                }
                                4 -> {
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
                R.id.ivDownload ->
                {
                    var data = fileListChooseAdapter!!.data[position]
                    FileUtil.recordRecentFile(String(Base58.decode(data.fileName!!.substring(data.fileName!!.lastIndexOf("/") + 1))), 1, 1, "")
                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                    var msgId= data.msgId
                    var fileOrginName = String(Base58.decode(fileMiName))
                    var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileOrginName
                    var fileMiPath = PathUtils.getInstance().tempPath.toString() + "/" + fileOrginName
                    var file = File(filePath)
                    if(file.exists())
                    {
                        val fileMD5 = FileUtil.getFileMD5(file)
                        if(fileMD5.equals(data.fileMD5))
                        {
                            startActivity(Intent(AppConfig.instance, PdfViewActivity::class.java).putExtra("fileMiPath", data!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
                            return@setOnItemChildClickListener
                        }

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
                        var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + data.fileName
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
                            val uploadFile = UpLoadFile(fileMiName, filledUri,0, true, false, "0", 0, 1, 0, false,data.userKey,data.fileFrom,0,msgId.toString(),false)
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
                R.id.sendFriend ->
                {
                    var data = fileListChooseAdapter!!.data[position]
                    var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
                    KLog.i("转发的MiName= " + fileMiName)
                    var fileOrginName = String(Base58.decode(fileMiName))
                    KLog.i("转发的名字= "+ fileOrginName)
                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var intent =  Intent(activity!!, selectFriendSendFileActivity::class.java)
                    intent.putExtra("fromId", selfUserId);
                    intent.putExtra("msgId",data.msgId);
                    intent.putExtra("fileName",fileOrginName)
                    intent.putExtra("fileInfo",data.fileInfo)
                    intent.putExtra("fileKey",data.userKey)
                    startActivity(intent);
                }
            }
        }
        sort.setOnClickListener {
            PopWindowUtil.showFileSortWindow(activity!!, sort, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    SpUtil.putInt(AppConfig.instance, ConstantValue.currentArrangeType, position)
                    when (position) {
                        0 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { String(Base58.decode(it.fileName.substring(it.fileName.lastIndexOf("/") + 1))) }.toMutableList())
                        }
                        1 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.timestamp }.toMutableList())
                        }
                        2 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedByDescending { it.fileSize }.toMutableList())
                        }
                        3 -> {
                            fileListChooseAdapter?.setNewData(fileListChooseAdapter!!.data.sortedBy { String(RxEncodeTool.base64Decode(it.sender)) }.toMutableList())
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
        var view = activity!!.inflateLayout(R.layout.layout_filelist_empty, null, false)
        fileListChooseAdapter?.emptyView = view
        fileListChooseAdapter?.setEnableLoadMore(true)
        fileListChooseAdapter?.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener{
            override fun onLoadMoreRequested() {
                recyclerView.postDelayed({
                    currentPage = fileListChooseAdapter!!.data[fileListChooseAdapter!!.data.size - 1].msgId
                    pullFileList(fileListChooseAdapter!!.data[fileListChooseAdapter!!.data.size - 1].msgId)
                }, 500)
            }

        })
    }

    var beforeList = mutableListOf<JPullFileListRsp.ParamsBean.PayloadBean>()

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

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshList(pullFileList: PullFileList) {
        pullFileList(0)
    }

    fun showRenameDialog(data : JPullFileListRsp.ParamsBean.PayloadBean, postion : Int) {
        val view = View.inflate(activity, R.layout.rename_dialog_layout, null)
        val etContent = view.findViewById<EditText>(R.id.etContent)
        val btnLeft = view.findViewById<Button>(R.id.btn_left)
        val btnRight = view.findViewById<Button>(R.id.btn_right)
        var formatDialog = CommonDialog(activity)
        formatDialog?.setView(view)
        formatDialog?.show()
        var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
        var nameAndType = String(Base58.decode(fileMiName))
        var name = nameAndType.substring(0,nameAndType.lastIndexOf("."))
        var type =  nameAndType.substring(nameAndType.lastIndexOf("."),nameAndType.length)
        etContent.setText(name)
        etContent.setSelection(etContent.text.length)
        etContent.requestFocus()
        etContent.postDelayed({
            var imm = AppConfig.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etContent, InputMethodManager.SHOW_FORCED);
        }, 100)
        btnLeft.setOnClickListener {
            var imm = AppConfig.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            formatDialog.dismissWithAnimation()
        }
        btnRight.setOnClickListener {
            var imm = AppConfig.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            formatDialog.dismissWithAnimation()
            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var fileMiName = data.fileName.substring(data.fileName.lastIndexOf("/") + 1, data.fileName.length)
            var rename = Base58.encode((etContent.text.toString()+type).toByteArray())
            var fileRenameReq = FileRenameReq(selfUserId!!, data.msgId, fileMiName, rename )
            if(fileMiName.equals(rename))
            {
                toast(R.string.Name_not_changed)
                return@setOnClickListener
            }
            reNamePos = postion
            reName = rename
            var sendData = BaseData(4, fileRenameReq)
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
            KLog.i(etContent.text.toString())
        }
    }


    override fun setupFragmentComponent() {
        DaggerFileListComponent
                .builder()
                .appComponent((AppConfig.instance).applicationComponent)
                .fileListModule(FileListModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileListContract.FileListContractPresenter) {
        mPresenter = presenter as FileListPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    fun  showProgressDialog(text: String) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setOnTouchOutside(false)
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}