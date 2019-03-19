package com.stratagile.pnrouter.entity.events

import okio.ByteString

class ReminderUpdateEvent {
}

class EditNickName{}

class RouterChange

//meesage默认为0，只传toid，往websocket发、      1= websocket 连接成功, 2 发送成功， 3 收到信息, 4断开websocket连接
class FileTransformEntity(var toId : String, var message : Int = 0, var retMsg : String = "", var httpUrl : String = "", var userAgent : String = "lws-pnr-bin", var widthAndHeight : String = "")

class TransformStrMessage(var toId : String, var message : String)

class TransformFileMessage(var toId : String, var message : ByteArray)

class TransformStrMessageUrlFail(var toId : String)
//发送文件接收的反馈
class TransformReceiverFileMessage(var toId : String, var message : ByteArray)

//status 0 失败要移除 ，1成功 更新显示  2 文件大于100M 发送失败要移除
class FileTransformStatus(var msgid : String,var LogIdIdResult:String,var friendId:String, var status : Int = 0 )


//meesage默认为0，只传toid，往websocket发、      1= websocket 连接成功, 2 发送成功， 3 收到信息, 4断开websocket连接
class FileMangerTransformEntity(var toId : String, var message : Int = 0, var retMsg : String = "", var httpUrl : String = "", var userAgent : String = "lws-pnr-bin")

class FileMangerTransformStrMessage(var toId : String, var message : String)

class FileMangerTransformMessage(var toId : String, var message : ByteArray)
//发送文件接收的反馈
class FileMangerTransformReceiverMessage(var toId : String, var message : ByteArray)