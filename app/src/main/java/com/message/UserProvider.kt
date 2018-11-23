package com.message

import android.util.Base64
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.entity.events.UnReadContactCount
import com.stratagile.pnrouter.utils.MutableListToArrayList
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import org.greenrobot.eventbus.EventBus
import java.util.*

class UserProvider : PNRouterServiceMessageReceiver.UserControlleCallBack {

    var friendOperateListener : FriendOperateListener? = null
    var addFriendDelListener : AddFriendDelListener? = null

    /**
     * APP添加新好友，发送好友请求，等待好友同意
     * 添加好友第一步的返回
     */
    override fun addFriendBack(addFriendRsp: JAddFreindRsp) {
        KLog.i("添加好友请求的返回")
        friendOperateListener?.addFriendRsp(addFriendRsp.params.retCode)
    }

    /**
     * 7.	好友请求处理
     * APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
     * 第三步，我处理对方给我的好友请求
     */
    override fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp) {
        userList.forEach {
            if (it.userId.equals(jAddFriendDealRsp.params.friendName)) {
                if (jAddFriendDealRsp.params.result == 0) {
                    it.friendStatus = 0
                } else if (jAddFriendDealRsp.params.result == 1) {
                    it.friendStatus = 2
                }
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                return@forEach
            }
        }
        addFriendDelListener?.addFriendDealRsp(jAddFriendDealRsp.params.retCode)
        refreshFriend("")
    }

    /**
     * APP发起，用户删除目标好友
     *
     */
    override fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp) {
        if (jDelFriendCmdRsp.params.retCode == 0) {
            userList.forEach {
                if (it.userId.equals(jDelFriendCmdRsp.params.friendId)) {
                    it.friendStatus = 6
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                    refreshFriend(jDelFriendCmdRsp.params.friendId)
                    return@forEach
                }
            }
            friendOperateListener?.delFriendRsp(jDelFriendCmdRsp.params.retCode)
        }
    }

    /**
     * 对方添加我为好友，推送给我，等待我同意
     * 添加好友第二步
     */
    override fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp) {
        KLog.i("推送过来了添加好友的请求")
        userList.forEach {
            if (it.userId.equals(jAddFriendPushRsp.params.friendId)) {
                it.friendStatus = 3
                it.nickName = jAddFriendPushRsp.params.nickName;
                it.publicKey = jAddFriendPushRsp.params.userKey
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendPushReq = AddFriendPushReq(0,userId!!, "")
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendPushReq,jAddFriendPushRsp.msgid))
                EventBus.getDefault().post(FriendChange())
                return
            }
        }
        var newFriend = UserEntity();
        newFriend.nickName = jAddFriendPushRsp.params.nickName
        newFriend.friendStatus = 3
        newFriend.userId = jAddFriendPushRsp.params.friendId
        newFriend.addFromMe = false
        newFriend.timestamp = Calendar.getInstance().timeInMillis
        newFriend.noteName = ""
        newFriend.publicKey = jAddFriendPushRsp.params.userKey
        userList.add(newFriend)
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var addFriendPushReq = AddFriendPushReq(0,userId!!, "")
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendPushReq,jAddFriendPushRsp.msgid))
        EventBus.getDefault().post(FriendChange())
    }

    fun refreshFriend(friendId:String) {
        userList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll().MutableListToArrayList()
        var newFriendCount = 0
        for (i in userList) {
            if (i.userId.equals(selfUserId)) {
                continue
            }
            if (i.friendStatus == 3) {
                newFriendCount++
            }
        }
        EventBus.getDefault().post(UnReadContactCount(newFriendCount))
        EventBus.getDefault().post(FriendChange(friendId))
    }


    /**
     * 目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友请求的结果
     * 第四步，对方处理完我发出的好友请求，给我推送处理结果
     */
    override fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp) {
        userList.forEach {
            if (it.userId.equals(jAddFriendReplyRsp.params.friendId)) {
                if (jAddFriendReplyRsp.params.result == 0) {
                    it.friendStatus = 0
                } else if (jAddFriendReplyRsp.params.result == 1) {
                    it.friendStatus = 2
                }
                it.nickName = jAddFriendReplyRsp.params.nickname
                it.publicKey = jAddFriendReplyRsp.params.userKey
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendReplyReq = AddFriendReplyReq(0,userId!!, "")
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid))
                refreshFriend("")
                return
            }
        }

        var userEntity = UserEntity()
        userEntity.userId = jAddFriendReplyRsp.params.friendId
        userEntity.nickName = jAddFriendReplyRsp.params.friendName
        if (jAddFriendReplyRsp.params.result == 0) {
            userEntity.friendStatus = 0
        } else if (jAddFriendReplyRsp.params.result == 1) {
            userEntity.friendStatus = 2
        }
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var addFriendReplyReq = AddFriendReplyReq(0,userId!!, "")
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid))
        refreshFriend("")
    }

    override fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp) {
        userList.forEach {
            if (it.userId.equals(jDelFriendPushRsp.params.friendId)) {
                it.friendStatus = 6
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                MessageProvider.getInstance().deletConversationByDeleteFriend(jDelFriendPushRsp.params.friendId)
                return@forEach
            }
        }
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var delFriendPushReq = DelFriendPushReq(0,userId!!, "")
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendPushReq,jDelFriendPushRsp.msgid))
        EventBus.getDefault().post(FriendChange(jDelFriendPushRsp.params.friendId))
    }

    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        KLog.i("firendList")
    }

    var userList = arrayListOf<UserEntity>()
    var selfUserId = ""
    companion object {
        var userProvider : UserProvider? = null
        fun getInstance() : UserProvider{
            if (userProvider == null) {
                userProvider = UserProvider()
                return userProvider!!
            } else {
                return userProvider!!
            }
        }

        fun init() {
            getInstance()
        }
    }

    init {
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        userList.addAll(list)
        if (!"".equals(selfUserId)) {
            var hasSelf = false
            list.forEach {
                if (selfUserId!!.equals(it.userId)) {
                    hasSelf = true
                }
            }
            if (!hasSelf) {
                var self = UserEntity()
                self.userId = selfUserId
                self.nickName = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(self)
                userList.add(self)
            }
        }
    }

    fun getUserById(userId : String) : UserEntity? {
        userList.forEach{
            if (it.userId.equals(userId)) {
                return it
            }
        }
        getUserFromServer()
        return null
    }

    fun getUserFromServer() {

    }

    fun addFriend(selfUserId : String, nickName : String, toUserId: String) {
        val strBase64 = RxEncodeTool.base64Encode2String(nickName.toByteArray())
        var login = AddFriendReq( selfUserId, strBase64, toUserId,ConstantValue.publicRAS,"")
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(login))
    }

    fun deleteFriend(selfUserId : String, toUserId: String) {
        var delFriendCmdReq = DelFriendCmdReq(selfUserId, toUserId)
        AppConfig.instance.messageSender!!.send(BaseData(delFriendCmdReq))
    }

    fun accepteAddFriend(selfNickName : String, toNickName : String, selfUserId: String, toUserId : String,friendKey :String) {
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(selfNickName!!.toByteArray())
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(toNickName!!.toByteArray())
        var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, ConstantValue.publicRAS, friendKey,0)
        AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
    }

    fun refuseAddFriend(selfNickName : String, toNickName : String, selfUserId: String, toUserId : String,friendKey :String) {
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(selfNickName!!.toByteArray())
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(toNickName!!.toByteArray())
        var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, "",friendKey,1)
        AppConfig.instance.messageSender!!.send(BaseData(addFriendDealReq))
    }

    interface FriendOperateListener {
        fun delFriendRsp(retCode : Int)
        fun accepteFriendRsp(retCode : Int)
        fun refuseFriendRsp(retCode : Int)
        fun addFriendRsp(retCode : Int)
    }

    interface AddFriendDelListener {
        fun addFriendDealRsp(retCode : Int)
    }

}