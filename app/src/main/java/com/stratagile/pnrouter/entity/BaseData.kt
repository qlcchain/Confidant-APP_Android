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
data class LoginReq(var RouteId : String, var UserId : String, var UserDataVersion :Int, var Action : String = "Login") : BaseData<LoginReq>()

/**
 * 1.	APP登录（包含新用户注册）
 * (2)	响应（Router->APP）
 */
data class LoginRsp(var retcode : Int, var UserId : String, var NeedSynch :Int, var Action : String) : BaseData<LoginReq>()

/**
 * 2.	注销用户
 * (1)	请求（APP-->Router）
 */
data class CancellationReq(var RouteId : String, var UserId : String, var Action : String = "Destory") : BaseData<CancellationReq>()

/**
 * 2.	注销用户
 * (2)	响应（Router->APP）
 */
data class CancellationRsp(var RouteId : String, var UserId : String, var Action : String) :BaseData<CancellationRsp>()

/**
 * 5.	添加好友，发送好友请求
 * (1)	请求（APP-->Router）
 */
data class AddFriendReq(var UserId : String, var NickName :String, var FrendId : String, var Action : String = "AddFriendReq") :BaseData<AddFriendReq>()

/**
 * 5.	添加好友，发送好友请求
 * (2)	响应（Router->APP）
 */
data class AddFriendRsp(var retcode : Int, var Msg : String, var Action : String) : BaseData<AddFriendRsp>()

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendPushRsp(var UserId : String, var FriendId : String, var FriendNickName : String, var Action : String) :BaseData<AddFriendPushRsp>()

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (2)	响应（APP->Router）
 */
data class AddFriendPushReq(var retcode : Int, var Msg : String, var Action : String = "AddFriendPush") : BaseData<AddFriendPushReq>()

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (1)	请求（APP-->Router）
 */
data class AddFriendDealReq(var UserId : String, var FriendId : String, var Result : String, var Action : String = "AddFriendDeal") : BaseData<AddFriendDealReq>()

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (2)	响应（Router->APP）
 */
data class AddFriendDealRsp(var retcode : Int, var Msg : String, var Action : String) : BaseData<AddFriendDealRsp>()

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendReplyRsp(var UserId : String, var FriendId : String, var Result : String, var Action : String) : BaseData<AddFriendReplyRsp>()

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (2)	响应（APP-->Router）
 */
data class AddFriendReplyReq(var retcode : Int, var Msg : String, var Action : String = "AddFriendReply") : BaseData<AddFriendReplyReq>()

/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (1)	请求（APP-->Router）
 */
data class DelFriendCmdReq(var UserId : String, var FriendId : String, var Action : String = "DelFriendCmd") :BaseData<DelFriendCmdReq>()


/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (2)	响应（Router->APP）
 */
data class DelFriendCmdRsp(var retcode : Int, var Msg : String, var Action : String) : BaseData<DelFriendCmdRsp>()

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class DelFriendPushRsp(var UserId : String, var FriendId : String, var Action : String) : BaseData<DelFriendPushRsp>()

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (2)	响应（APP->Router）
 */
data class DelFriendPushReq(var retcode : Int, var Msg : String, var Action : String = "DelFriendPush") : BaseData<DelFriendPushReq>()

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (1)	请求（APP-->Router）
 */
data class SendMsgReq(var ToId :String, var Msg : String, var Action : String = "SendMsg") :BaseData<SendMsgReq>()

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (2)	响应（APP->Router）
 */
data class SendMsgRsp(var retcode : Int, var Msg : String, var Action : String) :BaseData<SendMsgRsp>()


/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (1)	请求（Router->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class PushMsgRsp(var FromId : String, var ToId :String, var Msg : String, var Action : String) :BaseData<PushMsgRsp>()

/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (2)	响应（APP->Router）
 */
data class PushMsgReq(var FromId : String, var ToId :String, var Msg : String = "PushMsg") :BaseData<PushMsgReq>()

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgReq(var UserId : String, var Friendid : String, var Action : String = "DelMsg") :BaseData<DelMsgReq>()

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgRsp(var Action : String, var retcode : Int, var Msg : String) :BaseData<DelMsgRsp>()

data class ShareBean(var avatar : String, var name : String)

