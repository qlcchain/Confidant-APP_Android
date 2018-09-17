package com.stratagile.pnrouter.data.web

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.protobuf.InvalidProtocolBufferException
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.message.WebSocketProtos
import okhttp3.*
import java.util.*

import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

import com.stratagile.pnrouter.data.web.message.WebSocketProtos.WebSocketRequestMessage
import com.stratagile.pnrouter.data.web.message.WebSocketProtos.WebSocketResponseMessage
import com.stratagile.pnrouter.data.web.message.WebSocketProtos.WebSocketMessage
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.HeartBeatReq
import com.stratagile.pnrouter.entity.JHeartBeatRsp
import com.stratagile.pnrouter.entity.LoginRspWrapper
import com.stratagile.pnrouter.utils.GsonUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.WiFiUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import java.lang.Exception
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.Future
import javax.net.ssl.*

class WebSocketConnection(httpUri: String, private val trustStore: TrustStore, private val credentialsProvider: CredentialsProvider, private val userAgent: String?, private val listener: ConnectivityListener?) : WebSocketListener() {

    private val incomingRequests = LinkedList<BaseData>()
    private val outgoingRequests = HashMap<Long, SettableFuture<Pair<Integer, String>>>()

    private val wsUri: String

    private var client: WebSocket? = null
    private var keepAliveSender: KeepAliveSender? = null
    private var reConnectThread: ReConnectThread? = null
    private var attempts: Int = 0
    private var connected: Boolean = false
    private var reConnectTimeOut = false
    open var onMessageReceiveListener : OnMessageReceiveListener? = null
    private var retryTime = 0
    private var retryInterval = arrayListOf<Int>(5000, 15000, 30000, 60000, 120000)
    private var port = ":18006/"
    private var ipAddress = ""
    private var filledUri = ""

    init {
        this.attempts = 0
        this.connected = false
        reConnectThread = ReConnectThread()
        this.wsUri = httpUri.replace("https://", "wss://")
                .replace("http://", "ws://")
//        this.wsUri = "wss://47.96.76.184:18000"
//        this.wsUri = "wss://47.96.76.184:18001/"
//        this.wsUri = httpUri.replace("https://", "wss://")
//                .replace("http://", "ws://") + "/v1/websocket/?login=%s&password=%s"
    }

    fun isWifiConnect() : Boolean{
        var connManager  = AppConfig.instance.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return mWifi.isConnected()
    }

    @Synchronized
    fun connect() {
        Log.w(TAG, "WSC connect()...")
        KLog.i("网管地址为：${WiFiUtil.getGateWay(AppConfig.instance)}")
        WiFiUtil.getGateWay(AppConfig.instance)
        filledUri = wsUri
        if (client == null) {
            if (isWifiConnect()) {
                ipAddress = WiFiUtil.getGateWay(AppConfig.instance)
                filledUri = "wss://" + ipAddress + port
            } else {
                filledUri = wsUri
            }
            KLog.i("连接的地址为：${filledUri}")
            val socketFactory = createTlsSocketFactory(trustStore)

            val okHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(socketFactory.first)
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(hostname: String, session: SSLSession): Boolean {
                            return true
                        }
                    })
//                    .sslSocketFactory(socketFactory.first, socketFactory.second)
                    .readTimeout((KEEPALIVE_TIMEOUT_SECONDS + 10).toLong(), TimeUnit.SECONDS)
                    .connectTimeout((KEEPALIVE_TIMEOUT_SECONDS + 10).toLong(), TimeUnit.SECONDS)
                    .build()
            val requestBuilder = Request.Builder().url(filledUri)

            if (userAgent != null) {
//                requestBuilder.addHeader("X-Signal-Agent", userAgent)
                requestBuilder.addHeader("Sec-WebSocket-Protocol", userAgent)
            }

            listener?.onConnecting()

