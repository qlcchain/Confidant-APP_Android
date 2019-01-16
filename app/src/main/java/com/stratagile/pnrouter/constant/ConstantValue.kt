package com.stratagile.pnrouter.constant

import com.stratagile.pnrouter.entity.LoginReq
import java.util.HashMap

object ConstantValue {
        var encryptionType = "0" // 加密方式 0:RSA+AES  ; 1 : libsodium
        var localPath = "/RouterData13"
        var httpUrl ="https://pprouter.online:9001/v1/pprmap/Check?rid="
        var isInit = false;
        var isRefeshed = false
        var testValue = "testValue"
        var fingerprintUnLock = "fingerprintUnLock"
        var fingerPassWord = "fingerPassWord" //指纹密码
        var userId = "UserId"
        var username = "username"
        var message = "message_"
        var userFriendname = "freindrname"
        var userFriendId = "userFriendId"
        var routerId = "routerId"
        var selfImageName = "selfImageName"
        var deleteMsgId = ""
        var LOCALVERSIONCODE = "localversioncode"
        var selectFriend = "selectFriend"
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

        var libsodiumprivateKeySp ="libsodiumprivateKeySp"
        var libsodiumpublicKeySp ="libsodiumpublicKeySp"
        var libsodiumprivateKey:String? = ""
        var libsodiumpublicKey:String? = ""

        var updRouterData: HashMap<String,String> = HashMap()
        var scanRouterId:String =""//二维码扫出来的路由id
        var scanRouterSN:String =""//二维码扫出来的usersn
        var currentRouterIp:String = ""//记录组播寻找到的路由器ip
        var currentRouterId:String = ""//记录组播寻找到的路由器id
        var currentRouterSN:String = ""//记录组播寻找到的路由器SN  02000018B827EBD089CB00005BFB70B9     02000006B827EBD4703000005BFDF5E8 华为
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
        var loginReq:LoginReq? = null  //登录过的账号数据
        var isWebsocketReConnect = false  //websocket是否重连过
        var isToxReConnect = false  //tox是否重连过
        var mRegId = ""
        var pushURL = "http://47.96.76.184:9000/v1/pareg"
        var loginOut = false
        var freindStatus = 0

}
