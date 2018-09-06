package com.stratagile.pnrouter.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stratagile.pnrouter.data.service.MessageRetrievalService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val messageRetrievalService = Intent(context, MessageRetrievalService::class.java)
            context.startService(messageRetrievalService)
        }
    }
}
