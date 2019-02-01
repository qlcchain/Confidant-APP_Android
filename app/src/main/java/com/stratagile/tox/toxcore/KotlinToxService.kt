package com.stratagile.tox.toxcore

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import com.google.gson.Gson
import com.socks.library.KLog
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.tox.entity.DhtJson
import com.stratagile.tox.toxcallback.ToxCallbackListener
import com.stratagile.tox.toxcallback.ToxConnection
import java.io.File
import kotlin.concurrent.thread

class KotlinToxService : Service() {
    lateinit var thread : Thread
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val toxCallbackListener = ToxCallbackListener()
        val dataFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath, "")
        if (!dataFile.exists()) {
            dataFile.mkdir()
        }
        val path = dataFile.path + "/"
        ToxCoreJni.getInstance().toxCallbackListener = toxCallbackListener
        thread = thread(true, false, null, "toxThread", -1) {
            ToxCoreJni.getInstance().createTox(path)
        }
    }

    override fun onDestroy() {
        ToxCoreJni.getInstance().toxKill()
        ToxCoreJni.getInstance().toxCallbackListener = null

        super.onDestroy()
    }
}