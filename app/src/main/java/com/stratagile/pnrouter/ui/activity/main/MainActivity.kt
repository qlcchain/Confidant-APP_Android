package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.google.gson.Gson
import com.hyphenate.chat.*
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.ui.EaseContactListFragment
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.message.MessageProvider
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxSendInfoEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View, PNRouterServiceMessageReceiver.MainInfoBack, MessageProvider.MessageListener {
    override fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()

        for (j in localFriendList) {
            if (jUserInfoPushRsp.params.friendId.equals(j.userId)) {
                j.nickName = jUserInfoPushRsp.params.nickName
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                break
            }
        }
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = UserInfoPushRsp(0, userId!!,"")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData,jUserInfoPushRsp.msgid))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,msgData,jUserInfoPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }
        runOnUiThread {
            contactFragment?.updataUI()
        }
    }

    override fun unReadCount(unReadCOunt: Int) {
        runOnUiThread {
            if (unread_count != null) {
                if (unReadCOunt == 0) {
                    unread_count.visibility = View.INVISIBLE
                    unread_count.text = ""
                } else {
                    unread_count.visibility = View.VISIBLE
                    unread_count.text = unReadCOunt.toString()
                }
            }
        }
    }

    override fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(jPushFileMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            val gson = Gson()
            val Message = Message()
            Message.msgType = jPushFileMsgRsp.params.fileType
            Message.fileName = jPushFileMsgRsp.params.fileName
            Message.msg = ""
            Message.from = userId
            Message.to = jPushFileMsgRsp.params.fromId
            Message.timeStatmp = System.currentTimeMillis()
            val baseDataJson = gson.toJson(Message)
            if (Message.sender == 0) {
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
            } else {
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
            }
            if (ConstantValue.isInit) {
                runOnUiThread {
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                    controlleMessageUnReadCount(UnReadMessageCount)
                }
                conversationListFragment?.refresh()
                ConstantValue.isRefeshed = true
            }
//        }else{
            /* var ipAddress = WiFiUtil.getGateWay(AppConfig.instance);
             var filledUri = "https://" + ipAddress + ConstantValue.port +jPushFileMsgRsp.params.filePath
             var files_dir = this.filesDir.absolutePath + "/image/"
             FileDownloadUtils.doDownLoadWork(filledUri, files_dir, this, jPushFileMsgRsp.params.deleteMsgId, handler)*/
        }
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {

        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(delMsgPushRsp.params.friendId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(delMsgPushRsp.params.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            var conversation2: EMConversation = EMClient.getInstance().chatManager().getConversation(delMsgPushRsp.params.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation2 != null) {
                val lastMessage2 = conversation2.lastMessage
                var all2 = conversation2.allMessages
                var aa = "";
            }
            if (conversation != null) {
                val forward_msg = EMClient.getInstance().chatManager().getMessage(delMsgPushRsp.params.msgId.toString())
                if (forward_msg != null) {
                    val var3 = EMTextMessageBody(resources.getString(R.string.withdrawn))
                    forward_msg.addBody(var3)
                    conversation.updateMessage(forward_msg)
                    if (ConstantValue.isInit) {
                        conversationListFragment?.refresh()
                        ConstantValue.isRefeshed = true
                    }
                }
                val lastMessage = conversation.lastMessage
                var all = conversation.allMessages

                if(lastMessage != null&&lastMessage.msgId.contains(delMsgPushRsp.params.msgId.toString()))
                {
                    val message = EMMessage.createTxtSendMessage(resources.getString(R.string.withdrawn), delMsgPushRsp.params.friendId)
                    message.setDirection(EMMessage.Direct.RECEIVE)
                    message.msgId = delMsgPushRsp.params.msgId.toString()
                    message.from = delMsgPushRsp.params.friendId
                    message.to = delMsgPushRsp.params.userId
                    message.isUnread = true
                    message.isAcked = true
                    message.setStatus(EMMessage.Status.SUCCESS)
                    if(conversation != null)
                    {
                        conversation.insertMessage(message)
                        KLog.i("insertMessage:" + "MainActivity"+"_pushDelMsgRsp")
                        if (ConstantValue.isInit) {
                            conversationListFragment?.refresh()
                            ConstantValue.isRefeshed = true
                        }
                    }
                }
                if (forward_msg.type == EMMessage.Type.IMAGE) {
                    val imgBody = forward_msg.body as EMImageMessageBody
                    val localUrl = imgBody.localUrl
                    FileUtil.deleteFile(localUrl)
                } else if (forward_msg.type == EMMessage.Type.VIDEO) {
                    val imgBody = forward_msg.body as EMVideoMessageBody
                    val localUrl = imgBody.localUrl
                    FileUtil.deleteFile(localUrl)
                } else if (forward_msg.type == EMMessage.Type.VOICE) {
                    val imgBody = forward_msg.body as EMVoiceMessageBody
                    val localUrl = imgBody.localUrl
                    FileUtil.deleteFile(localUrl)
                }else if (forward_msg.type == EMMessage.Type.FILE) {
                    val imgBody = forward_msg.body as EMNormalFileMessageBody
                    val localUrl = imgBody.localUrl
                    FileUtil.deleteFile(localUrl)
                }


                val userId = SpUtil.getString(this, ConstantValue.userId, "")
                val eMMessage = conversation.lastMessage
                val gson = Gson()
                val Message = Message()
                Message.msg = ""
                if(eMMessage != null)
                {
                    when (eMMessage.type) {
                        EMMessage.Type.LOCATION -> {
                        }
                        EMMessage.Type.IMAGE -> Message.msgType = 1
                        EMMessage.Type.VOICE -> Message.msgType = 2
                        EMMessage.Type.VIDEO -> Message.msgType = 4
                        EMMessage.Type.TXT -> {
                            Message.msgType = 0
                            Message.msg = (eMMessage.body as EMTextMessageBody).message
                        }
                        EMMessage.Type.FILE -> Message.msgType = 5
                        else -> {
                        }
                    }
                    Message.fileName = "abc"
                    Message.from = userId
                    Message.to = delMsgPushRsp.params.userId
                    Message.timeStatmp = System.currentTimeMillis()
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + delMsgPushRsp.params.userId, baseDataJson)
                }else{
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + delMsgPushRsp.params.userId, "")
                }
            }

        }

    }

    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (i in jPullFriendRsp.params.payload) {
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    j.nickName = i.name
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }

            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var isLocalFriendStatus = false
            for (j in localFriendStatusList) {
                if (i.id.equals(j.friendId) && j.userId.equals(userId)) {
                    isLocalFriendStatus = true
                    j.friendLocalStatus = 0
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriendStatus) {
                var friendEntity = FriendEntity()
                friendEntity.userId = userId
                friendEntity.friendId = i.id
                friendEntity.friendLocalStatus = 0
                friendEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(friendEntity)
            }
        }
        if (!ConstantValue.isRefeshed) {
            conversationListFragment?.refresh()
            ConstantValue.isRefeshed = true
        }
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(pushMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), userId!!,0, "")
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData,pushMsgRsp?.msgid))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData,pushMsgRsp?.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
//                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }

            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            val msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
            var message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
            if (msgSouce != null && msgSouce != "") {
                message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
            }
            message.setDirection(EMMessage.Direct.RECEIVE)

            message.msgId = "" + pushMsgRsp?.params.msgId
            message.from = pushMsgRsp.params.fromId
            message.to = pushMsgRsp.params.toId
            message.isUnread = true
            message.isAcked = true
            message.setStatus(EMMessage.Status.SUCCESS)
            if (conversation != null){
                var gson = Gson()
                var Message = Message()
                Message.setMsg(pushMsgRsp.getParams().getMsg())
                Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                Message.setFrom(pushMsgRsp.getParams().getFromId())
                Message.setTo(pushMsgRsp.getParams().getToId())
                var baseDataJson = gson.toJson(Message)
                var userId =   SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                SpUtil.putString(AppConfig.instance,ConstantValue.message+userId+"_"+pushMsgRsp.params.fromId,baseDataJson)
                KLog.i("insertMessage:" + "MainActivity"+"_pushMsgRsp")
                conversation.insertMessage(message)
            }
            if (ConstantValue.isInit) {
                runOnUiThread {
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                    controlleMessageUnReadCount(UnReadMessageCount)
                }
                conversationListFragment?.refresh()
                ConstantValue.isRefeshed = true
            }
        }
    }

    //别人删除我，服务器给我的推送
    override fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp) {

        /*var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (jDelFriendPushRsp.params.friendId.equals(i.userId) || jDelFriendPushRsp.params.userId.equals(i.userId) ) {
                i.friendStatus = 4
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
            }
        }*/
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jDelFriendPushRsp.params.friendId.equals(j.friendId) || jDelFriendPushRsp.params.userId.equals(j.friendId)) {
                    j.friendLocalStatus = 4
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                }
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
            }
        }

        var delFriendPushReq = DelFriendPushReq(0,userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendPushReq))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendPushReq)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

        EventBus.getDefault().post(FriendChange(jDelFriendPushRsp.params.friendId,jDelFriendPushRsp.params.userId))
    }

    /**
     * 目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友请求的结果
     */
    override fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId) && jAddFriendReplyRsp.params.userId.equals(j.friendId)) {
                if (jAddFriendReplyRsp.params.result == 0) {
                    j.friendLocalStatus = 0
                } else if (jAddFriendReplyRsp.params.result == 1) {
                    j.friendLocalStatus = 2
                }
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                break
            }
        }

        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            //jAddFriendReplyRsp.params.userId==对方的id
            if (i.userId.equals(jAddFriendReplyRsp.params.userId)) {
                /* if (jAddFriendReplyRsp.params.result == 0) {
                     i.friendStatus = 0
                 } else if (jAddFriendReplyRsp.params.result == 1) {
                     i.friendStatus = 2
                 }*/
                i.nickName = jAddFriendReplyRsp.params.nickname
                i.publicKey = jAddFriendReplyRsp.params.userKey
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendReplyReq = AddFriendReplyReq(0,userId!!, "")
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid))
                }else if (ConstantValue.isToxConnected) {
                    var baseData = BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
//                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                return
            }
        }

    }

    /**
     * 当一个用户A被其他用户B请求添加好友，router推送消息到A
     */
    override fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp) {
        var newFriend = UserEntity()

        /*var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (i.userId.equals(jAddFriendPushRsp.params.friendId)) {
                if (i.friendStatus == 0) {
                    return
                } else {
                    i.friendStatus = 3
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                    runOnUiThread {
                        viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                    }
                    return
                }
            }
        }*/
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId) && jAddFriendPushRsp.params.friendId.equals(j.friendId)) {
                if (j.friendLocalStatus == 0) {
                    return
                } else {
                    j.friendLocalStatus = 3
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    runOnUiThread {
                        viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                    }
                    return
                }
            }
        }
        newFriend.nickName = jAddFriendPushRsp.params.nickName
        //newFriend.friendStatus = 3
        newFriend.userId = jAddFriendPushRsp.params.friendId
        newFriend.addFromMe = false
        newFriend.timestamp = Calendar.getInstance().timeInMillis
        newFriend.noteName = ""
        newFriend.publicKey = jAddFriendPushRsp.params.userKey
        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
        newFriend.routerUserId = selfUserId
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)

        var newFriendStatus = FriendEntity()
        newFriendStatus.userId = userId;
        newFriendStatus.friendId = jAddFriendPushRsp.params.friendId
        newFriendStatus.friendLocalStatus = 3
        newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)

        var addFriendPushReq = AddFriendPushReq(0,userId!!, "")
        runOnUiThread {
            viewModel.freindChange.value = Calendar.getInstance().timeInMillis
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendPushReq,jAddFriendPushRsp.msgid))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(addFriendPushReq,jAddFriendPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
        }

    }

    private var exitTime: Long = 0
    lateinit var viewModel: MainViewModel
    private var conversationListFragment: EaseConversationListFragment? = null
    private var contactFragment: ContactFragment? = null
    private var contactListFragment: EaseContactListFragment? = null

    override fun showToast() {
        showProgressDialog()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息："+toxSendInfoEvent.info)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNikNameChange(editnickName : EditNickName) {
        contactFragment?.initData()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        if (friendChange.userId != null && !friendChange.userId.equals("")) {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendChange.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation != null) {
                conversation.clearAllMessages()
                if (ConstantValue.isInit) {
                    var count =  conversationListFragment?.removeFriend()
                    var UnReadMessageCount:UnReadMessageCount = UnReadMessageCount(count!!)
                    controlleMessageUnReadCount(UnReadMessageCount)
                    ConstantValue.isRefeshed = true
                }
            }
        }
        if (friendChange.firendId != null && !friendChange.firendId.equals("")) {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendChange.firendId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation != null) {
                conversation.clearAllMessages()
                if (ConstantValue.isInit) {
                    var count=    conversationListFragment?.removeFriend()
                    var UnReadMessageCount:UnReadMessageCount = UnReadMessageCount(count!!)
                    controlleMessageUnReadCount(UnReadMessageCount)
                    ConstantValue.isRefeshed = true
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: MainPresenter

    override fun setPresenter(presenter: MainContract.MainContractPresenter) {
        mPresenter = presenter as MainPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun initData() {
        try {
            AppConfig.instance.messageReceiver!!.mainInfoBack = this
        }catch (e : Exception) {
            e.printStackTrace()
        }
        if(!ConstantValue.mRegId.equals(""))
        {
            FileUtil.saveUserData2Local(ConstantValue.mRegId,"mRegId")
        }else{
            ConstantValue.mRegId  = FileUtil.getLocalUserData("mRegId")
        }
        Thread(Runnable() {
            run() {

                var map:HashMap<String, String>  =  HashMap()
                var os = VersionUtil.getDeviceBrand()
                map.put("os",os.toString())
                map.put("appversion","1.0.1")
                map.put("regid",ConstantValue.mRegId)
                map.put("topicid","")
                var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
                map.put("routerid",ConstantValue.currentRouterId)
                map.put("userid",selfUserId!!)
                var lastLoginUserSn = FileUtil.getLocalUserData("usersn")
                map.put("usersn",lastLoginUserSn)
                LogUtil.addLog("小米推送注册regid:"+ConstantValue.mRegId,"MainActivity")
                OkHttpUtils.getInstance().doPost(ConstantValue.pushURL, map,  object : OkHttpUtils.OkCallback {
                    override fun onFailure( e :Exception) {
                        LogUtil.addLog("小米推送注册失败:","MainActivity")
                    }

                    override fun  onResponse(json:String ) {
                        LogUtil.addLog("小米推送注册成功:","MainActivity")
                        //Toast.makeText(AppConfig.instance,"成功",Toast.LENGTH_SHORT).show()
                    }
                });
            }
        }).start()
        MessageProvider.getInstance().messageListenter = this
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
        tvTitle.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.toAddUserId.observe(this, android.arch.lifecycle.Observer<String> { toAddUserId ->
            KLog.i(toAddUserId)
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            if (toAddUserId.equals(selfUserId)) {
                return@Observer
            }
            if (!"".equals(toAddUserId)) {
                var toAddUserIdTemp = toAddUserId!!.substring(0,toAddUserId!!.indexOf(","))
                var intent = Intent(this, SendAddFriendActivity::class.java)
                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                for (i in useEntityList) {
                    if (i.userId.equals(toAddUserIdTemp)) {
                        var freindStatusData = FriendEntity()
                        freindStatusData.friendLocalStatus = 7
                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
                        if (localFriendStatusList.size > 0)
                            freindStatusData = localFriendStatusList[0]

                        if(freindStatusData.friendLocalStatus == 0)
                        {
                            intent.putExtra("user", i)
                            startActivity(intent)
                        }else{
                            intent = Intent(this, SendAddFriendActivity::class.java)
                            intent.putExtra("user", i)
                            startActivity(intent)
                        }

                        return@Observer
                    }
                }
                intent = Intent(this, SendAddFriendActivity::class.java)
                var userEntity = UserEntity()
                //userEntity.friendStatus = 7
                userEntity.userId = toAddUserId!!.substring(0,toAddUserId!!.indexOf(","))
                userEntity.nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") +1,toAddUserId.length)
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)


                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var newFriendStatus = FriendEntity()
                newFriendStatus.userId = userId;
                newFriendStatus.friendId = toAddUserId
                newFriendStatus.friendLocalStatus = 7
                newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
                intent.putExtra("user", userEntity)
                startActivity(intent)
            }
        })
        setToNews()
        ivQrCode.setOnClickListener {
            mPresenter.getScanPermission()
        }
        if (!ConstantValue.isInit) {
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            var pullFriend = PullFriendReq(selfUserId!!)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullFriend))
            }else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(pullFriend)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
//                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }

            ConstantValue.isInit = true
        }