            this.connected = false
            this.client = okHttpClient.newWebSocket(requestBuilder.build(), this)
        }
    }

    @Synchronized
    fun disconnect() {
        Log.w(TAG, "WSC disconnect()...")

        if (client != null) {
            client!!.close(1000, "OK")
            client = null
            connected = false
        }

        if (keepAliveSender != null) {
            keepAliveSender!!.shutdown()
            keepAliveSender = null
        }
    }

    @Synchronized
    @Throws(TimeoutException::class, IOException::class)
    fun readRequest(timeoutMillis: Long): BaseData {
        if (client == null) {
            throw IOException("Connection closed!")
        }

        val startTime = System.currentTimeMillis()

        while (client != null && incomingRequests.isEmpty() && elapsedTime(startTime) < timeoutMillis) {
//            Util.wait(Object(), Math.max(1, timeoutMillis - elapsedTime(startTime)))
        }

        return if (incomingRequests.isEmpty() && client == null)
            throw IOException("Connection closed!")
        else if (incomingRequests.isEmpty())
            throw TimeoutException("Timeout exceeded")
        else
            incomingRequests.removeFirst()
    }

    fun send(message : String?) : Boolean{
//        Log.i("websocketConnection", message)
        if (client == null || !connected) {
            Log.i("websocket", "No connection!")
            return false
        }
        if (!client!!.send(message!!)) {
//            throw IOException("Write failed!")
            return false
        } else {
            Log. i("WenSocketConnetion", "发送成功")
            return true
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun sendKeepAlive() {
        if (keepAliveSender != null && client != null) {
            //todo keepalive message
            var heartBeatReq = HeartBeatReq(SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!)
            send(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
            KLog.i("心跳消息为：：")
            KLog.i(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
        }
    }

//    @Synchronized
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        if (client != null && keepAliveSender == null) {
            KLog.i("onConnected()")
            attempts = 0
            connected = true
            retryTime = 0
            reConnectThread!!.shutdown()
            reConnectThread = null
            keepAliveSender = KeepAliveSender()
            keepAliveSender!!.start()

            if (listener != null) listener!!.onConnected()
        }
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket?, payload: ByteString?) {
        Log.w(TAG, "WSC onMessage()")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.w(TAG, "onMessage(text)! " + text!!)
        try {
            val gson = GsonUtil.getIntGson()
            var baseData = gson.fromJson(text, BaseData::class.java)
            if (JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action").equals("HeartBeat")) {
                val heartBeatRsp  = gson.fromJson(text, JHeartBeatRsp::class.java)
                if (heartBeatRsp.params.retCode == 0) {
                    KLog.i("心跳监测和服务器的连接正常~~~")
                }
            } else {
                onMessageReceiveListener!!.onMessage(baseData, text)
            }
//            KLog.i("解析消息")
//            KLog.i(baseData.toString())
//            KLog.i(baseData.timestamp)
//            KLog.i(baseData.appid)
//            KLog.i(baseData.params.toString())
//            Log.i(TAG, baseData.params.toString())
//            var jsonObject  = JSONObject.parseObject(text)
//            Log.i(TAG, JSONObject.parseObject(text).getString("timestamp")!!)
//            Log.i(TAG, (JSONObject.parseObject(text)).get("params").toString())
//            Log.i(TAG, JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action"))
//            incomingRequests.add(baseData)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    @Synchronized
    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        Log.w(TAG, "onClosed()...")
        this.connected = false

//        val iterator = outgoingRequests.entries.iterator()
//
//        while (iterator.hasNext()) {
//            val entry = iterator.next()
//            entry.value.setException(IOException("Closed: $code, $reason"))
//            iterator.remove()
//        }

        if (keepAliveSender != null) {
            keepAliveSender!!.shutdown()
            keepAliveSender = null
        }

        listener?.onDisconnected()

        reConnectThread = ReConnectThread()

//        Util.wait(this, Math.min((++attempts * 200).toLong(), TimeUnit.SECONDS.toMillis(15)))

        if (client != null) {
            client!!.close(1000, "OK")
//            client!!.cancel()
            client = null
            connected = false
        }
        if (reConnectThread != null) {
            reConnectThread?.reStart()
        }

//        notifyAll()
    }

    fun reConnect() {
        Log.w(TAG, "WSC reConnect()...")

        if (client == null) {
//            val filledUri = String.format(wsUri, credentialsProvider.user, credentialsProvider.password)
            val socketFactory = createTlsSocketFactory(trustStore)

            val okHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(socketFactory.first)
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(hostname: String, session: SSLSession): Boolean {
                            return true
                        }
                    })
//                    .sslSocketFactory(socketFactory.first, socketFactory.second)
                    .readTimeout((KEEPALIVE_TIMEOUT_SECONDS + 10).toLong(), TimeUnit.SECONDS)
                    .connectTimeout((KEEPALIVE_TIMEOUT_SECONDS + 10).toLong(), TimeUnit.SECONDS)
                    .build()
            val requestBuilder = Request.Builder().url(filledUri)

            if (userAgent != null) {
//                requestBuilder.addHeader("X-Signal-Agent", userAgent)
                requestBuilder.addHeader("Sec-WebSocket-Protocol", userAgent)
            }

            listener?.onConnecting()

            this.connected = false
            this.client = okHttpClient.newWebSocket(requestBuilder.build(), this)
        }
    }

    @Synchronized
    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        Log.w(TAG, "onFailure()")
        Log.w(TAG, t)

        if (response != null && (response.code() == 401 || response.code() == 403)) {
            if (listener != null) listener!!.onAuthenticationFailure()
        }

        if (client != null) {
            onClosed(webSocket, 1000, "OK")
        }
    }

    @Synchronized
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        Log.w(TAG, "onClosing()!...")
        webSocket!!.close(1000, "OK")
    }

    private fun elapsedTime(startTime: Long): Long {
        return System.currentTimeMillis() - startTime
    }

    private fun createTlsSocketFactory(trustStore: TrustStore): Pair<SSLSocketFactory, X509TrustManager> {
        try {
            val context = SSLContext.getInstance("TLS")
            val trustManagers = BlacklistingTrustManager.createFor(trustStore)
            try {
                context.init(null, arrayOf<TrustManager>(object : X509TrustManager {

                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

                    }


                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls<X509Certificate>(0)
                    }
                }), SecureRandom())
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }

