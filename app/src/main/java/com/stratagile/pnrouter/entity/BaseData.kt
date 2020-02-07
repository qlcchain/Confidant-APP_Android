package com.stratagile.pnrouter.entity

import com.alibaba.fastjson.JSONObject
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.utils.LibsodiumUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import java.util.*

/**
 * 数据基类
 */
open class BaseData() {
    var timestamp : String? = null
    var appid : String? = null
    var apiversion :Int ? = null
    var msgid :Int ? = null
    var offset :Int ? = null
    var more :Int ? = null
    var params : Any? = null
    var sign : String? = null


    constructor(apiverion:Int,params : Any) : this() {
        var apiverionNew = apiverion
        if(apiverion < 6)
        {
            apiverionNew = 6;
        }
        this.timestamp = ((Calendar.getInstance().timeInMillis) / 1000).toString()
        this.appid = "MiFi"
        this.apiversion = apiverionNew
        this.params = params
        this.offset = 0
        this.more = 0
        this.msgid =  ConstantValue.msgIndex++
        if(apiverionNew >=6)//版本6以及以上需要签名
        {
            var paramsStr = params.baseDataToJson()
            var action = (JSONObject.parseObject(paramsStr)).get("Action").toString()
            this.sign = LibsodiumUtil.cryptoSign(action +this.timestamp,ConstantValue.libsodiumprivateSignKey!!)
            if(this.sign.equals(""))
            {
                if(BuildConfig.DEBUG)
                {
                    //throw Exception()//抛出错误说明签名为空，版本6以及以上需要签名
                }
            }
        }
    }
    constructor(apiverion:Int,params : Any,msgId:Int) : this() {
        var apiverionNew = apiverion
        if(apiverion < 6)
        {
            apiverionNew = 6;
        }
        this.timestamp = ((Calendar.getInstance().timeInMillis) / 1000).toString()
        this.appid = "MiFi"
        this.apiversion = apiverionNew
        this.offset = 0
        this.more = 0
        this.params = params
        if(msgId != null)
        {
            this.msgid =  msgId
        }
        else{
            this.msgid =  ConstantValue.msgIndex++
        }
        if(apiverionNew >=6)//版本6以及以上需要签名
        {
            var paramsStr = params.baseDataToJson()
            var action = (JSONObject.parseObject(paramsStr)).get("Action").toString()
            this.sign = LibsodiumUtil.cryptoSign(action +this.timestamp,ConstantValue.libsodiumprivateSignKey!!)
            if(this.sign.equals(""))
            {
                if(BuildConfig.DEBUG)
                {
                    //throw Exception()//抛出错误说明签名为空，版本6以及以上需要签名
                }
            }
        }
    }
    constructor(params : Any) : this() {
        this.timestamp = ((Calendar.getInstance().timeInMillis) / 1000).toString()
        this.appid = "MiFi"
        this.apiversion = Integer.valueOf(BuildConfig.APIVERSION)
        if(this.apiversion!! < 6)
        {
            this.apiversion = 6
        }
        this.params = params
        this.offset = 0
        this.more = 0
        this.msgid =  ConstantValue.msgIndex++

        if(this.apiversion!! >=6)//版本6以及以上需要签名
        {
            var paramsStr = params.baseDataToJson()
            var action = (JSONObject.parseObject(paramsStr)).get("Action").toString()
            this.sign = LibsodiumUtil.cryptoSign(action +this.timestamp,ConstantValue.libsodiumprivateSignKey!!)
            if(this.sign.equals(""))
            {
                if(BuildConfig.DEBUG)
                {
                    //throw Exception()//抛出错误说明签名为空，版本6以及以上需要签名
                }
            }
        }
    }
    constructor(params : Any,msgId:Int) : this() {
        this.timestamp = ((Calendar.getInstance().timeInMillis) / 1000).toString()
        this.appid = "MiFi"
        this.apiversion = Integer.valueOf(BuildConfig.APIVERSION)
        var apiverionNew = this.apiversion
        if(apiverionNew!! < 6)
        {
            this.apiversion = 6
        }
        this.offset = 0
        this.more = 0
        this.params = params
        if(msgId != null)
        {
            this.msgid =  msgId
        }
        else{
            this.msgid =  ConstantValue.msgIndex++
        }
        if(this.apiversion!! >=6)//版本6以及以上需要签名
        {
            var paramsStr = params.baseDataToJson()
            var action = (JSONObject.parseObject(paramsStr)).get("Action").toString()
            this.sign = LibsodiumUtil.cryptoSign(action +this.timestamp,ConstantValue.libsodiumprivateSignKey!!)
            if(this.sign.equals(""))
            {
                if(BuildConfig.DEBUG)
                {
                    //throw Exception()//抛出错误说明签名为空，版本6以及以上需要签名
                }
            }
        }
    }
}
/**
 * 1.	APP用户找回
 * (1)	请求（APP-->Router）
 */
