package com.stratagile.pnrouter.data.web

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.events.FileMangerTransformEntity
import com.stratagile.pnrouter.entity.events.FileMangerTransformReceiverMessage
import com.stratagile.pnrouter.utils.FileUtil
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.WiFiUtil
import okhttp3.*
import okio.ByteString
import org.greenrobot.eventbus.EventBus
import java.io.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.net.ssl.*

class FileMangerWebSocketConnection(httpUri: String, private val trustStore: TrustStore, private val userAgent: String?, private val listener: ConnectivityListener?) : WebSocketListener() {

    private val incomingRequests = LinkedList<BaseData>()
    private val wsUri: String

    private var client: WebSocket? = null
    private var attempts: Int = 0
    private var connected: Boolean = false
    private var reConnectTimeOut = false
    open var onMessageReceiveListener : OnMessageReceiveListener? = null
    private var retryTime = 0
    private var retryInterval = arrayListOf<Int>(5000, 15000, 30000, 60000, 120000)
    private var filledUri = ""
    var toId = ""

    init {
        this.attempts = 0
        this.connected = false
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
        KLog.i("文件管理网管地址为：${WiFiUtil.getGateWay(AppConfig.instance)}")
        WiFiUtil.getGateWay(AppConfig.instance)
        filledUri = wsUri
        if (client == null) {
            if (isWifiConnect()) {
            } else {
                filledUri = wsUri
            }
            KLog.i("文件管理连接的地址为：${filledUri}")
            if(filledUri == null || filledUri.equals("") || filledUri.indexOf("wss://:") >-1)
            {
                return
            }
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

    var isShutDown = false

    @Synchronized
    fun disconnect(isShutDown : Boolean) {
        Log.w(TAG, "文件管理WSC disconnect()...")
        this.isShutDown = isShutDown
        if (client != null) {
            client!!.close(1000, "OK")
            client = null
            connected = false
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
        if(ConstantValue.loginOut == true)
        {
            KLog.i("文件管理监测到登出。。")
            disconnect(true)
            return false
        }
        KLog.i("文件管理开始传输字符串。。")
        KLog.i("send:"+message)
        if (client == null || !connected) {
            Log.i("websocket", "No connection!")
            return false
        }
        if (!client!!.send(message!!)) {
//            throw IOException("Write failed!")
            return false
        } else {
            KLog.i("文件管理发送成功")
            EventBus.getDefault().post(FileMangerTransformEntity(toId, 2))
            return true
        }
    }


    fun sendFile(file : File) : Boolean{

        if (client == null || !connected) {
            KLog.i("No connection!")
            return false
        }
        var byteString = FileUtil.readFile(file)
        KLog.i("文件管理开始传输文件。。"+byteString)
        if (!client!!.send(byteString)) {
            return false
        } else {
            KLog.i("文件管理发送成功")
            EventBus.getDefault().post(FileMangerTransformEntity(toId, 2))
            return true
        }
    }

    fun sendByteString(bytes : ByteArray) : Boolean {
        try {
            if(ConstantValue.loginOut == true)
            {
                KLog.i("文件管理监测到登出。。")
                disconnect(true)
                return false
            }
            KLog.i("文件管理开始传输文件。。"+ FileUtil.toByteString(bytes))
            if (client!= null && !client!!.send(FileUtil.toByteString(bytes))) {
                return false
            } else {
                KLog.i("文件管理发送成功")
                EventBus.getDefault().post(FileMangerTransformEntity(toId, 2))
                return true
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 获得指定文件的byte数组
     */
    private fun getBytes(file: File): ByteArray? {
        var buffer: ByteArray? = null
        try {
            val fis = FileInputStream(file)
            val bos = ByteArrayOutputStream(1000)
            val b = ByteArray(1000)
            var n = 0
            while ({n = (fis.read(b)); n}()!= -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            bos.close()
            buffer = bos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return buffer
    }

    //    @Synchronized
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        if (client != null) {
            KLog.i("onConnected()")
            KLog.i(client!!.request().url())
            LogUtil.addLog("文件管理websocket连接成功：${client!!.request().url()}")
            attempts = 0
            connected = true
            retryTime = 0
            if (listener != null) listener!!.onConnected()
            //连接成功，告诉外接,准备发送文件的消息
            EventBus.getDefault().post(FileMangerTransformEntity(toId, 1))
        }
    }

    @Synchronized
    override fun onMessage(webSocket: WebSocket?, payload: ByteString?) {
        if(ConstantValue.loginOut == true)
        {
            KLog.i("文件管理监测到登出。。")
            disconnect(true)
            return
        }
        var text = payload.toString();
        KLog.i("文件管理WSC onMessage()" + text);
        EventBus.getDefault().post(FileMangerTransformReceiverMessage(toId, payload!!.toByteArray()))
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        if(ConstantValue.loginOut == true)
        {
            KLog.i("文件管理监测到登出。。")
            disconnect(true)
            return
        }
        KLog.i("文件管理websocketFilereceive " + text!!)
        EventBus.getDefault().post(FileMangerTransformEntity(toId, 3, text))
//        LogUtil.addLog("接收信息：${text}")
//        try {
//            val gson = GsonUtil.getIntGson()
//            var baseData = gson.fromJson(text, BaseData::class.java)
//            if (JSONObject.parseObject((JSONObject.parseObject(text)).get("params").toString()).getString("Action").equals("HeartBeat")) {
//                val heartBeatRsp  = gson.fromJson(text, JHeartBeatRsp::class.java)
//                if (heartBeatRsp.params.retCode == 0) {
//                    KLog.i("心跳监测和服务器的连接正常~~~")
//                }
//            } else {
//                onToxMessageReceiveListener!!.onMessage(baseData, text)
//            }
//        } catch (e : Exception) {
//            e.printStackTrace()
//        }
    }


    @Synchronized
    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        KLog.i("文件管理onClosed()...")
        this.connected = false

        listener?.onDisconnected()

        if (client != null) {
            client!!.close(1000, "OK")
            client = null
            connected = false
        }

    }

    @Synchronized
    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        KLog.i("文件管理onFailure()")
        KLog.i(t)

        if (response != null && (response.code() == 401 || response.code() == 403)) {
            if (listener != null) listener!!.onAuthenticationFailure()
        }

        if (client != null) {
            onClosed(webSocket, 1000, "OK")
        }
    }

    @Synchronized
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        KLog.i("文件管理onClosing()!...")
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

    companion object {

        private val TAG = FileMangerWebSocketConnection::class.java.simpleName
        private val KEEPALIVE_TIMEOUT_SECONDS = 30
    }

    interface OnMessageReceiveListener {
        fun onMessage(message : BaseData, text: String?)
    }

}
