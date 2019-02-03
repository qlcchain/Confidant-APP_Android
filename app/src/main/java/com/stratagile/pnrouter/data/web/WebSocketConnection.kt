package com.stratagile.pnrouter.data.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.ConstantValue.port
import com.stratagile.pnrouter.data.web.java.WsStatus
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
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.net.ssl.*

class WebSocketConnection(httpUri: String, private val trustStore: TrustStore, private val credentialsProvider: CredentialsProvider, private val userAgent: String?, private val listener: ConnectivityListener?) : WebSocketListener() {

    private val incomingRequests = LinkedList<BaseData>()
    private val outgoingRequests = HashMap<Long, SettableFuture<Pair<Integer, String>>>()
    private val RECONNECT_INTERVAL = 1 * 1000    //重连自增步长
    private val wsUri: String
    private var isLocalLogin = false //是否本地路由器登录
    private var webSocketClient: WebSocket? = null
    private var keepAliveSender: KeepAliveSender? = null
    //private var reConnectThread: ReConnectThread? = null
    private var attempts: Int = 0
    private var connected: Boolean = false
    private var reConnectTimeOut = false
    open var onMessageReceiveListener : OnMessageReceiveListener? = null
    private var reconnectCount = 0
    private var retryInterval = arrayListOf<Int>(1000, 2000, 3000)
    private var ipAddress = ""
    private var filledUri = ""
    private var isNeedReConnect = true;  //客户端主动关闭不要重连
    private var requestBuilder:Request.Builder?= null
    private var mOkHttpClient: OkHttpClient? = null
    private var mLock: Lock? = null
    private var mCurrentStatus = WsStatus.DISCONNECTED     //websocket连接状态
    private val wsMainHandler = Handler(Looper.getMainLooper())
    private val reconnectRunnable = Runnable {
        Log.i("websocket", "服务器重连接中...")
        buildConnect()
    }
    init {
        this.attempts = 0
        this.connected = false
        this.mLock = ReentrantLock()
        //reConnectThread = ReConnectThread()
        //reConnectThread!!.begin()
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
        ipAddress = WiFiUtil.getGateWay(AppConfig.instance)
        filledUri = "wss://" + ipAddress + port
        ConstantValue.currentIp = WiFiUtil.getGateWay(AppConfig.instance)
        if (webSocketClient == null) {
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

            if(mOkHttpClient == null)
            {
                mOkHttpClient = OkHttpClient.Builder()
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
            }
            requestBuilder = Request.Builder().url(filledUri)
            if (userAgent != null) {
//                requestBuilder.addHeader("X-Signal-Agent", userAgent)
                requestBuilder!!.addHeader("Sec-WebSocket-Protocol", userAgent)
            }
            listener?.onConnecting()
            this.connected = false
            mOkHttpClient!!.dispatcher().cancelAll()
            /*try {
                mLock!!.lockInterruptibly()
                try {

                } finally {
                    mLock!!.unlock()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }*/
            this.webSocketClient = mOkHttpClient!!.newWebSocket(requestBuilder!!.build(), this)
            Log.i("websocket：this.client1",""+ (this.webSocketClient == null))
        }
    }

    var isShutDown = false

