package com.stratagile.pnrouter.data.web

import android.content.Context
import com.stratagile.pnrouter.R
import java.io.InputStream

class SignalServiceTrustStore(context: Context) : TrustStore {

    private val context: Context

   override val keyStoreInputStream: InputStream
        get() = context.resources.openRawResource(R.raw.whisper)

    override val keyStorePassword: String
        get() = "whisper"

    init {
        this.context = context.applicationContext
    }
}
