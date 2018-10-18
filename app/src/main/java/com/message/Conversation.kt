package com.message

import com.stratagile.pnrouter.db.UserEntity

class Conversation {
    var lastMessageTime : Long = 0
    var userId = ""
    var lastMessage : Message? = null
    var userEntity : UserEntity? = null
    /**
     * 是否置顶
     */
    var isTop = false
    var unReadCount = 0

    override fun toString(): String {
        return "Conversation(lastMessageTime=$lastMessageTime, userId='$userId', lastMessage=$lastMessage, userEntity=$userEntity, isTop=$isTop)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Conversation

        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }
}