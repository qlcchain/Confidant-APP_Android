package com.stratagile.pnrouter.constant

import java.util.HashMap

object ConstantValue {
        var localPath = "/RouterData3"
        var isInit = false;
        var isRefeshed = false
        var testValue = "testValue"
        var fingerprintUnLock = "fingerprintUnLock"
        var fingerPassWord = "fingerPassWord" //指纹密码
        var userId = "UserId"
        var username = "username"
        var routerId = "routerId"
        var selfImageName = "selfImageName"
        var msgId = ""
        var LOCALVERSIONCODE = "localversioncode"
        var selectFriend = "selectFriend"
        var realKeyboardHeight = "realKeyboardHeight"
        var port =":18006"
        var currentIp= ""//当前选择的ip
        var filePort =":18007"
        var status = "status"
        var msgIndex:Int = 1 //客户端的消息ID
        var privateRAS:String = ""
        var publicRAS:String = ""
        var updRouterData: HashMap<String,String> = HashMap()
        var currentRouterIp:String = "192.168.88.1"//记录组播寻找到的路由器ip
        var currentRouterId:String = "A1DA6FFE24611BDE1D14B55B02F180961A3DFB8C9C9B2A572EB274896B7EAC30B4CDCDCE68B8"//记录组播寻找到的路由器id
        var currentRouterSN:String = "02000001B827EBD4703000005BFBDB3B"//记录组播寻找到的路由器SN  02000018B827EBD089CB00005BFB70B9     01000017B827EBD089CB00005BFB7067 华为
        var isRegister:Boolean = false //记录当前选择路由器是否注册
        var isConnected:Boolean = false
        var shouBegin:Long = 0;
        var shouEnd:Long = 0;

}
