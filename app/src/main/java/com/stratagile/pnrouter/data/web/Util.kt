package com.stratagile.pnrouter.data.web

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*

object Util {

    fun join(vararg input: ByteArray): ByteArray {
        try {
            val baos = ByteArrayOutputStream()
            for (part in input) {
                baos.write(part)
            }

            return baos.toByteArray()
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    fun split(input: ByteArray, firstLength: Int, secondLength: Int): Array<ByteArray?> {
        val parts = arrayOfNulls<ByteArray>(2)

        parts[0] = ByteArray(firstLength)
        System.arraycopy(input, 0, parts[0], 0, firstLength)

        parts[1] = ByteArray(secondLength)
        System.arraycopy(input, firstLength, parts[1], 0, secondLength)

        return parts
    }

    fun trim(input: ByteArray, length: Int): ByteArray {
        val result = ByteArray(length)
        System.arraycopy(input, 0, result, 0, result.size)

        return result
    }

    fun isEmpty(value: String?): Boolean {
        return value == null || value.trim { it <= ' ' }.length == 0
    }

    fun getSecretBytes(size: Int): ByteArray {
        try {
            val secret = ByteArray(size)
            SecureRandom.getInstance("SHA1PRNG").nextBytes(secret)
            return secret
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        }

    }

    fun getRandomLengthBytes(maxSize: Int): ByteArray {
        val secureRandom = SecureRandom()
        val result = ByteArray(secureRandom.nextInt(maxSize) + 1)
        secureRandom.nextBytes(result)
        return result
    }

    @Throws(IOException::class)
    fun readFully(`in`: InputStream): String {
        val bout = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var read = -1

        while ((`in`.read(buffer)).also { read = it } != -1) {
            bout.write(buffer, 0, read)
        }

        `in`.close()

        return String(bout.toByteArray())
    }

    @Throws(IOException::class)
    fun readFully(`in`: InputStream, buffer: ByteArray) {
        var offset = 0

        while (true) {
            val read = `in`.read(buffer, offset, buffer.size - offset)

            if (read + offset < buffer.size)
                offset += read
            else
                return
        }
    }

    @Throws(IOException::class)
    fun copy(ins: InputStream, out: OutputStream) {
        val buffer = ByteArray(4096)
        var read = -1

        while (ins.read(buffer).also { read = it } != -1) {

            out.write(buffer, 0, read)
        }

        ins.close()
        out.close()
    }

    fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            throw AssertionError(e)
        }

    }

    fun wait(lock: Object, millis: Long) {
        try {
            lock.wait(millis)
        } catch (e: InterruptedException) {
            throw AssertionError(e)
        }

    }

    fun toIntExact(value: Long): Int {
        if (value.toInt().toLong() != value) {
            throw ArithmeticException("integer overflow")
        }
        return value.toInt()
    }

    fun <T> immutableList(vararg elements: T): List<T> {
        return Collections.unmodifiableList(Arrays.asList(*elements.clone()))
    }

}
