package com.stratagile.pnrouter.data.tox

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.data.web.WebSocketConnection
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JHeartBeatRsp
import com.stratagile.pnrouter.utils.GsonUtil
import com.stratagile.pnrouter.utils.LogUtil
import events.ToxMessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ToxMessageReceiver(){



    init {
        EventBus.getDefault().register(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxMessageEvent: ToxMessageEvent) {
        var text = toxMessageEvent.message
        if(text!!.indexOf("HeartBeat") < 0)
        {
            Log.w(ToxMessageReceiver.TAG, "onMessage(text)! " + text!!)
        }
        LogUtil.addLog("接收信息：${text}")
        try {
            val gson = GsonUtil.getIntGson()
            var baseData = gson.fromJson(text, BaseData::class.java)
            if (JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action").equals("HeartBeat")) {
                val heartBeatRsp  = gson.fromJson(text, JHeartBeatRsp::class.java)
                if (heartBeatRsp.params.retCode == 0) {
                    //KLog.i("心跳监测和服务器的连接正常~~~")
                }
            } else {
                AppConfig.instance.onToxMessageReceiveListener!!.onMessage(baseData, text)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
    companion object {

        private val TAG = ToxMessageReceiver::class.java.simpleName
        private val KEEPALIVE_TIMEOUT_SECONDS = 30
    }
    interface OnMessageReceiveListener {
        fun onMessage(message : BaseData, text: String?)
    }
}