package com.stratagile.pnrouter.data.web

import javax.inject.Inject

class SignalServiceConfiguration @Inject constructor(val signalServiceUrls: Array<SignalServiceUrl>, val signalCdnUrls: Array<SignalCdnUrl>)