//        SpUtil.putString(this, ConstantValue.userId, "271D61D2976D9A06A7F07274D5198EB511C8A334ACC07844868A9C260233F15E80D50696CC76")
//        bottomNavigation.enableAnimation(false)
//        bottomNavigation.enableShiftingMode(false)
//        bottomNavigation.enableItemShiftingMode(false)
//        bottomNavigation.setTextSize(10F)
//        viewPager.offscreenPageLimit = 2
//        bottomNavigation.setIconSizeAt(0, 17.6f, 21.2f)
//        bottomNavigation.setIconSizeAt(1, 23.6f, 18.8f)
//        bottomNavigation.setIconSizeAt(2, 22F, 18.8f)
//        bottomNavigation.setIconsMarginTop(resources.getDimension(R.dimen.x22).toInt())
//        bottomNavigation.selectedItemId = R.id.item_news
        contactListFragment?.setContactsMap(getContacts())
        conversationListFragment?.setConversationListItemClickListener(
                EaseConversationListFragment.EaseConversationListItemClickListener
                { userid -> startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userid)) })
        contactListFragment?.setContactListItemClickListener(EaseContactListFragment.EaseContactListItemClickListener { user -> startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, user.username)) })
        if (AppConfig.instance.tempPushMsgList.size != 0) {
            Thread(Runnable() {
                run() {
                    Thread.sleep(1000);
                    for (pushMsgRsp in AppConfig.instance.tempPushMsgList) {
                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId),userId!!, 0, "")
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData,pushMsgRsp?.msgid))
                        }else if (ConstantValue.isToxConnected) {
                            var baseData = BaseData(msgData,pushMsgRsp?.msgid)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }
                        var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
                        val msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
                        val message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
                        message.setDirection(EMMessage.Direct.RECEIVE)
                        message.msgId = pushMsgRsp?.params.msgId.toString()
                        message.from = pushMsgRsp.params.fromId
                        message.to = pushMsgRsp.params.toId
                        message.isUnread = true
                        message.isAcked = true
                        message.setStatus(EMMessage.Status.SUCCESS)
                        if(conversation !=null)
                        {
                            var gson = Gson()
                            var Message = Message()
                            Message.setMsg(pushMsgRsp.getParams().getMsg())
                            Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                            Message.setFrom(pushMsgRsp.getParams().getFromId())
                            Message.setTo(pushMsgRsp.getParams().getToId())
                            var baseDataJson = gson.toJson(Message)
                            var userId =   SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                            SpUtil.putString(AppConfig.instance,ConstantValue.message+userId+"_"+pushMsgRsp.params.fromId,baseDataJson)
                            KLog.i("insertMessage:" + "MainActivity"+"_tempPushMsgList")
                            conversation.insertMessage(message)
                        }

                        if (ConstantValue.isInit) {
                            conversationListFragment?.refresh()
                            ConstantValue.isRefeshed = true
                        }
                    }
                    AppConfig.instance.tempPushMsgList = ArrayList<JPushMsgRsp>()
                }
            }).start()


        }
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return conversationListFragment!!
                    1 -> return FileFragment()
                    2 -> return contactFragment!!
                    else -> return MyFragment()
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        // 为ViewPager添加页面改变事件
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                // 将当前的页面对应的底部标签设为选中状态
//                bottomNavigation.getMenu().getItem(position).setChecked(true)
                when (position) {
                    0 -> setToNews()
                    1 -> setToFile()
                    2 -> setToContact()
                    3 -> setToMy()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        alphaIndicator.setViewPager(viewPager)
        // 为bnv设置选择监听事件
//        bottomNavigation.setOnNavigationItemSelectedListener {
//            when (it.getItemId()) {
//                R.id.item_news -> viewPager.setCurrentItem(0, false)
//                R.id.item_file -> viewPager.setCurrentItem(1, false)
//                R.id.item_contacts -> viewPager.setCurrentItem(2, false)
//                R.id.item_my -> viewPager.setCurrentItem(3, false)
//            }
//            true
//        }
//        tv_hello.text = "hahhaha"
//        tv_hello.setOnClickListener {
//            mPresenter.showToast()
//            startActivity(Intent(this, TestActivity::class.java))
//        }
//        tv_hello.typeface.style
        viewPager.offscreenPageLimit = 4

    }

    override fun onResume() {
        exitTime = System.currentTimeMillis() - 2001
        super.onResume()
        var localFriendList: List<UserEntity> = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var hasUnReadMsg: Boolean = false;
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        /* for (friendData in localFriendList) {
             var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendData.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
             if (conversation != null) {
                 val msgs: List<EMMessage> = conversation.allMessages
                 for (msg in msgs) {
                     if (msg.isUnread && !userId.equals(msg.from)) {
                         hasUnReadMsg = true
                     }
                 }
             }
         }*/
        val keyMap = SpUtil.getAll(AppConfig.instance)
        var hasLinShi = ""
        for (key in keyMap.keys) {

            if (key.contains(ConstantValue.message) && key.contains(userId!! + "_")) {
                val toChatUserId = key.substring(key.lastIndexOf("_") + 1, key.length)
                if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null") {
                    val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list()
                    if (localFriendList.size == 0) {
                        continue
                    }
                    val cachStr = SpUtil.getString(AppConfig.instance, key, "")
                    if ("" != cachStr) {
                        val gson = GsonUtil.getIntGson()
                        val Message = gson.fromJson(cachStr, Message::class.java)
                        if (Message != null) {
                            hasLinShi += toChatUserId +"#"
                        }
                    }
                }
            }
        }
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(j.friendId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
                if (conversation != null) {
                    val msgs: List<EMMessage> = conversation.allMessages
                    for (msg in msgs) {
                        if (msg.isUnread && !userId.equals(msg.from) && hasLinShi.contains(msg.from)) {
                            hasUnReadMsg = true
                        }
                    }
                }
            }
        }
        if (hasUnReadMsg) {
            var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
            controlleMessageUnReadCount(UnReadMessageCount)
        } else {
            var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
            controlleMessageUnReadCount(UnReadMessageCount)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlleContactUnReadCount(unReadContactCount: UnReadContactCount) {
        if (unReadContactCount.messageCount == 0) {
            new_contact.visibility = View.INVISIBLE
            new_contact.text = ""
        } else {
            new_contact.visibility = View.VISIBLE
            //new_contact.text = "" + unReadContactCount.messageCount
            new_contact.text = ""
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlleMessageUnReadCount(unReadMessageCount: UnReadMessageCount) {
        if(unread_count!= null)
        {
            if (unReadMessageCount.messageCount == 0) {
                unread_count.visibility = View.INVISIBLE
                unread_count.text = ""
            } else {
                unread_count.visibility = View.VISIBLE
                unread_count.text = ""
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                reConnect.visibility = View.GONE
            }
            1 -> {

            }
            2 -> {
                reConnect.visibility = View.VISIBLE
            }
            3 -> {
                reConnect.visibility = View.VISIBLE
            }
        }
    }
    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                closeProgressDialog()
                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if(isCanShotNetCoonect)
                {
                    closeProgressDialog()
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if(isCanShotNetCoonect)
                {
                    closeProgressDialog()
                    showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

//    fun getEaseConversationListFragment(): EaseConversationListFragment? {
//        return conversationListFragment
//    }

    fun setToNews() {
        tvTitle.text = getString(R.string.private_faith)
        mainIv1.visibility = View.GONE
        llSort.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
    }

    fun setToFile() {
        tvTitle.text = getString(R.string.file_)
        mainIv1.visibility = View.VISIBLE
        ivQrCode.visibility = View.VISIBLE
        llSort.visibility = View.VISIBLE
    }

    fun setToContact() {
        tvTitle.text = getString(R.string.contacts)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
        llSort.visibility = View.GONE
        //contactFragment?.updata()
    }

    fun setToMy() {
        tvTitle.text = getString(R.string.my)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.GONE
        llSort.visibility = View.GONE
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        tvTitle.text = getString(R.string.news)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        contactListFragment = EaseContactListFragment()
        contactListFragment?.hideTitleBar()
        contactFragment  = ContactFragment()
    }

    override fun setupActivityComponent() {
        DaggerMainComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result")
            if(!result.contains(","))
            {
                toast(getString(R.string.codeerror))
                return;
            }
            viewModel.toAddUserId.value = data!!.getStringExtra("result")
            return
        }
    }

    private fun getContacts(): Map<String, EaseUser> {
        val contacts = HashMap<String, EaseUser>()
        val aa = arrayOf("aa", "cc", "ff", "gg", "kk", "ll", "bb", "jj", "oo", "zz", "mm")
        for (i in 1..10) {
            val user = EaseUser(aa[i])
            contacts[aa[i]] = user
        }
        return contacts
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitToast()
        }
        return false
    }

    fun exitToast(): Boolean {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.Press_again, Toast.LENGTH_SHORT)
                    .show()
            exitTime = System.currentTimeMillis()
        } else {
            AppConfig.instance.stopAllService()
            //android进程完美退出方法。
            var intent = Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
            this.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.runFinalizersOnExit(true);
            System.exit(0);
        }
        return false
    }
    internal var handler: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x12 -> {

                }
                0x16 -> {
                }
            }//goMain();
            //goMain();
        }
    }
}
