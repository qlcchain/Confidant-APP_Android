package com.stratagile.pnrouter.constant

import java.util.HashMap

object ConstantValue {
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
        var currentRouterId:String = "D2339E23514255AEE2FB35F21C54B50EC7B2E2A7DD33ABCFA83CF88077B208121E8DF0A5A472"//记录组播寻找到的路由器id
        var currentRouterSN:String = "02000001B827EBD089CB00005BEBF3BF"//记录组播寻找到的路由器SN
        var isRegister:Boolean = false //记录当前选择路由器是否注册
        var isConnected:Boolean = false;

}
