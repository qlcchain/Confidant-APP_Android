package com.stratagile.pnrouter.data.web

import android.app.admin.SecurityLog
import android.content.Context
import android.content.Intent

class SecurityEventListener(context: Context) : PNRouterServiceMessageSender.EventListener {

    private val context: Context

    init {
        this.context = context.applicationContext
    }

    override fun onSecurityEvent(textSecureAddress: SignalServiceAddress) {
        SecurityEvent.broadcastSecurityUpdateEvent(context)
    }

    companion object {

        private val TAG = SecurityEventListener::class.java.simpleName
    }

    class SecurityEvent {

        companion object {
            val SECURITY_UPDATE_EVENT = "org.thoughtcrime.securesms.KEY_EXCHANGE_UPDATE"
            fun broadcastSecurityUpdateEvent(context: Context) {
                val intent = Intent(SECURITY_UPDATE_EVENT)
                intent.setPackage(context.packageName)
                context.sendBroadcast(intent, "org.thoughtcrime.securesms.ACCESS_SECRETS")
            }
        }

    }
}
