package com.stratagile.tox.toxcallback

interface ToxCoreEventListener {
    fun toxSelfConnectionStatus(toxConnectStatus: ToxConnection)

    fun toxFreindConnectionStatus(friendId : String, toxConnection: ToxConnection)

    fun toxOnMessage(friendId: String, message : String)
}