package com.stratagile.pnrouter.entity.file

class FileOpreateType(var icon :String, var name : String) {

}

/**
 * fileKey  文件key
 * path  路径
 * fileSize 大小
 * isDownLoad 下载还是上传 true： 下载，false： 上传
 * isComplete 是否完成
 * isStop 0:开始状态（false）  ，1：停止状态（true）  ，2：过渡状态
 * segSeqResult 发送片段
 * segSeqTotal 总的片段
 * speed  上传和下载速度
 * SendGgain 是否重新开始
 * userKey  用户 公钥
 * fileFrom 谁发的文件
 * status 0 不操作状态 ，操作状态
 * msgId 消息id
 * isCheck  是否选中转态
 */
class UpLoadFile(var fileKey : String,var path:String,var fileSize:Long, var isDownLoad : Boolean = true, var isComplete : Boolean = false, var isStop : String = "0", var segSeqResult : Int = 0, var segSeqTotal : Int = 0, var speed : Int = 0,var SendGgain:Boolean = false,var userKey:String ="",var fileFrom:Int =0,var status:Int = 0,var msgId:String = "",var isCheck:Boolean= false)

class Arrange(var name: String, var isSelect : Boolean)