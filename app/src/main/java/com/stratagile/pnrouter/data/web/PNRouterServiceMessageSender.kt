package com.stratagile.pnrouter.data.web

import android.util.Log
import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.stratagile.pnrouter.data.service.MessageRetrievalService.pipe
import io.reactivex.annotations.SchedulerSupport.CUSTOM
import java.io.IOException
import java.security.InvalidKeyException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class PNRouterServiceMessageSender @Inject constructor(pipe: Optional<SignalServiceMessagePipe>, private val eventListener: Optional<EventListener>) {
    private val pipe: AtomicReference<Optional<SignalServiceMessagePipe>>
    init {
        this.pipe = AtomicReference(pipe)
    }
    fun send(message : String) : Boolean{
        Log.i("dddd", "test....")
        return pipe.get().get().webSocketConnection().send(message)
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
//        sendMessage(recipient, message.getWhen(), content, true)
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
//        sendMessage(recipient, System.currentTimeMillis(), content, true)
//    }



    interface EventListener {
        fun onSecurityEvent(address: SignalServiceAddress)
    }

    companion object {

        private val TAG = PNRouterServiceMessageSender::class.java.simpleName
    }

}