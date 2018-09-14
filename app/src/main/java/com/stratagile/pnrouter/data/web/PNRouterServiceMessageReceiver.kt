package com.stratagile.pnrouter.data.web

import com.alibaba.fastjson.JSONObject
import com.socks.library.KLog
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.utils.GsonUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import java.io.IOException


class PNRouterServiceMessageReceiver
/**
 * Construct a PNRouterServiceMessageReceiver.
 *
 * @param urls The URL of the Signal Service.
 * @param credentials The Signal Service user's credentials.
 */
constructor(private val urls: SignalServiceConfiguration, private val credentialsProvider: CredentialsProvider, private val userAgent: String, private val connectivityListener: ConnectivityListener) : SignalServiceMessagePipe.MessagePipeCallback {

    override fun onMessage(baseData: BaseData, text :String?) {
        KLog.i(baseData.baseDataToJson())
        KLog.i(baseData.params.toString())
        var gson = GsonUtil.getIntGson()
        var paramsStr = (JSONObject.parseObject(baseData.baseDataToJson())).get("params").toString()
        KLog.i(paramsStr)
        when (JSONObject.parseObject(paramsStr).getString("Action")) {
            "Login" -> {
                val loginRsp  = gson.fromJson(text, LoginRspWrapper::class.java)
                KLog.i(loginRsp)
                loginBackListener?.loginBack(loginRsp)
            }
            "AddFriendReq" -> {
                val addFreindRsp  = gson.fromJson(text, JAddFreindRsp::class.java)
                KLog.i(addFreindRsp.toString())
                addfrendCallBack?.addFriendBack(addFreindRsp)
            }
            "AddFriendPush"-> {
                val addFreindPusRsp  = gson.fromJson(text, JAddFriendPushRsp::class.java)
                KLog.i(addFreindPusRsp.toString())
                mainInfoBack?.addFriendPushRsp(addFreindPusRsp)
            }
            "AddFriendDeal"-> {
                val addFriendDealRsp = gson.fromJson(text, JAddFriendDealRsp::class.java)
                addFriendDealCallBack?.addFriendDealRsp(addFriendDealRsp)
            }
            "AddFriendReply"-> {
                val jAddFriendReplyRsp = gson.fromJson(text, JAddFriendReplyRsp::class.java)
                mainInfoBack?.addFriendReplyRsp(jAddFriendReplyRsp)
            }
            "SendMsg"-> {
                val JSendMsgRsp = gson.fromJson(text, JSendMsgRsp::class.java)
                chatCallBack?.sendMsgRsp(JSendMsgRsp)
            }"PushMsg"-> {
            val JPushMsgRsp = gson.fromJson(text, JPushMsgRsp::class.java)
            chatCallBack?.pushMsgRsp(JPushMsgRsp)
            }
        }
        messageListner?.onMessage(baseData)
    }



//    private val socket: PushServiceSocket

    var  pipe : SignalServiceMessagePipe? = null
    var messageListner : MessageReceivedCallback? = null
    var loginBackListener : LoginMessageCallback? = null
    var addfrendCallBack : AddfrendCallBack? = null
    var mainInfoBack : MainInfoBack? = null
    var addFriendDealCallBack : AddFriendDealCallBack? = null
    var chatCallBack : ChatCallBack? = null

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
        fun onMessage(baseData : BaseData)
    }

    class NullMessageReceivedCallback : MessageReceivedCallback {
        override fun onMessage(envelope: BaseData) {}
    }

    interface LoginMessageCallback {
        fun loginBack(loginRsp : LoginRspWrapper)
    }

    interface AddfrendCallBack {
        fun addFriendBack(addFriendRsp : JAddFreindRsp)
    }

    interface MainInfoBack {
        fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp)
        fun addFriendReplyRsp(jAddFriendReplyRsp : JAddFriendReplyRsp)
    }
    interface AddFriendDealCallBack {
        fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp)
    }

    interface ChatCallBack {
        fun sendMsg(FromId:String,ToId:String,Msg:String);
        fun sendMsgRsp(sendMsgRsp : JSendMsgRsp)
        fun pushMsgRsp(pushMsgRsp : JPushMsgRsp)
    }
}

