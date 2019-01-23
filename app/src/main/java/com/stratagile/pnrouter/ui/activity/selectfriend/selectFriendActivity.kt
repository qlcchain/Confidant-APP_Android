package com.stratagile.pnrouter.ui.activity.selectfriend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.LinearLayout
import butterknife.ButterKnife
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.hyphenate.chat.*
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerselectFriendComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxSendFileFinishedEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_select_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import javax.inject.Inject


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: $description
 * @date 2018/09/25 14:58:33
 */

class selectFriendActivity : BaseActivity(), selectFriendContract.View {

    @Inject
    internal lateinit var mPresenter: selectFriendPresenter
    var fragment: ContactFragment? = null
    var  userEntity: UserEntity? = null
    var fromId:String? = null
    var message: EMMessage? = null

    val sendFileSizeMax = 1024 * 1024 * 2
    var sendMsgMap = HashMap<String, EMMessage>()
    var sendMsgLocalMap = HashMap<String, Boolean>()
    var sendFilePathMap = HashMap<String, String>()
    val sendToxFileDataMap = HashMap<String, ToxFileData>()
    val receiveToxFileNameMap = HashMap<String, String>()
    val sendFileFriendKeyMap = HashMap<String, String>()
    val sendFileAESKeyByteMap = HashMap<String, String>()
    val sendFileFriendKeyByteMap = HashMap<String, ByteArray>()
    val sendFileMyKeyByteMap = HashMap<String, ByteArray>()
    var sendFileResultMap = HashMap<String, Boolean>()
    val sendFileNameMap = HashMap<String, String>()
    val sendFileLastByteSizeMap = HashMap<String, Int>()
    val sendFileLeftByteMap = HashMap<String, ByteArray>()
    val sendMsgIdMap = HashMap<String, String>()
    val receiveFileDataMap = HashMap<String, Message>()
    val receiveToxFileDataMap = HashMap<String, Message>()
    val receiveToxFileIdMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        //        setContentView(R.layout.activity_selectFriend);
        ButterKnife.bind(this)
        setToorBar(false)
        fromId = intent.getStringExtra("fromId")
        message = intent.getParcelableExtra("message")
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_select_friend)
        tvTitle.text = getString(R.string.Contacts)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }

    override fun initData() {
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
        fragment = ContactFragment();
        val bundle = Bundle()
        bundle.putString(ConstantValue.selectFriend, "select")
        bundle.putString("fromId", fromId)
        bundle.putParcelable("message",message)
        fragment!!.setArguments(bundle)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragment!!
            }

            override fun getCount(): Int {
                return 1
            }
        }
        viewPager.offscreenPageLimit = 1
        llCancel.setOnClickListener {

            finish()
        }
        send.setOnClickListener {
            showSendDialog()
        }
        multiSelectBtn.setOnClickListener {

            fragment!!.selectOrCancelAll()
        }
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                progressDialog.hide()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    /**
     * 显示转发的弹窗
     */
    fun showSendDialog() {
        var contactSelectedList: ArrayList<UserEntity> = fragment!!.getAllSelectedFriend()
        if (contactSelectedList.size == 0) {
            toast(R.string.noSelected)
        } else {
            for (i in contactSelectedList) {
                when (message!!.type) {
                    EMMessage.Type.TXT -> {
                        try {
                            var eMTextMessageBody: EMTextMessageBody = message!!.body as EMTextMessageBody
                            var msg:String = eMTextMessageBody!!.message
                            val userId = SpUtil.getString(this, ConstantValue.userId, "")
                            var aesKey = RxEncryptTool.generateAESKey()
                            var my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                            var friend = RxEncodeTool.base64Decode(i.signPublicKey)
                            var SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                            var DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                            var miMsg = AESCipher.aesEncryptString(msg, aesKey)
                            var msgData = SendMsgReq(userId!!, i.userId!!, miMsg, String(SrcKey), String(DstKey))
                            if (ConstantValue.isWebsocketConnected) {
                                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
                            } else if (ConstantValue.isToxConnected) {
                                var baseData = BaseData(msgData)
                                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                if (ConstantValue.isAntox) {
                                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                }else{
                                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                }
                            }
                        } catch (e: Exception) {
                            toast(R.string.Encryptionerror)
                        }
                    }
                    EMMessage.Type.IMAGE -> {
                        try {
                            var eMImageMessageBody: EMImageMessageBody = message!!.body as EMImageMessageBody
                            var imagePath = eMImageMessageBody.localUrl
                            Thread(Runnable {
                                try {
                                    val file = File(imagePath)
                                    val isHas = file.exists()
                                    if (isHas) {
                                        val fileName = (System.currentTimeMillis() / 1000).toInt().toString() + "_" + imagePath.substring(imagePath.lastIndexOf("/") + 1)
                                        val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                                        val codeSave = FileUtil.copySdcardPicAndCompress(imagePath, files_dir, false)
                                        val message = EMMessage.createImageSendMessage(files_dir, true, i.userId)
                                        val userId = SpUtil.getString(this, ConstantValue.userId, "")
                                        message.from = userId
                                        message.to = i.userId
                                        message.isDelivered = true
                                        message.isAcked = false
                                        message.isUnread = true
                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            sendMsgMap.put(uuid, message)
                                            sendMsgLocalMap.put(uuid, false)
                                            sendFilePathMap.put(uuid, files_dir)
                                            sendFileFriendKeyMap.put(uuid, i.signPublicKey)

                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                            val friend = RxEncodeTool.base64Decode(i.signPublicKey)
                                            var SrcKey = ByteArray(256)
                                            var DstKey = ByteArray(256)
                                            try {
                                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                sendFileAESKeyByteMap.put(uuid, aesKey)
                                                sendFileMyKeyByteMap.put(uuid, SrcKey)
                                                sendFileFriendKeyByteMap.put(uuid, DstKey)
                                            } catch (e: Exception) {

                                                return@Runnable
                                            }

                                            if (codeSave == 1) {
                                                val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                                                EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))
                                            } else {

                                                return@Runnable
                                            }

                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxPicAndEncrypt(imagePath, base58files_dir, aesKey, false)
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                sendMsgMap.put(uuid.toString() + "", message)
                                                sendMsgLocalMap.put(uuid.toString() + "", false)
                                                sendFilePathMap.put(uuid.toString() + "", base58files_dir)
                                                sendFileFriendKeyMap.put(uuid.toString() + "", i.signPublicKey)
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.userId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE
                                                toxFileData.fileId = uuid
                                                val FriendPublicKey = i.signPublicKey
                                                val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                                val friend = RxEncodeTool.base64Decode(FriendPublicKey)
                                                var SrcKey = ByteArray(256)
                                                var DstKey = ByteArray(256)
                                                try {
                                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                } catch (e: Exception) {

                                                }

                                                toxFileData.srcKey = String(SrcKey)
                                                toxFileData.dstKey = String(DstKey)

                                                var fileNumber = ""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }
                                                sendToxFileDataMap.put(fileNumber, toxFileData)
                                            } else {

                                                return@Runnable
                                            }

                                        }
                                    } else {

                                    }

                                } catch (e: Exception) {

                                }
                            }).start()
                        } catch (e: Exception) {
                            toast(R.string.Encryptionerror)
                        }
                    }
                    EMMessage.Type.VIDEO -> {
                        try {
                            var eMVideoMessageBody: EMVideoMessageBody = message!!.body as EMVideoMessageBody
                            var videoPath = eMVideoMessageBody.localUrl
                            Thread(Runnable {
                                try {
                                    val file = File(videoPath)
                                    val isHas = file.exists()
                                    if (isHas) {
                                        val videoFileName = videoPath.substring(videoPath.lastIndexOf("/") + 1)
                                        val videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1, videoPath.lastIndexOf(".") + 1)
                                        val thumbPath = PathUtils.getInstance().imagePath.toString() + "/" + videoName + ".png"
                                        val bitmap = EaseImageUtils.getVideoPhoto(videoPath)
                                        val videoLength = EaseImageUtils.getVideoDuration(videoPath)
                                        FileUtil.saveBitmpToFile(bitmap, thumbPath)
                                        val message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, i.userId)
                                        val userId = SpUtil.getString(this, ConstantValue.userId, "")
                                        message.from = userId
                                        message.to = i.userId
                                        message.isDelivered = true
                                        message.isAcked = false
                                        message.isUnread = true

                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            message.msgId = uuid
                                            sendMsgMap[uuid] = message
                                            sendMsgLocalMap[uuid] = false
                                            sendFilePathMap[uuid] = videoPath
                                            sendFileFriendKeyMap[uuid] = i.signPublicKey

                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                            val friend = RxEncodeTool.base64Decode(i.signPublicKey)
                                            var SrcKey = ByteArray(256)
                                            var DstKey = ByteArray(256)
                                            try {
                                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                sendFileAESKeyByteMap[uuid] = aesKey
                                                sendFileMyKeyByteMap[uuid] = SrcKey
                                                sendFileFriendKeyByteMap[uuid] = DstKey
                                            } catch (e: Exception) {
                                                return@Runnable
                                            }

                                            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))

                                        } else {
                                            val strBase58 = Base58.encode(videoFileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(videoPath, base58files_dir, aesKey)
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                       sendMsgMap[uuid.toString() + ""] = message
                                                sendMsgLocalMap[uuid.toString() + ""] = false
                                                sendFilePathMap[uuid.toString() + ""] = base58files_dir
                                                sendFileFriendKeyMap[uuid.toString() + ""] = i.signPublicKey
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.userId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_MEDIA
                                                toxFileData.fileId = uuid
                                                val FriendPublicKey = i.signPublicKey
                                                val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                                val friend = RxEncodeTool.base64Decode(FriendPublicKey)
                                                var SrcKey = ByteArray(256)
                                                var DstKey = ByteArray(256)
                                                try {
                                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                } catch (e: Exception) {

                                                }

                                                toxFileData.srcKey = String(SrcKey)
                                                toxFileData.dstKey = String(DstKey)
                                                var fileNumber = ""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }

                                                sendToxFileDataMap[fileNumber] = toxFileData
                                            }
                                        }
                                    } else {

                                    }
                                } catch (e: Exception) {

                                }
                            }).start()
                        } catch (e: Exception) {
                            toast(R.string.Encryptionerror)
                        }
                    }
                    EMMessage.Type.FILE -> {
                        try {
                            var eMNormalFileMessageBody: EMNormalFileMessageBody = message!!.body as EMNormalFileMessageBody
                            var filePath = eMNormalFileMessageBody.localUrl
                            Thread(Runnable {
                                try {
                                    val file = File(filePath)
                                    val isHas = file.exists()
                                    if (isHas) {
                                        val fileName = (System.currentTimeMillis() / 1000).toInt().toString() + "_" + filePath.substring(filePath.lastIndexOf("/") + 1)

                                        val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                                        val message = EMMessage.createFileSendMessage(filePath, i.userId)
                                        val userId = SpUtil.getString(this, ConstantValue.userId, "")
                                        message.from = userId
                                        message.to = i.userId
                                        message.isDelivered = true
                                        message.isAcked = false
                                        message.isUnread = true


                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            message.msgId = uuid
                                            sendMsgMap[uuid] = message
                                            sendMsgLocalMap[uuid] = false
                                            sendFilePathMap[uuid] = files_dir
                                            sendFileFriendKeyMap[uuid] = i.signPublicKey

                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                            val friend = RxEncodeTool.base64Decode(i.signPublicKey)
                                            var SrcKey = ByteArray(256)
                                            var DstKey = ByteArray(256)
                                            try {
                                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                sendFileAESKeyByteMap[uuid] = aesKey
                                                sendFileMyKeyByteMap[uuid] = SrcKey
                                                sendFileFriendKeyByteMap[uuid] = DstKey
                                            } catch (e: Exception) {

                                                return@Runnable
                                            }

                                            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))
                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, aesKey)
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                sendMsgMap[uuid.toString() + ""] = message
                                                sendMsgLocalMap[uuid.toString() + ""] = false
                                                sendFilePathMap[uuid.toString() + ""] = base58files_dir
                                                sendFileFriendKeyMap[uuid.toString() + ""] = i.signPublicKey
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.userId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_FILE
                                                toxFileData.fileId = uuid
                                                val FriendPublicKey = i.signPublicKey
                                                val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                                val friend = RxEncodeTool.base64Decode(FriendPublicKey)
                                                var SrcKey = ByteArray(256)
                                                var DstKey = ByteArray(256)
                                                try {
                                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                } catch (e: Exception) {

                                                }

                                                toxFileData.srcKey = String(SrcKey)
                                                toxFileData.dstKey = String(DstKey)
                                                var fileNumber =""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }

                                                sendToxFileDataMap[fileNumber] = toxFileData
                                            }

                                        }
                                        FileUtil.copySdcardFile(filePath, files_dir)
                                    } else {

                                    }

                                } catch (e: Exception) {

                                }
                            }).start()
                        } catch (e: Exception) {
                            toast(R.string.Encryptionerror)
                        }
                    }
                }
            }
            toast(R.string.hasbeensent)
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectWebSocket(fileTransformEntity: FileTransformEntity) {

        val EMMessage = sendMsgMap[fileTransformEntity.toId] ?: return
        if (fileTransformEntity.message == 0) {
            return
        }
        when (fileTransformEntity.message) {
            1 -> Thread(Runnable {
                try {
                    val EMMessage = sendMsgMap.get(fileTransformEntity.toId)
                    val filePath = sendFilePathMap.get(fileTransformEntity.toId)
                    val fileName = filePath!!.substring(filePath!!.lastIndexOf("/") + 1)
                    val aesKey = sendFileAESKeyByteMap.get(fileTransformEntity.toId)
                    val SrcKey = sendFileMyKeyByteMap.get(fileTransformEntity.toId)
                    val DstKey = sendFileFriendKeyByteMap.get(fileTransformEntity.toId)
                    val file = File(filePath)
                    if (file.exists()) {
                        val fileSize = file.length()
                        val fileMD5 = FileUtil.getFileMD5(file)
                        val fileBuffer = FileUtil.file2Byte(filePath)
                        val fileId = (System.currentTimeMillis() / 1000).toInt()
                        //String aesKey =  RxEncryptTool.generateAESKey();
                        var fileBufferMi = ByteArray(0)
                        try {
                            val miBegin = System.currentTimeMillis()
                            fileBufferMi = AESCipher.aesEncryptBytes(fileBuffer, aesKey!!.toByteArray(charset("UTF-8")))
                            val miend = System.currentTimeMillis()
                            KLog.i("jiamiTime:" + (miend - miBegin) / 1000)
                            sendFileByteData(fileBufferMi, fileName, EMMessage!!.getFrom(), EMMessage!!.getTo(), fileTransformEntity.toId, fileId, 1, aesKey!!, SrcKey!!, DstKey!!)
                        } catch (e: Exception) {
                            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                            EventBus.getDefault().post(FileTransformEntity(fileTransformEntity.toId, 4, "", wssUrl, "lws-pnr-bin"))
                        }

                    }
                } catch (e: Exception) {

                }
            }).start()
            2 -> {
            }
            3 -> {
                val gson = Gson()
                val filePathOk = sendFilePathMap.get(fileTransformEntity.toId)
                val fileOk = File(filePathOk)
                val retMsg = fileTransformEntity.retMsg
                val aa = retMsg.toByteArray()
                val aabb = ""
            }
            else -> {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectWebSocket(transformReceiverFileMessage: TransformReceiverFileMessage) {
        val EMMessageData = sendMsgMap[transformReceiverFileMessage.toId] ?: return
        val retMsg = transformReceiverFileMessage.message
        val Action = ByteArray(4)
        val FileId = ByteArray(4)
        val LogId = ByteArray(4)
        val SegSeq = ByteArray(4)
        val CRC = ByteArray(2)
        val Code = ByteArray(2)
        val FromId = ByteArray(76)
        val ToId = ByteArray(76)
        System.arraycopy(retMsg, 0, Action, 0, 4)
        System.arraycopy(retMsg, 4, FileId, 0, 4)
        System.arraycopy(retMsg, 8, LogId, 0, 4)
        System.arraycopy(retMsg, 12, SegSeq, 0, 4)
        System.arraycopy(retMsg, 16, CRC, 0, 2)
        System.arraycopy(retMsg, 18, Code, 0, 2)
        System.arraycopy(retMsg, 20, FromId, 0, 76)
        System.arraycopy(retMsg, 97, ToId, 0, 76)
        val ActionResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(Action))
        val FileIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(FileId))
        val LogIdIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(LogId))
        val SegSeqResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(SegSeq))
        val CRCResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(CRC))
        val CodeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(Code))
        val FromIdResult = String(FromId)
        val ToIdResult = String(ToId)
        val aa = ""
        KLog.i("CodeResult:$CodeResult")
        var CodeResultWhen:String = CodeResult.toString()
        when (CodeResultWhen) {
            "0" -> {
                val lastSendSize = sendFileLastByteSizeMap.get(FileIdResult.toString() + "")
                val fileBuffer = sendFileLeftByteMap.get(FileIdResult.toString() + "")
                val leftSize = fileBuffer!!.size - lastSendSize!!
                val msgId = sendMsgIdMap.get(FileIdResult.toString() + "")
                if (leftSize > 0) {
                    Thread(Runnable {
                        try {
                            val fileLeftBuffer = ByteArray(leftSize)
                            System.arraycopy(fileBuffer, sendFileSizeMax, fileLeftBuffer, 0, leftSize)
                            val fileName = sendFileNameMap.get(FileIdResult.toString() + "")
                            val aesKey = sendFileAESKeyByteMap.get(FileIdResult.toString() + "")
                            val SrcKey = sendFileMyKeyByteMap.get(FileIdResult.toString() + "")
                            val DstKey = sendFileFriendKeyByteMap.get(FileIdResult.toString() + "")
                            sendFileByteData(fileLeftBuffer, fileName!!, FromIdResult + "", ToIdResult + "", msgId!!, FileIdResult, SegSeqResult + 1, aesKey!!, SrcKey!!, DstKey!!)
                        } catch (e: Exception) {

                        }
                    }).start()

                } else {
                    val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                    EventBus.getDefault().post(FileTransformEntity(msgId!!, 4, "", wssUrl, "lws-pnr-bin"))
                    KLog.i("文件发送成功！")

                }
            }
            "1" -> {
            }
            "2" -> {
            }
            "3" -> {
            }
        }
    }

    private fun sendFileByteData(fileLeftBuffer: ByteArray, fileName: String, From: String, To: String, msgId: String, fileId: Int, segSeq: Int, aesKey: String, SrcKey: ByteArray, DstKey: ByteArray) {
        val FriendPublicKey = sendFileFriendKeyMap.get(msgId)
        try {
            KLog.i("发送中>>>刚调用From:$From  To:$To")
            val MsgType = fileName.substring(fileName.lastIndexOf(".") + 1)
            var action = 1
            when (MsgType) {
                "png", "jpg" -> action = 1
                "amr" -> action = 2
                "mp4" -> action = 4
                else -> action = 5
            }
            val sendFileData = SendFileData()
            val segSize = if (fileLeftBuffer.size > sendFileSizeMax) sendFileSizeMax else fileLeftBuffer.size
            sendFileData.magic = FormatTransfer.reverseInt(0x0dadc0de)
            sendFileData.action = FormatTransfer.reverseInt(action)
            sendFileData.segSize = FormatTransfer.reverseInt(segSize)
            val aa = FormatTransfer.reverseInt(9437440)
            sendFileData.segSeq = FormatTransfer.reverseInt(segSeq)
            var fileOffset = 0
            fileOffset = (segSeq - 1) * sendFileSizeMax
            sendFileData.fileOffset = FormatTransfer.reverseInt(fileOffset)
            sendFileData.fileId = FormatTransfer.reverseInt(fileId)
            sendFileData.crc = FormatTransfer.reverseShort(0.toShort())
            val segMore = if (fileLeftBuffer.size > sendFileSizeMax) 1 else 0
            sendFileData.segMore = segMore.toByte()
            sendFileData.cotinue = 0.toByte()
            //String strBase64 = RxEncodeTool.base64Encode2String(fileName.getBytes());
            val strBase58 = Base58.encode(fileName.toByteArray())
            sendFileData.fileName = strBase58.toByteArray()
            sendFileData.fromId = From.toByteArray()
            sendFileData.toId = To.toByteArray()
            sendFileData.srcKey = SrcKey
            sendFileData.dstKey = DstKey
            val content = ByteArray(sendFileSizeMax)
            System.arraycopy(fileLeftBuffer, 0, content, 0, segSize)
            sendFileData.content = content
            var sendData = sendFileData.toByteArray()
            val newCRC = CRC16Util.getCRC(sendData, sendData.size)
            sendFileData.crc = FormatTransfer.reverseShort(newCRC.toShort())
            sendData = sendFileData.toByteArray()
            sendFileNameMap.put(fileId.toString() + "", fileName)
            sendFileLastByteSizeMap.put(fileId.toString() + "", segSize)
            sendFileLeftByteMap.put(fileId.toString() + "", fileLeftBuffer)
            sendMsgIdMap.put(fileId.toString() + "", msgId)
            sendFileAESKeyByteMap.put(fileId.toString() + "", aesKey)
            sendFileMyKeyByteMap.put(fileId.toString() + "", SrcKey)
            sendFileFriendKeyByteMap.put(fileId.toString() + "", DstKey)
            EventBus.getDefault().post(TransformFileMessage(msgId, sendData))
            val s = String(content)
            val aabb = FileUtil.bytesToHex(content)
            //KLog.i("发送中>>>内容"+"content:"+aabb);
            KLog.i("发送中>>>" + "segMore:" + segMore + "  " + "segSize:" + segSize + "   " + "left:" + (fileLeftBuffer.size - segSize) + "  segSeq:" + segSeq + "  fileOffset:" + fileOffset + "  setSegSize:" + sendFileData.segSize + " CRC:" + newCRC)

        } catch (e: Exception) {
            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
            EventBus.getDefault().post(FileTransformEntity(msgId, 4, "", wssUrl, "lws-pnr-bin"))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber=  toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        onToxFileSendFinished(fileNumber,key)
    }
    fun onToxFileSendFinished(fileNumber: Int, key: String) {
        val toxFileData = sendToxFileDataMap[fileNumber.toString() + ""]
        if (toxFileData != null) {
            val sendToxFileNotice = SendToxFileNotice(toxFileData.fromId, toxFileData.toId, toxFileData.fileName, toxFileData.fileMD5, toxFileData.fileSize, toxFileData.fileType.value(), toxFileData.fileId, toxFileData.srcKey, toxFileData.dstKey, "SendFile")
            val baseData = BaseData(sendToxFileNotice)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        //EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectFriendChange(selectFriendChange: SelectFriendChange) {
        selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.people)
    }
    override fun setupActivityComponent() {
        DaggerselectFriendComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectFriendModule(selectFriendModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: selectFriendContract.selectFriendContractPresenter) {
        mPresenter = presenter as selectFriendPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }
}