package com.stratagile.pnrouter.entity

import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.constant.ConstantValue
import java.util.*

/**
 * 数据基类
 */
open class BaseData() {
    var timestamp : String? = null
    var appid : String? = null
    var apiversion :Int ? = null
    var msgid :Int ? = null
    var params : Any? = null

    constructor(apiverion:Int,params : Any) : this() {
        this.timestamp = (Calendar.getInstance().timeInMillis / 1000).toInt().toString()
        this.appid = "MiFi"
        this.apiversion = apiverion
        this.params = params
        this.msgid =  ConstantValue.msgIndex++
    }
    constructor(params : Any) : this() {
        this.timestamp = (Calendar.getInstance().timeInMillis / 1000).toInt().toString()
        this.appid = "MiFi"
        this.apiversion = Integer.valueOf(BuildConfig.APIVERSION)
        this.params = params
        this.msgid =  ConstantValue.msgIndex++
    }
    constructor(params : Any,msgId:Int) : this() {
        this.timestamp = (Calendar.getInstance().timeInMillis / 1000).toInt().toString()
        this.appid = "MiFi"
        this.apiversion = Integer.valueOf(BuildConfig.APIVERSION)
        this.params = params
        if(msgId != null)
        {
            this.msgid =  msgId
        }
        else{
            this.msgid =  ConstantValue.msgIndex++
        }
    }
}
/**
 * 1.	APP用户找回
 * (1)	请求（APP-->Router）
 */
data class RecoveryReq(var RouteId : String, var UserSn : String, var Action : String = "Recovery")
/**
 * 1.	APP注册（包含新用户注册）
 * (1)	请求（APP-->Router）
 */
data class RegeisterReq(var RouteId : String, var UserSn : String, var IdentifyCode :String,var LoginKey :String,var NickName :String, var Action : String = "Register")
/**
 * 1.	APP登录
 * (1)	请求（APP-->Router）
 */
data class LoginReq(var RouteId : String, var UserSn:String, var UserId : String, var LoginKey : String, var DataFileVersion :Int, var Action : String = "Login")

/**
 * 1.	APP登录（包含新用户注册）
 * (2)	响应（Router->APP）
 */
data class LoginRsp(var Retcode : Int, var UserId : String, var NeedSynch :Int, var Action : String)

/**
 * 2.	注销用户
 * (1)	请求（APP-->Router）
 */
data class CancellationReq(var RouteId : String, var UserId : String, var Action : String = "Destory")

/**
 * 2.	注销用户
 * (2)	响应（Router->APP）
 */
data class CancellationRsp(var RouteId : String, var UserId : String, var Action : String)
/**
 * 5.	添加好友，发送好友请求
 * (1)	请求（APP-->Router）
 */
data class AddFriendReq(var UserId : String, var NickName :String, var FriendId : String, var Action : String = "AddFriendReq")

/**
 * 5.	添加好友，发送好友请求
 * (2)	响应（Router->APP）
 */
data class AddFriendRsp(var Retcode : Int, var Msg : String, var Action : String)

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendPushRsp(var UserId : String, var FriendId : String, var FriendNickName : String, var Action : String)

/**
 * 6.	Router推送好友请求给app
 * 场景：当一个用户A被其他用户B请求添加好友，router推送消息到A
 * (2)	响应（APP->Router）
 */
data class AddFriendPushReq(var Retcode : Int, var Msg : String, var Action : String = "AddFriendPush")

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (1)	请求（APP-->Router）
 */
data class AddFriendDealReq(var Nickname : String, var FriendName : String, var UserId : String, var FriendId : String, var Result : Int, var Action : String = "AddFriendDeal")

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (2)	响应（Router->APP）
 */
data class AddFriendDealRsp(var Retcode : Int, var Msg : String, var Action : String)

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class AddFriendReplyRsp(var UserId : String, var FriendId : String, var Result : String, var Action : String)

/**
 * 8.	好友请求完成
 * 场景：目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友
 * (2)	响应（APP-->Router）
 */
data class AddFriendReplyReq(var Retcode : Int, var Msg : String, var Action : String = "AddFriendReply")

/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (1)	请求（APP-->Router）
 */
data class DelFriendCmdReq(var UserId : String, var FriendId : String, var Action : String = "DelFriendCmd")


/**
 * 9.	删除好友
 * 场景：APP发起，用户删除目标好友
 * (2)	响应（Router->APP）
 */
data class DelFriendCmdRsp(var Retcode : Int, var Msg : String, var Action : String)

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (1)	请求（Router-->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class DelFriendPushRsp(var UserId : String, var FriendId : String, var Action : String)

/**
 * 10.	删除好友推送
 * 场景：用户A发起了删除用户B好友的行为，由router推送消息给用户B，删除对应好友
 * (2)	响应（APP->Router）
 */
data class DelFriendPushReq(var Retcode : Int, var Msg : String, var Action : String = "DelFriendPush")

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (1)	请求（APP-->Router）
 */
data class SendMsgReq(var FromId :String ,var ToId :String, var Msg : String, var Action : String = "SendMsg")

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (2)	响应（APP->Router）
 */
data class SendMsgRsp(var Retcode : Int, var Msg : String, var Action : String)


/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (1)	请求（Router->APP）
 * 这对接口是先路由给app发消息，再app处理之后给路由发消息
 */
data class PushMsgRsp(var FromId : String, var ToId :String, var Msg : String, var Action : String)

/**
 * 12.	接收消息
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (2)	响应（APP->Router）
 */
data class PushMsgReq(var MsgId :Int, var RetCode : Int, var Msg : String, var Action : String = "PushMsg")

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgReq(var UserId : String, var Friendid : String, var MsgId :Int,var Action : String = "DelMsg")

/**
 * 13.	删除消息
 * 场景：用户A发起删除和自己好友用户B的会话记录，删除后，路由器上也不再保存
 * (1)	请求（APP->Router）
 */
data class DelMsgRsp(var Action : String, var Retcode : Int, var Msg : String)

data class PullFriendReq(var UserId: String, var Action: String = "PullFriend")

data class HeartBeatReq(var UserId: String, var Action: String = "HeartBeat")

data class ShareBean(var avatar : String, var name : String)

/**
 * 12.	拉取历史消息
 * 场景：用户A拉取自己跟好友B的聊天记录
 * (2)	响应（APP->Router）
 */
data class PullMsgReq(var UserId :String, var FriendId : String, var MsgType : Int, var MsgStartId :Int,var MsgNum : Int ,var Action : String = "PullMsg")

data class SendStrMsg(var FromId :String, var ToId : String, var FileName : String, var FileSize :Long,var FileMD5 : String ,var Action : String = "SendFile")

/**
 * 12.	接收文件
 * 场景：用户A向自己的好友用户B发送消息，router推送该消息到用户B
 * (2)	响应（APP->Router）
 */
data class PushFileReq(var FromId :String, var ToId : String, var FilePath : String, var FileName :String,var FileMD5 : String ,var MsgId:Int,var Action : String = "SendFile")

/**
 * 12.	接收到文件反馈给服务器
 * (2)	响应（APP->Router）
 */
data class PushFileRespone(var Retcode :Int, var FromId : String, var ToId : String,var MsgId : Int,  var Action : String = "PushFile")
