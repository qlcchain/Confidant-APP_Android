package com.stratagile.pnrouter.entity

import com.stratagile.pnrouter.BuildConfig
import java.util.*

/**
 * 数据基类
 */
open class BaseData<T>() {
    var timestamp : String? = null
    var appid : String? = null
    var apiversion :Int ? = null
    var params : T? = null

    constructor(params : T) : this() {
        this.timestamp = (Calendar.getInstance().timeInMillis / 1000).toInt().toString()
        this.appid = "MiFi"
        this.apiversion = Integer.valueOf(BuildConfig.APIVERSION)
        this.params = params
    }
}

/**
 * 1.	APP登录（包含新用户注册）
 * (1)	请求（APP-->Router）
 */
data class LoginReq(var Action : String = "login", var RouteId : String, var UserId : String, var UserDataVersion :Int) : BaseData<LoginReq>()

/**
 * 1.	APP登录（包含新用户注册）
 * (2)	响应（Router->APP）
 */
data class LoginRsp(var Action : String, var retcode : Int, var UserId : String, var NeedSynch :Int) : BaseData<LoginReq>()

/**
 * 2.	注销用户
 * (1)	请求（APP-->Router）
 */
data class CancellationReq(var Action : String = "Destory", var RouteId : String, var UserId : String) : BaseData<CancellationReq>()

/**
 * 2.	注销用户
 * (2)	响应（Router->APP）
 */
data class CancellationRsp(var Action : String, var RouteId : String, var UserId : String) :BaseData<CancellationRsp>()

/**
 * 5.	添加好友，发送好友请求
 * (1)	请求（APP-->Router）
 */
data class AddFriendReq(var Action : String = "AddFriendReq", var UserId : String, var NickName :String, var FrendId : String) :BaseData<AddFriendReq>()

/**
 * 5.	添加好友，发送好友请求
 * (2)	响应（Router->APP）
 */
data class AddFriendRsp(var Action : String, var retcode : Int, var Msg : String) : BaseData<AddFriendRsp>()

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendPushRsp(var Action : String, var UserId : String, var FriendId : String, var FriendNickName : String) :BaseData<AddFriendPushRsp>()

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (2)	响应（APP->Router）
 */
data class AddFriendPushReq(var Action : String = "AddFriendPush", var retcode : Int, var Msg : String) : BaseData<AddFriendPushReq>()

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (1)	请求（APP-->Router）
 */
data class AddFriendDealReq(var Action : String = "AddFriendDeal", var UserId : String, var FriendId : String, var Result : String) : BaseData<AddFriendDealReq>()

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (2)	响应（Router->APP）
 */
data class AddFriendDealRsp(var Action : String, var retcode : Int, var Msg : String) : BaseData<AddFriendDealRsp>()

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendReplyRsp(var Action : String, var UserId : String, var FriendId : String, var Result : String) : BaseData<AddFriendReplyRsp>()

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (2)	响应（APP-->Router）
 */
data class AddFriendReplyReq(var Action : String = "AddFriendReply", var retcode : Int, var Msg : String) : BaseData<AddFriendReplyReq>()

/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (1)	请求（APP-->Router）
 */
data class DelFriendCmdReq(var Action : String = "DelFriendCmd", var UserId : String, var FriendId : String) :BaseData<DelFriendCmdReq>()


/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (2)	响应（Router->APP）
 */
data class DelFriendCmdRsp(var Action : String, var retcode : Int, var Msg : String) : BaseData<DelFriendCmdRsp>()

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class DelFriendPushRsp(var Action : String, var UserId : String, var FriendId : String) : BaseData<DelFriendPushRsp>()

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (2)	响应（APP->Router）
 */
data class DelFriendPushReq(var Action : String = "DelFriendPush", var retcode : Int, var Msg : String) : BaseData<DelFriendPushReq>()

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (1)	请求（APP-->Router）
 */
data class SendMsgReq(var Action : String = "SendMsg", var ToId :String, var Msg : String) :BaseData<SendMsgReq>()

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (2)	响应（APP->Router）
 */
data class SendMsgRsp(var Action : String, var retcode : Int, var Msg : String) :BaseData<SendMsgRsp>()


/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (1)	请求（Router->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class PushMsgRsp(var Action : String, var FromId : String, var ToId :String, var Msg : String) :BaseData<PushMsgRsp>()

/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (2)	响应（APP->Router）
 */
data class PushMsgReq(var Action : String = "PushMsg", var FromId : String, var ToId :String, var Msg : String) :BaseData<PushMsgReq>()

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgReq(var Action : String = "DelMsg", var UserId : String, var Friendid : String) :BaseData<DelMsgReq>()

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgRsp(var Action : String, var retcode : Int, var Msg : String) :BaseData<DelMsgRsp>()

data class ShareBean(var avatar : String, var name : String)

