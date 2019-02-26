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

class MyService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        KLog.i("MyService_onStart")
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}