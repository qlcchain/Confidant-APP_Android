//package com.stratagile.pnrouter.data.web
//
//import android.util.Log
//import okio.ByteString
//import java.io.IOException
//import java.security.InvalidAlgorithmParameterException
//import java.security.InvalidKeyException
//import java.security.NoSuchAlgorithmException
//import java.util.*
//import javax.crypto.*
//import javax.crypto.spec.IvParameterSpec
//import javax.crypto.spec.SecretKeySpec
//
//class SignalServiceEnvelope {
//
//    private val envelope: Envelope
//
//    /**
//     * @return The envelope's sender.
//     */
//    val source: String
//        get() = envelope.getSource()
//
//    /**
//     * @return The envelope's sender device ID.
//     */
//    val sourceDevice: Int
//        get() = envelope.getSourceDevice()
//
//    /**
//     * @return The envelope's sender as a SignalServiceAddress.
//     */
//    val sourceAddress: SignalServiceAddress
//        get() = SignalServiceAddress(envelope.getSource(),
//                if (envelope.hasRelay())
//                    Optional.fromNullable(envelope.getRelay())
//                else
//                    Optional.absent<String>())
//
//    /**
//     * @return The envelope content type.
//     */
//    val type: Int
//        get() = envelope.getType().getNumber()
//
//    /**
//     * @return The federated server this envelope came from.
//     */
//    val relay: String
//        get() = envelope.getRelay()
//
//    /**
//     * @return The timestamp this envelope was sent.
//     */
//    val timestamp: Long
//        get() = envelope.getTimestamp()
//
//    /**
//     * @return The envelope's containing SignalService message.
//     */
//    val legacyMessage: ByteArray
//        get() = envelope.getLegacyMessage().toByteArray()
//
//    /**
//     * @return The envelope's encrypted SignalServiceContent.
//     */
//    val content: ByteArray
//        get() = envelope.getContent().toByteArray()
//
//    /**
//     * @return true if the containing message is a [org.whispersystems.libsignal.protocol.SignalMessage]
//     */
//    val isSignalMessage: Boolean
//        get() = envelope.getType().getNumber() === Envelope.Type.CIPHERTEXT_VALUE
//
//    /**
//     * @return true if the containing message is a [org.whispersystems.libsignal.protocol.PreKeySignalMessage]
//     */
//    val isPreKeySignalMessage: Boolean
//        get() = envelope.getType().getNumber() === Envelope.Type.PREKEY_BUNDLE_VALUE
//
//    /**
//     * @return true if the containing message is a delivery receipt.
//     */
//    val isReceipt: Boolean
//        get() = envelope.getType().getNumber() === Envelope.Type.RECEIPT_VALUE
//
//    /**
//     * Construct an envelope from a serialized, Base64 encoded SignalServiceEnvelope, encrypted
//     * with a signaling key.
//     *
//     * @param message The serialized SignalServiceEnvelope, base64 encoded and encrypted.
//     * @param signalingKey The signaling key.
//     * @throws IOException
//     * @throws InvalidVersionException
//     */
//    @Throws(IOException::class, InvalidVersionException::class)
//    constructor(message: String, signalingKey: String) : this(RxEncodeTool.base64Decode(message), signalingKey) {
//    }
//
//    /**
//     * Construct an envelope from a serialized SignalServiceEnvelope, encrypted with a signaling key.
//     *
//     * @param ciphertext The serialized and encrypted SignalServiceEnvelope.
//     * @param signalingKey The signaling key.
//     * @throws InvalidVersionException
//     * @throws IOException
//     */
//    @Throws(InvalidVersionException::class, IOException::class)
//    constructor(ciphertext: ByteArray, signalingKey: String) {
//        if (ciphertext.size < VERSION_LENGTH || ciphertext[VERSION_OFFSET].toInt() != SUPPORTED_VERSION)
//            throw InvalidVersionException("Unsupported version!")
//
//        val cipherKey = getCipherKey(signalingKey)
//        val macKey = getMacKey(signalingKey)
//
//        verifyMac(ciphertext, macKey)
//
//        this.envelope = Envelope.parseFrom(getPlaintext(ciphertext, cipherKey))
//    }
//
//    constructor(type: Int, source: String, sourceDevice: Int,
//                relay: String, timestamp: Long,
//                legacyMessage: ByteArray?, content: ByteArray?) {
//        val builder = Envelope.newBuilder()
//                .setType(Envelope.Type.valueOf(type))
//                .setSource(source)
//                .setSourceDevice(sourceDevice)
//                .setRelay(relay)
//                .setTimestamp(timestamp)
//
//        if (legacyMessage != null) builder.setLegacyMessage(ByteString.copyFrom(legacyMessage))
//        if (content != null) builder.setContent(ByteString.copyFrom(content))
//
//        this.envelope = builder.build()
//    }
//
//    /**
//     * @return Whether the envelope contains a SignalServiceDataMessage
//     */
//    fun hasLegacyMessage(): Boolean {
//        return envelope.hasLegacyMessage()
//    }
//
//    /**
//     * @return Whether the envelope contains an encrypted SignalServiceContent
//     */
//    fun hasContent(): Boolean {
//        return envelope.hasContent()
//    }
//
//    @Throws(IOException::class)
//    private fun getPlaintext(ciphertext: ByteArray, cipherKey: SecretKeySpec): ByteArray {
//        try {
//            val ivBytes = ByteArray(IV_LENGTH)
//            System.arraycopy(ciphertext, IV_OFFSET, ivBytes, 0, ivBytes.size)
//            val iv = IvParameterSpec(ivBytes)
//
//            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
//            cipher.init(Cipher.DECRYPT_MODE, cipherKey, iv)
//
//            return cipher.doFinal(ciphertext, CIPHERTEXT_OFFSET,
//                    ciphertext.size - VERSION_LENGTH - IV_LENGTH - MAC_SIZE)
//        } catch (e: NoSuchAlgorithmException) {
//            throw AssertionError(e)
//        } catch (e: NoSuchPaddingException) {
//            throw AssertionError(e)
//        } catch (e: InvalidKeyException) {
//            throw AssertionError(e)
//        } catch (e: InvalidAlgorithmParameterException) {
//            throw AssertionError(e)
//        } catch (e: IllegalBlockSizeException) {
//            throw AssertionError(e)
//        } catch (e: BadPaddingException) {
//            Log.w(TAG, e)
//            throw IOException("Bad padding?")
//        }
//
//    }
//
//    @Throws(IOException::class)
//    private fun verifyMac(ciphertext: ByteArray, macKey: SecretKeySpec) {
//        try {
//            val mac = Mac.getInstance("HmacSHA256")
//            mac.init(macKey)
//
//            if (ciphertext.size < MAC_SIZE + 1)
//                throw IOException("Invalid MAC!")
//
//            mac.update(ciphertext, 0, ciphertext.size - MAC_SIZE)
//
//            val ourMacFull = mac.doFinal()
//            val ourMacBytes = ByteArray(MAC_SIZE)
//            System.arraycopy(ourMacFull, 0, ourMacBytes, 0, ourMacBytes.size)
//
//            val theirMacBytes = ByteArray(MAC_SIZE)
//            System.arraycopy(ciphertext, ciphertext.size - MAC_SIZE, theirMacBytes, 0, theirMacBytes.size)
//
//            Log.w(TAG, "Our MAC: " + Hex.toString(ourMacBytes))
//            Log.w(TAG, "Thr MAC: " + Hex.toString(theirMacBytes))
//
//            if (!Arrays.equals(ourMacBytes, theirMacBytes)) {
//                throw IOException("Invalid MAC compare!")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            throw AssertionError(e)
//        } catch (e: InvalidKeyException) {
//            throw AssertionError(e)
//        }
//
//    }
//
//
//    @Throws(IOException::class)
//    private fun getCipherKey(signalingKey: String): SecretKeySpec {
//        val signalingKeyBytes = RxEncodeTool.base64Decode(signalingKey)
//        val cipherKey = ByteArray(CIPHER_KEY_SIZE)
//        System.arraycopy(signalingKeyBytes, 0, cipherKey, 0, cipherKey.size)
//
//        return SecretKeySpec(cipherKey, "AES")
//    }
//
//
//    @Throws(IOException::class)
//    private fun getMacKey(signalingKey: String): SecretKeySpec {
//        val signalingKeyBytes = RxEncodeTool.base64Decode(signalingKey)
//        val macKey = ByteArray(MAC_KEY_SIZE)
//        System.arraycopy(signalingKeyBytes, CIPHER_KEY_SIZE, macKey, 0, macKey.size)
//
//        return SecretKeySpec(macKey, "HmacSHA256")
//    }
//
//    companion object {
//
//        private val TAG = SignalServiceEnvelope::class.java.simpleName
//
//        private val SUPPORTED_VERSION = 1
//        private val CIPHER_KEY_SIZE = 32
//        private val MAC_KEY_SIZE = 20
//        private val MAC_SIZE = 10
//
//        private val VERSION_OFFSET = 0
//        private val VERSION_LENGTH = 1
//        private val IV_OFFSET = VERSION_OFFSET + VERSION_LENGTH
//        private val IV_LENGTH = 16
//        private val CIPHERTEXT_OFFSET = IV_OFFSET + IV_LENGTH
//    }
//
//}
