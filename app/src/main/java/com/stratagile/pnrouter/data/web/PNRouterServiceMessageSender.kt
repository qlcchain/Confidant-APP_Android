package com.stratagile.pnrouter.data.web

import android.graphics.BitmapFactory
import android.util.Log
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.smailnet.eamil.Utils.AESCipher
import com.smailnet.eamil.Utils.AESToolsCipher
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.MessageEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.concurrent.thread

class PNRouterServiceMessageSender @Inject constructor(pipe: Optional<SignalServiceMessagePipe>, private val eventListener: Optional<EventListener>) {
    private var pipe: AtomicReference<Optional<SignalServiceMessagePipe>>
    var javaObject = Object()
    lateinit var msgHashMap: ConcurrentHashMap<String, ConcurrentLinkedQueue<BaseData>>
    lateinit var fileHashMap: ConcurrentHashMap<String, ConcurrentLinkedQueue<SendFileInfo>>
    lateinit var toSendChatMessage: ConcurrentLinkedQueue<BaseData>
    lateinit var toSendChatFileMessage: ConcurrentLinkedQueue<SendFileInfo>
    lateinit var toSendMessage: ConcurrentLinkedQueue<BaseData>
    lateinit var thread: Thread
    lateinit var sendMsgLocalMap: ConcurrentHashMap<String, Boolean>
    lateinit var sendFilePathMap : ConcurrentHashMap<String, String>
    lateinit var deleteFileMap : ConcurrentHashMap<String, Boolean>
    lateinit var sendFileFriendKeyMap : ConcurrentHashMap<String, String>
    lateinit var sendFileKeyByteMap : ConcurrentHashMap<String, String>
    lateinit var sendFileFriendKeyByteMap : ConcurrentHashMap<String, ByteArray>
    lateinit var sendFileWidthAndHeightMap : ConcurrentHashMap<String, String>
    lateinit var sendFilePorpertyMap : ConcurrentHashMap<String, String>
    lateinit var sendFileMyKeyByteMap : ConcurrentHashMap<String, ByteArray>
    lateinit var sendFileResultMap:ConcurrentHashMap<String, Boolean>
    lateinit var sendFileNameMap:ConcurrentHashMap<String, String>
    lateinit var sendFileLastByteSizeMap:ConcurrentHashMap<String, Int>
    lateinit var sendFileLeftByteMap:ConcurrentHashMap<String, ByteArray>
    lateinit var sendMsgIdMap:ConcurrentHashMap<String, String>
    lateinit var receiveFileDataMap:ConcurrentHashMap<String, Message>
    lateinit var receiveToxFileDataMap:ConcurrentHashMap<String, Message>
    lateinit var receiveToxFileIdMap:ConcurrentHashMap<String, String>
    lateinit var sendFileMsgTimeMap:ConcurrentHashMap<String, String>;
    var fileLock = Object()
    init {
        sendMsgLocalMap = ConcurrentHashMap<String, Boolean>()
        sendFilePathMap = ConcurrentHashMap<String, String>()
        deleteFileMap = ConcurrentHashMap<String, Boolean>()
        sendFileFriendKeyMap = ConcurrentHashMap<String, String>()
        sendFileKeyByteMap = ConcurrentHashMap<String, String>()
        sendFileFriendKeyByteMap = ConcurrentHashMap<String, ByteArray>()
        sendFileMyKeyByteMap = ConcurrentHashMap<String, ByteArray>()

        sendFileResultMap = ConcurrentHashMap<String, Boolean>()
        sendFileNameMap = ConcurrentHashMap<String, String>()
        sendFileWidthAndHeightMap = ConcurrentHashMap<String, String>()
        sendFilePorpertyMap = ConcurrentHashMap<String, String>()
        sendFileLastByteSizeMap = ConcurrentHashMap<String, Int>()
        sendFileLeftByteMap = ConcurrentHashMap<String, ByteArray>()
        sendMsgIdMap = ConcurrentHashMap<String, String>()
        receiveFileDataMap = ConcurrentHashMap<String, Message>()
        receiveToxFileDataMap = ConcurrentHashMap<String, Message>()
        receiveToxFileIdMap = ConcurrentHashMap<String, String>()
        sendFileMsgTimeMap = ConcurrentHashMap<String, String>()
        EventBus.getDefault().register(this)
        msgHashMap = ConcurrentHashMap<String,ConcurrentLinkedQueue<BaseData>>()
        fileHashMap = ConcurrentHashMap<String,ConcurrentLinkedQueue<SendFileInfo>>()
        toSendMessage = ConcurrentLinkedQueue()
        //toSendChatMessage = LinkedList()
        KLog.i("超时调试：PNRouterServiceMessageSender init"+pipe)
        this.pipe = AtomicReference(pipe)
        initThread()
    }
    fun setPipe(pipeFrom: Optional<SignalServiceMessagePipe>)
    {
        pipe = AtomicReference(pipeFrom)
    }
    fun addDataFromSql(userId:String, BaseDataStr:String)
    {
        var gson = GsonUtil.getIntGson()
        val message = gson.fromJson(BaseDataStr, BaseData::class.java)
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }
        toSendChatMessage.offer(message)
    }
    fun addFileDataFromSql(userId:String, sendFileInfo:SendFileInfo)
    {
        if(fileHashMap.get(userId) == null)
        {
            fileHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }else{
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }
        toSendChatFileMessage.offer(sendFileInfo)
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

    /**
     * 私聊发送文字消息
     */
    fun sendChatMsg(message: BaseData){
        Log.i("sender", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }
        toSendChatMessage.offer(message)
        var gson = GsonUtil.getIntGson()
        val SendMsgReqV3 =  message.params as SendMsgReqV3
        var messageEntity  = MessageEntity()
        messageEntity.userId = userId;
        messageEntity.friendId = SendMsgReqV3.To
        messageEntity.sendTime = message.timestamp
        messageEntity.type = "0"
        messageEntity.msgId = message.msgid.toString()
        messageEntity.baseData = message.baseDataToJson().replace("\\", "")
        messageEntity.complete = false
        KLog.i("消息数据增加文本：userId："+userId +" friendId:"+SendMsgReqV3.To)
        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.insert(messageEntity)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            sendChatMessage(true,false)
        }
    }

    /**
     * 群聊发送文字消息
     */
    fun sendGroupChatMsg(message: BaseData){
        Log.i("sender", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as ConcurrentLinkedQueue<BaseData>
        }
        toSendChatMessage.offer(message)  //开发期间不要重复
        var gson = GsonUtil.getIntGson()
        val GroupSendMsgReq =  message.params as GroupSendMsgReq
        var messageEntity  = MessageEntity()
        messageEntity.userId = userId;
        messageEntity.friendId = GroupSendMsgReq.GId
        messageEntity.sendTime = message.timestamp
        messageEntity.type = "0"
        messageEntity.msgId = message.msgid.toString()
        messageEntity.baseData = message.baseDataToJson().replace("\\", "")
        messageEntity.complete = false
        KLog.i("群聊消息数据增加文本：userId："+userId +" friendId:"+GroupSendMsgReq.GId)
        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.insert(messageEntity)//开发期间不要重复
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            sendChatMessage(true,false)
        }
    }
    fun sendEmailFileMsg(message: SendFileInfo){

        Log.i("sendEmailFileMsg", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(fileHashMap.get(userId) == null)
        {
            fileHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }else{
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }
        toSendChatFileMessage.offer(message)

        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            if(!ConstantValue.currentRouterId.equals(""))
            {
                sendChatFileMessage(true,false)
            }

        }

    }
    fun sendFileMsg(message: SendFileInfo){

        Log.i("senderFile", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(fileHashMap.get(userId) == null)
        {
            fileHashMap.put(userId!!,ConcurrentLinkedQueue())
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }else{
            toSendChatFileMessage = fileHashMap.get(userId!!) as ConcurrentLinkedQueue<SendFileInfo>
        }
        toSendChatFileMessage.offer(message)

        var messageEntity  = MessageEntity()
        messageEntity.userId = userId;
        messageEntity.friendId = message.friendId
        messageEntity.sendTime = message.sendTime
        messageEntity.type = message.type
        messageEntity.msgId = message.msgId
        messageEntity.baseData = ""
        messageEntity.complete = false
        messageEntity.filePath = message.files_dir
        messageEntity.friendSignPublicKey = message.friendSignPublicKey
        messageEntity.friendMiPublicKey = message.friendMiPublicKey
        messageEntity.voiceTimeLen = message.voiceTimeLen
        messageEntity.widthAndHeight = message.widthAndHeight
        messageEntity.porperty = message.porperty
        KLog.i("消息数据增加文件：userId："+userId +" friendId:"+message.friendId)
        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.insert(messageEntity)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            if(!ConstantValue.currentRouterId.equals(""))
            {
                sendChatFileMessage(true,false)
            }

        }

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
//        sendMessageTo(recipient, System.currentTimeMillis() / 1000, content, true)
//    }

    @Synchronized
    fun  initThread() {
        thread =  thread(true, false, null, "senderThread") {
            while (true) {
                //KLog.i("发送线程运行中等待。。。")
                Thread.sleep(10 * 1000)
                //KLog.i( "发送线程运行中。。。")
                if(WiFiUtil.isNetworkConnected() && ConstantValue.logining && ConstantValue.curreantNetworkType.equals("WIFI"))
                {
                    sendChatMessage(false,false)
                    if(!ConstantValue.currentRouterIp.equals(""))
                    {
                        sendChatFileMessage(false,false)
                    }
                }else{
                    ConstantValue.sendFileMsgMap.clear()
                }
//               Log.i("sender", "线程运行中。。。")
            }
        }
        thread.name = "sendThread"
    }


    fun sendChatMessage(sendNow:Boolean,remove:Boolean) {
        try {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(msgHashMap.get(userId!!) == null)
            {
                return
            }
            var toSendChatMessageQueue = msgHashMap.get(userId!!) as Queue<BaseData>
            if (toSendChatMessageQueue != null && toSendChatMessageQueue.isNotEmpty()){
                KLog.i("sendChat_size" + toSendChatMessageQueue.size.toString())
                if(sendNow)
                {
                    var message = BaseData()
                    if(remove)
                    {
                        message = toSendChatMessageQueue.poll()
                    }else{
                        message = toSendChatMessageQueue.peek()
                    }
                    KLog.i("sendChat_message" + message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                    var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送结果：${reslut}")
                }else{
                    if(ConstantValue.logining)
                    {
                        Log.i("sendChat_size_Auto", toSendChatMessageQueue.size.toString())
                        for (item in toSendChatMessageQueue)
                        {
                            if(Calendar.getInstance().timeInMillis - item.timestamp!!.toLong() > 10 * 1000)
                            {
                                item.timestamp = Calendar.getInstance().timeInMillis.toString();
                                KLog.i("sendChat_message_Thread" + item.baseDataToJson().replace("\\", ""))
                                LogUtil.addLog("发送信息：${item.baseDataToJson().replace("\\", "")}")
                                var reslut= pipe.get().get().webSocketConnection().send(item.baseDataToJson().replace("\\", ""))
                                LogUtil.addLog("发送结果：${reslut}")
                            }
                        }
                    }

                }

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    fun sendChatFileMessage(sendNow:Boolean,remove:Boolean) {
        try {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(fileHashMap.get(userId!!) == null)
            {
                return
            }
            var toSendChatFileQueue = fileHashMap.get(userId!!) as Queue<SendFileInfo>
            if (toSendChatFileQueue != null && toSendChatFileQueue.isNotEmpty()){
                KLog.i("sendFile_size" + toSendChatFileQueue.size.toString())
                if(sendNow)
                {
                    var message = SendFileInfo()
                    if(remove)
                    {
                        message = toSendChatFileQueue.poll()
                    }else{
                        message = toSendChatFileQueue.peek()
                    }
                    when(message.type){
                        "1" ->
                        {
     
                            sendImageMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey, message.widthAndHeight,message.porperty)
                        }
                        "2" ->
                        {
                            sendVoiceMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey,message.voiceTimeLen,message.porperty!!)
                        }
                        "3" ->
                        {
                            sendVideoMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey,message.porperty!!)
                        }
                        "4" ->
                        {
                            sendFileMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey,message.porperty!!)
                        }
                        "5" ->
                        {
                            sendEmailFileMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey,message.porperty!!)
                        }
                    }
                }else{
                    if(ConstantValue.logining)
                    {
                        KLog.i("sendFile_size_Auto" + toSendChatFileQueue.size.toString())
                        Thread(Runnable() {
                            run() {

                                try {
                                    //synchronized(fileLock){
                                        var iterator = toSendChatFileQueue.iterator()
                                        while (iterator.hasNext()) {
                                            var  item = iterator.next()
                                            KLog.i("aa")
                                            Thread.sleep(200)//防止服务器压力过大
                                            KLog.i("bb")
                                            if(sendFileMsgTimeMap[item.msgId] != null)
                                            {
                                                KLog.i("sendFile_size_Auto1" + "重置前:"+sendFileMsgTimeMap[item.msgId] +"   相差：" +(Calendar.getInstance().timeInMillis - sendFileMsgTimeMap[item.msgId]!!.toLong()))
                                                if(Calendar.getInstance().timeInMillis - sendFileMsgTimeMap[item.msgId]!!.toLong() > 40 * 1000)
                                                {
                                                    KLog.i("sendFile_size_Auto2" + "重置")
                                                    val message = EMMessage.createImageSendMessage(item.files_dir, true, item.friendId)
                                                    ConstantValue.sendFileMsgMap[item.msgId] = message
                                                    val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                                    EventBus.getDefault().post(FileTransformEntity(item.msgId, 4, "", wssUrl, "lws-pnr-bin"))
                                                }
                                            }
                                            item.sendTime = Calendar.getInstance().timeInMillis.toString();
                                            when(item.type){
                                                "1" ->
                                                {
         
                                                    sendImageMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey, item.widthAndHeight,item.porperty!!)
                                                }
                                                "2" ->
                                                {
        
                                                    sendVoiceMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey,item.voiceTimeLen,item.porperty!!)
                                                }
                                                "3" ->
                                                {
    
                                                    sendVideoMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey,item.porperty!!)
                                                }
                                                "4" ->
                                                {
       
                                                    sendFileMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey,item.porperty!!)
                                                }


                                            }
                                        }
                                    //}
                                }catch (e:Exception)
                                {

                                    e.printStackTrace()
                                }

                                /* for (item in toSendChatFileQueue)
                                 {
                                     KLog.i("aa")
                                     Thread.sleep(1000)
                                     KLog.i("bb")
                                     if(sendFileMsgTimeMap[item.msgId] != null)
                                     {
                                         if(Calendar.getInstance().timeInMillis - sendFileMsgTimeMap[item.msgId]!!.toLong() > 2 * 60 * 1000)
                                         {
                                             Log.i("sendFile_size_Auto2", "重置")
                                             val message = EMMessage.createImageSendMessage(item.files_dir, true, item.friendId)
                                             ConstantValue.sendFileMsgMap[item.msgId] = message
                                         }
                                     }
                                     item.sendTime = Calendar.getInstance().timeInMillis.toString();
                                     when(item.type){
                                         "1" ->
                                         {
                                             sendImageMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                         }
                                         "2" ->
                                         {
                                             sendVoiceMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey,item.voiceTimeLen)
                                         }
                                         "3" ->
                                         {
                                             sendVideoMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                         }
                                         "4" ->
                                         {
                                             sendFileMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                         }
                                     }
                                 }*/
                            }
                        }).start()

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
                KLog.i("send " + message.baseDataToJson().replace("\\", ""))
                LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                KLog.i("超时调试：4" + pipe)
                KLog.i("超时调试：4___" + AppConfig.instance.messageSender!!.pipe)
                KLog.i("发送信息2："+pipe.get())
                KLog.i("发送信息3："+pipe.get().get())
                KLog.i("超时调试：webSocketConnection 5"+pipe.get().get().webSocketConnection())
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(TransformStrMessageUrlFail: TransformStrMessageUrlFail) {
        KLog.i("websocket状态MainActivity:" + TransformStrMessageUrlFail.toId)
        if (TransformStrMessageUrlFail.toId != null) {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var toSendChatFileQueue = fileHashMap.get(userId!!) as Queue<SendFileInfo>
            for (item in toSendChatFileQueue)
            {
                if(item.msgId.equals(TransformStrMessageUrlFail.toId))
                {
                    val message = EMMessage.createImageSendMessage(item.files_dir, true, item.friendId)
                    ConstantValue.sendFileMsgMap[item.msgId] = message
                }
                for (msg in sendFileMsgTimeMap)
                {
                    if(msg.key.equals(TransformStrMessageUrlFail.toId))
                    {
                        msg.setValue((System.currentTimeMillis()- 300 *60 * 1000).toString())
                    }

                }
            }

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态MainActivity:" + connectStatus.status)
        if (connectStatus.status != 0) {
            ConstantValue.sendFileMsgMap = ConcurrentHashMap<String, EMMessage>()
            for (item in sendFileMsgTimeMap)
            {
                item.setValue((System.currentTimeMillis()- 300 *60 * 1000).toString())
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteMsgEvent(deleteMsgEvent: DeleteMsgEvent) {
        deleteFileMap[deleteMsgEvent.msgId] = true
        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
        if(messageEntityList != null)
        {
            messageEntityList.forEach {
                if (it.msgId.equals(deleteMsgEvent.msgId)) {
                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                    KLog.i("消息数据删除")
                }
            }
        }
        var  toSendMessage = toSendChatFileMessage
        if(toSendMessage != null)
        {
            for (item in toSendMessage)
            {
                if(item.msgId.equals(deleteMsgEvent.msgId))
                {
                    toSendMessage.remove(item)
                    break
                }
            }
        }
    }
    /**
     * 开始发送文件
     * @param fileTransformEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBeginSendFile(fileTransformEntity: FileTransformEntity) {
        val EMMessage = ConstantValue.sendFileMsgMap[fileTransformEntity.toId] ?: return
        if(EMMessage.from == null)
        {
            return
        }
        if (fileTransformEntity.message == 0) {
            return
        }
//        sendFileWidthAndHeightMap[fileTransformEntity.toId] = fileTransformEntity.widthAndHeight
//        KLog.i("设置图片的宽高：" + fileTransformEntity.toId + "  " + fileTransformEntity.widthAndHeight)
        when (fileTransformEntity.message) {
            1 -> /*Thread(Runnable {*/
                try {
                    //KLog.i("错误：onBeginSendFile:" +fileTransformEntity.toId)
                    sendFileMsgTimeMap[fileTransformEntity.toId] = System.currentTimeMillis().toString()
                    val EMMessage = ConstantValue.sendFileMsgMap[fileTransformEntity.toId]
                    if(EMMessage!!.from == null)
                    {
                        return
                    }
                    val filePath = sendFilePathMap[fileTransformEntity.toId] ?: return
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                    val fileKey = sendFileKeyByteMap[fileTransformEntity.toId]
                    val SrcKey = sendFileMyKeyByteMap[fileTransformEntity.toId]
                    val DstKey = sendFileFriendKeyByteMap[fileTransformEntity.toId]
                    val file = File(filePath)
                    if (file.exists()) {
                        val fileSize = file.length()
                        val fileBuffer = FileUtil.file2Byte(filePath)
                        val fileId = (System.currentTimeMillis() / 1000 ).toInt()
                        var fileBufferMi = ByteArray(0)
                        try {
                            val miBegin = System.currentTimeMillis() / 1000
                            /*if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                            {
                                fileBufferMi = LibsodiumUtil.INSTANCE.EncryptSendFile(fileBuffer,fileKey);
                            }else{
                                fileBufferMi = AESToolsCipher.aesEncryptBytes(fileBuffer,fileKey.getBytes("UTF-8"));
                            }*/
                            fileBufferMi = AESToolsCipher.aesEncryptBytes(fileBuffer, fileKey!!.toByteArray(charset("UTF-8")))
                            KLog.i("密文件大小 发送:" + fileBufferMi.size+"_aesKey:"+fileKey)
                            val miend = System.currentTimeMillis() / 1000
                            KLog.i("jiamiTime:" + (miend - miBegin) / 1000)

                            if (deleteFileMap[fileTransformEntity.toId] != null) {
                                sendFileByteData(fileBufferMi, fileName, EMMessage!!.getFrom(), EMMessage!!.getTo(), fileTransformEntity.toId, fileId, 1, fileKey, SrcKey!!, DstKey!!)
                            } else {
                                var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                                if(messageEntityList != null)
                                {
                                    messageEntityList.forEach {
                                        if (it.msgId.equals(fileTransformEntity.toId)) {
                                            AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                            KLog.i("消息数据删除")
                                        }
                                    }
                                }
                                var  toSendMessage = toSendChatFileMessage
                                if(toSendMessage != null)
                                {
                                    for (item in toSendMessage)
                                    {
                                        if(item.msgId.equals(fileTransformEntity.toId))
                                        {
                                            toSendMessage.remove(item)
                                            break
                                        }
                                    }
                                }
                                 sendFileLeftByteMap.remove(fileTransformEntity.toId)
                                 sendFileNameMap.remove(fileTransformEntity.toId)
                                 sendFileLastByteSizeMap.remove(fileTransformEntity.toId)
                                 sendFileKeyByteMap.remove(fileTransformEntity.toId)
                                 sendFileMyKeyByteMap.remove(fileTransformEntity.toId)
                                 sendFileFriendKeyByteMap.remove(fileTransformEntity.toId)
                                sendFileWidthAndHeightMap.remove(fileTransformEntity.toId)
                                sendFilePorpertyMap.remove(fileTransformEntity.toId)
                                System.gc()
                                KLog.i("websocket文件发送前取消！")
                                val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                                EventBus.getDefault().post(FileTransformEntity(fileTransformEntity.toId, 4, "", wssUrl, "lws-pnr-bin", fileTransformEntity.widthAndHeight))
                                EventBus.getDefault().post(FileTransformStatus(fileTransformEntity.toId,"", EMMessage!!.getTo(),0))
                            }

                        } catch (e: Exception) {
                             sendFileLeftByteMap.remove(fileTransformEntity.toId)
                             sendFileNameMap.remove(fileTransformEntity.toId)
                             sendFileLastByteSizeMap.remove(fileTransformEntity.toId)
                             sendFileKeyByteMap.remove(fileTransformEntity.toId)
                             sendFileMyKeyByteMap.remove(fileTransformEntity.toId)
                             sendFileFriendKeyByteMap.remove(fileTransformEntity.toId)
                            sendFileWidthAndHeightMap.remove(fileTransformEntity.toId)
                            sendFilePorpertyMap.remove(fileTransformEntity.toId)
                             System.gc()
                            val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                            EventBus.getDefault().post(FileTransformEntity(fileTransformEntity.toId, 4, "", wssUrl, "lws-pnr-bin"))
                        }

                    }
                } catch (e: Exception) {
                     sendFileLeftByteMap.remove(fileTransformEntity.toId)
                     sendFileNameMap.remove(fileTransformEntity.toId)
                     sendFileLastByteSizeMap.remove(fileTransformEntity.toId)
                     sendFileKeyByteMap.remove(fileTransformEntity.toId)
                     sendFileMyKeyByteMap.remove(fileTransformEntity.toId)
                     sendFileFriendKeyByteMap.remove(fileTransformEntity.toId)
                    sendFileWidthAndHeightMap.remove(fileTransformEntity.toId)
                    sendFilePorpertyMap.remove(fileTransformEntity.toId)
                     System.gc()
                }
            /*}).start()*/
            2 -> {
            }
            3 -> {
            }
            else -> {
            }
        }
    }

    /**
     * 片段发送中
     * @param transformReceiverFileMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendFileing(transformReceiverFileMessage: TransformReceiverFileMessage) {
        //KLog.i("错误：onSendFileing:" +transformReceiverFileMessage.toId)
        val EMMessageData = ConstantValue.sendFileMsgMap[transformReceiverFileMessage.toId] ?: return
        if(EMMessageData.from == null)
        {
            return
        }
        //KLog.i("错误：onSendFileing2:" +transformReceiverFileMessage.toId)
        sendFileMsgTimeMap[transformReceiverFileMessage.toId] =  System.currentTimeMillis().toString()
        val retMsg = transformReceiverFileMessage.message
        val Action = ByteArray(4)
        val FileId = ByteArray(4)
        val LogId = ByteArray(4)
        val SegSeq = ByteArray(4)
        val CRC = ByteArray(2)
        val Code = ByteArray(2)
        val FromId = ByteArray(76)
        val ToId = ByteArray(76)
        //val serverTime = ByteArray(4)
        System.arraycopy(retMsg, 0, Action, 0, 4)
        System.arraycopy(retMsg, 4, FileId, 0, 4)
        System.arraycopy(retMsg, 8, LogId, 0, 4)
        System.arraycopy(retMsg, 12, SegSeq, 0, 4)
        System.arraycopy(retMsg, 16, CRC, 0, 2)
        System.arraycopy(retMsg, 18, Code, 0, 2)
        System.arraycopy(retMsg, 20, FromId, 0, 76)
        System.arraycopy(retMsg, 97, ToId, 0, 76)
        //System.arraycopy(retMsg, 173, serverTime, 0, 4)
        val ActionResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(Action))
        val FileIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(FileId))
        val LogIdIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(LogId))
        val SegSeqResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(SegSeq))
        val CRCResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(CRC))
        val CodeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(Code)).toInt()
        val FromIdResult = String(FromId)
        val ToIdResult = String(ToId)
        //val serverTimeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(serverTime)).toInt()
        val serverTimeResult = 0;
        KLog.i("CodeResult:$CodeResult" +"FileIdResult:$FileIdResult")

        //val msgId = sendMsgIdMap.get(FileIdResult.toString() + "")
        val msgId =transformReceiverFileMessage.toId
        for (msg in sendMsgIdMap)
        {
            //KLog.i("错误：msgIdmsgId key  "+msg.key)
            //KLog.i("错误：msgIdmsgId value "+msg.value)
        }
        val lastSendSize = sendFileLastByteSizeMap.get(msgId)
        for (msg in sendFileLastByteSizeMap)
        {
            KLog.i("错误：lastSendSize  "+msg.key+"_"+msgId)
        }
        val fileBuffer = sendFileLeftByteMap.get(msgId)
        for (msg in sendFileLeftByteMap)
        {
            KLog.i("错误：fileBuffer  "+msg.key +"_"+msgId)
        }
        //KLog.i("错误：sendFileLeftByteMap size:"+sendFileLeftByteMap.size + "  msgId:"+msgId +" fileBuffer: "+fileBuffer+" lastSendSize "+lastSendSize)
        KLog.i("错误：fileBufferobject  "+fileBuffer +"_"+lastSendSize)
        val leftSize = fileBuffer!!.size - lastSendSize!!
        val filePath = sendFilePathMap[msgId]
        when (CodeResult) {
            0 -> {

                if (leftSize > 0)
                {
                    /*Thread(Runnable {*/
                    try {
                        val fileLeftBuffer = ByteArray(leftSize)
                        System.arraycopy(fileBuffer, ConstantValue.sendFileSizeMax, fileLeftBuffer, 0, leftSize)
                        val fileName = sendFileNameMap.get(msgId)
                        val fileKey = sendFileKeyByteMap[msgId]
                        val SrcKey = sendFileMyKeyByteMap[msgId]
                        val DstKey = sendFileFriendKeyByteMap[msgId]

                        //KLog.i("错误：onSendFileing6:" +transformReceiverFileMessage.toId)
                        if (deleteFileMap[msgId] != null) {
                            sendFileByteData(fileLeftBuffer, fileName!!, FromIdResult + "", ToIdResult + "", msgId!!, FileIdResult, SegSeqResult + 1, fileKey!!, SrcKey!!, DstKey!!)
                        } else {
                            KLog.i("websocket文件发送中取消！")
                             sendFileLeftByteMap.remove(msgId)
                             sendFileNameMap.remove(msgId)
                             sendFileLastByteSizeMap.remove(msgId)
                             sendFileKeyByteMap.remove(msgId)
                             sendFileMyKeyByteMap.remove(msgId)
                             sendFileFriendKeyByteMap.remove(msgId)
                            sendFileWidthAndHeightMap.remove(msgId)
                            sendFilePorpertyMap.remove(msgId)
                             System.gc()
                            val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                            EventBus.getDefault().post(FileTransformEntity(msgId!!, 4, "", wssUrl, "lws-pnr-bin"))
                            EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                            var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                            if(messageEntityList != null)
                            {
                                messageEntityList.forEach {
                                    if (it.msgId.equals(msgId)) {
                                        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                        KLog.i("消息数据删除")
                                    }
                                }
                            }
                            var  toSendMessage = toSendChatFileMessage
                            if(toSendMessage != null)
                            {
                                for (item in toSendMessage)
                                {
                                    if(item.msgId.equals(msgId))
                                    {
                                        toSendMessage.remove(item)
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                         sendFileLeftByteMap.remove(msgId)
                         sendFileNameMap.remove(msgId)
                         sendFileLastByteSizeMap.remove(msgId)
                         sendFileKeyByteMap.remove(msgId)
                         sendFileMyKeyByteMap.remove(msgId)
                        sendFileWidthAndHeightMap.remove(msgId)
                        sendFilePorpertyMap.remove(msgId)
                         sendFileFriendKeyByteMap.remove(msgId)
                         System.gc()
                    }
                    /*}).start()*/

                } else {
                    //KLog.i("错误：onSendFileing3:" +transformReceiverFileMessage.toId)
                    if (deleteFileMap[msgId] != null) {
                        /*val EMMessage = EMClient.getInstance().chatManager().getMessage(msgId)
                        conversation.removeMessage(msgId)
                        EMMessage.msgId = LogIdIdResult.toString() + ""
                        EMMessage.isAcked = true
                        sendMessageTo(EMMessage)
                        conversation.updateMessage(EMMessage)*/
                        val fileName = filePath!!.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                        if(messageEntityList != null)
                        {
                            messageEntityList.forEach {
                                if (it.msgId.equals(transformReceiverFileMessage.toId)) {
                                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                    KLog.i("消息数据删除："+transformReceiverFileMessage.toId)
                                }
                            }
                        }
                        var  toSendMessage = toSendChatFileMessage
                        if(toSendMessage != null)
                        {
                            for (item in toSendMessage)
                            {
                                if(item.msgId.equals(transformReceiverFileMessage.toId))
                                {
                                    toSendMessage.remove(item)
                                    break
                                }
                            }
                        }

                        //KLog.i("错误ToIdResult："+ToIdResult +"  "+ "LogIdIdResult:"+LogIdIdResult +"  msgId:"+msgId)


                        KLog.i("websocket文件发送成功！")
                        var porperty = sendFilePorpertyMap.get(msgId)
                        if (porperty!= null && porperty.equals("1")) {//群特殊处理
                            EventBus.getDefault().post(FileGroupTransformStatus(transformReceiverFileMessage.toId, LogIdIdResult.toString(),ToIdResult,1,FileIdResult))
                        }else{
                            EventBus.getDefault().post(FileTransformStatus(transformReceiverFileMessage.toId, LogIdIdResult.toString(),ToIdResult,1,serverTimeResult))
                        }
                        if (porperty!= null && porperty.equals("1")) {
                            val file = File(filePath)

                            if(file.exists())
                            {
                                val base58files_dir = PathUtils.getInstance().tempPath.toString() + "/" + fileName
                                val fileKey = sendFileKeyByteMap.get(msgId)
                                val code = FileUtil.copySdcardToxFileAndEncrypt(filePath, base58files_dir, fileKey!!.substring(0, 16))
                                if(code == 1)
                                {
                                    val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                    val fileBase58Name = Base58.encode(fileName.toByteArray())
                                    val fileMD5 = FileUtil.getFileMD5(File(base58files_dir))
                                    val fileNew = File(base58files_dir)
                                    var fileInfo = ""
                                    if(ActionResult == 1)
                                    {
                                        val bitmap = BitmapFactory.decodeFile(filePath)
                                        if(bitmap != null)
                                        {
                                            fileInfo = "" + bitmap.width + ".0000000" + "*" + bitmap.height + ".0000000"
                                        }else{
                                            fileInfo = "200.0000000*200.0000000"
                                        }

                                    }else if(ActionResult == 4)
                                    {
                                        fileInfo = "200.0000000*200.0000000"
                                    }
                                    val GroupSendFileDoneReq = GroupSendFileDoneReq(userId!!,ToIdResult, fileBase58Name, fileMD5!!,fileInfo,fileNew.length().toInt(),ActionResult,FileIdResult.toString(), "GroupSendFileDone")
                                    if (ConstantValue.isWebsocketConnected) {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupSendFileDoneReq))
                                    } else if (ConstantValue.isToxConnected) {
                                        val baseData = BaseData(4, GroupSendFileDoneReq)
                                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                        if (ConstantValue.isAntox) {
                                            val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                        } else {
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                    }
                                }

                            }

                        }

                        //KLog.i("错误：onSendFileing4:" +transformReceiverFileMessage.toId)
                        sendFileLeftByteMap.remove(msgId)
                        sendFileNameMap.remove(msgId)
                        sendFileLastByteSizeMap.remove(msgId)
                        sendFileWidthAndHeightMap.remove(msgId)

                        sendFileKeyByteMap.remove(msgId)
                        sendFileMyKeyByteMap.remove(msgId)
                        sendFileFriendKeyByteMap.remove(msgId)
                        System.gc()
                    } else {
                          sendFileLeftByteMap.remove(msgId)
                          sendFileNameMap.remove(msgId)
                          sendFileLastByteSizeMap.remove(msgId)
                          sendFileKeyByteMap.remove(msgId)
                        sendFileWidthAndHeightMap.remove(msgId)
                        sendFilePorpertyMap.remove(msgId)
                          sendFileMyKeyByteMap.remove(msgId)
                          sendFileFriendKeyByteMap.remove(msgId)
                          System.gc()
                        val msgData = DelMsgReq(FromIdResult, ToIdResult, LogIdIdResult, "DelMsg")
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
                        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                        if(messageEntityList != null)
                        {
                            messageEntityList.forEach {
                                if (it.msgId.equals(msgId)) {
                                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                    KLog.i("消息数据删除")
                                }
                            }
                        }
                        var  toSendMessage = toSendChatFileMessage
                        if(toSendMessage != null)
                        {
                            for (item in toSendMessage)
                            {
                                if(item.msgId.equals(msgId))
                                {
                                    toSendMessage.remove(item)
                                    break
                                }
                            }
                        }
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                        KLog.i("websocket文件发送成功后取消！")
                    }
                    val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                    EventBus.getDefault().post(FileTransformEntity(msgId!!, 4, "", wssUrl, "lws-pnr-bin"))


                }
            }
            else -> {
                 sendFileLeftByteMap.remove(msgId)
                 sendFileNameMap.remove(msgId)
                 sendFileLastByteSizeMap.remove(msgId)
                 sendFileKeyByteMap.remove(msgId)
                 sendFileMyKeyByteMap.remove(msgId)
                sendFileWidthAndHeightMap.remove(msgId)
                sendFilePorpertyMap.remove(msgId)
                 sendFileFriendKeyByteMap.remove(msgId)
                 System.gc()
                var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                if(messageEntityList != null)
                {
                    messageEntityList.forEach {
                        if (it.msgId.equals(msgId)) {
                            AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                            KLog.i("消息数据删除")
                        }
                    }
                }
                var  toSendMessage = toSendChatFileMessage
                if(toSendMessage != null)
                {
                    for (item in toSendMessage)
                    {
                        if(item.msgId.equals(msgId))
                        {
                            toSendMessage.remove(item)
                            break
                        }
                    }
                }
                EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                //SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + ToIdResult, "")
            }
        }
    }

    private fun sendFileByteData(fileLeftBuffer: ByteArray, fileName: String, From: String, To: String, msgId: String, fileId: Int, segSeq: Int, fileKey: String, SrcKey: ByteArray, DstKey: ByteArray) {
        val FriendPublicKey = sendFileFriendKeyMap[msgId]
        //String fileKey =  RxEncryptTool.generateAESKey();
        /*  byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
        byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
        byte[] SrcKey = new byte[256];
        byte[] DstKey = new byte[256];*/
        try {
            /*  SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(fileKey.getBytes(),my));
            DstKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(fileKey.getBytes(),friend));*/
            KLog.i("发送中>>>刚调用From:$From  To:$To")
            val MsgType = fileName.substring(fileName.lastIndexOf(".") + 1)
            var action = 1
            when (MsgType) {
                "png", "jpg", "jpeg","webp" -> action = 1
                "amr" -> action = 2
                "mp4" -> action = 4
                "zip" -> action = 7
                else -> action = 5
            }
            val sendFileData = SendFileData()
            val segSize = if (fileLeftBuffer.size > ConstantValue.sendFileSizeMax) ConstantValue.sendFileSizeMax else fileLeftBuffer.size
            sendFileData.magic = FormatTransfer.reverseInt(0x0dadc0de)
            sendFileData.action = FormatTransfer.reverseInt(action)
            sendFileData.segSize = FormatTransfer.reverseInt(segSize)
            val aa = FormatTransfer.reverseInt(9437440)
            sendFileData.segSeq = FormatTransfer.reverseInt(segSeq)
            val segMore = if (fileLeftBuffer.size > ConstantValue.sendFileSizeMax) 1 else 0
            var fileOffset = 0
            fileOffset = (segSeq - 1) * ConstantValue.sendFileSizeMax
            sendFileData.fileOffset = FormatTransfer.reverseInt(fileOffset)
            sendFileData.fileId = FormatTransfer.reverseInt(fileId)
            sendFileData.crc = FormatTransfer.reverseShort(0.toShort())
            sendFileData.segMore = segMore.toByte()
            sendFileData.cotinue = 0.toByte()
            //String strBase64 = RxEncodeTool.base64Encode2String(fileName.getBytes());
            val strBase58 = Base58.encode(fileName.toByteArray())
            if (action == 1) {
                sendFileData.fileName = (strBase58 + sendFileWidthAndHeightMap[msgId]).toByteArray()
                KLog.i("发送文件的宽高为："  + msgId + "  " + sendFileWidthAndHeightMap[msgId])
                //KLog.i("发送的文件的名字为：" + String(sendFileData.fileName))
            } else {
                sendFileData.fileName = strBase58.toByteArray()
            }
            sendFileData.fromId = From.toByteArray()
            sendFileData.toId = To.toByteArray()
            sendFileData.srcKey = SrcKey
            sendFileData.dstKey = DstKey
            val content = ByteArray(segSize)
            System.arraycopy(fileLeftBuffer, 0, content, 0, segSize)
            var porperty = byteArrayOf(0)//点对点聊天
            if( sendFilePorpertyMap[msgId] != null && sendFilePorpertyMap[msgId].equals("1"))
            {
                porperty = byteArrayOf(1)//群聊
            }
            sendFileData.porperty = porperty
            val ver = byteArrayOf(1)
            sendFileData.ver = ver
            sendFileData.content = content
            var sendData =  byteArrayOf(0)
            //int newCRC = CRC16Util.getCRC(sendData,sendData.length);
            val newCRC = 1
            sendFileData.crc = FormatTransfer.reverseShort(newCRC.toShort())
            sendData = sendFileData.toByteArray()
            sendFileNameMap[msgId] = fileName
            sendFileLastByteSizeMap[msgId] = segSize
            sendFileLeftByteMap[msgId] = fileLeftBuffer
            //KLog.i("错误：sendFileLeftByteMap size put " + sendFileLeftByteMap.size)
            sendMsgIdMap[fileId.toString() + ""] = msgId
            /*sendFileKeyByteMap[fileId.toString() + ""] = fileKey
            sendFileMyKeyByteMap[fileId.toString() + ""] = SrcKey
            sendFileFriendKeyByteMap[fileId.toString() + ""] = DstKey*/
            //KLog.i("发送中>>>内容"+"content:"+aabb);
            KLog.i("发送中>>>"+"msgId:"+msgId +" "+"fileLeftBuffer:"+fileLeftBuffer.size + "  "+ "content:" + content.size + "  "+ "segMore:" + segMore + "  " + "segSize:" + segSize + "   " + "left:" + (fileLeftBuffer.size - segSize) + "  segSeq:" + segSeq + "  fileOffset:" + fileOffset + "  setSegSize:" + segSize + " CRC:" + newCRC)
            EventBus.getDefault().post(TransformFileMessage(msgId, sendData))

        } catch (e: Exception) {
            val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
            EventBus.getDefault().post(FileTransformEntity(msgId, 4, "", wssUrl, "lws-pnr-bin"))
        }

    }
    fun sendImageMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String, widthAndHeigh : String,porperty:String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(files_dir)
                val isHas = file.exists()
                if (isHas) {
                    if (file.length() > 1024 * 1024 * 100) {
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 2))
                    }
                    val fileName = (System.currentTimeMillis() / 1000 ).toInt().toString() + "_" + files_dir.substring(files_dir.lastIndexOf("/") + 1)
                    val message = EMMessage.createImageSendMessage(files_dir, true, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {
                        message.msgId = msgId
                        ConstantValue.sendFileMsgMap[msgId] = message
                        sendFileMsgTimeMap[msgId] =  System.currentTimeMillis() .toString()
                        sendMsgLocalMap.put(msgId, false)
                        sendFilePathMap.put(msgId, files_dir)
                        deleteFileMap.put(msgId, false)
                        sendFileFriendKeyMap.put(msgId, friendSignPublicKey)

                        var fileKey = RxEncryptTool.generateAESKey()
                        if(porperty != null && porperty.equals("1"))
                        {
                            fileKey = LibsodiumUtil.DecryptShareKey(friendSignPublicKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                        }
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {
                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap.put(msgId, fileKey.substring(0, 16))
                            sendFileMyKeyByteMap.put(msgId, SrcKey)
                            sendFileFriendKeyByteMap.put(msgId, DstKey)
                            KLog.i("设置图片的宽高：" + msgId + "  " + widthAndHeigh)
                            sendFileWidthAndHeightMap.put(msgId, widthAndHeigh)
                            sendFilePorpertyMap.put(msgId, porperty)
                        } catch (e: Exception) {
                            var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                            if(messageEntityList != null)
                            {
                                messageEntityList.forEach {
                                    if (it.msgId.equals(msgId)) {
                                        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                        KLog.i("消息数据删除")
                                    }
                                }
                            }
                            var  toSendMessage = toSendChatFileMessage
                            if(toSendMessage != null)
                            {
                                for (item in toSendMessage)
                                {
                                    if(item.msgId.equals(msgId))
                                    {
                                        toSendMessage.remove(item)
                                        break
                                    }
                                }
                            }
                            EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 0))
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin", widthAndHeigh))

                    }
                   /* val gson = Gson()
                    val Message = Message()
                    Message.msgType = 1
                    Message.fileName = fileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId

                    Message.timeStamp = System.currentTimeMillis() / 1000
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)*/
                }

            } catch (e: Exception) {

            }
        }).start()

    }

    fun sendVoiceMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String, length: Int,porperty:String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        try {
            val file = File(files_dir)
            val isHas = file.exists()
            if (isHas) {
                val fileName = files_dir.substring(files_dir.lastIndexOf("/") + 1)
                val message = EMMessage.createVoiceSendMessage(files_dir, length, friendId)
                message.from = userId
                message.to = friendId
                message.isDelivered = true
                message.isAcked = false
                message.isUnread = true
                if (ConstantValue.curreantNetworkType == "WIFI") {
                    message.msgId = msgId
                    ConstantValue.sendFileMsgMap[msgId] = message
                    sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                    sendMsgLocalMap[msgId] = false
                    sendFilePathMap[msgId] = files_dir
                    deleteFileMap[msgId] = false
                    sendFileFriendKeyMap[msgId] = friendSignPublicKey

                    var fileKey = RxEncryptTool.generateAESKey()
                    if(porperty != null && porperty.equals("1"))
                    {
                        fileKey = LibsodiumUtil.DecryptShareKey(friendSignPublicKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                    }
                    val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                    val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                    var SrcKey = ByteArray(256)
                    var DstKey = ByteArray(256)
                    try {

                        if (ConstantValue.encryptionType == "1") {
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                            DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                        } else {
                            SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                            DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                        }
                        sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                        sendFileMyKeyByteMap[msgId] = SrcKey
                        sendFileFriendKeyByteMap[msgId] = DstKey
                        sendFilePorpertyMap.put(msgId, porperty)
                    } catch (e: Exception) {
                        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                        if(messageEntityList != null)
                        {
                            messageEntityList.forEach {
                                if (it.msgId.equals(msgId)) {
                                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                    KLog.i("消息数据删除")
                                }
                            }
                        }
                        var  toSendMessage = toSendChatFileMessage
                        if(toSendMessage != null)
                        {
                            for (item in toSendMessage)
                            {
                                if(item.msgId.equals(msgId))
                                {
                                    toSendMessage.remove(item)
                                    break
                                }
                            }
                        }
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 0))
                        return
                    }


                    val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                    EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))
                }
               /* val gson = Gson()
                val Message = Message()
                Message.msgType = 2
                Message.fileName = fileName
                Message.msg = ""
                Message.from = userId
                Message.to = friendId
                Message.timeStamp = System.currentTimeMillis() / 1000
                Message.unReadCount = 0
                val baseDataJson = gson.toJson(Message)
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)*/

            }
        } catch (e: Exception) {

        }

    }


    fun sendVideoMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String,porperty:String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(files_dir)
                val isHas = file.exists()
                if (isHas) {
                    if (file.length() > 1024 * 1024 * 100) {
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 2))
                    }
                    val videoFileName = files_dir.substring(files_dir.lastIndexOf("/") + 1)
                    val videoName = files_dir.substring(files_dir.lastIndexOf("/") + 1, files_dir.lastIndexOf("."))
                    val thumbPath = PathUtils.getInstance().imagePath.toString() + "/" + videoName + ".png"
                    val bitmap = EaseImageUtils.getVideoPhoto(files_dir)
                    val videoLength = EaseImageUtils.getVideoDuration(files_dir)
                    val message = EMMessage.createVideoSendMessage(files_dir, thumbPath, videoLength, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {

                        message.msgId = msgId

                        ConstantValue.sendFileMsgMap[msgId] = message
                        sendFileMsgTimeMap[msgId] =  System.currentTimeMillis() .toString()
                        sendMsgLocalMap[msgId] = false
                        sendFilePathMap[msgId] = files_dir
                        deleteFileMap[msgId] = false
                        sendFileFriendKeyMap[msgId] = friendSignPublicKey
                        var fileKey = RxEncryptTool.generateAESKey()
                        if(porperty != null && porperty.equals("1"))
                        {
                            fileKey = LibsodiumUtil.DecryptShareKey(friendSignPublicKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                        }
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {

                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileMyKeyByteMap[msgId] = SrcKey
                            sendFileFriendKeyByteMap[msgId] = DstKey
                            sendFilePorpertyMap.put(msgId, porperty)
                        } catch (e: Exception) {
                            var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                            if(messageEntityList != null)
                            {
                                messageEntityList.forEach {
                                    if (it.msgId.equals(msgId)) {
                                        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                        KLog.i("消息数据删除")
                                    }
                                }
                            }
                            var  toSendMessage = toSendChatFileMessage
                            if(toSendMessage != null)
                            {
                                for (item in toSendMessage)
                                {
                                    if(item.msgId.equals(msgId))
                                    {
                                        toSendMessage.remove(item)
                                        break
                                    }
                                }
                            }
                            EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 0))
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))

                    }
                   /* val gson = Gson()
                    val Message = Message()
                    Message.msgType = 4
                    Message.fileName = videoFileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId

                    Message.timeStamp = System.currentTimeMillis() / 1000
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)*/

                } else {
                    //Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

            }
        }).start()


    }


    fun sendFileMessage(userId: String, friendId: String, filePath: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String,porperty:String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+filePath)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(filePath)
                val isHas = file.exists()
                if (isHas) {
                    if (file.length() > 1024 * 1024 * 100) {
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 2))
                    }
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)

                    val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                    val message = EMMessage.createFileSendMessage(filePath, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {

                        message.msgId = msgId
                        ConstantValue.sendFileMsgMap[msgId] = message
                        sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                        sendMsgLocalMap[msgId] = false
                        sendFilePathMap[msgId] = files_dir
                        deleteFileMap[msgId] = false
                        sendFileFriendKeyMap[msgId] = friendSignPublicKey

                        var fileKey = RxEncryptTool.generateAESKey()
                        if(porperty != null && porperty.equals("1"))
                        {
                            fileKey = LibsodiumUtil.DecryptShareKey(friendSignPublicKey,ConstantValue.libsodiumpublicMiKey!!,ConstantValue.libsodiumprivateMiKey!!)
                        }
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {

                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileMyKeyByteMap[msgId] = SrcKey
                            sendFileFriendKeyByteMap[msgId] = DstKey
                            sendFilePorpertyMap.put(msgId, porperty)
                        } catch (e: Exception) {
                            var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                            if(messageEntityList != null)
                            {
                                messageEntityList.forEach {
                                    if (it.msgId.equals(msgId)) {
                                        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                        KLog.i("消息数据删除")
                                    }
                                }
                            }
                            var  toSendMessage = toSendChatFileMessage
                            if(toSendMessage != null)
                            {
                                for (item in toSendMessage)
                                {
                                    if(item.msgId.equals(msgId))
                                    {
                                        toSendMessage.remove(item)
                                        break
                                    }
                                }
                            }
                            EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 0))
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))
                    }
                    //FileUtil.copySdcardFile(filePath, files_dir)
                   /* val gson = Gson()
                    val Message = Message()
                    Message.msgType = 5
                    Message.fileName = fileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId

                    Message.timeStamp = System.currentTimeMillis() / 1000
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)*/

                } else {
                    //Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {

            }
        }).start()
    }
    fun sendEmailFileMessage(userId: String, friendId: String, filePath: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String,porperty:String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+filePath)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(filePath)
                val isHas = file.exists()
                if (isHas) {
                    if (file.length() > 1024 * 1024 * 100) {
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 2))
                    }
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)

                    val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                    val message = EMMessage.createFileSendMessage(filePath, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {

                        message.msgId = msgId
                        ConstantValue.sendFileMsgMap[msgId] = message
                        sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                        sendMsgLocalMap[msgId] = false
                        sendFilePathMap[msgId] = files_dir
                        deleteFileMap[msgId] = false
                        sendFileFriendKeyMap[msgId] = friendSignPublicKey

                        var fileKey = RxEncryptTool.generateAESKey()
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {

                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            //sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileMyKeyByteMap[msgId] = SrcKey
                            sendFileFriendKeyByteMap[msgId] = DstKey
                            sendFilePorpertyMap.put(msgId, porperty)
                        } catch (e: Exception) {
                            var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                            if(messageEntityList != null)
                            {
                                messageEntityList.forEach {
                                    if (it.msgId.equals(msgId)) {
                                        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                        KLog.i("消息数据删除")
                                    }
                                }
                            }
                            var  toSendMessage = toSendChatFileMessage
                            if(toSendMessage != null)
                            {
                                for (item in toSendMessage)
                                {
                                    if(item.msgId.equals(msgId))
                                    {
                                        toSendMessage.remove(item)
                                        break
                                    }
                                }
                            }
                            EventBus.getDefault().post(FileTransformStatus(msgId!!,"",friendId, 0))
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentRouterIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))
                    }
                    //FileUtil.copySdcardFile(filePath, files_dir)
                    /* val gson = Gson()
                     val Message = Message()
                     Message.msgType = 5
                     Message.fileName = fileName
                     Message.msg = ""
                     Message.from = userId
                     Message.to = friendId

                     Message.timeStamp = System.currentTimeMillis() / 1000
                     Message.unReadCount = 0
                     val baseDataJson = gson.toJson(Message)
                     SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)*/

                } else {
                    //Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {

            }
        }).start()
    }
}