package com.stratagile.pnrouter.ui.activity.chat

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.LinearLayout
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseGroupChatFragment
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.entity.events.DeleteMsgEvent
import com.stratagile.pnrouter.ui.activity.chat.component.DaggerGroupChatComponent
import com.stratagile.pnrouter.ui.activity.chat.contract.GroupChatContract
import com.stratagile.pnrouter.ui.activity.chat.module.GroupChatModule
import com.stratagile.pnrouter.ui.activity.chat.presenter.GroupChatPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxChatReceiveFileFinishedEvent
import events.ToxChatReceiveFileNoticeEvent
import events.ToxSendFileFinishedEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_chat.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.ConcurrentHashMap

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: $description
 * @date 2019/03/18 15:06:56
 */

class GroupChatActivity : BaseActivity(), GroupChatContract.View , PNRouterServiceMessageReceiver.GroupChatCallBack, ViewTreeObserver.OnGlobalLayoutListener {
    override fun droupSysPushRsp(jGroupSysPushRsp: JGroupSysPushRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = GroupSysPushRsp(0, userId!!)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, msgData, jGroupSysPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(4, msgData, jGroupSysPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        when(jGroupSysPushRsp.params.type){

            1->{

            }
            2->{

            }
            3->{
                chatFragment?.delFreindMsg(jGroupSysPushRsp)
            }
            4->{
                chatFragment?.delFreindMsg(jGroupSysPushRsp)
            }
            241->{

            }
            242->{

            }
            243->{

            }

        }
    }

    @Inject
    internal lateinit var mPresenter: GroupChatPresenter

    var activityInstance: GroupChatActivity? = null
    private var chatFragment: EaseGroupChatFragment? = null
    internal var toChatUserID: String? = null
    var statusBarHeight: Int = 0
    var groupEntity : GroupEntity? = null
    var receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
    var receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
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

    override fun userInfoGroupPushRsp(jUserInfoPushRsp: JUserInfoPushRsp) {
        chatFragment?.updatFriendName(jUserInfoPushRsp)
    }

    override fun pullGroupFileMsgRsp(jJToxPullFileRsp: JToxPullFileRsp) {
        if(jJToxPullFileRsp.params.retCode != 0)
        {
            toast(R.string.acceptanceerror)
        }
    }

    override fun sendGroupToxFileRsp(jSendToxFileRsp: JSendToxFileRsp) {
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
                chatFragment?.receiveFileMessage(fileName,jPushFileMsgRsp.params.msgId.toString(),fromId,toId,FileType)
                receiveFileDataMap.remove(fileMiName)
            }
        }else{
            chatFragment?.onToxReceiveFileFinished(fileMiName)
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
            SpUtil.putInt(this@GroupChatActivity, ConstantValue.realKeyboardHeight, softInputHeight)
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

    override fun pushDelGroupMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {

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
            //chatFragment?.delFreindMsg(delMsgPushRsp)
        }
    }
    override fun pushGroupFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        KLog.i("abcdefshouTime:" + (System.currentTimeMillis() - ConstantValue.shouBegin) / 1000)
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val gson = Gson()
        val Message = Message()
        Message.msgType = jPushFileMsgRsp.params.fileType
        Message.fileName = jPushFileMsgRsp.params.fileName
        Message.msg = ""
        Message.from = userId
        Message.to = jPushFileMsgRsp.params.fromId
        Message.timeStatmp = System.currentTimeMillis() / 1000
        Message.unReadCount = 0
        Message.chatType = EMMessage.ChatType.GroupChat
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
            var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port +jPushFileMsgRsp.params.filePath
            var files_dir = PathUtils.getInstance().filePath.toString()+"/"
            if (ConstantValue.isWebsocketConnected) {
                receiveFileDataMap.put(jPushFileMsgRsp.params.msgId.toString(),jPushFileMsgRsp)
                FileDownloadUtils.doDownLoadWork(filledUri, files_dir, this,jPushFileMsgRsp.params.msgId, handler,jPushFileMsgRsp.params.dstKey)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteMsgEvent(deleteMsgEvent: DeleteMsgEvent) {
        chatFragment?.delMyMsgOnSending(deleteMsgEvent.msgId)
    }
    override fun delGroupMsgRsp(delMsgRsp: JGroupDelMsgRsp) {
        if (delMsgRsp.params.retCode == 0) {
            chatFragment?.delMyMsgOnSuccess(delMsgRsp.params.msgId.toString())
        }
    }
    override fun pullGroupMsgRsp(pushMsgRsp: JGroupMsgPullRsp) {

        var messageList: List<Message> = pushMsgRsp.params.payload
        KLog.i("insertMessage:GroupChatActivity"+chatFragment)
        /*val size = messageList.size
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

        }*/
       /* if(!msgIdStr.equals(""))
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
        }*/
        chatFragment?.refreshData(messageList,pushMsgRsp.params.userId,pushMsgRsp.params.gId)
    }

    override fun pushGroupMsgRsp(pushMsgRsp: JGroupMsgPushRsp) {

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = GroupMsgPushRsp(0,userId!!, toChatUserID!!, "")
        var sendData = BaseData(4,msgData,pushMsgRsp?.msgid)
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
        if (pushMsgRsp.params.gId.equals(toChatUserID)) {//正好在聊天窗口聊天
            chatFragment?.receiveTxtMessageV3(pushMsgRsp)

        }
    }

    override fun sendGroupMsg(userId: String, gId: String, point :String, Msg: String,UserKey:String):String {
        var msgId = 0
        try {
            if(userId.equals("") || gId.equals(""))
            {
                toast(R.string.Empty_with_parameters)
                return msgId.toString()
            }
            if(Msg.length >264)
            {
                toast(R.string.nomorecharacters)
                return msgId.toString()
            }

            var friendMiPublic = RxEncodeTool.base64Decode(point)
            LogUtil.addLog("groupSendMsgV3 UserKey:",UserKey)
            var aesKey = LibsodiumUtil.DecryptShareKey(UserKey)
            var fileBufferMi = AESCipher.aesEncryptBytes(Msg.toByteArray(), aesKey!!.toByteArray(charset("UTF-8")))
            var msgMi = RxEncodeTool.base64Encode2String(fileBufferMi);
            var groupSendMsgReq = GroupSendMsgReq(userId!!, gId!!, point,msgMi)
            var baseData = BaseData(4,groupSendMsgReq)
            msgId = baseData.msgid!!
            if (ConstantValue.curreantNetworkType.equals("WIFI")) {
                AppConfig.instance.getPNRouterServiceMessageSender().sendGroupChatMsg(baseData)

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
    override fun sendGroupMsgRsp(jGroupSendMsgRsp: JGroupSendMsgRsp) {
        chatFragment?.upateMessage(jGroupSendMsgRsp)
        //todo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        KLog.i("insertMessage:GroupChatActivity_onCreate"+chatFragment)
        toChatUserID = intent.extras!!.getString(EaseConstant.EXTRA_USER_ID)
        groupEntity = intent.extras!!.getParcelable(EaseConstant.EXTRA_CHAT_GROUP)
        receiveFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        receiveToxFileDataMap = ConcurrentHashMap<String, JPushFileMsgRsp>()
        super.onCreate(savedInstanceState)

    }

    override fun initView() {
        setContentView(R.layout.activity_chat)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        activityInstance = this
        //user or group id
        AppConfig.instance.isChatWithFirend = toChatUserID
        chatFragment = EaseGroupChatFragment()
        //set arguments
        chatFragment?.setArguments(intent.extras)
        chatFragment?.setChatUserId(toChatUserID)
        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment!!).commit()
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        view1.setLayoutParams(llp)
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(this@GroupChatActivity)
    }

    override fun initData() {
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.groupchatCallBack = this
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        //var pullMsgList = PullMsgReq(userId!!, toChatUserID!!, 1, 0, 10)

        val pullMsgList = GroupMsgPullReq(userId!!, ConstantValue.currentRouterId, UserDataManger.currentGroupData.gId.toString() + "", 0, 0, 10, "GroupMsgPull")
        var sendData = BaseData(pullMsgList)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(4,pullMsgList)
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
            AppConfig.instance.messageReceiver!!.groupchatCallBack = null
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
        DaggerGroupChatComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .groupChatModule(GroupChatModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: GroupChatContract.GroupChatContractPresenter) {
        mPresenter = presenter as GroupChatPresenter
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
                        var jPushFileMsgRsp: JPushFileMsgRsp = receiveFileDataMap.get(msgId.toString())!!
                        var fileName:String = jPushFileMsgRsp.params.fileName;
                        var fromId = jPushFileMsgRsp.params.fromId;
                        var toId = jPushFileMsgRsp.params.toId
                        var FileType = jPushFileMsgRsp.params.fileType
                        chatFragment?.receiveFileMessage(fileName,msgId.toString(),fromId,toId,FileType)
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