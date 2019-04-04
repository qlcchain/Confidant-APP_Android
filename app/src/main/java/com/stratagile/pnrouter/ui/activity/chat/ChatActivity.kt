package com.stratagile.pnrouter.ui.activity.chat

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.LinearLayout
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseChatFragment
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.ConstantValue.port
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.BeginDownloadForwad
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.DeleteMsgEvent
import com.stratagile.pnrouter.entity.events.SaveMsgEvent
import com.stratagile.pnrouter.ui.activity.chat.component.DaggerChatComponent
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.module.ChatModule
import com.stratagile.pnrouter.ui.activity.chat.presenter.ChatPresenter
import com.stratagile.pnrouter.utils.*
import events.ToxChatReceiveFileFinishedEvent
import events.ToxChatReceiveFileNoticeEvent
import events.ToxSendFileFinishedEvent
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: $description
 * @date 2018/09/13 13:18:46
 */

class ChatActivity : BaseActivity(), ChatContract.View, PNRouterServiceMessageReceiver.ChatCallBack, ViewTreeObserver.OnGlobalLayoutListener {
    override fun fileForwardReq(jFileForwardRsp: JFileForwardRsp) {
        chatFragment?.upateForwardMessage(jFileForwardRsp)
    }

    override fun updateAvatarReq(jUpdateAvatarRsp: JUpdateAvatarRsp) {
        if(jUpdateAvatarRsp.params.retCode == 0)
        {

            var filePath = jUpdateAvatarRsp.params.fileName
            var fileBase58Name = filePath.substring(8,filePath.length)
            var fileName = String(Base58.decode(fileBase58Name));
            val filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + filePath
            var fileSavePath  = PathUtils.getInstance().filePath.toString() + "/"
            var msgId = Calendar.getInstance().timeInMillis /1000
            FileDownloadUtils.doDownLoadWork(filledUri, fileSavePath, this, msgId.toInt(), handlerDown, "","0")
        }
    }
    internal var handlerDown: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                }
                0x55 -> {
                }
            }//goMain();
            //goMain();
        }
    }
    override fun QueryFriendRep(jQueryFriendRsp: JQueryFriendRsp) {
        chatFragment?.setFriendStatus(jQueryFriendRsp.params.retCode)
    }

    override fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp) {
        chatFragment?.updatFriendName(jUserInfoPushRsp)
    }

    override fun pullFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp) {
        if(jJToxPullFileRsp.params.retCode != 0)
        {
            toast(R.string.acceptanceerror)
        }
    }

    override fun sendToxFileRsp(jSendToxFileRsp: JSendToxFileRsp) {
        chatFragment?.onToxFileSendRsp(jSendToxFileRsp)

    }

    override fun readMsgPushRsp(jReadMsgPushRsp: JReadMsgPushRsp) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = ReadMsgPushReq(0, "", userId!!)
        var msgId:String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData,jReadMsgPushRsp.msgid))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,msgData,jReadMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        chatFragment?.refreshReadData(jReadMsgPushRsp.params.readMsgs)
    }

    @Inject
    internal lateinit var mPresenter: ChatPresenter
    var activityInstance: ChatActivity? = null
    private var chatFragment: EaseChatFragment? = null
    internal var toChatUserID: String? = null
    var statusBarHeight: Int = 0
    var receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
    var receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
    override fun onGlobalLayout() {
        var myLayout = getWindow().getDecorView();
        val r = Rect()
        // 使用最外层布局填充，进行测算计算
        parentLayout.getWindowVisibleDisplayFrame(r)
        val screenHeight = myLayout.getRootView().getHeight()
        val heightDiff = screenHeight - (r.bottom - r.top)
        if (heightDiff > 100) {
            // 如果超过100个像素，它可能是一个键盘。获取状态栏的高度
            statusBarHeight = 0
        }
        getSupportSoftInputHeight()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber=  toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        chatFragment?.onToxFileSendFinished(fileNumber,key)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnToxChatReceiveFileNoticeEvent(toxReceiveFileNoticeEvent: ToxChatReceiveFileNoticeEvent) {
        var fileNumber=  toxReceiveFileNoticeEvent.fileNumber
        var key = toxReceiveFileNoticeEvent.key
        var fileName = toxReceiveFileNoticeEvent.filename
        chatFragment?.onAgreeReceivwFileStart(fileNumber,key,fileName)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxChatReceiveFileFinishedEvent(toxReceiveFileFinishedEvent: ToxChatReceiveFileFinishedEvent) {
        var fileNumber=  toxReceiveFileFinishedEvent.fileNumber
        var key = toxReceiveFileFinishedEvent.key
        var fileNameSouce =  chatFragment?.getToxReceiveFileName(fileNumber,key)
        var fileNameList = fileNameSouce!!.split(":")
        var fileMiName = fileNameList[1]
        var jPushFileMsgRsp = receiveToxFileDataMap.get(fileMiName)
        if(jPushFileMsgRsp != null)
        {
            var fileName:String = jPushFileMsgRsp!!.params.fileName;
            val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + fileName
            val files_dir = PathUtils.getInstance().filePath.toString() + "/" + fileName
            var aesKey = ""
            if(ConstantValue.encryptionType.equals("1"))
            {
                aesKey = LibsodiumUtil.DecryptShareKey(jPushFileMsgRsp!!.params.dstKey)
            }else{
                aesKey = RxEncodeTool.getAESKey(jPushFileMsgRsp!!.params.dstKey)
            }

            var code = FileUtil.copySdcardToxFileAndDecrypt(base58files_dir,files_dir,aesKey)
            if(code == 1)
            {
                var fromId = jPushFileMsgRsp!!.params.fromId;
                var toId = jPushFileMsgRsp!!.params.toId
                var FileType = jPushFileMsgRsp!!.params.fileType
                chatFragment?.receiveFileMessage(fileName,jPushFileMsgRsp.params.msgId.toString(),fromId,toId,FileType, "")
                receiveFileDataMap.remove(fileMiName)
            }
        }else{
            chatFragment?.onToxReceiveFileFinished(fileMiName)
        }

    }
    override fun queryFriend(FriendId :String) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = QueryFriendReq(userId!!, FriendId)
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
    /**
     * 获取软件盘的高度
     * @return
     */
    private fun getSupportSoftInputHeight(): Int {
        val r = Rect()
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight = getWindow().getDecorView().getRootView().getHeight()
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight()
        }

        if (softInputHeight < 0) {
            KLog.w("EmotionKeyboard--Warning: value of softInputHeight is below zero!")
        }
        //存一份到本地
        if (softInputHeight > 0) {
            KLog.i("获取到的键盘的高度为: " + softInputHeight)
            SpUtil.putInt(this@ChatActivity, ConstantValue.realKeyboardHeight, softInputHeight)
        }
        return softInputHeight
    }


    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getSoftButtonsBarHeight(): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {

        var msgData = DelMsgRsp(0,"", delMsgPushRsp.params.friendId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData,delMsgPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(msgData,delMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        if (delMsgPushRsp.params.userId.equals(toChatUserID)) {//正好在聊天窗口聊天
            chatFragment?.delFreindMsg(delMsgPushRsp)
        }
    }
    override fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        KLog.i("abcdefshouTime:" + (System.currentTimeMillis() - ConstantValue.shouBegin) / 1000)
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val gson = Gson()
        val Message = Message()
        Message.msgType = jPushFileMsgRsp.params.fileType
        Message.fileName = jPushFileMsgRsp.params.fileName
        Message.filePath = jPushFileMsgRsp.params.filePath
        Message.msg = ""
        Message.from = userId
        Message.to = jPushFileMsgRsp.params.fromId
        Message.timeStamp = System.currentTimeMillis() / 1000
        Message.chatType = EMMessage.ChatType.Chat
        Message.unReadCount = 0
        val baseDataJson = gson.toJson(Message)
        if (Message.sender == 0) {
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
        } else {
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
        }
        var msgDataPushFileRsp = PushFileRespone(0,jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,jPushFileMsgRsp.params.msgId)
        var msgId:String = jPushFileMsgRsp?.params.msgId.toString()
        var readMsgReq  =  ReadMsgReq(userId!!,jPushFileMsgRsp.params.fromId,msgId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgDataPushFileRsp,jPushFileMsgRsp.msgid))
            if(!msgId.equals(""))
            {
                if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
                }
            }
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(msgDataPushFileRsp,jPushFileMsgRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
            if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                var baseData2 = BaseData(2,readMsgReq)
                var baseDataJson2 = baseData2.baseDataToJson().replace("\\", "")
                if(!msgId.equals(""))
                {
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson2, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson2, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }

        }
        if (jPushFileMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
            var filledUri = "https://" + ConstantValue.currentRouterIp + port+jPushFileMsgRsp.params.filePath
            var files_dir = PathUtils.getInstance().filePath.toString()+"/"
            if (ConstantValue.isWebsocketConnected) {
                receiveFileDataMap.put(jPushFileMsgRsp.params.msgId.toString(),jPushFileMsgRsp)
                FileDownloadUtils.doDownLoadWork(filledUri, files_dir, this,jPushFileMsgRsp.params.msgId, handler,jPushFileMsgRsp.params.dstKey,"0")
            }else{

                var base58Name =  Base58.encode(jPushFileMsgRsp.params.fileName.toByteArray())
                receiveToxFileDataMap.put(base58Name,jPushFileMsgRsp)
                var msgData = PullFileReq(jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId,base58Name,jPushFileMsgRsp.params.msgId,2,1)
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
    }

    override fun delMsgRsp(delMsgRsp: JDelMsgRsp) {
        if (delMsgRsp.params.retCode == 0) {
            chatFragment?.delMyMsgOnSuccess(delMsgRsp.params.msgId.toString())
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteMsgEvent(deleteMsgEvent: DeleteMsgEvent) {
        chatFragment?.delMyMsgOnSending(deleteMsgEvent.msgId)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun saveMsgEvent(saveMsgEvent: SaveMsgEvent) {
        if(saveMsgEvent.result == 1)
        {
            runOnUiThread {
                toast(R.string.success)
            }

        }
    }
    override fun pullMsgRsp(pushMsgRsp: JPullMsgRsp) {


        var messageList: List<Message> = pushMsgRsp.params.payload
        KLog.i("insertMessage:ChatActivity"+chatFragment)
        val size = messageList.size
        var msgIdStr:String = "";
        for (mesage in messageList)
        {
            if(mesage.sender == 1)//对方发的消息
            {
                if(mesage.status == 0 || mesage.status == 1)//未读
                {
                    msgIdStr += (mesage.msgId.toString() + ",")
                }
            }

        }
        if(!msgIdStr.equals(""))
        {
            val userId = SpUtil.getString(this, ConstantValue.userId, "")
            var readMsgReq  =  ReadMsgReq(userId!!,pushMsgRsp.params.friendId,msgIdStr)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(2,readMsgReq)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        chatFragment?.refreshData(messageList,pushMsgRsp.params.userId,pushMsgRsp.params.friendId)
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId),userId!!, 0, "")
        var msgId:String = pushMsgRsp?.params.msgId.toString()
        var readMsgReq  =  ReadMsgReq(userId,pushMsgRsp.params.fromId,msgId)
        var sendData = BaseData(msgData,pushMsgRsp?.msgid)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(3,msgData,pushMsgRsp?.msgid)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            if(!msgId.equals(""))
            {
                if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,readMsgReq))
                }
            }

        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
            if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
                var baseData2 = BaseData(2,readMsgReq)
                var baseDataJson2 = baseData2.baseDataToJson().replace("\\", "")
                //var friendKey2: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                if(!msgId.equals(""))
                {
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }

        }
        if (pushMsgRsp.params.fromId.equals(toChatUserID)) {//正好在聊天窗口聊天
            if(ConstantValue.encryptionType.equals("1"))
            {
                chatFragment?.receiveTxtMessageV3(pushMsgRsp)
            }else{
                chatFragment?.receiveTxtMessage(pushMsgRsp)
            }

        }
    }

    override fun sendMsg(FromId: String, ToId: String, FriendPublicKey:String, Msg: String) {
        try {
            if(Msg.length >264)
            {
                toast(R.string.nomorecharacters)
                return
            }
            var aesKey =  RxEncryptTool.generateAESKey()
            LogUtil.addLog("sendMsg aesKey:",aesKey)
            var my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
            LogUtil.addLog("sendMsg myKey:",ConstantValue.publicRAS)
            var friend = RxEncodeTool.base64Decode(FriendPublicKey)
            LogUtil.addLog("sendMsg friendKey:",FriendPublicKey)
            var SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),my))
            LogUtil.addLog("sendMsg SrcKey:",SrcKey.toString())
            var DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),friend))
            LogUtil.addLog("sendMsg DstKey:",SrcKey.toString())
            var miMsg = AESCipher.aesEncryptString(Msg,aesKey)
            LogUtil.addLog("sendMsg miMsg:",miMsg)
            var msgData = SendMsgReq(FromId!!, ToId!!, miMsg,String(SrcKey),String(DstKey))
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }catch (e:Exception)
        {
            chatFragment?.deleteMessage()
            LogUtil.addLog("sendMsg 错误:",e.toString())
            toast(R.string.Encryptionerror)
        }
    }
    override fun sendMsgV3(FromIndex: String, ToIndex: String, FriendMiPublicKey :String, Msg: String):String {
        var msgId = 0
        try {
            if(FromIndex.equals("") || ToIndex.equals("") || FriendMiPublicKey.equals(""))
            {
                toast(R.string.Empty_with_parameters)
                return msgId.toString()
            }
            if(Msg.length >264)
            {
                toast(R.string.nomorecharacters)
                return msgId.toString()
            }

            var friendMiPublic = RxEncodeTool.base64Decode(FriendMiPublicKey)
            LogUtil.addLog("sendMsg2 friendKey:",FriendMiPublicKey)
            var msgMap = LibsodiumUtil.EncryptSendMsg(Msg,friendMiPublic)
            var msgData = SendMsgReqV3(FromIndex!!, ToIndex!!, msgMap.get("encryptedBase64")!!,msgMap.get("signBase64")!!,msgMap.get("NonceBase64")!!,msgMap.get("dst_shared_key_Mi_My64")!!)

            var baseData = BaseData(3,msgData)
            msgId = baseData.msgid!!
            if (ConstantValue.curreantNetworkType.equals("WIFI")) {
                AppConfig.instance.getPNRouterServiceMessageSender().sendChatMsg(baseData)

            }else if (ConstantValue.isToxConnected) {
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            return msgId.toString()
        }catch (e:Exception)
        {
            LogUtil.addLog("sendMsg2 错误:",e.toString())
            toast(R.string.Encryptionerror)
            chatFragment?.removeLastMessage()
        }
        return msgId.toString()
    }
    override fun sendMsgRsp(sendMsgRsp: JSendMsgRsp) {
        chatFragment?.upateMessage(sendMsgRsp)
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        KLog.i("insertMessage:ChatActivity_onCreate"+chatFragment)
        toChatUserID = intent.extras!!.getString(EaseConstant.EXTRA_USER_ID)
        receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        super.onCreate(savedInstanceState)

    }

    override fun initView() {
        setContentView(R.layout.activity_chat)
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        activityInstance = this
        //user or group id
        AppConfig.instance.isChatWithFirend = toChatUserID
        chatFragment = EaseChatFragment()
        //set arguments
        chatFragment?.setArguments(intent.extras)
        chatFragment?.setChatUserId(toChatUserID)
        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment!!).commit()
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        view1.setLayoutParams(llp)
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(this@ChatActivity)
        queryFriend(toChatUserID!!)
    }

    override fun initData() {
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.chatCallBack = this
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullMsgList = PullMsgReq(userId!!, toChatUserID!!, 0, 0, 10)
        var sendData = BaseData(pullMsgList)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(3,pullMsgList)
        }
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
        EventBus.getDefault().register(this)

        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData!!.signPublicKey))
        var filePath  = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
        var fileMD5 = FileUtil.getFileMD5(File(filePath))
        if(fileMD5 == null)
        {
            fileMD5 = ""
        }
        val updateAvatarReq = UpdateAvatarReq(userId!!, UserDataManger.curreantfriendUserData!!.userId, fileMD5)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, updateAvatarReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        view1.postDelayed({
            if (SpUtil.getInt(this@ChatActivity, ConstantValue.realKeyboardHeight, 0) == 0) {
                chatFragment?.inputMenu?.chatPrimaryMenu?.showKeyBorad()
            }
        }, 300)
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
    override fun onDestroy() {
        try {
            super.onDestroy()
            AppConfig.instance.messageReceiver!!.chatCallBack = null
            AppConfig.instance.isChatWithFirend = null
            activityInstance = null
            EventBus.getDefault().unregister(this)
        }catch (e :Exception)
        {

        }
    }

    override fun onNewIntent(intent: Intent) {
        // enter to chat activity when click notification bar, here make sure only one chat activiy
        val username = intent.getStringExtra("userId")
        if (toChatUserID == username)
            super.onNewIntent(intent)
        else {
            finish()
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        chatFragment?.onBackPressed()
    }

    fun getToChatUsername(): String {
        return toChatUserID!!
    }

    override fun setupActivityComponent() {
        DaggerChatComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .chatModule(ChatModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ChatContract.ChatContractPresenter) {
        mPresenter = presenter as ChatPresenter
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

                }
                0x55 -> {
                    try {
                        var data:Bundle = msg.data;
                        var msgId = data.getInt("msgID")
                        var jPushFileMsgRsp:JPushFileMsgRsp = receiveFileDataMap.get(msgId.toString())!!
                        var fileName:String = jPushFileMsgRsp.params.fileName;
                        var fromId = jPushFileMsgRsp.params.fromId;
                        var toId = jPushFileMsgRsp.params.toId
                        var FileType = jPushFileMsgRsp.params.fileType
                        if (jPushFileMsgRsp.params.fileInfo != null) {
                            chatFragment?.receiveFileMessage(fileName,msgId.toString(),fromId,toId,FileType, jPushFileMsgRsp.params.fileInfo)
                        } else {
                            chatFragment?.receiveFileMessage(fileName,msgId.toString(),fromId,toId,FileType, "")
                        }
                        receiveFileDataMap.remove(msgId.toString())
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                    }

                }
            }//goMain();
            //goMain();
        }
    }
}