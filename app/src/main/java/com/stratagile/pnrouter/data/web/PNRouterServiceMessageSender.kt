package com.stratagile.pnrouter.data.web

import android.util.Log
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.stratagile.pnrouter.data.service.MessageRetrievalService.pipe
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import io.reactivex.annotations.SchedulerSupport.CUSTOM
import java.io.IOException
import java.security.InvalidKeyException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.concurrent.thread

class PNRouterServiceMessageSender @Inject constructor(pipe: Optional<SignalServiceMessagePipe>, private val eventListener: Optional<EventListener>) {
    private val pipe: AtomicReference<Optional<SignalServiceMessagePipe>>
    var javaObject = Object()
    lateinit var toSendMessage: Queue<BaseData>
    lateinit var thread: Thread

    init {
        toSendMessage = LinkedList()
        this.pipe = AtomicReference(pipe)
        initThread()
    }

    fun send(message: BaseData){
        Log.i("sender", "添加")
        toSendMessage.offer(message)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
//        javaObject.notifyAll()
//        return sendMessageTo()
    }
    /**
     * Send a read receipt for a received message.
     *
     * @param recipient The sender of the received message you're acknowledging.
     * @param message The read receipt to deliver.
     * @throws IOException
     * @throws UntrustedIdentityException
     */
//    fun sendReceipt(recipient: SignalServiceAddress, message: SignalServiceReceiptMessage) {
//        val content = createReceiptContent(message)
//        sendMessageTo(recipient, message.getWhen(), content, true)
//    }

    /**
     * Send a call setup message to a single recipient.
     *
     * @param recipient The message's destination.
     * @param message The call message.
     * @throws IOException
     */
//    fun sendCallMessage(recipient: SignalServiceAddress, message: SignalServiceCallMessage) {
//        val content = createCallContent(message)
//        sendMessageTo(recipient, System.currentTimeMillis(), content, true)
//    }

    @Synchronized
    fun  initThread() {
       thread =  thread {
           while (true) {
               sendMessage()
//               Log.i("sender", "线程运行中。。。")
           }
        }
    }

    fun sendMessage() {
        try {
            if (toSendMessage != null && toSendMessage.isNotEmpty()) {
                var message = toSendMessage.poll()
                Log.i("send", message.baseDataToJson().replace("\\", ""))
                LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                LogUtil.addLog("发送结果：${reslut}")
            } else {

            }
        }catch (e:Exception)
        {

        }

    }


    interface EventListener {
        fun onSecurityEvent(address: SignalServiceAddress)
    }

    companion object {

        private val TAG = PNRouterServiceMessageSender::class.java.simpleName
    }

}