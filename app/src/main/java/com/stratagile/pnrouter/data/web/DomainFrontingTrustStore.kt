package com.stratagile.pnrouter.data.web

import android.content.Context
import com.stratagile.pnrouter.R
import java.io.InputStream

class DomainFrontingTrustStore(context: Context) : TrustStore {

    private val context: Context

    override val keyStoreInputStream: InputStream
        get() = context.resources.openRawResource(R.raw.censorship_fronting)

    override val keyStorePassword: String
        get() = "whisper"

    init {
        this.context = context.applicationContext
    }

}
