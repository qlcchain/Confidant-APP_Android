package com.stratagile.pnrouter.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.data.web.FileMangerWebSocketConnection
import com.stratagile.pnrouter.entity.events.FileMangerTransformEntity
import com.stratagile.pnrouter.entity.events.FileMangerTransformMessage
import com.stratagile.pnrouter.entity.events.FileMangerTransformReceiverMessage
import com.stratagile.pnrouter.utils.FileMangerUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FileDownloadUploadService : Service() {
    lateinit var webSocketList : ArrayList<FileMangerWebSocketConnection>
    override fun onBind(p0: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()
    }
    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        webSocketList = ArrayList()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun startNewConnect(fileMangerTransformEntity: FileMangerTransformEntity) {
        if (fileMangerTransformEntity.message == 0) {
            KLog.i("收到eventbus消息。。")
            val fileWebSocketConnection = FileMangerWebSocketConnection(fileMangerTransformEntity.httpUrl, AppConfig.instance.messageReceiver!!.getTrustStore(), fileMangerTransformEntity.userAgent,null)
            fileWebSocketConnection.connect()
            fileWebSocketConnection.toId = fileMangerTransformEntity.toId
            webSocketList.add(fileWebSocketConnection)
        } else if (fileMangerTransformEntity.message == 4) {
            webSocketList.forEach {
                if (it.toId.equals(fileMangerTransformEntity.toId)) {
                    it.disconnect(true)
                    webSocketList.remove(it)
                    return
                }
            }
        }
        FileMangerUtil.onFileMangerTransformEntity(fileMangerTransformEntity)
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun sendMessage(fileMangertransfromMessage: FileMangerTransformMessage) {
        webSocketList.forEach {
            if (it.toId.equals(fileMangertransfromMessage.toId)) {
                it.sendByteString(fileMangertransfromMessage.message)
                return
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectWebSocket(transformReceiverFileMessage : FileMangerTransformReceiverMessage)
    {
        FileMangerUtil.onFileMangerTransformReceiverMessage(transformReceiverFileMessage)
    }
}