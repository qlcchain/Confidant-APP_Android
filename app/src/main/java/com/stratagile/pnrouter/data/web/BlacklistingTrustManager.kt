package com.stratagile.pnrouter.data.web

import java.io.IOException
import java.math.BigInteger
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class BlacklistingTrustManager(private val trustManager: X509TrustManager) : X509TrustManager {

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        trustManager.checkClientTrusted(chain, authType)
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        trustManager.checkServerTrusted(chain, authType)

        for (certificate in chain) {
            for (blacklistedSerial in BLACKLIST) {
                if (certificate.issuerDN.name == blacklistedSerial.first && certificate.serialNumber == blacklistedSerial.second) {
                    throw CertificateException("Blacklisted Serial: " + certificate.serialNumber)
                }
            }
        }

    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return trustManager.acceptedIssuers
    }

    companion object {

        private val BLACKLIST = object : LinkedList<Pair<String, BigInteger>>() {
            init {
                add(Pair("Open Whisper Systems", BigInteger("4098")))
            }
        }

        fun createFor(trustManagers: Array<TrustManager>): Array<BlacklistingTrustManager?> {
            for (trustManager in trustManagers) {
                if (trustManager is X509TrustManager) {
                    val results = arrayOfNulls<BlacklistingTrustManager>(1)
                    results[0] = BlacklistingTrustManager(trustManager)
                    return results
                }
            }

            throw AssertionError("No X509 Trust Managers!")
        }

        fun createFor(trustStore: TrustStore): Array<BlacklistingTrustManager?> {
            try {
                val keyStoreInputStream = trustStore.keyStoreInputStream
                val keyStore = KeyStore.getInstance("BKS")

                keyStore.load(keyStoreInputStream, trustStore.keyStorePassword.toCharArray())

                val trustManagerFactory = TrustManagerFactory.getInstance("X509")
                trustManagerFactory.init(keyStore)

                return BlacklistingTrustManager.createFor(trustManagerFactory.trustManagers)
            } catch (e: KeyStoreException) {
                throw AssertionError(e)
            } catch (e: CertificateException) {
                throw AssertionError(e)
            } catch (e: IOException) {
                throw AssertionError(e)
            } catch (e: NoSuchAlgorithmException) {
                throw AssertionError(e)
            }

        }
    }
}
