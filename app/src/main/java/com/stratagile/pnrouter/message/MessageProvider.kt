package com.stratagile.pnrouter.message

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.JPushMsgRsp
import com.stratagile.pnrouter.utils.SpUtil

class MessageProvider {
    var userMessageList : HashMap<String, ArrayList<Message>>
    var selfUserId : String
    companion object {
        private var instance: MessageProvider? = null
        fun getInstance() : MessageProvider{
            if (instance == null) {
                synchronized(MessageProvider::class.java) {
                    if (instance == null) {
                        instance = MessageProvider()
                    }
                }
            }
            return instance!!
        }
    }

    init {
        selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        userMessageList = HashMap()
    }

    fun getMessageListbyUserId(userId : String) {
        var messageList = userMessageList.get(userId)

    }

    fun addMessage(message : Message) {
        if (message.from.equals(selfUserId)) {
            userMessageList.get(message.to)?.add(message)
        } else {
            userMessageList.get(message.from)?.add(message)
        }
    }

    fun addMessage(userId : String, messageList : MutableList<Message>) {
        userMessageList.get(userId)?.addAll(messageList)
        var list = userMessageList.get(userId)
        userMessageList.remove(userId)
        userMessageList.put(userId, list!!)
    }


    fun addMessage(messageList : MutableList<Message>) {
        if (messageList.get(0).from.equals(selfUserId)) {
            userMessageList.get(messageList.get(0).to)?.addAll(messageList)
        } else {
            userMessageList.get(messageList.get(0).from)?.addAll(messageList)
        }
    }


    fun getConversationList() : HashMap<String, Message>{
        var lastMessageMap = HashMap<String, Message>()
        userMessageList.forEach {
            lastMessageMap.put(it.key, it.value.get(0))
        }
        return lastMessageMap
    }

}