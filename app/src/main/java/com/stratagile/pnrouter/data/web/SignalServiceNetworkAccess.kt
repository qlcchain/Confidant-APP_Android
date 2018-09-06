package com.stratagile.pnrouter.data.web

import android.content.Context
import com.stratagile.pnrouter.BuildConfig
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import java.util.HashMap
import javax.inject.Inject

class SignalServiceNetworkAccess @Inject constructor(context: Context) {

    private val censorshipConfiguration: Map<String, SignalServiceConfiguration>
    private val censoredCountries: Array<String>
    private val uncensoredConfiguration: SignalServiceConfiguration

    init {
        val trustStore = DomainFrontingTrustStore(context)
        val service = SignalServiceUrl("https://cms.souqcdn.com", SERVICE_REFLECTOR_HOST, trustStore, SOUQ_CONNECTION_SPEC)
        val serviceCdn = SignalCdnUrl("https://cms.souqcdn.com", SERVICE_REFLECTOR_HOST, trustStore, SOUQ_CONNECTION_SPEC)
        val serviceConfig = SignalServiceConfiguration(arrayOf(service),
                arrayOf(serviceCdn))

        this.censorshipConfiguration = object : HashMap<String, SignalServiceConfiguration>() {
            init {
                put(COUNTRY_CODE_EGYPT, serviceConfig)
                put(COUNTRY_CODE_UAE, serviceConfig)
                put(COUNTRY_CODE_OMAN, serviceConfig)
                put(COUNTRY_CODE_QATAR, serviceConfig)
            }
        }

        this.uncensoredConfiguration = SignalServiceConfiguration(arrayOf(SignalServiceUrl(BuildConfig.SIGNAL_URL, SignalServiceTrustStore(context))),
                arrayOf(SignalCdnUrl(BuildConfig.SIGNAL_CDN_URL, SignalServiceTrustStore(context))))
        this.uncensoredConfiguration.signalServiceUrls[0].trustStore.keyStoreInputStream
        this.censoredCountries = this.censorshipConfiguration.keys.toTypedArray()
    }

    fun getConfiguration(context: Context): SignalServiceConfiguration {
        val localNumber = TextSecurePreferences.getLocalNumber(context)
        return getConfiguration(localNumber)
    }

    fun getConfiguration(localNumber: String?): SignalServiceConfiguration {
        if (localNumber == null) return this.uncensoredConfiguration

        for (censoredRegion in this.censoredCountries) {
            if (localNumber.startsWith(censoredRegion)) {
                return this.censorshipConfiguration[censoredRegion]!!
            }
        }

        return this.uncensoredConfiguration
    }

    fun isCensored(context: Context): Boolean {
        return getConfiguration(context) !== this.uncensoredConfiguration
    }

    fun isCensored(number: String): Boolean {
        return getConfiguration(number) !== this.uncensoredConfiguration
    }

    companion object {

        private val TAG = SignalServiceNetworkAccess::class.java.name

        private val COUNTRY_CODE_EGYPT = "+20"
        private val COUNTRY_CODE_UAE = "+971"
        private val COUNTRY_CODE_OMAN = "+968"
        private val COUNTRY_CODE_QATAR = "+974"

        private val SERVICE_REFLECTOR_HOST = "textsecure-service-reflected.whispersystems.org"

        open val SOUQ_CONNECTION_SPEC = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA)
                .supportsTlsExtensions(true)
                .build()
    }

}