    @Synchronized
    fun disconnect(isShutDown : Boolean) {
        Log.w(TAG, "WSC disconnect()...")
        setCurrentStatus(WsStatus.DISCONNECTED)
        this.isShutDown = isShutDown
        if (webSocketClient != null) {
            webSocketClient!!.close(1000, "OK")
            webSocketClient = null
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
        if (webSocketClient != null) {
            webSocketClient!!.close(1000, "OK")
            connected = false
        }

        if (keepAliveSender != null) {
            keepAliveSender!!.shutdown()
        }

    }
    @Synchronized
    @Throws(TimeoutException::class, IOException::class)
    fun readRequest(timeoutMillis: Long): BaseData {
        if (webSocketClient == null) {
            throw IOException("Connection closed!")
        }

        val startTime = System.currentTimeMillis()

        while (webSocketClient != null && incomingRequests.isEmpty() && elapsedTime(startTime) < timeoutMillis) {
//            Util.wait(Object(), Math.max(1, timeoutMillis - elapsedTime(startTime)))
        }

        return if (incomingRequests.isEmpty() && webSocketClient == null)
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
        if (webSocketClient == null || !connected) {
            Log.i("websocket", "No connection!")
            return false
        }
        if (!webSocketClient!!.send(message!!)) {
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
        if (keepAliveSender != null && webSocketClient != null && !ConstantValue.loginOut) {
            //todo keepalive message
            var active = 0;
            var isBack = SystemUtil.isBackground(AppConfig.instance)
            if(isBack)
            {
                active = 1
                LogUtil.addLog("APP切换到后台")
            }else{
                LogUtil.addLog("APP切换到前台")
            }
            var heartBeatReq = HeartBeatReq(SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!,active)
            LogUtil.addLog("发送信息：${heartBeatReq.baseDataToJson().replace("\\", "")}")
            var reslut = send(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
            LogUtil.addLog("发送结果：${reslut}")
            KLog.i("发送心跳消息"+isBack)
            //KLog.i(BaseData(heartBeatReq).baseDataToJson().replace("\\", ""))
        }
    }

    //    @Synchronized
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        if (webSocketClient != null && keepAliveSender == null) {
            setCurrentStatus(WsStatus.CONNECTED)
            this.webSocketClient = webSocket
            KLog.i("onConnected()")
            KLog.i(webSocketClient!!.request().url())
            LogUtil.addLog("连接成功：${webSocketClient!!.request().url()}")
            attempts = 0
            connected = true
            reconnectCount = 0
            /* Log.w(TAG, "ReConnectThread_onOpen_id:"+reConnectThread!!.getId())
             reConnectThread!!.shutdown()
             reConnectThread = null
             Log.w(TAG, "ReConnectThread_onOpen_shutdown_mainid:"+Thread.currentThread().getId())*/
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
        Log.w(TAG, "onClosed()..."+code+"+"+reason)
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

        /*if(reConnectThread == null)
        {
            reConnectThread = ReConnectThread()
            Log.w(TAG, "ReConnectThread_"+"new ReConnectThread"+reConnectThread!!.getId())
        }else{
            Log.w(TAG, "ReConnectThread_"+"old ReConnectThread"+reConnectThread!!.getId())
        }*/

//        Util.wait(this, Math.min((++attempts * 200).toLong(), TimeUnit.SECONDS.toMillis(15)))

        if (webSocketClient != null) {
            webSocketClient!!.close(1000, "OK")
//            webSocketClient!!.cancel()
            webSocketClient = null
            connected = false
        }
        Log.i(TAG, "ReConnectThread_"+"isNeedReConnect:"+isNeedReConnect)
        if(isNeedReConnect)
        {
            reconnectCount ++
            Log.i(TAG, "ReConnectThread_"+"reconnectCount:[$reconnectCount]")
            val delay = (reconnectCount * RECONNECT_INTERVAL).toLong()
            wsMainHandler.postDelayed(reconnectRunnable, delay)

            if(reconnectCount > 2)
            {
                reconnectCount = 0
            }
        }else{
            isNeedReConnect = true
        }
        /*if (reConnectThread != null && isNeedReConnect) {
            isLocalLogin = false;
            Log.w(TAG, "ReConnectThread_reStart_id:"+reConnectThread!!.getId())
            reConnectThread?.reStart()
        }else{
            filledUri = wsUri  //局域登录不了立即跳转外网
            if (webSocketClient != null) {
//                        webSocketClient!!.close(1000, "OK")
                webSocketClient!!.cancel()
                webSocketClient = null
                connected = false
            }
            if(isNeedReConnect)
            {
                reConnect()
                isNeedReConnect = true
            }

        }*/

//        notifyAll()
    }

    fun buildConnect() {
        if (!isNetworkConnected(AppConfig.instance)) {
            setCurrentStatus(WsStatus.DISCONNECTED)
        }
        reConnect()
        /*when (getCurrentStatus()) {
            WsStatus.CONNECTED, WsStatus.CONNECTING -> {
            }
            else -> {
                setCurrentStatus(WsStatus.CONNECTING)

            }
        }*/
    }

    //检查网络是否连接
    private fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @SuppressLint("MissingPermission") val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }
    fun reConnect() {
        setCurrentStatus(WsStatus.RECONNECT)
        Log.w(TAG, "WSC reConnect()...")
        KLog.i("ReConnectThread_websocket_reConnect"+webSocketClient)
        /*if (webSocketClient == null) {
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
            this.webSocketClient = okHttpClient.newWebSocket(requestBuilder.build(), this)
            ConstantValue.isWebsocketReConnect = true
            Log.i("websocket：this.client2",""+ (this.webSocketClient == null))
        }*/
        val socketFactory = createTlsSocketFactory(trustStore)
        if(mOkHttpClient == null)
        {
            mOkHttpClient = OkHttpClient.Builder()
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


        }
        requestBuilder = Request.Builder().url(filledUri)
        if (userAgent != null) {
//                requestBuilder.addHeader("X-Signal-Agent", userAgent)
            requestBuilder!!.addHeader("Sec-WebSocket-Protocol", userAgent)
        }
        listener?.onConnecting()
        this.connected = false
        ConstantValue.isWebsocketReConnect = true
        mOkHttpClient!!.dispatcher().cancelAll()
        /*try {
            mLock!!.lockInterruptibly()
            try {


            } finally {
                mLock!!.unlock()
            }
        } catch (e: InterruptedException) {
        }*/
        this.webSocketClient = mOkHttpClient!!.newWebSocket(requestBuilder!!.build(), this)
        Log.i("websocket：this.client1",""+ (this.webSocketClient == null))
    }

    @Synchronized
    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        KLog.i("ReConnectThread_onFailure()")
        KLog.i( t!!.printStackTrace())

        if (response != null && (response.code() == 401 || response.code() == 403)) {
            if (listener != null) listener!!.onAuthenticationFailure()
        }
        isNeedReConnect = true;
        if (webSocketClient != null) {
            onClosed(webSocket, 1000, "OK")
        }
    }

    @Synchronized
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        Log.w(TAG, "onClosing()!..."+code+"_"+reason)
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
            Log.w(TAG, "ReConnectThread_"+"beginreConnect..."+(!stop.get() &&! reConnectTimeOut)+"_id:"+this.getId())
            while (!stop.get() &&! reConnectTimeOut) {
                try {
                    if (reconnectCount > retryInterval.size) {
                        reConnectTimeOut = true
                        return
                    }
                    if(reconnectCount > retryInterval.size -1)
                    {
                        KLog.i("ReConnectThread_重新计数……")
                        reconnectCount = 0;
                    }
                    Log.w(TAG, "ReConnectThread_reConnect1..." + retryInterval[reconnectCount]+"_id:"+this.getId())
                    Thread.sleep(retryInterval[reconnectCount].toLong())
                    Log.w(TAG, "ReConnectThread_afterSleeep..." + retryInterval[reconnectCount]+"_id:"+this.getId()+"_"+this.isInterrupted()+"_"+stop.get())
                    if (this.isInterrupted() || stop.get()) {
                        reconnectCount = 0
                        KLog.i("ReConnectThread_中断任务"+"_id:"+this.getId())
                        return;
                    }
                    reconnectCount++
                    Log.w(TAG, "ReConnectThread_reConnect2..."+"_id:"+this.getId())
                    if (connected) {
                        //shutdown()
                        reconnectCount = 0
                        KLog.i("ReConnectThread_websocket已经连接上了，此处将继续重连的逻辑清除")
                        return
                    }

                    /*//测试服务器，测试用
                    if (reconnectCount >=3) {
                        KLog.i("重连次数过多，切换公网服务器连接。。")
                        filledUri = wsUri
                        *//*webSocketClient!!.cancel()
                        webSocketClient = null
                        connected = false
                        listener?.onConnectFail()
                        return*//*
                    }*/
                    KLog.i("ReConnectThread_websocket继续重连"+webSocketClient)
                    if (webSocketClient != null) {
//                        webSocketClient!!.close(1000, "OK")
                        webSocketClient!!.cancel()
                        webSocketClient = null
                        connected = false
                    }
                    KLog.i("ReConnectThread_websocket继续重连2")
                    reConnect()
                } catch (e: Throwable) {
                    Log.w(TAG, e)
                }

            }
        }

        fun shutdown() {
            Log.w(TAG, "ReConnectThread_shutdown_id:"+this.getId())
            stop.set(true)
            try {
                //this.interrupt()
            }catch (e:Exception)
            {

            }

        }
        fun begin()
        {
            /*if(!reConnectThread!!.isAlive)
                reConnectThread!!.start()*/
        }
        fun reStart() {
            stop.set(false)
            Log.w(TAG, "ReConnectThread_reStart_id:"+this.getId())
            reConnectTimeOut = false
            run()

        }
    }

    fun getCurrentStatus(): Int {
        return mCurrentStatus
    }

    fun setCurrentStatus(currentStatus: Int) {
        this.mCurrentStatus = currentStatus
    }
    companion object {

        private val TAG = WebSocketConnection::class.java.simpleName
        private val KEEPALIVE_TIMEOUT_SECONDS = 30
    }

    interface OnMessageReceiveListener {
        fun onMessage(message : BaseData, text: String?)
    }

}