data class RecoveryReq(var RouteId : String, var UserSn : String, var Pubkey : String, var Action : String = "Recovery")
/**
 * 1.	APP注册（包含新用户注册）
 * (1)	请求（APP-->Router）
 */
data class RegeisterReq(var RouteId : String, var UserSn : String, var IdentifyCode :String,var LoginKey :String,var NickName :String, var Action : String = "Register")
/**
 * 57.	APP新用户注册_V4
 * (1)	请求（APP-->Router）
 */
data class RegeisterReq_V4(var RouteId : String, var UserSn : String, var Sign :String,var Pubkey :String,var NickName :String, var Action : String = "Register")
/**
 * 1.	APP登录
 * (1)	请求（APP-->Router）
 */
data class LoginReq(var RouteId : String, var UserSn:String, var UserId : String, var LoginKey : String, var DataFileVersion :Int, var Action : String = "Login")

/**
 *58.	APP用户登陆_V4
 * (1)	请求（APP-->Router）
 */
data class LoginReq_V4(var RouteId : String, var UserSn:String, var UserId : String, var Sign : String, var DataFileVersion :Int,var NickName :String, var Action : String = "Login")

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
data class AddFriendReq(var UserId : String, var NickName :String, var FriendId : String,var UserKey:String ,var Msg:String,var Action : String = "AddFriendReq")
data class AddFriendReq2(var UserId : String, var NickName :String, var FriendId : String,var UserKey:String,var FriendDevId:String ,var Msg:String,var Action : String = "AddFriendReq")
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
data class AddFriendPushReq(var Retcode : Int,var ToId : String, var Msg : String, var Action : String = "AddFriendPush")

/**
 * 7.	好友请求处理
 * 场景：APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
 * (1)	请求（APP-->Router）
 */
data class AddFriendDealReq(var Nickname : String, var FriendName : String, var UserId : String, var FriendId : String, var UserKey : String, var FriendKey : String, var Sign : String, var Result : Int, var Action : String = "AddFriendDeal")

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
data class AddFriendReplyReq(var Retcode : Int,var ToId : String, var Msg : String, var Action : String = "AddFriendReply")

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
data class DelFriendCmdRsp(var Retcode : Int,var ToId : String, var Msg : String, var Action : String ="DelFriendCmd")

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
data class DelFriendPushReq(var Retcode : Int,var ToId :String, var Msg : String, var Action : String = "DelFriendPush")

/**
 * 11.	发送消息
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (1)	请求（APP-->Router）
 */
data class SendMsgReq(var FromId :String ,var ToId :String, var Msg : String, var SrcKey : String, var DstKey : String, var Action : String = "SendMsg")

/**
 * 11.	发送消息V3
 * 场景：用户A向自己的好友用户B发送消息，A发送消息到router
 * (1)	请求（APP-->Router）
 */
data class SendMsgReqV3(var From :String ,var To:String, var Msg : String, var Sign : String, var Nonce : String, var PriKey: String,var AssocId:String,var Action : String = "SendMsg")

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
data class PushMsgReq(var MsgId :Int, var ToId :String, var RetCode : Int, var Msg : String, var Action : String = "PushMsg")

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
data class DelMsgRsp(var Retcode : Int, var Msg : String,var ToId:String,var Action: String = "PushDelMsg")

