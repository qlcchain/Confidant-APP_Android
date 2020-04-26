package com.stratagile.pnrouter.data.api

import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.VersionUtil

object DotLog {
    const val typeAction = 0
    const val typeSuccess = 100
    const val typeFailed = 0xff
    const val resultSucceess = 0

    const val PNR_IM_CMDTYPE_LOGIN = 1
    const val PNR_IM_CMDTYPE_DESTORY = 2
    const val PNR_IM_CMDTYPE_ADDFRIENDREQ = 3
    const val PNR_IM_CMDTYPE_ADDFRIENDPUSH = 4
    const val PNR_IM_CMDTYPE_ADDFRIENDDEAL = 5
    const val PNR_IM_CMDTYPE_ADDFRIENDREPLY = 6
    const val PNR_IM_CMDTYPE_DELFRIENDCMD = 7
    const val PNR_IM_CMDTYPE_DELFRIENDPUSH = 8
    const val PNR_IM_CMDTYPE_SENDMSG = 9
    const val PNR_IM_CMDTYPE_PULLMSG = 18
    const val PNR_IM_CMDTYPE_PULLFRIEND = 19
    const val PNR_IM_CMDTYPE_REGISTER = 27
    const val PNR_IM_CMDTYPE_GROUPLISTPULL = 56
    const val PNR_IM_CMDTYPE_GROUPUSERPULL = 57
    const val PNR_IM_CMDTYPE_GROUPMSGPULL = 58

    const val PNR_IM_CMDTYPE_LOGIN_str = "PNR_IM_CMDTYPE_LOGIN"
    const val PNR_IM_CMDTYPE_DESTORY_str = "PNR_IM_CMDTYPE_DESTORY"
    const val PNR_IM_CMDTYPE_ADDFRIENDREQ_str = "PNR_IM_CMDTYPE_ADDFRIENDREQ"
    const val PNR_IM_CMDTYPE_ADDFRIENDPUSH_str = "PNR_IM_CMDTYPE_ADDFRIENDPUSH"
    const val PNR_IM_CMDTYPE_ADDFRIENDDEAL_str = "PNR_IM_CMDTYPE_ADDFRIENDDEAL"
    const val PNR_IM_CMDTYPE_ADDFRIENDREPLY_str = "PNR_IM_CMDTYPE_ADDFRIENDREPLY"
    const val PNR_IM_CMDTYPE_DELFRIENDCMD_str = "PNR_IM_CMDTYPE_DELFRIENDCMD"
    const val PNR_IM_CMDTYPE_DELFRIENDPUSH_str = "PNR_IM_CMDTYPE_DELFRIENDPUSH"
    const val PNR_IM_CMDTYPE_SENDMSG_str = "PNR_IM_CMDTYPE_SENDMSG"
    const val PNR_IM_CMDTYPE_PULLMSG_str = "PNR_IM_CMDTYPE_PULLMSG"
    const val PNR_IM_CMDTYPE_PULLFRIEND_str = "PNR_IM_CMDTYPE_PULLFRIEND"
    const val PNR_IM_CMDTYPE_REGISTER_str = "PNR_IM_CMDTYPE_REGISTER"
    const val PNR_IM_CMDTYPE_GROUPLISTPULL_str = "PNR_IM_CMDTYPE_GROUPLISTPULL"
    const val PNR_IM_CMDTYPE_GROUPUSERPULL_str = "PNR_IM_CMDTYPE_GROUPUSERPULL"
    const val PNR_IM_CMDTYPE_GROUPMSGPULL_str = "PNR_IM_CMDTYPE_GROUPMSGPULL"

    fun generateNewId() : Int{
        var timeStamp = System.currentTimeMillis()
        return (timeStamp - ((timeStamp / 100000000L) * 100000000L)).toInt()
    }

    var idMap = hashMapOf<String, MutableList<Int>>()

    fun init() {
        idMap = hashMapOf()
        idMap[PNR_IM_CMDTYPE_LOGIN_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_DESTORY_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_ADDFRIENDREQ_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_ADDFRIENDPUSH_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_ADDFRIENDDEAL_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_ADDFRIENDREPLY_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_DELFRIENDCMD_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_DELFRIENDPUSH_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_SENDMSG_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_PULLMSG_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_PULLFRIEND_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_REGISTER_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_GROUPLISTPULL_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_GROUPUSERPULL_str] = mutableListOf()
        idMap[PNR_IM_CMDTYPE_GROUPMSGPULL_str] = mutableListOf()
    }

    fun getId(action : String) : Int{
        if (idMap[action]!!.size == 0) {
            return 0
        } else {
            var id = idMap[action]!![0]
            idMap[action]!!.remove(0)
            return id
        }
    }

    fun shouldsendBackLog(orginId : Int) : Boolean {
        var currentTime = generateNewId()
        return currentTime - orginId <= 10000
    }

