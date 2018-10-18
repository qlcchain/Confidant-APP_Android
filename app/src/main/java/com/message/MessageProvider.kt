package com.message

import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.utils.MutableListToArrayList
import com.stratagile.pnrouter.utils.SpUtil

class MessageProvider : PNRouterServiceMessageReceiver.CoversationCallBack {

    var unReadCount = 0
    var messageListenter : MessageListener? =null
    var receivedMessageListener : ReceivedMessageListener? = null
    var conversationChangeListener : ConversationChangeListener? = null
    override fun sendMsgRsp(sendMsgRsp: JSendMsgRsp) {
        KLog.i(sendMsgRsp)
//        var message = Message.createSendMessage(sendMsgRsp.params.msg, sendMsgRsp.params.fromId, sendMsgRsp.params.msgId, sendMsgRsp.params.toId)
        var messages = userMessageList.get(sendMsgRsp.params.toId)
        if (messages == null) {
            messages = arrayListOf()
            userMessageList.put(sendMsgRsp.params.toId, messages)
        }
        messages.forEachIndexed { index, message ->
            if (message.msg.equals(sendMsgRsp.params.msg)) {
                message.status = Message.Status.SUCCESS
                message.msgId = sendMsgRsp.params.msgId
                receivedMessageListener?.receivedMessage(index, message)
                return
            }
        }
//        messages.add(message)

    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        //服务器推送了别人的消息过来了。
        KLog.i("服务器推送了别人的消息过来了。")
        var messages = userMessageList.get(pushMsgRsp.params.fromId)
        if (messages == null) {
            messages = arrayListOf()
            userMessageList.put(pushMsgRsp.params.fromId, messages)
        }
        var message = Message.createReceivedMessage(pushMsgRsp.params.msg, pushMsgRsp.params.fromId, pushMsgRsp.params.msgId, pushMsgRsp.params.toId)
        messages.add(message)
        if (!hasConversation(pushMsgRsp.params.fromId)) {
            addConversation(pushMsgRsp.params.fromId, message)
        }
        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), 0, "")
        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
        calculateUnreadCount()
        receivedMessageListener?.receivedMessage()
        addConversation(pushMsgRsp.params.fromId, message)
    }

    /**
     * 拉消息的返回
     */
    override fun pullMsgRsp(pushMsgRsp: JPullMsgRsp) {
        var messageList: ArrayList<Message> = pushMsgRsp.params.payload.MutableListToArrayList()
        if (messageList.size != 0) {
            messageList.forEach {
                it.status = Message.Status.SUCCESS
                it.setType()
                it.isUnRead = false
            }
            if (messageList.get(0).from.equals(selfUserId)) {
                addConversation(messageList.get(0).to, messageList.last())
                if (userMessageList.get(messageList.get(0).to) != null) {
                    userMessageList.get(messageList.get(0).to)!!.addAll(0, messageList)
                } else {
                    userMessageList.put(messageList.get(0).to, messageList)
                }
            } else {
                addConversation(messageList.get(0).from, messageList.last())
                if (userMessageList.get(messageList.get(0).from) != null) {
                    userMessageList.get(messageList.get(0).from)!!.addAll(0, messageList)
                } else {
                    userMessageList.put(messageList.get(0).from, messageList)
                }
            }
            receivedMessageListener?.receivedMessage()
            calculateUnreadCount()
        }
    }

    fun hasConversation(userId: String) : Boolean{
        conversationList.forEach {
            if (it.userId.equals(userId)) {
                return true
            }
        }
        return false
    }

    /**
     * 添加消息到会话列表
     */
    fun addConversation(userId: String, message: Message) {
        conversationList.forEach {
            if (it.userId.equals(userId)) {
                it.lastMessageTime = message.timeStatmp
                it.lastMessage = message
                sortConversationList()
                conversationChangeListener?.conversationChange(conversationList)
                conversationList.forEach {
                    KLog.i(it.toString())
                }
                return
            }
        }
        var conversation = Conversation()
        conversation.userId = userId
        conversation.userEntity = UserProvider.getInstance().getUserById(userId)
        conversation.lastMessage = message
        conversation.lastMessageTime = message.timeStatmp
        conversationList.add(conversation)
        sortConversationList()
        conversationChangeListener?.conversationChange(conversationList)
        conversationList.forEach {
            KLog.i(it.toString())
        }
    }

    fun sortConversationList() : ArrayList<Conversation>{
        conversationList.sortByDescending{ it.lastMessageTime }
        conversationList.sortBy { it.isTop }
//        conversationList.forEach {
//            userMessageList.forEach { s, arrayList ->
//                if (s.equals(it.userId)) {
//                    var unReadCount = 0
//                    arrayList.forEach {
//                        if (it.isUnRead) {
//                            unReadCount++
//                        }
//                    }
//                    it.unReadCount = unReadCount
//                }
//            }
//        }
        return conversationList
    }

    override fun delMsgRsp(delMsgRsp: JDelMsgRsp) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 和用户聊天的所有消息
     */
    var userMessageList: HashMap<String, ArrayList<Message>>
    /**
     * 和用户聊天的会话列表
     */
    var conversationList: ArrayList<Conversation>

    /**
     * 自己的userId
     */
    var selfUserId: String

    companion object {
        private var instance: MessageProvider? = null
        fun getInstance(): MessageProvider {
            if (instance == null) {
                synchronized(MessageProvider::class.java) {
                    if (instance == null) {
                        instance = MessageProvider()
                    }
                }
            }
            return instance!!
        }

        fun init() {
            getInstance()
        }
    }

    init {
        selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        userMessageList = HashMap()
        conversationList = arrayListOf()
    }

    /**
     * 根据用户id来获取该用户的聊天列表
     */
    fun getMessageListbyUserId(userId: String): ArrayList<Message>? {
        var messageList = userMessageList.get(userId)
        if (messageList == null) {
            messageList = arrayListOf()
            userMessageList.put(userId, messageList)
            messageList = userMessageList.get(userId)

            val selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var pullMsgList = PullMsgReq(selfUserId!!, userId, 1, 0, 10)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(pullMsgList))
        }
        return messageList
    }

    /**
     * 根据userId清除某个用户的未读消息
     */
    fun clearUnreadCountByUserId(userId: String) {
        var messages = userMessageList.get(userId)
        messages?.forEach{
            it.isUnRead = false
        }
        calculateUnreadCount()
    }

    /**
     * 计算所有的未读消息数
     */
    fun calculateUnreadCount() {
        var allUnReadCount = 0
        conversationList.forEach {
            var conversationUnReadCount = 0
            userMessageList.forEach { s, arrayList ->
                if (it.userId.equals(s)) {
                    arrayList.forEach {
                        if (it.isUnRead) {
                            conversationUnReadCount++
                        }
                    }
                }
            }
            it.unReadCount = conversationUnReadCount
            allUnReadCount += conversationUnReadCount
        }
        unReadCount = allUnReadCount
        conversationChangeListener?.conversationChange(sortConversationList())
        messageListenter?.unReadCount(unReadCount)
    }

    fun deletConversationByDeleteFriend(userId: String) {
        userMessageList.remove(userId)
        conversationList.forEach {
            if (it.userId.equals(userId)) {
                conversationList.remove(it)
                return@forEach
            }
        }
    }

    interface MessageListener {
        fun unReadCount(unReadCOunt : Int)
    }

    interface ReceivedMessageListener {
        fun receivedMessage(index : Int, message: Message)
        fun receivedMessage()
    }

    interface ConversationChangeListener {
        fun conversationChange(arrayList: ArrayList<Conversation>)
    }
}