data class PullFriendReq_V4(var UserId: String, var Action: String = "PullFriend")

data class PullUserReq(var UserType: Int, var UserNum :Int ,var UserStartSN:String , var UserKey : String, var Action: String = "PullUserList")

data class HeartBeatReq(var UserId: String,var Active:Int, var Action: String = "HeartBeat")

data class ShareBean(var avatar : String, var name : String)

/**
 * 12.	拉取历史消息
 * 场景：用户A拉取自己跟好友B的聊天记录
 * (2)	响应（APP->Router）
 */
data class PullMsgReq(var UserId :String, var FriendId : String, var MsgType : Int, var MsgStartId :Int,var MsgNum : Int ,var SrcMsgId:Int,var Action : String = "PullMsg")


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


/**
 * 12.	18.	已阅消息
 * (2)	响应（APP->Router）
 */
data class ReadMsgReq(var UserId :String, var FriendId : String, var ReadMsgs : String,  var Action : String = "ReadMsg")


/**
 * 12.	18.	19.	已阅消息推送反馈
 * (2)	响应（APP->Router）
 */
data class ReadMsgPushReq(var RetCode :Int, var Msg : String, var ToId : String,  var Action : String = "ReadMsgPush")

/**
 * 31.	派生账户创建一个普通账户
 * (2)	响应（APP->Router）
 */
data class CreateNormalUserReq(var RouterId :String, var AdminUserId : String, var Mnemonic : String,var IdentifyCode : String,  var Action : String = "CreateNormalUser")

/**
 * 退出登录
 * (2)	响应（APP->Router）
 */
data class LogOutReq(var RouterId :String, var UserId : String, var UserSN : String,  var Action : String = "LogOut")

/**
 * 24.	发送文件_Tox消息
 * (2)	响应（APP->Router）
 */
data class SendToxFileNotice(var FromId :String, var ToId : String, var FileName : String, var FileMD5 : String, var FileInfo : String, var FileSize : Int, var FileType : Int, var FileId : Int, var SrcKey : String, var DstKey : String,  var Action : String = "SendFile")

/**
 * 12.	向服务器要tox文件
 * (2)	响应（APP->Router）
 */
data class PullFileReq(var FromId : String, var ToId : String,var FileName : String,var MsgId : Int,var FileOwner:Int, var FileFrom:Int  = 1,var Action : String = "PullFile")

/**
 * 个人信息修改
 * (2)	响应（APP->Router）
 */
data class UserInfoUpdateReq(var UserId :String, var NickName : String,  var Action : String = "UserInfoUpdate")

/**
 * 个人信息修改推送回馈
 * (2)	响应（APP->Router）
 */
data class UserInfoPushRsp(var RetCode :Int, var ToId : String, var Msg : String, var Action : String = "UserInfoPush")

/**
 * 36.	用户添加好友备注
 * (2)	响应（APP->Router）
 */
data class ChangeRemarksReq(var UserId :String, var FriendId : String, var Remarks : String,  var Action : String = "ChangeRemarks")

/**
 * 36.	查询好友关系
 * (2)	响应（APP->Router）
 */
data class QueryFriendReq(var UserId :String, var FriendId : String, var Action : String = "QueryFriend")

/**
 * 42.	拉取文件列表
 * （1）请求（APP-->Router）
 */
data class PullFileListReq(var UserId: String, var MsgStartId: Int, var MsgNum: Int, var Category : Int, var FileType: Int, var Action: String = "PullFileList")

//data class PullFileListRsp(var )


/**
 * 43.	管理员登录
 * (2)	响应（APP->Router）
 */
data class RouterLoginReq(var Mac :String, var LoginKey : String, var Action : String = "RouterLogin")



/**
 * 	44.	路由器修改管理密码
 * (2)	响应（APP->Router）
 */
data class ResetRouterKeyReq(var RouterId :String, var OldKey : String,var NewKey : String, var Action : String = "ResetRouterKey")


