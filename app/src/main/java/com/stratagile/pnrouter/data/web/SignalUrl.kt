package com.stratagile.pnrouter.data.web

import okhttp3.ConnectionSpec

open class SignalUrl(val url: String, hostHeader: String?,
                val trustStore: TrustStore,
                connectionSpec: ConnectionSpec?) {
    val hostHeader: Optional<String>
    private val connectionSpec: Optional<ConnectionSpec>

    val connectionSpecs: Optional<List<ConnectionSpec>>
        get() =
            if (connectionSpec.isPresent) Optional.of(listOf(connectionSpec.get())) else Optional.absent()

    constructor(url: String, trustStore: TrustStore) : this(url, null, trustStore, null) {}

    init {
        this.hostHeader = Optional.fromNullable(hostHeader)
        this.connectionSpec = Optional.fromNullable(connectionSpec)
    }

}
