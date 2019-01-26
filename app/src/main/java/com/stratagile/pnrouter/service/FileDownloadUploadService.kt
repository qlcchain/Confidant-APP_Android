package com.stratagile.pnrouter.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.data.web.FileWebSocketConnection
import com.stratagile.pnrouter.entity.events.FileTransformEntity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FileDownloadUploadService : Service() {

    override fun onBind(p0: Intent?): IBinder? {

        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun startNewConnect(fileTransformEntity: FileTransformEntity) {
        if (fileTransformEntity.message == 0) {
            KLog.i("收到eventbus消息。。")
            val fileWebSocketConnection = FileWebSocketConnection(fileTransformEntity.httpUrl, AppConfig.instance.messageReceiver!!.getTrustStore(), fileTransformEntity.userAgent,null)
            fileWebSocketConnection.connect()
            fileWebSocketConnection.toId = fileTransformEntity.toId
        } else if (fileTransformEntity.message == 4) {

        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}