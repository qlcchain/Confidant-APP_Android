package com.stratagile.pnrouter.entity.events

import java.io.File

class ReminderUpdateEvent {
}

class EditNickName{}

class RouterChange

//meesage默认为0，只传toid，往websocket发、      1= websocket 连接成功, 2 发送成功， 3 收到信息, 4断开websocket连接
class FileTransformEntity(var toId : String, var message : Int = 0, var retMsg : String = "", var httpUrl : String = "", var userAgent : String = "lws-pnr-bin")

class TransformStrMessage(var toId : String, var message : String)

class TransformFileMessage(var toId : String, var message : File)