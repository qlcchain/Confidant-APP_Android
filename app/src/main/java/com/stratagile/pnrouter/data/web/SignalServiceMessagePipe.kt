package com.stratagile.pnrouter.data.web

import com.stratagile.pnrouter.data.web.message.WebSocketProtos.WebSocketRequestMessage
import com.stratagile.pnrouter.data.web.message.WebSocketProtos.WebSocketResponseMessage
import okhttp3.internal.Util
import okio.ByteString
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SignalServiceMessagePipe internal constructor(private val websocket: WebSocketConnection, private val credentialsProvider: CredentialsProvider) {

    init {

        this.websocket.connect()
    }

    /**
     * A blocking call that reads a message off the pipe (see [.read]
     *
     * Unlike [.read], this method allows you
     * to specify a callback that will be called before the received message is acknowledged.
     * This allows you to write the received message to durable storage before acknowledging
     * receipt of it to the server.
     *
     * @param timeout The timeout to wait for.
     * @param unit The timeout time unit.
     * @param callback A callback that will be called before the message receipt is
     * acknowledged to the server.
     * @return The message read (same as the message sent through the callback).
     * @throws TimeoutException
     * @throws IOException
     * @throws InvalidVersionException
     */
//    @Throws(TimeoutException::class, IOException::class, InvalidVersionException::class)
//    @JvmOverloads
//    fun read(timeout: Long, unit: TimeUnit, callback: MessagePipeCallback = NullMessagePipeCallback()): SignalServiceEnvelope {
//        while (true) {
//            val request = websocket.readRequest(unit.toMillis(timeout))
//            val response = createWebSocketResponse(request)
//
//            try {
//                if (isSignalServiceEnvelope(request)) {
//                    val envelope = SignalServiceEnvelope(request.getBody().toByteArray(),
//                            credentialsProvider.signalingKey)
//
//                    callback.onMessage(envelope)
//                    return envelope
//                }
//            } finally {
//                websocket.sendResponse(response)
//            }
//        }
//    }

//    @Throws(IOException::class)
//    fun send(list: OutgoingPushMessageList): SendMessageResponse {
//        try {
//            val requestMessage = WebSocketRequestMessage.newBuilder()
//                    .setId(SecureRandom.getInstance("SHA1PRNG").nextLong())
//                    .setVerb("PUT")
//                    .setPath(String.format("/v1/messages/%s", list.getDestination()))
//                    .addHeaders("content-type:application/json")
//                    .setBody(ByteString.copyFrom(JsonUtil.toJson(list).getBytes()))
//                    .build()
//
//            val response = websocket.sendRequest(requestMessage).get(10, TimeUnit.SECONDS)
//
//            if (response.first() < 200 || response.first() >= 300) {
//                throw IOException("Non-successful response: " + response.first())
//            }
//
//            return if (Util.isEmpty(response.second()))
//                SendMessageResponse(false)
//            else
//                JsonUtil.fromJson(response.second(), SendMessageResponse::class.java)
//        } catch (e: NoSuchAlgorithmException) {
//            throw AssertionError(e)
//        } catch (e: InterruptedException) {
//            throw IOException(e)
//        } catch (e: ExecutionException) {
//            throw IOException(e)
//        } catch (e: TimeoutException) {
//            throw IOException(e)
//        }
//
//    }

//    @Throws(IOException::class)
//    fun getProfile(address: SignalServiceAddress): SignalServiceProfile {
//        try {
//            val requestMessage = WebSocketRequestMessage.newBuilder()
//                    .setId(SecureRandom.getInstance("SHA1PRNG").nextLong())
//                    .setVerb("GET")
//                    .setPath(String.format("/v1/profile/%s", address.getNumber()))
//                    .build()
//
//            val response = websocket.sendRequest(requestMessage).get(10, TimeUnit.SECONDS)
//
//            if (response.first() < 200 || response.first() >= 300) {
//                throw IOException("Non-successful response: " + response.first())
//            }
//
//            return JsonUtil.fromJson(response.second(), SignalServiceProfile::class.java)
//        } catch (nsae: NoSuchAlgorithmException) {
//            throw AssertionError(nsae)
//        } catch (e: InterruptedException) {
//            throw IOException(e)
//        } catch (e: ExecutionException) {
//            throw IOException(e)
//        } catch (e: TimeoutException) {
//            throw IOException(e)
//        }
//
//    }

    /**
     * Close this connection to the server.
     */
    fun shutdown() {
        websocket.disconnect()
    }

    fun webSocketConnection() : WebSocketConnection {
        return websocket
    }

    private fun isSignalServiceEnvelope(message: WebSocketRequestMessage): Boolean {
        return "PUT" == message.getVerb() && "/api/v1/message" == message.getPath()
    }

    private fun createWebSocketResponse(request: WebSocketRequestMessage): WebSocketResponseMessage {
        return if (isSignalServiceEnvelope(request)) {
            WebSocketResponseMessage.newBuilder()
                    .setId(request.getId())
                    .setStatus(200)
                    .setMessage("OK")
                    .build()
        } else {
            WebSocketResponseMessage.newBuilder()
                    .setId(request.getId())
                    .setStatus(400)
                    .setMessage("Unknown")
                    .build()
        }
    }

    /**
     * For receiving a callback when a new message has been
     * received.
     */
//    interface MessagePipeCallback {
//        fun onMessage(envelope: SignalServiceEnvelope)
//    }
//
//    private class NullMessagePipeCallback : MessagePipeCallback {
//        override fun onMessage(envelope: SignalServiceEnvelope) {}
//    }

    companion object {

        private val TAG = SignalServiceMessagePipe::class.java.name
    }

}
/**
 * A blocking call that reads a message off the pipe.  When this
 * call returns, the message has been acknowledged and will not
 * be retransmitted.
 *
 * @param timeout The timeout to wait for.
 * @param unit The timeout time unit.
 * @return A new message.
 *
 * @throws InvalidVersionException
 * @throws IOException
 * @throws TimeoutException
 */
