package com.stratagile.pnrouter.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.FileMangerWebSocketConnection
import com.stratagile.pnrouter.entity.events.FileMangerTransformEntity
import com.stratagile.pnrouter.entity.events.FileMangerTransformMessage
import com.stratagile.pnrouter.entity.events.FileMangerTransformReceiverMessage
import com.stratagile.pnrouter.entity.events.LogOutEvent
import com.stratagile.pnrouter.utils.FileMangerUtil
import events.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FileDownloadUploadService : Service() {
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
        ConstantValue.webSockeFileMangertList = ArrayList()
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
            ConstantValue.webSockeFileMangertList.add(fileWebSocketConnection)
        } else if (fileMangerTransformEntity.message == 4) {
            ConstantValue.webSockeFileMangertList.forEach {
                if (it.toId.equals(fileMangerTransformEntity.toId)) {
                    it.disconnect(true)
                    ConstantValue.webSockeFileMangertList.remove(it)
                    return
                }
            }
        }
        FileMangerUtil.onFileMangerTransformEntity(fileMangerTransformEntity)
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun sendMessage(fileMangertransfromMessage: FileMangerTransformMessage) {
        ConstantValue.webSockeFileMangertList.forEach {
            if (it.toId.equals(fileMangertransfromMessage.toId) && fileMangertransfromMessage!= null && fileMangertransfromMessage.message!= null) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber=  toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        FileMangerUtil.onToxFileSendFinished(fileNumber,key)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendFileProgressEvent(toxSendFileProgressEvent: ToxSendFileProgressEvent) {
        FileMangerUtil.onToxSendFileProgressEvent(toxSendFileProgressEvent.fileNumber,toxSendFileProgressEvent.key,toxSendFileProgressEvent.position,toxSendFileProgressEvent.filesize)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun OnToxReceiveFileNoticeEvent(toxReceiveFileNoticeEvent: ToxReceiveFileNoticeEvent) {
        var fileNumber=  toxReceiveFileNoticeEvent.fileNumber
        var key = toxReceiveFileNoticeEvent.key
        var fileName = toxReceiveFileNoticeEvent.filename
        FileMangerUtil.onAgreeReceivwFileStart(fileNumber,key,fileName)

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxReceiveFileFinishedEvent(toxReceiveFileFinishedEvent: ToxReceiveFileFinishedEvent) {
        var fileNumber=  toxReceiveFileFinishedEvent.fileNumber
        var key = toxReceiveFileFinishedEvent.key
        FileMangerUtil.onToxReceiveFileFinishedEvent(fileNumber,key)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxReceiveFileProgressEvent(toxReceiveFileProgressEvent: ToxReceiveFileProgressEvent) {
        FileMangerUtil.onToxReceiveFileProgressEvent(toxReceiveFileProgressEvent.fileNumber,toxReceiveFileProgressEvent.key,toxReceiveFileProgressEvent.position,toxReceiveFileProgressEvent.filesize)
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun stopAllWebSocket(logOutEvent: LogOutEvent)
    {

    }
}