package com.stratagile.tox.toxcore

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import com.google.gson.Gson
import com.socks.library.KLog
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.tox.entity.DhtJson
import com.stratagile.tox.toxcallback.ToxCallbackListener
import com.stratagile.tox.toxcallback.ToxConnection
import java.io.File
import kotlin.concurrent.thread

class KotlinToxService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val toxCallbackListener = ToxCallbackListener()
        val dataFile = File(Environment.getExternalStorageDirectory().toString() + "/toxCoreNew", "")
        if (!dataFile.exists()) {
            dataFile.mkdir()
        }
        val path = dataFile.path + "/"
        thread {
            ToxCoreJni.getInstance().createTox(path)
            ToxCoreJni.getInstance().iterate()
        }
        ToxCoreJni.getInstance().toxCallbackListener = toxCallbackListener
        thread {
            bootStrap()
        }
    }

    private fun bootStrap() {
        var count = 0
        while (ToxCoreJni.connectStatus == ToxConnection.NONE && count < 10) {
            KLog.i("开始引导")
            val toxJson = FileUtil.getAssetJson(this, "tox.json")
            val dhtJson = Gson().fromJson<DhtJson>(toxJson, DhtJson::class.java)
            var bootstrapped = false
            dhtJson.nodes.forEach {
                var sucess = ToxCoreJni.getInstance().bootStrap(it.ipv4, it.port, it.public_key)
                KLog.i("引导 " + it.ipv4 + " " + sucess)
                if (sucess == 1) {
                    bootstrapped = true
                }
            }
            if (bootstrapped) {
                KLog.i("bootstrapped 成功")
            } else {
                KLog.i("bootstrapped 失败")
            }
            count++
        }
    }

    override fun onDestroy() {
        ToxCoreJni.getInstance().toxCallbackListener = null

        super.onDestroy()
    }
}