/**
 * 45.	路由器修改账户激活码
 * (2)	响应（APP->Router）
 */
data class ResetUserIdcodeReq(var RouterId :String, var UserSn : String,var OldCode : String,var NewCode : String, var Action : String = "ResetUserIdcode")

/**
 * 43.	上传文件请求
 * (2)	响应（APP->Router）
 */
data class UploadFileReq(var UserId :String, var FileName : String,var FileSize : Long,var FileType : Int, var Action : String = "UploadFileReq")

/**
 * 45.	删除文件
 * (2)	响应（APP->Router）
 */
data class DelFileReq(var UserId :String, var FileName : String,var FilePath : String,var Action : String = "DelFile")

/**
 * 24.	发送文件_Tox消息 上传文件
 * (2)	响应（APP->Router）
 */
data class SendToxUploadFileNotice(var UserId :String,  var FileName : String, var FileInfo : String, var FileMD5 : String, var FileSize : Int, var FileType : Int,  var UserKey : String,  var Action : String = "UploadFile")

/**
 * 50.	设备磁盘统计信息
 * (2)	响应（APP->Router）
 */
data class GetDiskTotalInfoReq(var Action : String = "GetDiskTotalInfo")

/**
 * 51.	设备磁盘详细信息
 * (2)	响应（APP->Router）
 */
data class GetDiskDetailInfoReq(var Slot:Int,var Action : String = "GetDiskDetailInfo")
/**
 * 52.	设备磁盘模式配置
 * (2)	响应（APP->Router）
 */
data class FormatDiskReq(var Mode:String,var Action : String = "FormatDisk")

/**
 * 	53.	设备重启
 * (2)	响应（APP->Router）
 */
data class RebootReq(var Action : String = "Reboot")

/**
 * 	56.	设备管理员修改设备昵称
 * (2)	响应（APP->Router）
 */
data class ResetRouterNameReq(var RouterId :String,var Name : String,var Action : String = "ResetRouterName")

/**
 * 	60.	用户在线状态通知_V4
 * (2)	响应（APP->Router）
 */
data class OnlineStatusPushRsp(var RetCode :Int, var Msg : String, var ToId : String,var Action : String = "OnlineStatusPush")
/**
 * 46.	用户登出推送
 * (2)	响应（APP->Router）
 */
data class PushLogoutRsp(var RetCode :Int, var ToId : String, var Msg : String,var Action : String = "PushLogout")

/**
 * 77.	文件重命名
 * (2)	响应（APP->Router）
 */
data class FileRenameReq(var UserId :String, var MsgId : Int, var Filename : String, var Rename : String,var Action : String = "FileRename")
/**
 * 78.	文件转发
 * (2)	响应（APP->Router）
 */
data class FileForwardReq(var MsgId :Int, var FromId : String, var ToId : String, var FilePath : String, var FileName : String, var FileInfo : String, var FileKey : String,var Action : String = "FileForward")

/**
 * 79.	用户上传头像
 * (2)	响应（APP->Router）
 */
data class UploadAvatarReq(var Uid : String, var FileName : String, var FileMD5 : String, var Action : String = "UploadAvatar")

/**
 * 80.	更新好友用户和自己头像
 * (2)	响应（APP->Router）
 */
data class UpdateAvatarReq(var Uid : String, var Fid : String, var Md5 : String,var Action : String = "UpdateAvatar")

/**
 * 61.	用户创建群组会话
 * (2)	响应（APP->Router）
 */
data class CreateGroupReq(var UserId : String, var GroupName : String, var UserKey : String, var VerifyMode : Int, var FriendId : String, var FriendKey : String,var Action : String = "CreateGroup")

/**
 * 62.	邀请用户加群
 * (2)	响应（APP->Router）
 */
data class InviteGroupReq(var UserId : String, var GId : String, var FriendId : String, var FriendKey : String,var Action : String = "InviteGroup")



/**
 * 63.	邀请用户入群推送
 * (2)	响应（APP->Router）
 */
data class GroupInvitePushReq(var From : String, var To : String, var GId : String, var FromName : String, var GroupName : String,var Action : String = "GroupInvitePush")


