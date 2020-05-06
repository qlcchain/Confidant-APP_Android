package com.stratagile.pnrouter.constant

import android.app.Activity
import com.hyphenate.chat.EMMessage
import com.stratagile.pnrouter.data.web.FileMangerWebSocketConnection
import com.stratagile.pnrouter.data.web.FileWebSocketConnection
import com.stratagile.pnrouter.db.EmailConfigEntity
import com.stratagile.pnrouter.db.EmailMessageEntity
import com.stratagile.pnrouter.entity.LoginReq_V4
import com.stratagile.pnrouter.entity.ToxFileData
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

object ConstantValue {
    var encryptionType = "1" // 加密方式 0:RSA+AES  ; 1 : libsodium
    var isAntox = false   //是antox 还是自己封装的tox
    var fileNonce = "OmcKJrqehqQwNvdHkRBddXYyAvbGW2A1"
    var qlcNode = "http://47.103.54.171:29735"//"http://47.103.54.171:29735"李超
    var localPath = "/ARouterNewData"
    var httpUrl ="https://pprouter.online:9001/v1/pprmap/Check?rid="
    var httpMacTestUrl ="https://47.96.76.184:9001/v1/pprmap/"
    var httpMacUrl ="https://pprouter.online:9001/v1/pprmap/"
    var isInit = false;
    var helpUrl = "https://myconfidant.io/support"
    var isRefeshed = false
    var shareFromLocalPath = ""///storage/emulated/0/Huawei/MagazineUnlock/magazine-unlock-05-2.3.1311-_F195D9518633FCDD030C4B23A3B2FCE5.jpg
    var testValue = "testValue"
    var fingerprintUnLock = "fingerprintUnLock"
    var fingerPassWord = "fingerPassWord" //指纹密码
    var userId = "UserId"
    var userSnSp = "userSnSp"
    var userIndex = "userIndex"
    var username = "username"
    var message = "message_"
    var messageAT = "messageAT_"
    var userFriendname = "freindrname"
    var userFriendId = "userFriendId"
    var routerId = "routerId"
    var selfImageName = "selfImageName"
    var uploadAvatarMsgid = "abc12345678"
    var deleteMsgId = ""
    var sendToxFileDataMap = ConcurrentHashMap<String, ToxFileData>() //点对点tox发送文件列表
    var sendToxFileInGroupChapDataMap = ConcurrentHashMap<String, ToxFileData>() //群聊tox发送文件列表
    var LOCALVERSIONCODE = "localversioncode"
    var selectFriend = "selectFriend"
    var unlockTime = "unlocktime"
    var isUnLock = "isUnLock"
    var realKeyboardHeight = "realKeyboardHeight"
    var port =":18006"
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
    var oracleEmailQlcAdress = "qlc_38k6bk5wh9tpfh57wb3nicg9wyap4iaqhx9qxg18dz7u94cx7deweyhdsn6x";
    var oracleEmailAdress = "19464572@qq.com";
    var oracleEmailCode  =""
    var oracleEmailPubKey = ""
    var oracleEmailhash = ""
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
    var isCurrentRouterAdmin = false;
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
    var mJiGuangRegId = ""
    //        var pushURL = "http://47.96.76.184:9000/v1/pareg"
    var pushURL = "https://pprouter.online:9001/v1/pareg"
    var loginOut = false//是否登出
    var logining = false//登录状态
    var freindStatus = 0
    var autoLoginRouterSn = "autoLoginRouterSn"//设置的自动登录的路由器sn
    var fingerprintSetting = "fingerprintSetting"//指纹设置
    var screenshotsSetting = "screenshotsSetting"//截屏设置
    var isFormat = false
    var isReRegesterMiPush = "isReRegesterMiPush"
    var isShowVerify = false
    var fileNameMaxLen = 80//文件名最大长度
    var atMaxNum = 5 //群聊中@最大数量
    var notNeedVerify = false  //是否支持指纹和密码
    var sendMaxSize = 50000  //发送文字消息的最大字节数 原来是264 * 2，现在是5万
    var chooseEmailMenu = 0;//默认选择收件箱
    var chooseEmailMenuName = "Inbox";//默认选择收件箱
    var chooseEmailMenuServer = "INBOX";//默认选择收件箱
    var chooseFragMentMenu = "Circle";//默认选择Circle
    var sinaMenu = arrayOf("INBOX","节点","星标邮件","草稿夹","已发送","垃圾邮件","已删除");
    var qqMenu = arrayOf("INBOX","节点","星标邮件","Drafts","Sent Messages","Junk","Deleted Messages");
    var currentEmailConfigEntity:EmailConfigEntity ?= null
    var isNewUser = false
    var isGooglePlayServicesAvailable = true; //是否支持googleypaly
    var waitAddFreind = "";

    var firstOpenApp = "firstOpenApp"
    //1：qq企业邮箱
    //2：qq邮箱
    //3：163邮箱
    //4：gmail邮箱

}
