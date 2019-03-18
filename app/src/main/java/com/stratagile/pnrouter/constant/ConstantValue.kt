package com.stratagile.pnrouter.constant

import android.app.Activity
import com.hyphenate.chat.EMMessage
import com.stratagile.pnrouter.data.web.FileMangerWebSocketConnection
import com.stratagile.pnrouter.data.web.FileWebSocketConnection
import com.stratagile.pnrouter.entity.LoginReq_V4
import com.stratagile.pnrouter.entity.ToxFileData
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

object ConstantValue {
        var encryptionType = "1" // 加密方式 0:RSA+AES  ; 1 : libsodium
        var isAntox = false   //是antox 还是自己封装的tox
        var fileNonce = "OmcKJrqehqQwNvdHkRBddXYyAvbGW2A1"
        var localPath = "/ARouterNewData"
        var httpUrl ="https://pprouter.online:9001/v1/pprmap/Check?rid="
        var isInit = false;
        var isRefeshed = false
        var testValue = "testValue"
        var fingerprintUnLock = "fingerprintUnLock"
        var fingerPassWord = "fingerPassWord" //指纹密码
        var userId = "UserId"
        var userIndex = "userIndex"
        var username = "username"
        var message = "message_"
        var userFriendname = "freindrname"
        var userFriendId = "userFriendId"
        var routerId = "routerId"
        var selfImageName = "selfImageName"
        var uploadAvatarMsgid = "abc12345678"
        var deleteMsgId = ""
        var sendToxFileDataMap = ConcurrentHashMap<String, ToxFileData>() //tox发送文件列表
        var LOCALVERSIONCODE = "localversioncode"
        var selectFriend = "selectFriend"
        var unlockTime = "unlocktime"
        var isUnLock = "isUnLock"
        var realKeyboardHeight = "realKeyboardHeight"
        var port =":18006"
        var currentIp= ""//当前选择的ip
        var filePort =":18007"
        var status = "status"
        var msgIndex:Int = 1 //客户端的消息ID
        var privateRASSp ="privateRAS"
        var publicRASSp ="publicRAS"
        var privateRAS:String? = ""
        var publicRAS:String? = ""
        var sendFileMsgMap = ConcurrentHashMap<String, EMMessage>();
        var mainActivity:Activity? = null
        var libsodiumprivateSignKeySp ="libsodiumprivateSignKeySp"
        var libsodiumpublicSignKeySp ="libsodiumpublicSignKeySp"
        var localUserNameSp ="localUserNameSp"
        var libsodiumprivateSignKey:String? = ""
        var libsodiumpublicSignKey:String? = ""
        var localUserName:String? = ""

        var sendFileSizeMaxoInner = 1024 * 1024 * 2  //内网缓存区2M
        var sendFileSizeMaxoOuterNet = 1024 * 500 //外网缓存区500k
        var sendFileSizeMax = 1024 * 1024 * 2   //实际使用的缓冲区
        var libsodiumprivateMiKeySp ="libsodiumprivateMiKeySp"
        var libsodiumpublicMiKeySp ="libsodiumpublicMiKeySp"
        var libsodiumprivateMiKey:String? = ""
        var libsodiumpublicMiKey:String? = ""
        //文件展示的排序方式，0 name， 1 time， 2 size
        var currentArrangeType = "currentArrangeType"

        var libsodiumprivateTemKey:String? = ""
        var libsodiumpublicTemKey:String? = ""
        var webSocketFileList : ArrayList<FileWebSocketConnection> = ArrayList()
        var webSockeFileMangertList : ArrayList<FileMangerWebSocketConnection> = ArrayList()
        var receiveToxFileGlobalDataMap = ConcurrentHashMap<String, String>()
        var unSendMessage: ConcurrentHashMap<String,String> = ConcurrentHashMap() //待发送消息
        var unSendMessageFriendId: ConcurrentHashMap<String,String> = ConcurrentHashMap() //待发送消息的路由器id
        var unSendMessageSendCount: ConcurrentHashMap<String,Int> = ConcurrentHashMap() //待发送消息重发次数
        var scanRouterId:String =""//二维码扫出来的路由id
        var scanRouterSN:String =""//二维码扫出来的usersn
        var currentRouterIp:String = ""//记录组播寻找到的路由器ip
        var localCurrentRouterIp:String = ""//记录组播寻找到的路由器ip
        var currentRouterId:String = ""//记录组播寻找到的路由器id
        var currentRouterSN:String = ""//记录组播寻找到的路由器SN  02000018B827EBD089CB00005BFB70B9     02000006B827EBD4703000005BFDF5E8 华为
        var currentRouterMac:String = ""//二维码路由Mac
        var isRegister:Boolean = false //记录当前选择路由器是否注册
        var isWebsocketConnected:Boolean = false
        var shouBegin:Long = 0;
        var shouEnd:Long = 0;
        var curreantNetworkType = "TOX"   //   WIFI 或者 TOX
        var isToxConnected = false
        var lastNetworkType= "" //路由器最后可走的网络类型
        var lastRouterIp= "" //路由器最后可走的网络RouterIp
        var lastRouterId= "" //路由器最后可走的网络RouterId
        var lastRouterSN= "" //路由器最后可走的网络RouterSN
        var lastPort= ""     //路由器最后可走的网络Port=
        var lastFilePort= "" //路由器最后可走的网络FilePort
        var isHasWebsocketInit = false  //是否走过websocket连接初始化
        var isHeart = false //是否可以发心跳
        var hasLogin = false  //是否登录过
        var loginReq: LoginReq_V4? = null  //登录过的账号数据
        var isWebsocketReConnect = false  //websocket是否重连过
        var isToxReConnect = false  //tox是否重连过
        var mRegId = ""
        var mHuaWeiRegId = ""
//        var pushURL = "http://47.96.76.184:9000/v1/pareg"
        var pushURL = "https://pprouter.online:9001/v1/pareg"
        var loginOut = false
        var logining = false
        var freindStatus = 0
        var autoLoginRouterSn = "autoLoginRouterSn"//设置的自动登录的路由器sn
        var isFormat = false
        var isReRegesterMiPush = "isReRegesterMiPush"

}