/**
 * 64.	邀请用户入群应答（暂不实现）
 * (2)	响应（APP->Router）
 */
data class GroupInviteDealReq(var Userid : String, var SelfId : String, var GId : String, var GroupName : String, var Result : Int,var Action : String = "GroupInviteDeal")


/**
 * 65.	邀请用户入群审核
 * (2)	响应（APP->Router）
 */
data class GroupVerifyReq(var From : String, var To : String, var Aduit : String, var GId : String, var GName : String, var Result : Int, var UserKey : String,var Action : String = "GroupVerify")

/**
 * 服务器给app推送了用户入群的审核，告诉服务器收到了改消息
 */
data class GroupVerifyPush(var Retcode: Int, var ToId: String, var Msg: String = "", var Action: String = "GroupVerifyPush")

/**
 * 66.	用户退群
 * (2)	响应（APP->Router）
 */
data class GroupQuitReq(var UserId : String, var GId : String, var GroupName : String,var Action : String = "GroupQuit")


/**
 * 67.	拉取群列表
 * (2)	响应（APP->Router）
 */
data class GroupListPullReq(var UserId : String, var RouterId : String,var Action : String = "GroupListPull")


/**
 * 68.	拉取群好友信息
 * (2)	响应（APP->Router）
 */
data class GroupUserPullReq(var UserId : String, var RouterId : String, var GId : String, var TargetNum : Int, var StartId : String, var Action : String = "GroupUserPull")

/**
 * 69.	拉取群消息列表
 * (2)	响应（APP->Router）
 */
data class GroupMsgPullReq(var UserId : String, var RouterId : String, var GId : String, var MsgType : Int, var MsgStartId : Int, var MsgNum : Int,var SrcMsgId:Int,var Action : String = "GroupMsgPull")



/**
 * 70.	群组会话中发文本消息
 * (2)	响应（APP->Router）
 */
data class GroupSendMsgReq(var UserId : String, var GId : String, var Point : String, var Msg : String, var AssocId : String,var Action : String = "GroupSendMsg")


/**
 * 71.	群组会话中发送语音，图片，视频，文件等_预处理
 * (2)	响应（APP->Router）
 */
data class GroupSendFilePreReq(var UserId : String, var RouterId : String, var GId : String, var FileName : String, var FileSize : Int, var FileType : Int,var Action : String = "GroupSendFilePre")

/**
 * 72.	群组会话中发送语音，图片，视频，文件等_完成通知
 * (2)	响应（APP->Router）
 */
data class GroupSendFileDoneReq(var UserId : String, var GId : String, var FileName : String, var FileMD5 : String, var FileInfo : String, var FileSize : Int, var FileType : Int, var FileId : String,var Action : String = "GroupSendFileDone")

/**
 * 73.	群消息删除
 * (2)	响应（APP->Router）
 */
data class GroupDelMsgReq(var Type : Int, var From : String, var GId : String, var MsgId : Int,var Action : String = "GroupDelMsg")

/**
 * 74.	群消息推送
 * (2)	响应（APP->Router）
 */
data class GroupMsgPushReq(var From : String, var To : String, var Point : Int, var GId : String, var GroupName : String, var MsgId : Int, var Msg : String,var Action : String = "GroupMsgPush")
/**
 * 74.	群消息推送  反馈
 * (2)	响应（APP->Router）
 */
data class GroupMsgPushRsp(var RetCode : Int, var ToId : String, var GId : String, var Msg : String,var Action : String = "GroupMsgPush")

/**
 * 75.	群属性设置
 * (2)	响应（APP->Router）
 */
data class GroupConfigReq(var UserId : String, var GId : String, var Type : Int, var ToId : String, var Name : String, var NeedVerify : Int,var Action : String = "GroupConfig")

/**
 * 群属性设置的子接口
 * 修改群名称，只有群管理员有权限
 */
data class ModifyGroupNameReq(var UserId : String, var GId : String, var Name : String, var Type : Int = 0x01, var Action : String = "GroupConfig")