//            context.init(null, trustManagers, null)

            return Pair(context.socketFactory, trustManagers[0] as X509TrustManager)
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        }

    }

    private inner class KeepAliveSender : Thread() {

        private val stop = AtomicBoolean(false)

        override fun run() {
            while (!stop.get()) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(KEEPALIVE_TIMEOUT_SECONDS.toLong()))

                    Log.w(TAG, "Sending keep alive...")
                    sendKeepAlive()
                } catch (e: Throwable) {
                    Log.w(TAG, e)
                }

            }
        }

        fun shutdown() {
            stop.set(true)
        }
    }
    private inner class ReConnectThread : Thread() {

        private val stop = AtomicBoolean(false)

        override fun run() {
            while (!stop.get() &&! reConnectTimeOut) {
                try {
                    if (retryTime > retryInterval.size) {
                        reConnectTimeOut = true
                        return
                    }
                    Log.w(TAG, "reConnect1..." + retryInterval[retryTime])
                    Thread.sleep(retryInterval[retryTime].toLong())
                    retryTime++
                    Log.w(TAG, "reConnect2...")
                    if (connected) {
                        shutdown()
                        retryTime = 0
                        KLog.i("websocket已经连接上了，此处将继续重连的逻辑清除")
                        return
                    }
                    //测试服务器，测试用
                    if (retryTime >=3) {
                        filledUri = wsUri
                    }
                    if (client != null) {
//                        client!!.close(1000, "OK")
                        client!!.cancel()
                        client = null
                        connected = false
                    }
                    reConnect()
                } catch (e: Throwable) {
                    Log.w(TAG, e)
                }

            }
        }

        fun shutdown() {
            stop.set(true)
        }
        fun reStart() {
            stop.set(false)
            reConnectTimeOut = false
            run()
        }
    }

    companion object {

        private val TAG = WebSocketConnection::class.java.simpleName
        private val KEEPALIVE_TIMEOUT_SECONDS = 30
    }

    interface OnMessageReceiveListener {
        fun onMessage(message : BaseData, text: String?)
    }

}
