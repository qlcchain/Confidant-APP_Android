package com.stratagile.pnrouter.data.web.uti

import java.io.IOException

object Hex {

    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    @JvmOverloads
    fun toString(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): String {
        val buf = StringBuffer()
        for (i in 0 until length) {
            appendHexChar(buf, bytes[offset + i].toInt())
            buf.append(", ")
        }
        return buf.toString()
    }

    fun toStringCondensed(bytes: ByteArray): String {
        val buf = StringBuffer()
        for (i in bytes.indices) {
            appendHexChar(buf, bytes[i].toInt())
        }
        return buf.toString()
    }

    @Throws(IOException::class)
    fun fromStringCondensed(encoded: String): ByteArray {
        val data = encoded.toCharArray()
        val len = data.size

        if (len and 0x01 != 0) {
            throw IOException("Odd number of characters.")
        }

        val out = ByteArray(len shr 1)

        var i = 0
        var j = 0
        while (j < len) {
            var f = Character.digit(data[j], 16) shl 4
            j++
            f = f or Character.digit(data[j], 16)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }

        return out
    }

    private fun appendHexChar(buf: StringBuffer, b: Int) {
        buf.append("(byte)0x")
        buf.append(HEX_DIGITS[b shr 4 and 0xf])
        buf.append(HEX_DIGITS[b and 0xf])
    }

}
