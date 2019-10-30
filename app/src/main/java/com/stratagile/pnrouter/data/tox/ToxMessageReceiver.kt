package com.stratagile.pnrouter.data.tox

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.HeartBeatReq
import com.stratagile.pnrouter.entity.JHeartBeatRsp
import com.stratagile.pnrouter.entity.events.ToxMessageEvent
import com.stratagile.pnrouter.entity.events.ToxStatusEvent
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ToxMessageReceiver(){

    private var keepAliveSender: ToxMessageReceiver.KeepAliveSender? = null
    var segmentContent = ""
    init {
        EventBus.getDefault().register(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        when (toxStatusEvent.status) {
            0 -> {
                LogUtil.addLog("P2P连接成功","ToxMessageReceiver")
                ConstantValue.isToxConnected = true
                keepAliveSender = KeepAliveSender()
                keepAliveSender!!.start()
                if(ConstantValue.isToxReConnect && ConstantValue.loginReq != null && ConstantValue.hasLogin)
                {
                    var loginReq = ConstantValue.loginReq
                    LogUtil.addLog("Tox重连发送登录信息：${loginReq!!.baseDataToJson().replace("\\", "")}")
                    var baseDataJson = BaseData(4,loginReq).baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
            }
            1 -> {
                if(ConstantValue.isToxConnected)
                {
                    ConstantValue.isToxReConnect = true
                }
                LogUtil.addLog("P2P连接中Reconnecting:","ToxMessageReceiver")
            }
            2-> {
                LogUtil.addLog("P2P未连接:","ToxMessageReceiver")
                ConstantValue.isToxConnected = false
                if (keepAliveSender != null) {
                    keepAliveSender!!.shutdown()
                    keepAliveSender = null
                }
            }
            3-> {
                LogUtil.addLog("P2P连接网络错误:","ToxMessageReceiver")
                ConstantValue.isToxConnected = false
                if (keepAliveSender != null) {
                    keepAliveSender!!.shutdown()
                    keepAliveSender = null
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxMessageEvent: ToxMessageEvent) {
        var text = toxMessageEvent.message
        if(text!!.indexOf("HeartBeat") < 0)
        {
            //Log.w(ToxMessageReceiver.TAG, "onMessage(text)! " + text!!)
            LogUtil.addLog("TOX接收信息：${text}")
        }

        try {
            val gson = GsonUtil.getIntGson()
            var baseData = gson.fromJson(text, BaseData::class.java)
            val newTextaa = gson.toJson(baseData)
            if(baseData.more == null)
            {
                if (JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action").equals("HeartBeat")) {
                    val heartBeatRsp  = gson.fromJson(text, JHeartBeatRsp::class.java)
                    if (heartBeatRsp.params.retCode == 0) {
                        //KLog.i("心跳监测和服务器的连接正常~~~")
                    }
                } else {
                    text = text.replace("\\\\n", "").replace("\\n", "")
                    if(AppConfig.instance.onToxMessageReceiveListener != null)
                        AppConfig.instance.onToxMessageReceiveListener!!.onMessage(baseData, text)
                }
            }else{

                if(baseData.more == 1)
                {
                    segmentContent +=baseData.params.toString()
                    baseData.params = ""
                    baseData.offset =  baseData.offset!! + 1100
                    var aa = 110
                    var baseDataJson = baseData.baseDataToJson().replace("\\\\n", "").replace("\\n", "")
                    if (ConstantValue.isAntox) {
                        //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }else{
                    segmentContent +=baseData.params;
                    if(segmentContent.equals(""))
                    {
                        if(AppConfig.instance.onToxMessageReceiveListener != null)
                            AppConfig.instance.onToxMessageReceiveListener!!.onMessage(baseData, text)
                    }
                    else{
                        var test =  JSON.toJSONString(baseData)
                        baseData.params = segmentContent
                        baseData.params = segmentContent.replace("\\\\n", "").replace("\\", "")
                        val newText = gson.toJson(baseData)
                        var newText2 = newText.replace("\\\"", "\"").replace("\"params\":\"","\"params\":").replace("\",\"timestamp\"",",\"timestamp\"")
                        segmentContent = ""
                        if(AppConfig.instance.onToxMessageReceiveListener != null)
                            AppConfig.instance.onToxMessageReceiveListener!!.onMessage(baseData, newText2)
                    }

                }


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
    private inner class KeepAliveSender : Thread() {

        private val stop = AtomicBoolean(false)

        override fun run() {
            while (!stop.get()) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(ToxMessageReceiver.KEEPALIVE_TIMEOUT_SECONDS.toLong()))

                    //Log.w(TAG, "Sending keep alive...")
                    sendKeepAlive()
                } catch (e: Throwable) {
                    Log.w(ToxMessageReceiver.TAG, e)
                }

            }
        }

        fun shutdown() {
            stop.set(true)
        }
    }
    @Synchronized
    @Throws(IOException::class)
    private fun sendKeepAlive() {
        if (keepAliveSender != null && ConstantValue.isToxConnected && !ConstantValue.loginOut) {
            //todo keepalive message
            if (ConstantValue.curreantNetworkType == "TOX" && ConstantValue.isToxConnected && !ConstantValue.currentRouterId.equals(""))
            {
                var active = 0;
                var isBack = SystemUtil.isBackground(AppConfig.instance)
                if(isBack)
                {
                    LogUtil.addLog("APP在后台")
                    active = 1
                }else{
                    LogUtil.addLog("APP在前台")
                }
                var heartBeatReq = HeartBeatReq(SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!,active)
                //LogUtil.addLog("发送信息：${heartBeatReq.baseDataToJson().replace("\\", "")}")
                var baseDataJson = BaseData(heartBeatReq).baseDataToJson().replace("\\", "")
                 LogUtil.addLog("发送结果：${baseDataJson}")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

        }
    }
}