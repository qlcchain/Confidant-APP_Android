package com.stratagile.pnrouter.ui.activity.selectfriend

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import butterknife.ButterKnife
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.google.gson.Gson
import com.hyphenate.chat.*
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.ui.activity.main.ContactAndGroupFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerselectFriendComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.ToxCoreJni
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
    var fragment: ContactAndGroupFragment? = null
    var  userEntity: UserEntity? = null
    var fromId:String? = null
    var message: EMMessage? = null
    var groupCount = 0
    var personCount = 0

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
//        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
//        statusBar.setLayoutParams(llp)
    }

    override fun initData() {
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
        fragment = ContactAndGroupFragment();
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
            onBackPressed()
        }
        send.setOnClickListener {
            showSendDialog()
        }
        multiSelectBtn.setOnClickListener {

            fragment!!.selectOrCancelAll()
        }
        fragment!!.setRefreshEnable(false)
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
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    //showProgressDialog(getString(R.string.network_reconnecting))
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

        var groupSelectedList  = fragment!!.getAllSelectedGroup()
        if (contactSelectedList.size == 0 && groupSelectedList.size == 0) {
            toast(R.string.noSelected)
            return
        }
        if (contactSelectedList.size != 0) {//给好友列表
            for (i in contactSelectedList) {
                when (message!!.type) {
                    EMMessage.Type.TXT -> {
                        try {
                            var eMTextMessageBody: EMTextMessageBody = message!!.body as EMTextMessageBody
                            var msg:String = eMTextMessageBody!!.message
                            val userId = SpUtil.getString(this, ConstantValue.userId, "")
                            if(ConstantValue.encryptionType.equals("1"))
                            {
                                var friendMiPublic = RxEncodeTool.base64Decode(i.miPublicKey)
                                LogUtil.addLog("sendMsgV3 friendKey:",friendMiPublic.toString())
                                var msgMap = LibsodiumUtil.EncryptSendMsg(msg,friendMiPublic)
                                var msgData = SendMsgReqV3(userId!!, i.userId!!, msgMap.get("encryptedBase64")!!,msgMap.get("signBase64")!!,msgMap.get("NonceBase64")!!,msgMap.get("dst_shared_key_Mi_My64")!!)

                                if (ConstantValue.isWebsocketConnected) {
                                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(3,msgData))
                                }else if (ConstantValue.isToxConnected) {
                                    var baseData = BaseData(3,msgData)
                                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                    if (ConstantValue.isAntox) {
                                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                    }else{
                                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                    }
                                }
                            }else{
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
                            }
                            val gson = Gson()
                            val Message = Message()
                            Message.msg = msg
                            Message.from = userId
                            Message.to = i.userId
                            Message.status = 0
                            Message.timeStamp = System.currentTimeMillis() / 1000
                            Message.unReadCount = 0
                            Message.chatType = EMMessage.ChatType.Chat
                            val baseDataJson = gson.toJson(Message)
                            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.userId, baseDataJson)
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
                                        val bitmap = BitmapFactory.decodeFile(imagePath)
                                        val widthAndHeight = "," + bitmap.width + ".0000000" + "*" + bitmap.height + ".0000000"

                                        message.from = userId
                                        message.to = i.userId
                                        message.isDelivered = true
                                        message.setAttribute("wh", widthAndHeight.replace(",", ""))
                                        message.isAcked = false
                                        message.isUnread = true
                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            if (codeSave == 1) {
                                                val SendFileInfo = SendFileInfo()
                                                SendFileInfo.userId = userId
                                                SendFileInfo.friendId = i.userId
                                                SendFileInfo.files_dir = files_dir
                                                SendFileInfo.msgId = uuid
                                                SendFileInfo.setWidthAndHeight(widthAndHeight)
                                                SendFileInfo.friendSignPublicKey = i.signPublicKey
                                                SendFileInfo.friendMiPublicKey = i.miPublicKey
                                                SendFileInfo.voiceTimeLen = 0
                                                SendFileInfo.type = "1"
                                                SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                                SendFileInfo.porperty = "0"
                                                AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                                /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                                EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/
                                            } else {

                                                return@Runnable
                                            }

                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxPicAndEncrypt(imagePath, base58files_dir, aesKey.substring(0,16), false)
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
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
                                                toxFileData.widthAndHeight = widthAndHeight.substring(1, widthAndHeight.length)
                                                toxFileData.porperty = "0"
                                                val FriendPublicKey = i.signPublicKey
                                                val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                                                val friend = RxEncodeTool.base64Decode(FriendPublicKey)
                                                var SrcKey = ByteArray(256)
                                                var DstKey = ByteArray(256)
                                                try {
                                                    if (ConstantValue.encryptionType == "1") {
                                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, ConstantValue.libsodiumpublicMiKey!!))
                                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, i.miPublicKey))
                                                    }else{
                                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                    }
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
                                                ConstantValue.sendToxFileDataMap.put(fileNumber, toxFileData)
                                            } else {

                                                return@Runnable
                                            }

                                        }
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 1
                                        Message.fileName = fileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.userId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.Chat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.userId, baseDataJson)
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
                                            val SendFileInfo = SendFileInfo()
                                            SendFileInfo.userId = userId
                                            SendFileInfo.friendId = i.userId
                                            SendFileInfo.files_dir = videoPath
                                            SendFileInfo.msgId = uuid
                                            SendFileInfo.friendSignPublicKey = i.signPublicKey
                                            SendFileInfo.friendMiPublicKey = i.miPublicKey
                                            SendFileInfo.voiceTimeLen = 0
                                            SendFileInfo.type = "3"
                                            SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                            SendFileInfo.porperty = "1"
                                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                            /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/

                                        } else {
                                            val strBase58 = Base58.encode(videoFileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(videoPath, base58files_dir, aesKey.substring(0,16))
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                /*sendMsgMap[uuid.toString() + ""] = message
                                                sendMsgLocalMap[uuid.toString() + ""] = false
                                                sendFilePathMap[uuid.toString() + ""] = base58files_dir
                                                sendFileFriendKeyMap[uuid.toString() + ""] = i.signPublicKey*/
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

                                                    if (ConstantValue.encryptionType == "1") {
                                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, ConstantValue.libsodiumpublicMiKey!!))
                                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, i.miPublicKey))
                                                    }else{
                                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                    }
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

                                                ConstantValue.sendToxFileDataMap[fileNumber] = toxFileData
                                            }
                                        }
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 4
                                        Message.fileName = videoFileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.userId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.Chat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.userId, baseDataJson)
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

                                            val SendFileInfo = SendFileInfo()
                                            SendFileInfo.userId = userId
                                            SendFileInfo.friendId = i.userId
                                            SendFileInfo.files_dir = files_dir
                                            SendFileInfo.msgId = uuid
                                            SendFileInfo.friendSignPublicKey = i.signPublicKey
                                            SendFileInfo.friendMiPublicKey = i.miPublicKey
                                            SendFileInfo.voiceTimeLen = 0
                                            SendFileInfo.type = "4"
                                            SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                            SendFileInfo.porperty = "1"
                                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                            /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/
                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = RxEncryptTool.generateAESKey()
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, aesKey.substring(0,16))
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
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

                                                    if (ConstantValue.encryptionType == "1") {
                                                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, ConstantValue.libsodiumpublicMiKey!!))
                                                        DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(aesKey, i.miPublicKey))
                                                    }else{
                                                        SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), my))
                                                        DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(), friend))
                                                    }
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

                                                ConstantValue.sendToxFileDataMap[fileNumber] = toxFileData
                                            }

                                        }
                                        FileUtil.copySdcardFile(filePath, files_dir)
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 5
                                        Message.fileName = fileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.userId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.Chat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.userId, baseDataJson)
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
            //toast(R.string.hasbeensent)

        }
        if (groupSelectedList.size != 0) {//发给群列表
            for (i in groupSelectedList) {
                when (message!!.type) {
                    EMMessage.Type.TXT -> {
                        try {
                            var eMTextMessageBody: EMTextMessageBody = message!!.body as EMTextMessageBody
                            var msg:String = eMTextMessageBody!!.message
                            val userId = SpUtil.getString(this, ConstantValue.userId, "")
                            var aesKey = LibsodiumUtil.DecryptShareKey(i.userKey)
                            var fileBufferMi = AESCipher.aesEncryptBytes(msg.toByteArray(), aesKey!!.toByteArray(charset("UTF-8")))
                            var msgMi = RxEncodeTool.base64Encode2String(fileBufferMi);
                            var groupSendMsgReq = GroupSendMsgReq(userId!!, i.gId!!, "",msgMi)
                            var baseData = BaseData(4,groupSendMsgReq)
                            var msgId = baseData.msgid!!
                            if (ConstantValue.curreantNetworkType.equals("WIFI")) {
                                AppConfig.instance.getPNRouterServiceMessageSender().sendGroupChatMsg(baseData)
                            }else if (ConstantValue.isToxConnected) {
                                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            }
                            val gson = Gson()
                            val Message = Message()
                            Message.msg = msg
                            Message.from = userId
                            Message.to = i.gId
                            Message.status = 0
                            Message.timeStamp = System.currentTimeMillis() / 1000
                            Message.unReadCount = 0
                            Message.chatType = EMMessage.ChatType.GroupChat
                            val baseDataJson = gson.toJson(Message)
                            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.gId, baseDataJson)
                        } catch (e: Exception) {
                            toast(R.string.Encryptionerror)
                        }
                    }
                    EMMessage.Type.IMAGE -> {
                        try {
                            var eMImageMessageBody: EMImageMessageBody = message!!.body as EMImageMessageBody
                            var imagePath = eMImageMessageBody.localUrl
                            val userId = SpUtil.getString(this, ConstantValue.userId, "")
                            Thread(Runnable {
                                try {
                                    val file = File(imagePath)
                                    val isHas = file.exists()
                                    if (isHas) {
                                        var imgeSouceName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                                        if (imgeSouceName.contains("_")) {
                                            imgeSouceName = imgeSouceName.substring(imgeSouceName.indexOf("_") + 1, imgeSouceName.length)
                                        }
                                        val fileName = (System.currentTimeMillis() / 1000).toInt().toString() + "_" + imgeSouceName
                                        val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                                        val codeSave = FileUtil.copySdcardPicAndCompress(imagePath, files_dir, false)
                                        val message = EMMessage.createImageSendMessage(files_dir, true, i.gId)

                                        val bitmap = BitmapFactory.decodeFile(imagePath)
                                        val widthAndHeight = "," + bitmap.width + ".0000000" + "*" + bitmap.height + ".0000000"
                                        message.from = userId
                                        message.to = i.gId
                                        message.isDelivered = true
                                        message.setAttribute("wh", widthAndHeight.replace(",", ""))
                                        message.isAcked = false
                                        message.isUnread = true
                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            if (codeSave == 1) {
                                                val SendFileInfo = SendFileInfo()
                                                SendFileInfo.userId = userId
                                                SendFileInfo.friendId = i.gId
                                                SendFileInfo.files_dir = files_dir
                                                SendFileInfo.msgId = uuid
                                                SendFileInfo.widthAndHeight = widthAndHeight
                                                SendFileInfo.friendSignPublicKey = i.userKey
                                                SendFileInfo.friendMiPublicKey = i.userKey
                                                SendFileInfo.voiceTimeLen = 0
                                                SendFileInfo.type = "1"
                                                SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                                SendFileInfo.porperty = "1"
                                                AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                                /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                                EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/
                                            } else {

                                                return@Runnable
                                            }

                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = LibsodiumUtil.DecryptShareKey(i.userKey);
                                            val code = FileUtil.copySdcardToxPicAndEncrypt(imagePath, base58files_dir, aesKey.substring(0,16), false)
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.gId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_IMAGE
                                                toxFileData.fileId = uuid
                                                toxFileData.widthAndHeight = widthAndHeight.substring(1, widthAndHeight.length)
                                                toxFileData.porperty = "1"

                                                toxFileData.srcKey = i.userKey
                                                toxFileData.dstKey = i.userKey

                                                var fileNumber = ""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }
                                                ConstantValue.sendToxFileInGroupChapDataMap.put(fileNumber, toxFileData)
                                            } else {

                                                return@Runnable
                                            }

                                        }
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 1
                                        Message.fileName = fileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.gId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.GroupChat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.gId, baseDataJson)
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
                                        val message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, i.gId)
                                        val userId = SpUtil.getString(this, ConstantValue.userId, "")
                                        message.from = userId
                                        message.to = i.gId
                                        message.isDelivered = true
                                        message.isAcked = false
                                        message.isUnread = true

                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            val SendFileInfo = SendFileInfo()
                                            SendFileInfo.userId = userId
                                            SendFileInfo.friendId = i.gId
                                            SendFileInfo.files_dir = videoPath
                                            SendFileInfo.msgId = uuid
                                            SendFileInfo.friendSignPublicKey = i.userKey
                                            SendFileInfo.friendMiPublicKey = i.userKey
                                            SendFileInfo.voiceTimeLen = 0
                                            SendFileInfo.type = "3"
                                            SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                            SendFileInfo.porperty = "1"
                                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                            /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/

                                        } else {
                                            val strBase58 = Base58.encode(videoFileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = LibsodiumUtil.DecryptShareKey(i.userKey);
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(videoPath, base58files_dir, aesKey.substring(0,16))
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                /*sendMsgMap[uuid.toString() + ""] = message
                                                sendMsgLocalMap[uuid.toString() + ""] = false
                                                sendFilePathMap[uuid.toString() + ""] = base58files_dir
                                                sendFileFriendKeyMap[uuid.toString() + ""] = i.signPublicKey*/
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.gId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_MEDIA
                                                toxFileData.fileId = uuid
                                                toxFileData.porperty = "1"

                                                toxFileData.srcKey = i.userKey
                                                toxFileData.dstKey = i.userKey
                                                var fileNumber = ""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }

                                                ConstantValue.sendToxFileDataMap[fileNumber] = toxFileData
                                            }
                                        }
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 4
                                        Message.fileName = videoFileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.gId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.GroupChat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.gId, baseDataJson)
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
                                        val message = EMMessage.createFileSendMessage(filePath, i.gId)
                                        val userId = SpUtil.getString(this, ConstantValue.userId, "")
                                        message.from = userId
                                        message.to = i.gId
                                        message.isDelivered = true
                                        message.isAcked = false
                                        message.isUnread = true


                                        if (ConstantValue.curreantNetworkType == "WIFI") {
                                            val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase()
                                            message.msgId = uuid

                                            val SendFileInfo = SendFileInfo()
                                            SendFileInfo.userId = userId
                                            SendFileInfo.friendId = i.gId
                                            SendFileInfo.files_dir = files_dir
                                            SendFileInfo.msgId = uuid
                                            SendFileInfo.friendSignPublicKey = i.userKey
                                            SendFileInfo.friendMiPublicKey = i.userKey
                                            SendFileInfo.voiceTimeLen = 0
                                            SendFileInfo.type = "4"
                                            SendFileInfo.sendTime = (System.currentTimeMillis()).toString() + ""
                                            SendFileInfo.porperty = "1"
                                            AppConfig.instance.getPNRouterServiceMessageSender().sendFileMsg(SendFileInfo)
                                            /*val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                            EventBus.getDefault().post(FileTransformEntity(uuid, 0, "", wssUrl, "lws-pnr-bin"))*/
                                        } else {
                                            val strBase58 = Base58.encode(fileName.toByteArray())
                                            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + strBase58
                                            val aesKey = LibsodiumUtil.DecryptShareKey(i.userKey);
                                            val code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, aesKey.substring(0,16))
                                            if (code == 1) {
                                                val uuid = (System.currentTimeMillis() / 1000).toInt()
                                                message.msgId = uuid.toString() + ""
                                                val toxFileData = ToxFileData()
                                                toxFileData.fromId = userId
                                                toxFileData.toId = i.gId
                                                val fileMi = File(base58files_dir)
                                                val fileSize = fileMi.length()
                                                val fileMD5 = FileUtil.getFileMD5(fileMi)
                                                toxFileData.fileName = strBase58
                                                toxFileData.fileMD5 = fileMD5
                                                toxFileData.fileSize = fileSize.toInt()
                                                toxFileData.fileType = ToxFileData.FileType.PNR_IM_MSGTYPE_FILE
                                                toxFileData.fileId = uuid
                                                toxFileData.porperty = "1"

                                                toxFileData.srcKey = i.userKey
                                                toxFileData.dstKey = i.userKey
                                                var fileNumber =""
                                                if (ConstantValue.isAntox) {
                                                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                    fileNumber = MessageHelper.sendFileSendRequestFromKotlin(AppConfig.instance, base58files_dir, friendKey)
                                                }else{
                                                    fileNumber = ToxCoreJni.getInstance().senToxFile(base58files_dir, ConstantValue.currentRouterId.substring(0, 64)).toString()
                                                }

                                                ConstantValue.sendToxFileDataMap[fileNumber] = toxFileData
                                            }

                                        }
                                        FileUtil.copySdcardFile(filePath, files_dir)
                                        val gson = Gson()
                                        val Message = Message()
                                        Message.msgType = 5
                                        Message.fileName = fileName
                                        Message.msg = ""
                                        Message.from = userId
                                        Message.to = i.gId
                                        Message.timeStamp = System.currentTimeMillis() / 1000
                                        Message.unReadCount = 0
                                        Message.chatType = EMMessage.ChatType.GroupChat
                                        val baseDataJson = gson.toJson(Message)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + i.gId, baseDataJson)
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
            //toast(R.string.hasbeensent)

        }
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        //EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectFriendChange(selectFriendChange: SelectFriendChange) {
        personCount = selectFriendChange.friendNum
        if (personCount > 1) {
            if(groupCount > 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + "s And "+ personCount  +" "+ getString(R.string.person) + "s"
            }else if(groupCount == 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + " And "+ personCount  +" "+ getString(R.string.person) + "s"
            }else{
                selectTxt.text = getString(R.string.selected) +" "+ personCount  +" "+ getString(R.string.person) + "s"
            }

        } else {
            if(groupCount > 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + "s And "+ personCount  +" "+ getString(R.string.person)
            }else if(groupCount == 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + " And "+ personCount  +" "+ getString(R.string.person)
            }else{
                selectTxt.text = getString(R.string.selected) +" "+ personCount  +" "+ getString(R.string.person)
            }
        }
        if (personCount == 0 && groupCount == 0) {
            send.text = "Confirm"
        } else {
            send.text = "Confirm (" + (personCount + groupCount) + ")"
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun SelectGroupChanges(SelectGroupChange: SelectGroupChange) {
        groupCount = SelectGroupChange.groupNum
        if (personCount > 1) {
            if(groupCount > 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + "s And "+ personCount  +" "+ getString(R.string.person) + "s"
            }else if(groupCount == 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + " And "+ personCount  +" "+ getString(R.string.person) + "s"
            }else{
                selectTxt.text = getString(R.string.selected) +" "+ personCount  +" "+ getString(R.string.person) + "s"
            }

        } else {
            if(groupCount > 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + "s And "+ personCount  +" "+ getString(R.string.person)
            }else if(groupCount == 1)
            {
                selectTxt.text = getString(R.string.selected) +" "+ groupCount  +" "+ getString(R.string.group) + " And "+ personCount  +" "+ getString(R.string.person)
            }else{
                selectTxt.text = getString(R.string.selected) +" "+ personCount  +" "+ getString(R.string.person)
            }
        }
        if (personCount == 0 && groupCount == 0) {
            send.text = "Confirm"
        } else {
            send.text = "Confirm (" + (personCount + groupCount) + ")"
        }
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
            overridePendingTransition(0, R.anim.activity_translate_out_1)
        }
    }
}