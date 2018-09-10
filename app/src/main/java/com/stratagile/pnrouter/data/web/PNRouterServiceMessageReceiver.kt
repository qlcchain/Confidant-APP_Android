package com.stratagile.pnrouter.data.web

import android.util.Log
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.utils.baseDataToJson
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PNRouterServiceMessageReceiver
/**
 * Construct a PNRouterServiceMessageReceiver.
 *
 * @param urls The URL of the Signal Service.
 * @param credentials The Signal Service user's credentials.
 */
constructor(private val urls: SignalServiceConfiguration, private val credentialsProvider: CredentialsProvider, private val userAgent: String, private val connectivityListener: ConnectivityListener) : SignalServiceMessagePipe.MessagePipeCallback {
    override fun onMessage(baseData: BaseData<*>) {
        Log.i("ServiceMessageReceiver", baseData.baseDataToJson())
    }

//    private val socket: PushServiceSocket

    var  pipe : SignalServiceMessagePipe? = null
    var messageListner : MessageReceivedCallback? = null

    /**
     * Construct a PNRouterServiceMessageReceiver.
     *
     * @param urls The URL of the Signal Service.
     * @param user The Signal Service username (eg. phone number).
     * @param password The Signal Service user password.
     * @param signalingKey The 52 byte signaling key assigned to this user at registration.
     */
    constructor(urls: SignalServiceConfiguration,
                        user: String, password: String,
                        signalingKey: String, userAgent: String,
                        listener: ConnectivityListener) : this(urls, StaticCredentialsProvider(user, password, signalingKey), userAgent, listener) {
    }

    init {
//        this.socket = PushServiceSocket(urls, credentialsProvider, userAgent)
        createMessagePipe()
//        pipe!!.read(object : SignalServiceMessagePipe.NullMessagePipeCallback() {
//            override fun onMessage(envelope: BaseData<*>) {
//                Log.i("receiver", envelope.baseDataToJson())
//            }
//        })
    }

//    @Throws(IOException::class)
//    fun retrieveProfile(address: SignalServiceAddress): SignalServiceProfile {
//        return socket.retrieveProfile(address)
//    }
//
//    @Throws(IOException::class)
//    fun retrieveProfileAvatar(path: String, destination: File, profileKey: ByteArray, maxSizeBytes: Int): InputStream {
//        socket.retrieveProfileAvatar(path, destination, maxSizeBytes)
//        return ProfileCipherInputStream(FileInputStream(destination), profileKey)
//    }

    /**
     * Retrieves a SignalServiceAttachment.
     *
     * @param pointer The [SignalServiceAttachmentPointer]
     * received in a [SignalServiceDataMessage].
     * @param destination The download destination for this attachment.
     * @param listener An optional listener (may be null) to receive callbacks on download progress.
     *
     * @return An InputStream that streams the plaintext attachment contents.
     * @throws IOException
     * @throws InvalidMessageException
     */
//    @Throws(IOException::class, InvalidMessageException::class)
//    @JvmOverloads
//    fun retrieveAttachment(pointer: SignalServiceAttachmentPointer, destination: File, maxSizeBytes: Int, listener: ProgressListener? = null): InputStream {
//        if (!pointer.getDigest().isPresent()) throw InvalidMessageException("No attachment digest!")
//
//        socket.retrieveAttachment(pointer.getRelay().orNull(), pointer.getId(), destination, maxSizeBytes, listener)
//        return AttachmentCipherInputStream.createFor(destination, pointer.getSize().or(0), pointer.getKey(), pointer.getDigest().get())
//    }

    /**
     * Creates a pipe for receiving SignalService messages.
     *
     * Callers must call [SignalServiceMessagePipe.shutdown] when finished with the pipe.
     *
     * @return A SignalServiceMessagePipe for receiving Signal Service messages.
     */
    fun createMessagePipe(): SignalServiceMessagePipe {
        if (pipe == null) {
            val webSocket = WebSocketConnection(urls.signalServiceUrls[0].url,
                    urls.signalServiceUrls[0].trustStore,
                    credentialsProvider, userAgent, connectivityListener)
            pipe = SignalServiceMessagePipe(webSocket, credentialsProvider)
            pipe!!.messagePipeCallback = this
            return pipe!!
        } else {
            return pipe!!
        }
    }

//    @Throws(IOException::class)
//    @JvmOverloads
//    fun retrieveMessages(callback: MessageReceivedCallback = NullMessageReceivedCallback()): List<SignalServiceEnvelope> {
//        val results = LinkedList<E>()
//        val entities = socket.getMessages()
//
//        for (entity in entities) {
//            val envelope = SignalServiceEnvelope(entity.getType(), entity.getSource(),
//                    entity.getSourceDevice(), entity.getRelay(),
//                    entity.getTimestamp(), entity.getMessage(),
//                    entity.getContent())
//
//            callback.onMessage(envelope)
//            results.add(envelope)
//
//            socket.acknowledgeMessage(entity.getSource(), entity.getTimestamp())
//        }
//
//        return results
//    }


    /**
     * 作为对外暴露的接口，聊天消息统一用这个接口对外输出消息
     */
    interface MessageReceivedCallback {
        fun onMessage(baseData : BaseData<*>)
    }

    class NullMessageReceivedCallback : MessageReceivedCallback {
        override fun onMessage(envelope: BaseData<*>) {}
    }

}
/**
 * Retrieves a SignalServiceAttachment.
 *
 * @param pointer The [SignalServiceAttachmentPointer]
 * received in a [SignalServiceDataMessage].
 * @param destination The download destination for this attachment.
 *
 * @return An InputStream that streams the plaintext attachment contents.
 * @throws IOException
 * @throws InvalidMessageException
 */
