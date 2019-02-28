package com.stratagile.pnrouter.data.web

import android.util.Log
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class PNRouterServiceMessageSender @Inject constructor(pipe: Optional<SignalServiceMessagePipe>, private val eventListener: Optional<EventListener>) {
    private val pipe: AtomicReference<Optional<SignalServiceMessagePipe>>
    var javaObject = Object()
    lateinit var msgHashMap: HashMap<String,Queue<BaseData>>
    lateinit var toSendChatMessage: Queue<BaseData>
    lateinit var toSendMessage: Queue<BaseData>
    lateinit var thread: Thread

    init {
        msgHashMap = HashMap<String,Queue<BaseData>>()
        toSendMessage = LinkedList()
        //toSendChatMessage = LinkedList()
        this.pipe = AtomicReference(pipe)
        initThread()
    }

    fun send(message: BaseData){
        Log.i("sender", "添加")
        toSendMessage.offer(message)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        /*if (thread.state == Thread.State.NEW) {
            thread.start()
        }*/
        sendOtherMessage()
//        javaObject.notifyAll()
//        return sendMessageTo()
    }
    fun sendChatMsg(message: BaseData){
        Log.i("sender", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,LinkedList())
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }
        toSendChatMessage.offer(message)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        sendChatMessage(true,false)
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
                Log.i("sender", "发送线程运行中等待。。。")
                Thread.sleep(10 * 1000)
                Log.i("sender", "发送线程运行中。。。")
                sendChatMessage(false,false)
//               Log.i("sender", "线程运行中。。。")
            }
        }
        thread.name = "sendThread"
    }


    fun sendChatMessage(sendNow:Boolean,remove:Boolean) {
        try {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var toSendChatMessageQueue = msgHashMap.get(userId!!) as Queue<BaseData>
            if (toSendChatMessageQueue != null && toSendChatMessageQueue.isNotEmpty()){
                Log.i("sendChat_size", toSendChatMessageQueue.size.toString())
                if(sendNow)
                {
                    var message = BaseData()
                    if(remove)
                    {
                        message = toSendChatMessageQueue.poll()
                    }else{
                        message = toSendChatMessageQueue.peek()
                    }
                    Log.i("sendChat_message", message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                    var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送结果：${reslut}")
                }else{
                    for (item in toSendChatMessageQueue)
                    {
                        if(Calendar.getInstance().timeInMillis - item.timestamp!!.toLong() > 10 * 1000)
                        {
                            Log.i("sendChat_message_Thread", item.baseDataToJson().replace("\\", ""))
                            LogUtil.addLog("发送信息：${item.baseDataToJson().replace("\\", "")}")
                            var reslut= pipe.get().get().webSocketConnection().send(item.baseDataToJson().replace("\\", ""))
                            LogUtil.addLog("发送结果：${reslut}")
                        }
                    }
                }

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    fun sendOtherMessage() {
        try {
            if (toSendMessage != null && toSendMessage.isNotEmpty()){

                    var message = toSendMessage.poll()
                    Log.i("send", message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                    var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送结果：${reslut}")

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    interface EventListener {
        fun onSecurityEvent(address: SignalServiceAddress)
    }

    companion object {

        private val TAG = PNRouterServiceMessageSender::class.java.simpleName
    }

}