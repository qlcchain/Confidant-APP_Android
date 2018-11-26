package com.stratagile.pnrouter.constant

import java.util.HashMap

object ConstantValue {
        var localPath = "/RouterData2"
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
        var currentRouterIp:String = "192.168.1.103"//记录组播寻找到的路由器ip
        var currentRouterId:String = "3C285DE0D481DBBEE4A5A64B38D82E06F1C4A95A31228ED29BF4AED01F63E2619CEAD35B5A3E"//记录组播寻找到的路由器id
        var currentRouterSN:String = "01000017B827EBD089CB00005BFB7067"//记录组播寻找到的路由器SN  02000018B827EBD089CB00005BFB70B9     01000017B827EBD089CB00005BFB7067 华为
        var isRegister:Boolean = false //记录当前选择路由器是否注册
        var isConnected:Boolean = false
        var shouBegin:Long = 0;
        var shouEnd:Long = 0;

}
