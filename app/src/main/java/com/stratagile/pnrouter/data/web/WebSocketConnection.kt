package com.stratagile.pnrouter.data.web

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.ConstantValue.port
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.HeartBeatReq
import com.stratagile.pnrouter.entity.JHeartBeatRsp
import com.stratagile.pnrouter.utils.*
import okhttp3.*
import okio.ByteString
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.*

class WebSocketConnection(httpUri: String, private val trustStore: TrustStore, private val credentialsProvider: CredentialsProvider, private val userAgent: String?, private val listener: ConnectivityListener?) : WebSocketListener() {

    private val incomingRequests = LinkedList<BaseData>()
    private val outgoingRequests = HashMap<Long, SettableFuture<Pair<Integer, String>>>()

    private val wsUri: String
    private var isLocalLogin = false //是否本地路由器登录
    private var client: WebSocket? = null
    private var keepAliveSender: KeepAliveSender? = null
    private var reConnectThread: ReConnectThread? = null
    private var attempts: Int = 0
    private var connected: Boolean = false
    private var reConnectTimeOut = false
    open var onMessageReceiveListener : OnMessageReceiveListener? = null
    private var retryTime = 0
    private var retryInterval = arrayListOf<Int>(1000, 2000, 3000, 5000, 8000)
    private var ipAddress = ""
    private var filledUri = ""
    private var isNeedReConnect = true;  //客户端主动关闭不要重连

    init {
        this.attempts = 0
        this.connected = false
        reConnectThread = ReConnectThread()
        /*this.wsUri = httpUri.replace("https://", "wss://")
                .replace("http://", "ws://")*/
        ipAddress = WiFiUtil.getGateWay(AppConfig.instance)
        this.wsUri = "wss://" + ipAddress + port;
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
        ConstantValue.currentIp = WiFiUtil.getGateWay(AppConfig.instance)
        if (client == null) {
            if (isWifiConnect()) {
                isLocalLogin = true;
                ipAddress = WiFiUtil.getGateWay(AppConfig.instance)
                filledUri = "wss://" + ipAddress + port
            } else {
                isLocalLogin = false;
                //filledUri = wsUri
                filledUri = "wss://" + ipAddress + port
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

            Log.i("websocket：this.client1",""+ (this.client == null))
        }
    }

    var isShutDown = false

    @Synchronized
    fun disconnect(isShutDown : Boolean) {
        Log.w(TAG, "WSC disconnect()...")
        this.isShutDown = isShutDown
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
    fun close(isShutDown : Boolean) {
        Log.w(TAG, "WSC disconnect()...")
        this.isShutDown = isShutDown
        isNeedReConnect = false;
        if (client != null) {
            client!!.close(1000, "OK")
            connected = false
        }

        if (keepAliveSender != null) {
            keepAliveSender!!.shutdown()
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
        if(message!!.indexOf("HeartBeat") < 0)
        {
            //Log.i("websocketConnection", message)
        }
        if (client == null || !connected) {
            Log.i("websocket", "No connection!")
            return false
        }
        if (!client!!.send(message!!)) {
//            throw IOException("Write failed!")
            return false
        } else {
            if(message.indexOf("HeartBeat") < 0)
            {
                Log. i("WenSocketConnetion", "发送成功")
            }
            return true
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun sendKeepAlive() {
        if (keepAliveSender != null && client != null) {
            //todo keepalive message
            var heartBeatReq = HeartBeatReq(SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!)
            LogUtil.addLog("发送信息：${heartBeatReq.baseDataToJson().replace("\\", "")}")
            var reslut = send(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
            LogUtil.addLog("发送结果：${reslut}")
            //KLog.i("心跳消息为：：")
            //KLog.i(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
        }
    }

//    @Synchronized
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        if (client != null && keepAliveSender == null) {
            KLog.i("onConnected()")
            KLog.i(client!!.request().url())
            LogUtil.addLog("连接成功：${client!!.request().url()}")
            attempts = 0
            connected = true
            retryTime = 0
            reConnectThread!!.shutdown()
            reConnectThread = null
            keepAliveSender = KeepAliveSender()
            keepAliveSender!!.start()

            if (listener != null) listener!!.onConnected()

            if(ConstantValue.isWebsocketReConnect && ConstantValue.loginReq != null && ConstantValue.hasLogin)
            {
                var loginReq = ConstantValue.loginReq
                LogUtil.addLog("websocket重连发送登录信息：${loginReq!!.baseDataToJson().replace("\\", "")}")
                var reslut = send(BaseData(loginReq!!).baseDataToJson().replace("\\", ""))
            }
        }
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket?, payload: ByteString?) {
        Log.w(TAG, "WSC onMessage()")
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        if(text!!.indexOf("HeartBeat") < 0)
        {
            Log.w(TAG, "onMessage(text)! " + text!!)
            LogUtil.addLog("websocket接收信息：${text}")
        }

        try {
            val gson = GsonUtil.getIntGson()
            var baseData = gson.fromJson(text, BaseData::class.java)
            if (JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action").equals("HeartBeat")) {
                val heartBeatRsp  = gson.fromJson(text, JHeartBeatRsp::class.java)
                if (heartBeatRsp.params.retCode == 0) {
                    //KLog.i("心跳监测和服务器的连接正常~~~")
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
        if (reConnectThread != null && !isShutDown && !isLocalLogin && isNeedReConnect) {
            isLocalLogin = false;
            reConnectThread?.reStart()
        }else{
            filledUri = wsUri  //局域登录不了立即跳转外网
            if (client != null) {
//                        client!!.close(1000, "OK")
                client!!.cancel()
                client = null
                connected = false
            }
            if(isNeedReConnect)
            {
                reConnect()
                isNeedReConnect = true
            }

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
            ConstantValue.isWebsocketReConnect = true
            Log.i("websocket：this.client2",""+ (this.client == null))
        }
    }

    @Synchronized
    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        KLog.i("onFailure()")
        KLog.i( t!!.printStackTrace())

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

                    //Log.w(TAG, "Sending keep alive...")
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
            Log.w(TAG, "beginreConnect..."+(!stop.get() &&! reConnectTimeOut))
            while (!stop.get() &&! reConnectTimeOut) {
                try {
                    if (retryTime > retryInterval.size) {
                        reConnectTimeOut = true
                        return
                    }
                    if(retryTime > retryInterval.size -1)
                    {
                        retryTime = 0;
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
                        KLog.i("重连次数过多，切换公网服务器连接。。")
                        filledUri = wsUri
                        /*client!!.cancel()
                        client = null
                        connected = false
                        listener?.onConnectFail()
                        return*/
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
