package com.stratagile.pnrouter.ui.activity.main

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.BADGE_ICON_SMALL
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.NotificationCompat
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.huawei.android.hms.agent.HMSAgent
import com.huawei.android.hms.agent.common.handler.ConnectHandler
import com.hyphenate.chat.*
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.message.Message
import com.message.MessageProvider
import com.pawegio.kandroid.notificationManager
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.service.BackGroundService
import com.stratagile.pnrouter.data.service.FileDownloadUploadService
import com.stratagile.pnrouter.data.service.FileTransformService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.reciver.WinqMessageReceiver
import com.stratagile.pnrouter.ui.activity.add.addFriendOrGroupActivity
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.group.CreateGroupActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.login.VerifyingFingerprintActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.activity.user.QRCodeActivity
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ActiveTogglePopWindow
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import com.tencent.bugly.crashreport.CrashReport
import com.xiaomi.mipush.sdk.MiPushClient
import events.ToxFriendStatusEvent
import events.ToxSendFileFinishedEvent
import events.ToxSendInfoEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.activity_main.*
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View, PNRouterServiceMessageReceiver.MainInfoBack, MessageProvider.MessageListener, ActiveTogglePopWindow.OnItemClickListener {
    override fun groupListPull(jGroupListPullRsp: JGroupListPullRsp) {
        when (jGroupListPullRsp.params.retCode) {
            0 -> {
                AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.deleteAll()
                for (item in jGroupListPullRsp.params.payload) {
                    var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(item.gId)).list()
                    if (groupList.size > 0) {
                        var GroupLocal = groupList.get(0)
                        GroupLocal.userKey = item.userKey
                        GroupLocal.remark = item.remark
                        GroupLocal.gId = item.gId
                        GroupLocal.gAdmin = item.gAdmin
                        GroupLocal.gName = item.gName
                        GroupLocal.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.update(GroupLocal);
                    } else {
                        item.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.insert(item);
                    }

                }
            }
        }
    }

    override fun droupSysPushRsp(jGroupSysPushRsp: JGroupSysPushRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(jGroupSysPushRsp.params.gId)) {
            KLog.i("已经在群聊天窗口了，不处理该条数据！")
        } else {
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

            when (jGroupSysPushRsp.params.type) {

                1 -> {

                }
                2 -> {

                }
                3, 4 -> {
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    val keyMap = SpUtil.getAll(AppConfig.instance)
                    for (key in keyMap.keys) {

                        if (key.contains(ConstantValue.message) && key.contains(userId!! + "_")) {
                            val tempkey = key.replace(ConstantValue.message, "")
                            val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                            if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null" && toChatUserId.equals(jGroupSysPushRsp.params.gId)) {
                                val cachStr = SpUtil.getString(AppConfig.instance, key, "")
                                if ("" != cachStr) {
                                    val gson = GsonUtil.getIntGson()
                                    val MessageLocal = gson.fromJson(cachStr, Message::class.java)
                                    if (MessageLocal.from.equals(jGroupSysPushRsp.params.from)) {
                                        var gson = Gson()
                                        var Message = Message()
                                        Message.setMsg(resources.getString(R.string.withdrawn))
                                        Message.setMsgId(jGroupSysPushRsp.getParams().getMsgId())
                                        Message.setFrom(jGroupSysPushRsp.getParams().from)
                                        Message.setTo(jGroupSysPushRsp.getParams().gId)
                                        Message.msgType = 0
                                        Message.sender = 1
                                        Message.status = 2
                                        Message.timeStamp = jGroupSysPushRsp?.timestamp
                                        Message.msgId = jGroupSysPushRsp?.params.msgId
                                        Message.chatType = EMMessage.ChatType.GroupChat
                                        var unReadCount = MessageLocal.unReadCount
                                        if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                            unReadCount = MessageLocal.unReadCount
                                        }
                                        if (unReadCount > 0) {
                                            Message.unReadCount = unReadCount - 1;
                                        } else {
                                            Message.unReadCount = 0;
                                        }

                                        var baseDataJson = gson.toJson(Message)
                                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                        SpUtil.putString(AppConfig.instance, key, baseDataJson)
                                        if (ConstantValue.isInit) {
                                            runOnUiThread {
                                                var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
                                                if (Message.unReadCount > 0) {
                                                    UnReadMessageCount = UnReadMessageCount(1)
                                                }
                                                controlleMessageUnReadCount(UnReadMessageCount)
                                            }
                                            conversationListFragment?.refresh()
                                            ConstantValue.isRefeshed = true
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                241 -> {

                }
                242 -> {

                }
                243 -> {//有人被移除群
                    if (jGroupSysPushRsp.params.to.equals(userId))//如果是自己
                    {
                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var verifyList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.queryBuilder().where(GroupVerifyEntityDao.Properties.Aduit.eq(selfUserId)).list()
                        var hasVerify = false
                        verifyList.forEach {
                            if (it.gId.equals(jGroupSysPushRsp.params.gId) && it.userId.equals(selfUserId) && it.verifyType == 3) {
                                //存在
                                hasVerify = true
                                it.userId = selfUserId
                                it.verifyType = 3
                                AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.update(it)
                            }
                        }
                        if (!hasVerify) {
                            var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(jGroupSysPushRsp.params.gId)).list()
                            var groupVerifyEntity = GroupVerifyEntity()
                            groupVerifyEntity.verifyType = 3
                            groupVerifyEntity.from = jGroupSysPushRsp.params.from
                            groupVerifyEntity.gId = jGroupSysPushRsp.params.gId
                            groupVerifyEntity.userId = selfUserId
                            if (groupList != null && groupList.size > 0) {
                                groupVerifyEntity.gname = groupList[0].gName
                            }
                            groupVerifyEntity.fromUserName = jGroupSysPushRsp.params.fromUserName
                            AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.insert(groupVerifyEntity)
                        }

                        //需要细化处理 ，弹窗告知详情等
                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jGroupSysPushRsp.params.gId, "");//移除临时会话UI
                        if (ConstantValue.isInit) {
                            runOnUiThread {
                                conversationListFragment?.refresh()
                            }
                            ConstantValue.isRefeshed = true
                        }
                    } else {//是别人

                    }
                }

            }
        }
    }

    override fun uploadAvatarReq(jUploadAvatarRsp: JUploadAvatarRsp) {
        when (jUploadAvatarRsp.params.retCode) {
            0 -> {
                runOnUiThread {
                    //toast(getString(R.string.Avatar_Update_Successful))
                }
                var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
                var filePath = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + "__Avatar.jpg"
                var files_dir = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
                FileUtil.copySdcardFile(filePath, files_dir)
                AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, files_dir, System.currentTimeMillis())
            }
            1 -> {
                runOnUiThread {
                    //toast(getString(R.string.User_ID_error))
                }
            }
            2 -> {
                runOnUiThread {
                    //toast(getString(R.string.file_error))
                }
            }
            3 -> {
                runOnUiThread {
                    toast(getString(R.string.file_hasnot_changed))
                }
            }
            else -> {
                runOnUiThread {
                    //toast(getString(R.string.Other_mistakes))
                }
            }
        }
    }

    override fun pushLogoutRsp(jPushLogoutRsp: JPushLogoutRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = PushLogoutRsp(0, userId!!, "")
        var msgId: String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jPushLogoutRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jPushLogoutRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        if (jPushLogoutRsp.params.reason == 1) {
            runOnUiThread {
                toast(R.string.Other_devices)
                ConstantValue.isHasWebsocketInit = true
                if (AppConfig.instance.messageReceiver != null)
                    AppConfig.instance.messageReceiver!!.close()

                ConstantValue.loginOut = true
                ConstantValue.logining = false
                ConstantValue.isHeart = false
                ConstantValue.currentRouterIp = ""
                resetUnCompleteFileRecode()
                if (ConstantValue.isWebsocketConnected) {
                    FileMangerDownloadUtils.init()
                    ConstantValue.webSockeFileMangertList.forEach {
                        it.disconnect(true)
                        //ConstantValue.webSockeFileMangertList.remove(it)
                    }
                    ConstantValue.webSocketFileList.forEach {
                        it.disconnect(true)
                        //ConstantValue.webSocketFileList.remove(it)
                    }
                } else {
                    val intentTox = Intent(this, KotlinToxService::class.java)
                    this.stopService(intentTox)
                }
                ConstantValue.isWebsocketConnected = false
                onLogOutSuccess()
            }
        } else {
            runOnUiThread {
                toast(R.string.System_Upgrade)

            }
        }
        runOnUiThread {
            ConstantValue.isHasWebsocketInit = true
            if (AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.close()

            ConstantValue.loginOut = true
            ConstantValue.logining = false
            ConstantValue.isHeart = false
            ConstantValue.currentRouterIp = ""
            resetUnCompleteFileRecode()
            if (ConstantValue.isWebsocketConnected) {
                FileMangerDownloadUtils.init()
                ConstantValue.webSockeFileMangertList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSockeFileMangertList.remove(it)
                }
                ConstantValue.webSocketFileList.forEach {
                    it.disconnect(true)
                    //ConstantValue.webSocketFileList.remove(it)
                }
            } else {
                val intentTox = Intent(this, KotlinToxService::class.java)
                this.stopService(intentTox)
            }
            ConstantValue.isWebsocketConnected = false
            onLogOutSuccess()
        }
    }

    fun onLogOutSuccess() {
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(this, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }

    override fun readMsgPushRsp(jReadMsgPushRsp: JReadMsgPushRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = ReadMsgPushReq(0, "", userId!!)
        var msgId: String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jReadMsgPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jReadMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    override fun OnlineStatusPush(jOnlineStatusPushRsp: JOnlineStatusPushRsp) {

    }

    var SELECT_PHOTO = 2
    var SELECT_VIDEO = 3
    var SELECT_DEOCUMENT = 4
    var create_group = 5
    var add_activity = 6
    var isSendRegId = true
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
        var msgData = UserInfoPushRsp(0, userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jUserInfoPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jUserInfoPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber = toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        val toxFileData = ConstantValue.sendToxFileDataMap[fileNumber.toString() + ""]
        if (toxFileData != null) {//点对点聊天
            if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(toxFileData.toId)) {
                KLog.i("已经在聊天窗口了，不处理该条数据！")
            } else {
                val sendToxFileNotice = SendToxFileNotice(toxFileData.fromId, toxFileData.toId, toxFileData.fileName, toxFileData.fileMD5, toxFileData.widthAndHeight, toxFileData.fileSize, toxFileData.fileType.value(), toxFileData.fileId, toxFileData.srcKey, toxFileData.dstKey, "SendFile")
                val baseData = BaseData(sendToxFileNotice)
                val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                if (ConstantValue.isAntox) {
                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        val toxFileGroupChatData = ConstantValue.sendToxFileInGroupChapDataMap[fileNumber.toString() + ""]
        if (toxFileGroupChatData != null)//群聊
        {
            if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(toxFileGroupChatData.toId)) {
                KLog.i("已经在聊天窗口了，不处理该条数据！")
            } else {
                val fileBase58Name = toxFileGroupChatData.getFileName()
                val fileMD5 = FileUtil.getFileMD5(File(toxFileGroupChatData.getFilePath()))
                var fileInfo = ""
                if (toxFileGroupChatData.getFileType().value() == 1) {
                    if (toxFileGroupChatData.getWidthAndHeight() != null && !toxFileGroupChatData.getWidthAndHeight().equals("")) {
                        fileInfo = toxFileGroupChatData.getWidthAndHeight()
                    } else {
                        fileInfo = "200.0000000*200.0000000"
                    }

                } else if (toxFileGroupChatData.getFileType().value() == 4) {
                    fileInfo = "200.0000000*200.0000000"
                }
                val size = toxFileGroupChatData.getFileSize()
                val groupSendFileDone = GroupSendFileDoneReq(toxFileGroupChatData.getFromId(), toxFileGroupChatData.getToId(), fileBase58Name, fileMD5!!, fileInfo, size, toxFileGroupChatData.getFileType().value(), toxFileGroupChatData.getFileId().toString() + "", "GroupSendFileDone")
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupSendFileDone))
                } else if (ConstantValue.isToxConnected) {
                    val baseData = BaseData(4, groupSendFileDone)
                    val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }

    }

    override fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(jPushFileMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgDataPushFileRsp = PushFileRespone(0, jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId, jPushFileMsgRsp.params.msgId)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgDataPushFileRsp, jPushFileMsgRsp.msgid))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgDataPushFileRsp, jPushFileMsgRsp.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            val gson = Gson()
            val Message = Message()
            Message.msg = ""
            Message.msgId = jPushFileMsgRsp.params.msgId
            Message.from = jPushFileMsgRsp.params.fromId
            Message.to = jPushFileMsgRsp.params.toId
            Message.msgType = jPushFileMsgRsp.params.fileType
            Message.sender = 1
            Message.status = 1
            Message.chatType = EMMessage.ChatType.Chat
            Message.fileName = jPushFileMsgRsp.params.fileName
            Message.timeStamp = jPushFileMsgRsp.timestamp

            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, "")
            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
            var unReadCount = 0
            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                unReadCount = MessageLocal.unReadCount
            }
            Message.unReadCount = unReadCount + 1;

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
            var msgData = DelMsgRsp(0, "", delMsgPushRsp.params.friendId)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData, delMsgPushRsp.msgid))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData, delMsgPushRsp.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            val keyMap = SpUtil.getAll(AppConfig.instance)
            for (key in keyMap.keys) {

                if (key.contains(ConstantValue.message) && key.contains(userId!! + "_")) {
                    val tempkey = key.replace(ConstantValue.message, "")
                    val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                    if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null") {
                        val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list()
                        if (localFriendList.size == 0) {
                            continue
                        }
                        val cachStr = SpUtil.getString(AppConfig.instance, key, "")
                        if ("" != cachStr) {
                            val gson = GsonUtil.getIntGson()
                            val MessageLocal = gson.fromJson(cachStr, Message::class.java)
                            if (MessageLocal.from.equals(delMsgPushRsp.params.userId)) {
                                var gson = Gson()
                                var Message = Message()
                                Message.setMsg(resources.getString(R.string.withdrawn))
                                Message.setMsgId(delMsgPushRsp.getParams().getMsgId())
                                Message.setFrom(delMsgPushRsp.getParams().userId)
                                Message.setTo(delMsgPushRsp.getParams().friendId)
                                Message.msgType = 0
                                Message.sender = 1
                                Message.status = 2
                                Message.timeStamp = delMsgPushRsp?.timestamp
                                Message.msgId = delMsgPushRsp?.params.msgId
                                Message.chatType = EMMessage.ChatType.Chat
                                var unReadCount = MessageLocal.unReadCount
                                if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                    unReadCount = MessageLocal.unReadCount
                                }
                                if (unReadCount > 0) {
                                    Message.unReadCount = unReadCount - 1;
                                } else {
                                    Message.unReadCount = 0;
                                }

                                var baseDataJson = gson.toJson(Message)
                                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                SpUtil.putString(AppConfig.instance, key, baseDataJson)
                                if (ConstantValue.isInit) {
                                    runOnUiThread {
                                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
                                        if (Message.unReadCount > 0) {
                                            UnReadMessageCount = UnReadMessageCount(1)
                                        }
                                        controlleMessageUnReadCount(UnReadMessageCount)
                                    }
                                    conversationListFragment?.refresh()
                                    ConstantValue.isRefeshed = true
                                }
                                break
                            }
                        }
                    }
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


    /**
     * 播放系统默认提示音
     * 铃声的实现方式
     * @return MediaPlayer对象
     *
     * @throws Exception
     */
    fun defaultMediaPlayer() {
        var notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var r = RingtoneManager.getRingtone(this, notification)
        r.play()
    }

    /**
     * 音乐的实现方式
     */
    fun defaultMedia() {
        KLog.i("播放通知声音")
        var notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var bgmediaPlayer = MediaPlayer.create(this, notification)
        bgmediaPlayer.start()
        bgmediaPlayer.setVolume(0.7f, 0.7f)
    }

    fun defaultNotification() {
        KLog.i("播放通知声音")
        var mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder: NotificationCompat.Builder? = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            var channel = NotificationChannel("通知渠道ID", "通知渠道名称", NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true); //设置开启指示灯，如果设备有的话

            channel.setLightColor(Color.RED); //设置指示灯颜色

            channel.setShowBadge(true); //设置是否显示角标

            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知

//            channel.setDescription("通知渠道描述");//设置渠道描述


            channel.setBypassDnd(true);//设置是否绕过免打扰模式

            mNotificationManager.createNotificationChannel(channel);

//            createNotificationChannelGroups();
//
//            setNotificationChannelGroups(channel);

            builder = NotificationCompat.Builder(this, "通知渠道ID");

            builder.setBadgeIconType(BADGE_ICON_SMALL);//设置显示角标的样式

            builder.setNumber(3);//设置显示角标的数量

            builder.setTimeoutAfter(0);//设置通知被创建多长时间之后自动取消通知栏的通知。

        } else {

            builder = NotificationCompat.Builder(this);

        }

