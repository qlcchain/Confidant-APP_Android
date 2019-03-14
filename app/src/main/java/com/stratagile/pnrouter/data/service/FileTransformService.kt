package com.stratagile.pnrouter.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.FileWebSocketConnection
import com.stratagile.pnrouter.entity.events.FileTransformEntity
import com.stratagile.pnrouter.entity.events.LogOutEvent
import com.stratagile.pnrouter.entity.events.TransformFileMessage
import com.stratagile.pnrouter.entity.events.TransformStrMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FileTransformService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        ConstantValue.webSocketFileList = ArrayList()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun startNewConnect(fileTransformEntity: FileTransformEntity) {
        if (fileTransformEntity.message == 0) {
            KLog.i("收到eventbus消息。。")
            val fileWebSocketConnection = FileWebSocketConnection(fileTransformEntity.httpUrl, AppConfig.instance.messageReceiver!!.getTrustStore(), fileTransformEntity.userAgent,null)
            fileWebSocketConnection.connect()
            fileWebSocketConnection.toId = fileTransformEntity.toId
            ConstantValue.webSocketFileList.add(fileWebSocketConnection)
        } else if (fileTransformEntity.message == 4) {
            ConstantValue.webSocketFileList.forEach {
                if (it!= null && it.toId.equals(fileTransformEntity.toId)) {
                    it.disconnect(true)
                    ConstantValue.webSocketFileList.remove(it)
                    return
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun sendMessage(transformStrMessage: TransformStrMessage) {
        ConstantValue.webSocketFileList.forEach {
            if (it.toId.equals(transformStrMessage.toId)) {
                it.send(transformStrMessage.message)
                return
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun sendMessage(transfromFileMessage: TransformFileMessage) {
        ConstantValue.webSocketFileList.forEach {
            if(it!= null && transfromFileMessage != null )
            {
                if (it.toId.equals(transfromFileMessage.toId)) {
                    it.sendByteString(transfromFileMessage.message)
                    return
                }
            }else{
                KLog.i("FileWebSocketConnection或transfromFileMessage为空")
            }

        }
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun stopAllWebSocket(logOutEvent: LogOutEvent)
    {

    }
}