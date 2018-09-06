package com.stratagile.pnrouter.data.web

import okhttp3.ConnectionSpec
import javax.inject.Inject

class SignalCdnUrl : SignalUrl {
    constructor(url: String, trustStore: TrustStore) : super(url, trustStore) {}

    constructor(url: String, hostHeader: String, trustStore: TrustStore, connectionSpec: ConnectionSpec) : super(url, hostHeader, trustStore, connectionSpec) {}
}