    fun receiveLog(parameStr : String) {
        try {
            var action = JSONObject.parseObject(parameStr).getString("Action")
            KLog.i(action)
            var retCode = JSONObject.parseObject(parameStr).getIntValue("RetCode")
            if (retCode == resultSucceess) {
                when(action) {
                    "Login" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_LOGIN_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_LOGIN, orginId, typeSuccess, retCode)
                        }
                    }
                    "AddFriendReq" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_ADDFRIENDREQ_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_ADDFRIENDREQ, orginId, typeSuccess, retCode)
                        }
                    }
                    "AddFriendDeal" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_ADDFRIENDDEAL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_ADDFRIENDDEAL, orginId, typeSuccess, retCode)
                        }
                    }
                    "DelFriendCmd" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_DELFRIENDCMD_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_DELFRIENDCMD, orginId, typeSuccess, retCode)
                        }
                    }
                    "SendMsg" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_SENDMSG_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_SENDMSG, orginId, typeSuccess, retCode)
                        }
                    }
                    "PULLMSG" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_PULLMSG_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_PULLMSG, orginId, typeSuccess, retCode)
                        }
                    }
                    "PullFriend" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_PULLFRIEND_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_PULLFRIEND, orginId, typeSuccess, retCode)
                        }
                    }
                    "Register" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_REGISTER_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_REGISTER, orginId, typeSuccess, retCode)
                        }
                    }
                    "GroupListPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPLISTPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPLISTPULL, orginId, typeSuccess, retCode)
                        }
                    }
                    "GroupUserPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPUSERPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPUSERPULL, orginId, typeSuccess, retCode)
                        }
                    }
                    "GroupMsgPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPMSGPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPMSGPULL, orginId, typeSuccess, retCode)
                        }
                    }
                }
            } else {
                when(action) {
                    "Login" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_LOGIN_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_LOGIN, orginId, typeFailed, retCode)
                        }
                    }
                    "AddFriendReq" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_ADDFRIENDREQ_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_ADDFRIENDREQ, orginId, typeFailed, retCode)
                        }
                    }
                    "AddFriendDeal" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_ADDFRIENDDEAL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_ADDFRIENDDEAL, orginId, typeFailed, retCode)
                        }
                    }
                    "DelFriendCmd" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_DELFRIENDCMD_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_DELFRIENDCMD, orginId, typeFailed, retCode)
                        }
                    }
                    "SendMsg" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_SENDMSG_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_SENDMSG, orginId, typeFailed, retCode)
                        }
                    }
                    "PULLMSG" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_PULLMSG_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_PULLMSG, orginId, typeFailed, retCode)
                        }
                    }
                    "PullFriend" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_PULLFRIEND_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_PULLFRIEND, orginId, typeFailed, retCode)
                        }
                    }
                    "Register" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_REGISTER_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_REGISTER, orginId, typeFailed, retCode)
                        }
                    }
                    "GroupListPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPLISTPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPLISTPULL, orginId, typeFailed, retCode)
                        }
                    }
                    "GroupUserPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPUSERPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPUSERPULL, orginId, typeFailed, retCode)
                        }
                    }
                    "GroupMsgPull" -> {
                        var orginId = getId(PNR_IM_CMDTYPE_GROUPMSGPULL_str)
                        if (shouldsendBackLog(orginId)) {
                            uLogStr(PNR_IM_CMDTYPE_GROUPMSGPULL, orginId, typeFailed, retCode)
                        }
                    }
                }
            }

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun sendLog(baseData: BaseData) {
        try {
            var action = JSONObject.parseObject(Gson().toJson(baseData.params)).getString("Action")
            KLog.i(action)
            var logId = generateNewId()
            when(action) {
                "Login" -> {
                    idMap[PNR_IM_CMDTYPE_LOGIN_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_LOGIN, logId, typeAction, 0, "start login")
                }
                "AddFriendReq" -> {
                    idMap[PNR_IM_CMDTYPE_ADDFRIENDREQ_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_ADDFRIENDREQ, logId, typeAction, 0, "")
                }
                "AddFriendDeal" -> {
                    idMap[PNR_IM_CMDTYPE_ADDFRIENDDEAL_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_ADDFRIENDDEAL, logId, typeAction, 0, "")
                }
                "DelFriendCmd" -> {
                    idMap[PNR_IM_CMDTYPE_DELFRIENDCMD_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_DELFRIENDCMD, logId, typeAction, 0, "")
                }
                "SendMsg" -> {
                    idMap[PNR_IM_CMDTYPE_SENDMSG_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_SENDMSG, logId, typeAction, 0, "")
                }
                "PullFriend" -> {
                    idMap[PNR_IM_CMDTYPE_PULLFRIEND_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_PULLFRIEND, logId, typeAction, 0, "")
                }
                "Register" -> {
                    idMap[PNR_IM_CMDTYPE_REGISTER_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_REGISTER, logId, typeAction, 0, "")
                }
                "GroupListPull" -> {
                    idMap[PNR_IM_CMDTYPE_GROUPLISTPULL_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_GROUPLISTPULL, logId, typeAction, 0, "")
                }
                "GroupUserPull" -> {
                    idMap[PNR_IM_CMDTYPE_GROUPUSERPULL_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_GROUPUSERPULL, logId, typeAction, 0, "")
                }
                "GroupMsgPull" -> {
                    idMap[PNR_IM_CMDTYPE_GROUPMSGPULL_str]!!.add(logId)
                    uLogStr(PNR_IM_CMDTYPE_GROUPMSGPULL, logId, typeAction, 0, "")
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    /**
     *
     */
    fun uLogStr(action : Int, id : Int, type : Int, result : Int, info : String = "") {
        var map = hashMapOf<String, String>()
        map["app"] = 1.toString()
        map["action"] = action.toString()
        map["id"] = id.toString()
        map["level"] = 1.toString()
        map["version"] = VersionUtil.getAppVersionCode(AppConfig.instance).toString()
        map["timestamp"] = System.currentTimeMillis().toString()
        map["type"] = type.toString()
        map["result"] = result.toString()
        map["user"] = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        map["node"] = SpUtil.getString(AppConfig.instance, ConstantValue.routerId,"")!!
        map["info"] = info
        KLog.i("action = " + action.toString())
        KLog.i("id = " + id.toString())
        KLog.i("----------------")
        AppConfig.instance.applicationComponent!!.httpApiWrapper.uLogStr(map).subscribe({

        }, {
            it.printStackTrace()
        }, {

        })
    }

}