/**
 * 群属性设置的子接口
 * 设置是否需要群管理审核入群，只有管理员有权限
 *
 *  是否需要审核入群
 *   0：不需要
 *   1：必须
 */
data class SettingApproveInvitationReq(var UserId : String, var GId : String, var NeedVerify : Int, var Type : Int = 0x02, var Action : String = "GroupConfig")

/**
 * 群属性设置的子接口
 * 踢出某个用户，只有管理员有权限
 */
data class RemoveGroupMemberReq(var UserId : String, var GId : String, var ToId : String, var Type : Int = 0x03, var Action : String = "GroupConfig")


/**
 * 群属性设置的子接口
 * 修改群别名
 */
data class ModifyGroupAliasReq(var UserId : String, var GId : String, var Name : String, var Type : Int = 0xF1, var Action : String = "GroupConfig")

/**
 * 群属性设置的子接口
 * 修改群友别名
 */
data class ModifyGroupMemberAliasReq(var UserId : String, var GId : String, var ToId : String, var Name : String, var Type : Int = 0xF2, var Action : String = "GroupConfig")


/**
 * 群属性设置的子接口
 * 设置自己群中显示的别名
 */
data class ModifySelfAliasInGroupReq(var UserId : String, var GId : String, var Name : String, var Type : Int = 0xF3, var Action : String = "GroupConfig")





/**
 * 76.	群系统消息推送
 * (2)	响应（APP->Router）
 */
data class GroupSysPushReq(var UserId : String, var GId : String, var Type : Int, var From : String, var To : String, var MsgId : Int, var Name : String, var NeedVerify : Int,var Action : String = "GroupSysPush")
/**
 * 76.	群系统消息推送 反馈
 * (2)	响应（APP->Router
 */
data class GroupSysPushRsp(var RetCode : Int, var ToId : String,var Action : String = "GroupSysPush")

/**
 * 83.	拉取临时账户信息
 * (2)	响应（APP->Router）
 */
data class PullTmpAccountReq(var UserId :String,  var Action : String = "PullTmpAccount")

/**
 * 84.	节点owner删除节点上其他用户
 * (2)	响应（APP->Router）
 */
data class DelUserReq(var From :String,var To :String,var Sn :String,  var Action : String = "DelUser")
/**
 * 86.	节点owner开关qlc节点
 * (2)	响应（APP->Router）
 */
data class EnableQlcNode(var Enable :Int,var Seed :String,  var Action : String = "EnableQlcNode")

/**
 * 87.	节点owner检查qlc节点运行状态
 * (2)	响应（APP->Router）
 */
data class CheckQlcNode(var Action : String = "CheckQlcNode")

/**
 * 88.	用户设置邮箱配置
 * (2)	响应（APP->Router）
 */
data class SaveEmailConf(var Version:Int ,var Type:Int,var User :String,var Config :String,var Userkey :String,var Action : String = "SaveEmailConf")
/**
 * 89.	用户拉取邮箱配置
 * (2)	响应（APP->Router）
 */
data class PullEmailConf(var Type:Int,var Action : String = "PullEmailConf")

/**
 * 90.	用户删除邮箱配置
 * (2)	响应（APP->Router）
 */
data class DelEmailConf(var Type:Int,var User :String,var Action : String = "DelEmailConf")
/**
 * 91.	用户设置邮箱签名
 * (2)	响应（APP->Router）
 */
data class SetEmailSign(var Type:Int,var User :String,var Sign :String,var Action : String = "SetEmailSign")
/**
 * 92.	用户拉取邮件列表
 * (2)	响应（APP->Router）
 */
data class PullMailList(var Type:Int,var User :String,var StartId :Int,var Num :Int,var Action : String = "PullMailList")
/**
 * 93.	用户备份邮件到节点上
 * (2)	响应（APP->Router）
 */
data class BakupEmail(var Type:Int,var FileId :Int,var FileSize :Int,var FileMd5 :String,var User :String,var Uuid :String,var UserKey :String,var MailInfo :String,var Action : String = "BakupEmail")
/**
 * 94.	用户删除节点上备份邮件
 * (2)	响应（APP->Router）
 */
