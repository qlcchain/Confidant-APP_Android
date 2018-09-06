package com.stratagile.pnrouter.data.web

import java.io.InputStream

interface TrustStore {
    val keyStoreInputStream: InputStream
    val keyStorePassword: String
}