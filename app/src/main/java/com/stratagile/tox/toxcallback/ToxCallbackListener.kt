package com.stratagile.tox.toxcallback

class ToxCallbackListener : ToxCoreEventListener {

    val onSelfConnectionStatusCallback = OnSelfConnectionStatusCallback()
    val onConnectionStatusCallback = OnConnectionStatusCallback()
    val onMessageCallback = OnMessageCallback()

    override fun toxSelfConnectionStatus(toxConnectStatus: ToxConnection) {
        onSelfConnectionStatusCallback.onSelfConnectionStatusCallback(toxConnectStatus)
    }

    override fun toxFreindConnectionStatus(friendId: String, toxConnection: ToxConnection) {
        onConnectionStatusCallback.onConnectionStatusCallback(friendId, toxConnection)
    }

    override fun toxOnMessage(friendId: String, message: String) {
        onMessageCallback.onMessageCallback(friendId, message)
    }
}