data class DelEmail(var Type:Int ,var MailId :Int,var Action : String = "DelEmail")
/**
 * 95.	用户邮件标签设置
 * (2)	响应（APP->Router）
 */
data class SetEmailLabel(var Type:Int ,var MailId :Int ,var Status :Int,var Action : String = "SetEmailLabel")
/**
 * 96.	管理员设置用户磁盘限额
 * (2)	响应（APP->Router）
 */
data class SetCapactiy(var UserId:String ,var Capacity :Int,var Action : String = "SetCapactiy")
/**
 * 97.	管理员获取用户磁盘限额
 * (2)	响应（APP->Router）
 */
data class GetCapactiy(var UserId :String,var Action : String = "GetCapactiy")
/**
 * 98.	邮件加密时用户根据邮箱名查询目标公钥
 * (2)	响应（APP->Router）
 */
data class CheckmailUkey(var Unum:Int ,var Type:Int,var Users :String,var Action : String = "CheckmailUkey")
/**
 * 99.	用户查询自己账户下备份的邮件数量
 * (2)	响应（APP->Router）
 */
data class BakMailsNum(var User :String,var Action : String = "BakMailsNum")

/**
 * 99.	用户查询自己账户下备份的邮件数量
 * (2)	响应（APP->Router）
 */
data class BakMailsCheck(var User :String,var Uuid :String,var Action : String = "BakMailsCheck")
/**
 * 93.	用户发送邮件提醒
 * (2)	响应（APP->Router）
 */
data class MailSendNotice(var MailsTo :String,var Action : String = "MailSendNotice")

/**
 * 94.	系统提醒通知
 * (2)	响应（APP->Router）
 */
data class SysMsgPush(var Retcode :Int,var ToId:String,var Action : String = "SysMsgPush")
/**
 * 95.	自动添加好友
 * (1)	请求（APP-->Router）
 */
data class AddFriendsAutoReq(var Type : Int, var UserId :String, var Friends : String,var EmailId:String ,var Action : String = "AddFriendsAuto")
/**
 * 97.	用户数据同步接口
 * (1)	请求（APP-->Router）
 */
data class UserDataSysnReq(var UserId : String, var DType :Int, var Direction : Int,var Increment:Int,var FriendSeq:Int,var UinfoSeq:Int,var Data : String,var Action : String = "UserDataSysn")
/**
 * 98.	拉取文件夹列表
 * (1)	请求（APP-->Router）
 */
data class FilePathsPullReq(var UserId : String, var Type :Int,var Action : String = "FilePathsPull")
/**
 * 99.	拉取特定文件夹下文件列表
 * (1)	请求（APP-->Router）
 */
data class FilesListPullReq(var UserId : String, var Depens :Int, var PathId : Int,var PathName:String,var Sort:Int,var StartId:Int,var Num : Int,var Action : String = "FilesListPull")
/**
 * 100.	用户备份邮件到节点上
 * (1)	请求（APP-->Router）
 */
data class BakFileReq(var Depens :Int,var UserId : String,  var Type : Int,var FileId:Int,var Size:Long,var Md5:String,var FName : String,var FKey : String,var FInfo : String,var PathId : Int,var PathName : String,var Action : String = "BakFile")
/**
 * 101.	用户文件目录或者文件更新（新建，改名，删除）
 * (1)	请求（APP-->Router）
 */
data class FileActionReq(var UserId : String, var Depens :Int, var Type : Int,var React:Int,var FileId:Long,var PathId:Long,var Name : String,var OldName : String,var Action : String = "FileAction")

/**
 * 102.	用户查询自己账户下备份的通信录用户数量
 * (1)	请求（APP-->Router）
 */
data class BakAddrUserNumReq(var User : String, var FileId :Int,var Action : String = "BakAddrBookInfo")
/**
 * 103.	用户查询自己账户下备份的通信录用户数量
 * (1)	请求（APP-->Router）
 */
data class BakContentReq(var Type : String,var UserId : String, var Num :Int,var Payload : String,var Action : String = "BakContent")