//setContentTitle 通知栏通知的标题

//        builder.setContentTitle("内容标题");

//setContentText 通知栏通知的详细内容

//        builder.setContentText("内容文本信息");

//setAutoCancel 点击通知的清除按钮是否清除该消息（true/false）

        builder.setAutoCancel(true);

//setLargeIcon 通知消息上的大图标

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

//setSmallIcon 通知上面的小图标

        builder.setSmallIcon(R.mipmap.ic_launcher);//小图标

//创建一个意图

        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"));

        var pIntent = PendingIntent.getActivity(this, 1, intent, 0);

//setContentIntent 将意图设置到通知上

        builder.setContentIntent(pIntent);

//通知默认的声音 震动 呼吸灯

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

//构建通知

        var notification = builder.build();

//将构建好的通知添加到通知管理器中，执行通知

        mNotificationManager.notify(0, notification);

        ivQrCode.postDelayed({ mNotificationManager.cancel(0) }, 50)
    }

    override fun pushGroupMsgRsp(pushMsgRsp: JGroupMsgPushRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(pushMsgRsp.params.gId)) {
            KLog.i("已经在群聊天窗口了，不处理该条数据！")
        } else {
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if (pushMsgRsp.params.from != userId) {
                val userList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(pushMsgRsp.params.from)).list()
                if (userList.size == 0)   //群聊非好友成员数据
                {
                    val UserEntityLocal = UserEntity()
                    UserEntityLocal.nickName = pushMsgRsp.params.userName
                    UserEntityLocal.userId = pushMsgRsp.params.from
                    UserEntityLocal.index = ""
                    UserEntityLocal.signPublicKey = pushMsgRsp.params.userKey
                    UserEntityLocal.routeId = ""
                    UserEntityLocal.routeName = ""
                    val dst_public_MiKey_Friend = ByteArray(32)
                    val crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(pushMsgRsp.params.userKey))
                    if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                        UserEntityLocal.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                    }
                    UserEntityLocal.remarks = ""
                    UserEntityLocal.timestamp = Calendar.getInstance().timeInMillis
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(UserEntityLocal)
                }
                val friendList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(pushMsgRsp.params.from)).list()
                if (friendList.size == 0) {
                    //判断非好友头像是否需要更新
                    var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(pushMsgRsp.params.userKey))
                    var filePath = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
                    var fileMD5 = FileUtil.getFileMD5(File(filePath))
                    if (fileMD5 == null) {
                        fileMD5 = ""
                    }
                    val updateAvatarReq = UpdateAvatarReq(userId!!, pushMsgRsp.params.from, fileMD5)
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
                }
            }
            var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(pushMsgRsp.params.gId)).list()
            if (groupList.size > 0) {
                var GroupLocal = groupList.get(0)
                GroupLocal.userKey = pushMsgRsp.params.selfKey
                GroupLocal.remark = ""
                GroupLocal.gId = pushMsgRsp.params.gId
                GroupLocal.gAdmin = pushMsgRsp.params.gAdmin
                GroupLocal.gName = pushMsgRsp.params.groupName
                GroupLocal.routerId = ConstantValue.currentRouterId
                AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.update(GroupLocal);
            } else {
                var GroupLocal = GroupEntity()
                GroupLocal.userKey = pushMsgRsp.params.selfKey
                GroupLocal.remark = ""
                GroupLocal.gId = pushMsgRsp.params.gId
                GroupLocal.gAdmin = pushMsgRsp.params.gAdmin
                GroupLocal.gName = pushMsgRsp.params.groupName
                GroupLocal.routerId = ConstantValue.currentRouterId
                AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.insert(GroupLocal);
            }
            if (!AppConfig.instance.isBackGroud) {
                defaultMediaPlayer()
            }

            var msgData = GroupMsgPushRsp(0, userId!!, pushMsgRsp.params.gId, "")

            var sendData = BaseData(msgData, pushMsgRsp?.msgid)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(4, msgData, pushMsgRsp?.msgid)
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            } else if (ConstantValue.isToxConnected) {
                var baseData = sendData
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }

            when (pushMsgRsp.params.msgType) {
                0 -> {
                    val aesKey = LibsodiumUtil.DecryptShareKey(pushMsgRsp.params.selfKey)
                    val base64Scoure = RxEncodeTool.base64Decode(pushMsgRsp.getParams().getMsg())
                    var msgSouce: String? = ""
                    try {
                        msgSouce = String(AESCipher.aesDecryptBytes(base64Scoure, aesKey.toByteArray()))
                        var message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.from)
                        if (msgSouce != null && msgSouce != "") {
                            message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.from)
                        }
                        message.setDirection(EMMessage.Direct.RECEIVE)
                        message.msgId = "" + pushMsgRsp?.params.msgId
                        message.from = pushMsgRsp.params.from
                        message.to = pushMsgRsp.params.gId
                        message.isUnread = true
                        message.isAcked = true
                        message.setStatus(EMMessage.Status.SUCCESS)
                        //if (conversation != null){
                        var gson = Gson()
                        var Message = Message()
                        Message.setMsg(msgSouce)
                        Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                        Message.setFrom(pushMsgRsp.getParams().from)
                        Message.setTo(pushMsgRsp.getParams().gId)
                        Message.msgType = 0
                        Message.sender = 1
                        Message.status = 1
                        Message.timeStamp = pushMsgRsp?.timestamp
                        Message.chatType = EMMessage.ChatType.GroupChat
                        Message.msgId = pushMsgRsp?.params.msgId
                        var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, "")
                        val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                        var unReadCount = 0
                        if (MessageLocal != null && MessageLocal.unReadCount != null) {
                            unReadCount = MessageLocal.unReadCount
                        }
                        Message.unReadCount = unReadCount + 1;


                        var baseDataJson = gson.toJson(Message)
                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, baseDataJson)
                        KLog.i("insertMessage:" + "MainActivity" + "_pushMsgRsp")
                        //conversation.insertMessage(message)
                        //}
                        if (ConstantValue.isInit) {
                            runOnUiThread {
                                var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                                controlleMessageUnReadCount(UnReadMessageCount)
                            }
                            conversationListFragment?.refresh()
                            ConstantValue.isRefeshed = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    val gson = Gson()
                    val Message = Message()
                    Message.msg = ""
                    Message.msgId = pushMsgRsp.params.msgId
                    Message.from = pushMsgRsp.params.from
                    Message.to = pushMsgRsp.params.gId
                    Message.msgType = pushMsgRsp.params.msgType
                    Message.sender = 1
                    Message.status = 1
                    Message.chatType = EMMessage.ChatType.GroupChat
                    Message.fileName = pushMsgRsp.params.fileName
                    Message.timeStamp = pushMsgRsp.timestamp

                    var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, "")
                    val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                    var unReadCount = 0
                    if (MessageLocal != null && MessageLocal.unReadCount != null) {
                        unReadCount = MessageLocal.unReadCount
                    }
                    Message.unReadCount = unReadCount + 1;

                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, baseDataJson)
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

        }
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(pushMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            if (!AppConfig.instance.isBackGroud) {
                defaultMediaPlayer()
            }
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), userId!!, 0, "")

            var sendData = BaseData(msgData, pushMsgRsp?.msgid)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(3, msgData, pushMsgRsp?.msgid)
            }
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

            //var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            var msgSouce = "";

            if (ConstantValue.encryptionType.equals("1")) {
                msgSouce = LibsodiumUtil.DecryptFriendMsg(pushMsgRsp.getParams().getMsg(), pushMsgRsp.getParams().getNonce(), pushMsgRsp.getParams().getFrom(), pushMsgRsp.getParams().getSign())
            } else {
                msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
            }
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
            //if (conversation != null){
            var gson = Gson()
            var Message = Message()
            Message.setMsg(msgSouce)
            Message.setMsgId(pushMsgRsp.getParams().getMsgId())
            Message.setFrom(pushMsgRsp.getParams().getFromId())
            Message.setTo(pushMsgRsp.getParams().getToId())
            Message.msgType = 0
            Message.sender = 1
            Message.status = 1
            Message.chatType = EMMessage.ChatType.Chat
            Message.timeStamp = pushMsgRsp?.timestamp
            Message.msgId = pushMsgRsp?.params.msgId


            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, "")
            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
            var unReadCount = 0
            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                unReadCount = MessageLocal.unReadCount
            }
            Message.unReadCount = unReadCount + 1;


            var baseDataJson = gson.toJson(Message)
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, baseDataJson)
            KLog.i("insertMessage:" + "MainActivity" + "_pushMsgRsp")
            //conversation.insertMessage(message)
            //}
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

        var delFriendPushReq = DelFriendPushReq(0, userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendPushReq))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendPushReq)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        EventBus.getDefault().post(FriendChange(jDelFriendPushRsp.params.friendId, jDelFriendPushRsp.params.userId))
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
                i.signPublicKey = jAddFriendReplyRsp.params.userKey
                i.routeId = jAddFriendReplyRsp.params.routeId
                i.routeName = jAddFriendReplyRsp.params.routeName

                var dst_public_MiKey_Friend = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(jAddFriendReplyRsp.params.userKey))
                if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                    i.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                }
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendReplyReq = AddFriendReplyReq(0, userId!!, "")
                var sendData = BaseData(addFriendReplyReq, jAddFriendReplyRsp.msgid)
                if (ConstantValue.encryptionType.equals("1")) {
                    sendData = BaseData(4, addFriendReplyReq, jAddFriendReplyRsp.msgid)
                }
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
        newFriend.signPublicKey = jAddFriendPushRsp.params.userKey

        var dst_public_MiKey_Friend = ByteArray(32)
        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
        if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
            newFriend.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
        }
        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
        newFriend.routerUserId = selfUserId
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)

        var newFriendStatus = FriendEntity()
        newFriendStatus.userId = userId;
        newFriendStatus.friendId = jAddFriendPushRsp.params.friendId
        newFriendStatus.friendLocalStatus = 3
        newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)

        var addFriendPushReq = AddFriendPushReq(0, userId!!, "")
        runOnUiThread {
            viewModel.freindChange.value = Calendar.getInstance().timeInMillis
        }
        var sendData = BaseData(addFriendPushReq, jAddFriendPushRsp.msgid)
        if (ConstantValue.encryptionType.equals("1")) {
            sendData = BaseData(4, addFriendPushReq, jAddFriendPushRsp.msgid)
        }

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

    private var exitTime: Long = 0
    lateinit var viewModel: MainViewModel
    private var conversationListFragment: EaseConversationListFragment? = null
    private var contactFragment: ContactFragment? = null

    override fun showToast() {
        showProgressDialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息：" + toxSendInfoEvent.info)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNikNameChange(editnickName: EditNickName) {
        contactFragment?.initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        if (friendChange.userId != null && !friendChange.userId.equals("")) {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendChange.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation != null) {
                conversation.clearAllMessages()
                if (ConstantValue.isInit) {
                    var count = conversationListFragment?.removeFriend()
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
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
                    var count = conversationListFragment?.removeFriend()
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态MainActivity:" + connectStatus.status)
        if (connectStatus.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        KLog.i("tox状态MainActivity:" + toxStatusEvent.status)
        if (toxStatusEvent.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {
        KLog.i("tox好友状态MainActivity:" + toxFriendStatusEvent.status)
        if (toxFriendStatusEvent.status == 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }

    }

    private fun getToken() {
        HMSAgent.Push.getToken {
            KLog.i("华为推送 get token: end" + it)
            LogUtil.addLog("华为推送 get token: end" + it)
//            ConstantValue.mHuaWeiRegId = "" + it
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openSceen(screen: Sceen) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun resetUnCompleteFileRecode() {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList) {
            if (myFie.upLoadFile.isComplete == false) {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.isStop = "1"
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
    }

    override fun onResume() {
//        AppShortCutUtil.clearBadge(this)
        WinqMessageReceiver.count = 0
        ShortcutBadger.removeCount(this)
        exitTime = System.currentTimeMillis() - 2001

        super.onResume()
        notificationManager?.cancelAll()
        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
        controlleMessageUnReadCount(UnReadMessageCount)

    }

    override fun onPause() {
        ShortcutBadger.removeCount(this)
        super.onPause()
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

        var hasUnReadMsgCount = 0
        var hasUnReadMsg: Boolean = false;
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val keyMap = SpUtil.getAll(AppConfig.instance)
        for (key in keyMap.keys) {

            if (key.contains(ConstantValue.message) && key.contains(userId + "_")) {
                val tempkey = key.replace(ConstantValue.message, "")
                val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null") {
                    if (toChatUserId.indexOf("group") == 0)//这里处理群聊
                    {
                        val localGroupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(toChatUserId)).list()
                        if (localGroupList.size == 0)
                        //如果找不到用户
                        {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }

                    } else {//这里是普通聊天

                        val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list()
                        if (localFriendList.size == 0)
                        //如果找不到用户
                        {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }
                        var freindStatusData = FriendEntity()
                        freindStatusData.friendLocalStatus = 7
                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(userId), FriendEntityDao.Properties.FriendId.eq(toChatUserId)).list()
                        if (localFriendStatusList.size > 0) freindStatusData = localFriendStatusList[0]
                        if (freindStatusData.friendLocalStatus != 0) {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }
                    }

                    val cachStr = SpUtil.getString(AppConfig.instance, key, "")

                    if ("" != cachStr) {
                        val gson = GsonUtil.getIntGson()
                        val Message = gson.fromJson(cachStr, Message::class.java)
                        hasUnReadMsgCount += Message.unReadCount
                        if (hasUnReadMsgCount > 0) {
                            hasUnReadMsg = true
                        }
                    }
                }

            }
        }
        if (unread_count != null) {
            if (hasUnReadMsgCount == 0) {
                unread_count.visibility = View.INVISIBLE
                unread_count.text = ""
            } else if (hasUnReadMsgCount > 99) {
                unread_count.visibility = View.VISIBLE
                unread_count.text = "99+"
            } else {
                unread_count.visibility = View.VISIBLE
                unread_count.text = hasUnReadMsgCount.toString()
            }
        }
    }


    override fun onItemClick(id: Int) {
        when (id) {
//            R.id.rl_detail -> startActivity(Intent(this, FreeConnectActivity::class.java))
//            R.id.rl_rank -> startActivity(Intent(this, RankActivity::class.java))
            else -> {
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                reConnect.visibility = View.GONE
            }
            1 -> {

            }
            2 -> {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                reConnect.visibility = View.VISIBLE
            }
            3 -> {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                if (isCanShotNetCoonect) {
                    if (!ConstantValue.loginOut) {
                        closeProgressDialog()
                        //showProgressDialog(getString(R.string.network_reconnecting))
                    }
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if (isCanShotNetCoonect) {
                    closeProgressDialog()
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
    }

    override fun onDestroy() {
//        reRegesterMiPush()
        KLog.i("onDestroy")
        EventBus.getDefault().unregister(this)
//        exitToast()
        super.onDestroy()
    }

    fun clear(content: String) {
        var preferences = getSharedPreferences(content, Context.MODE_PRIVATE)
        var editor = preferences.edit()
        editor.clear()
        editor.apply()
    }


    fun setToNews() {
        tvTitle.text = getString(R.string.app_name)
        mainIv1.visibility = View.GONE
        llSort.visibility = View.GONE
        ivQrCode.visibility = View.GONE
        ivNewGroup.visibility = View.VISIBLE
    }

    fun setToFile() {
        tvTitle.text = getString(R.string.file_)
        mainIv1.visibility = View.VISIBLE
        ivQrCode.visibility = View.GONE
        llSort.visibility = View.VISIBLE
        ivNewGroup.visibility = View.GONE
    }

    fun setToContact() {
        tvTitle.text = getString(R.string.contacts)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
        llSort.visibility = View.GONE
        ivNewGroup.visibility = View.GONE
        //contactFragment?.updata()
    }

    fun setToMy() {
        tvTitle.text = getString(R.string.my)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.GONE
        llSort.visibility = View.GONE
        ivNewGroup.visibility = View.GONE
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        StatusBarUtil.setColor(this, resources.getColor(R.color.mainColor), 0)
        tvTitle.text = getString(R.string.news)
//        val llp = RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
//        statusBar.setLayoutParams(llp)
        val llp1 = RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        reConnect.setLayoutParams(llp1)
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        contactFragment = ContactFragment()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
    }

    override fun initData() {
        if (VersionUtil.getDeviceBrand() == 3) {
            HMSAgent.connect(this, ConnectHandler {
                KLog.i("华为推送 HMS connect end: " + it)
                LogUtil.addLog("华为推送 HMS connect end: " + it)
            })
            getToken()
        }
        SpUtil.putBoolean(this, ConstantValue.isUnLock, true)
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        FileMangerUtil.init()
        FileMangerDownloadUtils.init()
        try {
            AppConfig.instance.getPNRouterServiceMessageReceiver().mainInfoBack = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val backGroundService = Intent(this, BackGroundService::class.java)
        this.startService(backGroundService)
        var intent = Intent(this, FileTransformService::class.java)
        startService(intent)
        ConstantValue.mainActivity = this
        var startFileDownloadUploadService = Intent(this, FileDownloadUploadService::class.java)
        startService(startFileDownloadUploadService)
        Thread(Runnable() {
            run() {
                while (isSendRegId) {
                    Thread.sleep(10 * 1000)
                    var map: HashMap<String, String> = HashMap()
                    var os = VersionUtil.getDeviceBrand()
                    map.put("os", os.toString())
                    map.put("appversion", "1.0.1")
                    if (os == 3) {
                        map.put("regid", ConstantValue.mRegId)
                        map.put("token", ConstantValue.mHuaWeiRegId)
                    } else {
                        map.put("regid", ConstantValue.mRegId)
                    }
                    map.put("topicid", "")
                    var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
                    map.put("routerid", ConstantValue.currentRouterId)
                    map.put("userid", selfUserId!!)
                    var lastLoginUserSn = FileUtil.getLocalUserData("usersn")
                    map.put("usersn", lastLoginUserSn)
                    KLog.i("小米推送注册RegId= " + ConstantValue.mRegId)
                    LogUtil.addLog("小米推送注册RegId= " + ConstantValue.mRegId, "MainActivity")
                    KLog.i(map)
                    OkHttpUtils.getInstance().doPost(ConstantValue.pushURL, map, object : OkHttpUtils.OkCallback {
                        override fun onFailure(e: Exception) {
                            isSendRegId = true
                            KLog.i(e.printStackTrace())
                            LogUtil.addLog("小米推送注册失败:", "MainActivity")
                            KLog.i("小米推送注册失败:MainActivity")
                        }

                        override fun onResponse(json: String) {
                            isSendRegId = false
                            LogUtil.addLog("小米推送注册成功:", "MainActivity")
                            KLog.i("小米推送注册成功:MainActivity" + json)
                            //Toast.makeText(AppConfig.instance,"成功",Toast.LENGTH_SHORT).show()
                        }
                    });
                    Thread.sleep(10 * 1000)
                }

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
            if (toAddUserId!!.contains(selfUserId!!)) {
                return@Observer
            }
            if (!"".equals(toAddUserId)) {
                var toAddUserIdTemp = toAddUserId!!.substring(0, toAddUserId!!.indexOf(","))
                var intent = Intent(this, SendAddFriendActivity::class.java)
                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                for (i in useEntityList) {
                    if (i.userId.equals(toAddUserIdTemp)) {
                        var freindStatusData = FriendEntity()
                        freindStatusData.friendLocalStatus = 7
                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
                        if (localFriendStatusList.size > 0)
                            freindStatusData = localFriendStatusList[0]

                        if (freindStatusData.friendLocalStatus == 0) {
                            intent.putExtra("user", i)
                            startActivity(intent)
                        } else {
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
                userEntity.userId = toAddUserId!!.substring(0, toAddUserId!!.indexOf(","))
                userEntity.nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                userEntity.signPublicKey = toAddUserId!!.substring(toAddUserId!!.lastIndexOf(",") + 1, toAddUserId.length)
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
        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
        if (messageEntityList != null) {
            KLog.i("开始添加本地数据到重发列表" + messageEntityList.size)
            LogUtil.addLog("开始添加本地数据到重发列表" + messageEntityList.size)
            messageEntityList.sortBy { it.sendTime }
            for (i in messageEntityList) {
                if (i.type.equals("0")) {
                    KLog.i("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    //文本消息
                    AppConfig.instance.getPNRouterServiceMessageSender().addDataFromSql(i.userId, i.baseData)
                } else {
                    //文件消息
                    var SendFileInfo = SendFileInfo();
                    SendFileInfo.userId = i.userId
                    SendFileInfo.friendId = i.friendId
                    SendFileInfo.files_dir = i.filePath
                    SendFileInfo.msgId = i.msgId
                    SendFileInfo.friendSignPublicKey = i.friendSignPublicKey
                    SendFileInfo.friendMiPublicKey = i.friendMiPublicKey
                    SendFileInfo.voiceTimeLen = i.voiceTimeLen
                    SendFileInfo.type = i.type
                    SendFileInfo.sendTime = i.sendTime
                    SendFileInfo.widthAndHeight = i.widthAndHeight
                    SendFileInfo.porperty = i.porperty
                    KLog.i("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    AppConfig.instance.getPNRouterServiceMessageSender().addFileDataFromSql(i.userId, SendFileInfo)
                }

            }
        }
        var shelfId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupListPullReq(shelfId!!, ConstantValue.currentRouterId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
        setToNews()
        ivQrCode.setOnClickListener {
            //            val morePopWindow = ActiveTogglePopWindow(this)
//            morePopWindow.setOnItemClickListener(this@MainActivity)
//            morePopWindow.showPopupWindow(ivQrCode)
            startActivityForResult(Intent(this, addFriendOrGroupActivity::class.java), add_activity)
//            mPresenter.getScanPermission()
        }
        ivNewGroup.setOnClickListener {
            startActivityForResult(Intent(this, addFriendOrGroupActivity::class.java), add_activity)
            /* var list = arrayListOf<GroupEntity>()
             startActivityForResult(Intent(this, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), create_group)*/
        }
        llSort.setOnClickListener {
            startActivity(Intent(this, FileTaskListActivity::class.java))
        }
        mainIv1.setOnClickListener {
            PopWindowUtil.showFileUploadPopWindow(this@MainActivity, recyclerView, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    when (position) {
                        0 -> {
                            PictureSelector.create(this@MainActivity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .maxSelectNum(100)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
                                    .imageFormat(PictureMimeType.PNG)
                                    .isZoomAnim(true)
                                    .sizeMultiplier(0.5f)
                                    .setOutputCameraPath("/CustomPath")
                                    .enableCrop(false)
                                    .compress(false)
                                    .glideOverride(160, 160)
                                    .hideBottomControls(false)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .minimumCompressSize(100)
                                    .synOrAsy(true)
                                    .rotateEnabled(true)
                                    .scaleEnabled(true)
                                    .videoMaxSecond(60 * 60 * 3)
                                    .videoMinSecond(1)
                                    .isDragFrame(false)
                                    .forResult(SELECT_PHOTO)
                        }
                        1 -> {
                            PictureSelector.create(this@MainActivity)
                                    .openGallery(PictureMimeType.ofVideo())
//                                    .theme()
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
                                    .imageFormat(PictureMimeType.PNG)
                                    .isZoomAnim(true)
                                    .sizeMultiplier(0.5f)
                                    .setOutputCameraPath("/CustomPath")
                                    .enableCrop(false)
                                    .compress(false)
                                    .glideOverride(160, 160)
                                    .hideBottomControls(false)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .minimumCompressSize(100)
                                    .synOrAsy(true)
                                    .rotateEnabled(true)
                                    .scaleEnabled(true)
                                    .videoMaxSecond(60 * 60 * 3)
                                    .videoMinSecond(1)
                                    .isDragFrame(false)
                                    .forResult(SELECT_VIDEO)
                        }
                        2 -> {
                            startActivityForResult(Intent(this@MainActivity, FileChooseActivity::class.java).putExtra("fileType", 2), SELECT_DEOCUMENT)
                        }
                    }
                }

            })
        }
        if (!ConstantValue.isInit) {
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            var pullFriend = PullFriendReq_V4(selfUserId!!)
            var sendData = BaseData(pullFriend)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(4, pullFriend)
            }
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

            ConstantValue.isInit = true
        }
        conversationListFragment?.setConversationListItemClickListener(
                EaseConversationListFragment.EaseConversationListItemClickListener
                { userid, chatType ->
                    if (chatType.equals("Chat")) {
                        startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userid))
                    } else {

                        val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, userid)
                        intent.putExtra(EaseConstant.EXTRA_CHAT_GROUP, UserDataManger.currentGroupData)
                        startActivity(intent)
                    }
                    KLog.i("进入聊天页面，好友id为：" + userid)
                })
        if (AppConfig.instance.tempPushMsgList.size != 0) {
            Thread(Runnable() {
                run() {
                    Thread.sleep(1000);
                    for (pushMsgRsp in AppConfig.instance.tempPushMsgList) {
                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), userId!!, 0, "")
                        var sendData = BaseData(msgData, pushMsgRsp?.msgid)
                        if (ConstantValue.encryptionType.equals("1")) {
                            sendData = BaseData(3, msgData, pushMsgRsp?.msgid)
                        }
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
                        var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
                        var msgSouce = ""
                        if (ConstantValue.encryptionType.equals("1")) {
                            msgSouce = LibsodiumUtil.DecryptFriendMsg(pushMsgRsp.getParams().getMsg(), pushMsgRsp.getParams().getNonce(), pushMsgRsp.getParams().getFrom(), pushMsgRsp.getParams().getSign())
                        } else {
                            msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
                        }
                        val message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
                        message.setDirection(EMMessage.Direct.RECEIVE)
                        message.msgId = pushMsgRsp?.params.msgId.toString()
                        message.from = pushMsgRsp.params.fromId
                        message.to = pushMsgRsp.params.toId
                        message.isUnread = true
                        message.isAcked = true
                        message.setStatus(EMMessage.Status.SUCCESS)
                        if (conversation != null) {
                            var gson = Gson()
                            var Message = Message()
                            Message.setMsg(pushMsgRsp.getParams().getMsg())
                            Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                            Message.setFrom(pushMsgRsp.getParams().getFromId())
                            Message.setTo(pushMsgRsp.getParams().getToId())
                            Message.chatType = EMMessage.ChatType.Chat

                            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, "")
                            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                            var unReadCount = 0
                            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                unReadCount = MessageLocal.unReadCount
                            }
                            Message.unReadCount = unReadCount + AppConfig.instance.tempPushMsgList.size;

                            var baseDataJson = gson.toJson(Message)
                            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, baseDataJson)
                            KLog.i("insertMessage:" + "MainActivity" + "_tempPushMsgList")
                            //conversation.insertMessage(message)
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
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> return conversationListFragment!!
                    1 -> return FileListFragment()
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
                inputMethodManager?.hideSoftInputFromWindow(viewPager.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
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


        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
        var filePath = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
        var fileMD5 = FileUtil.getFileMD5(File(filePath))
        if (fileMD5 == null) {
            fileMD5 = ""
        }
        val updateAvatarReq = UpdateAvatarReq(userId!!, userId!!, fileMD5)
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun startVerify(startVerify: StartVerify) {
        KLog.i("要进入验证页面")
        if ((AppConfig.instance.mAppActivityManager.currentActivity() as Activity) is VerifyingFingerprintActivity) {
            return
        }
        runDelayed(50, {
            SpUtil.putBoolean(this, ConstantValue.isUnLock, false)
            val intent = Intent(AppConfig.instance, VerifyingFingerprintActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out_1)
        })
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
        if (savedInstanceState != null) {
            KLog.i("保存的东西不为空," + savedInstanceState.getString("save"))
            LogUtil.addLog("保存的东西不为空," + savedInstanceState.getString("save"))

        }
        needFront = true
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("save", "保存的东西")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        KLog.i("HELLO:如果应用进程被系统咔嚓，则再次打开应用的时候会进入")
        super.onRestoreInstanceState(savedInstanceState)
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
            if (!result.contains("type_0")) {
                toast(getString(R.string.codeerror))
                return;
            }
            viewModel.toAddUserId.value = result.substring(7, result.length)
            return
        } else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_DEOCUMENT && resultCode == Activity.RESULT_OK) {
            var list = ArrayList<LocalMedia>()
            var localMedia = LocalMedia()
            localMedia.path = data!!.getStringExtra("path")
            list.add(localMedia)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == create_group && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: java.util.ArrayList<GroupEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var intent = Intent(this, CreateGroupActivity::class.java)
                    intent.putExtra("personList", contactSelectedList)
                    startActivity(intent)
                }
            }
        }else if (requestCode == add_activity && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var result = data!!.getStringExtra("result")
                when(result)
                {
                    "0" ->
                    {
                        var list = arrayListOf<GroupEntity>()
                        startActivityForResult(Intent(this, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), create_group)
                    }
                    "1" ->
                    {
                        mPresenter.getScanPermission()
                    }
                    "2" ->
                    {
                        var intent = Intent(this, QRCodeActivity::class.java)
                        intent.putExtra("flag",0)
                        startActivity(intent)
                    }
                }

            }
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
            if (!CustomPopWindow.onBackPressed()) {
                moveTaskToBack(true)
//\                exitToast()
            }
        }
        return false
    }


    fun exitToast(): Boolean {
        CrashReport.closeBugly()
        CrashReport.closeCrashReport()
        MiPushClient.unregisterPush(this)
        AppConfig.instance.stopAllService()
        //android进程完美退出方法。
//            AppConfig.instance.mAppActivityManager.AppExit()
        var intent = Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
//        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        //System.runFinalizersOnExit(true);
//        System.exit(0)
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
