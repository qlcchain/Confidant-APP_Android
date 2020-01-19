package com.stratagile.tox.toxcallback

import com.socks.library.KLog
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.entity.events.ToxFriendStatusEvent
import com.stratagile.pnrouter.entity.events.ToxMessageEvent
import com.stratagile.pnrouter.entity.events.ToxStatusEvent
import org.greenrobot.eventbus.EventBus

class OnConnectionStatusCallback {
    fun onConnectionStatusCallback(freindId : String, toxConnection: ToxConnection) {
        KLog.i("好友id：" + freindId)
        KLog.i("好友连接状态：" + toxConnection)
        LogUtil.addLog("tox好友id：" + freindId)
        LogUtil.addLog("tox好友连接状态：" + toxConnection)
        if (toxConnection.ordinal == 0) {
            EventBus.getDefault().post(ToxFriendStatusEvent(0))
        } else {
            EventBus.getDefault().post(ToxFriendStatusEvent(1))
        }
    }
}

class OnMessageCallback {
    fun onMessageCallback(friendId : String, message : String) {
        KLog.i("收到好友的消息：" + message)
        KLog.i("好友的id为：" + friendId)
        EventBus.getDefault().post(ToxMessageEvent(message, friendId))
    }
}

class OnSelfConnectionStatusCallback {
    var selfConnectStatus : ToxConnection = ToxConnection.NONE
    fun onSelfConnectionStatusCallback(toxConnection: ToxConnection) {
        KLog.i("自己的tox状态为：" + toxConnection.ordinal)
        if (toxConnection.ordinal == 0) {
            EventBus.getDefault().post(ToxStatusEvent(1))
        } else {
            EventBus.getDefault().post(ToxStatusEvent(0))
        }
    }
}