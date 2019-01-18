package com.stratagile.pnrouter.utils

import android.content.Context
import android.content.SharedPreferences
import org.libsodium.jni.Sodium
import java.util.*

object LibsodiumUtil {

    /**
     * 生成对称秘钥，参数都是ByteArray
     */
    fun crypto_box_beforenm(remote_public_key:ByteArray, local_private_key:ByteArray):ByteArray
    {
        var dst_shared_key  = ByteArray(32)
        var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,remote_public_key,local_private_key)
        return dst_shared_key
    }
    /**
     * 生成对称秘钥，参数都是ByteArray
     */
    fun crypto_box_beforenm_string(remote_public_keyStr:String, local_private_keyStr:String):String
    {
        var remote_public_key = StringUitl.toBytes(remote_public_keyStr)
        var local_private_key= StringUitl.toBytes(local_private_keyStr)
        var dst_shared_key  = ByteArray(32)
        var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,remote_public_key,local_private_key)
        return RxEncodeTool.base64Encode2String(dst_shared_key)
    }
    /**
     * 加密，参数都是ByteArray
     */
    fun encrypt_data_symmetric(src_msg:ByteArray, src_nonce:ByteArray, src_key:ByteArray):ByteArray
    {
        val temp_plain = ByteArray(src_msg.size+ Sodium.crypto_box_zerobytes())
        val temp_encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes()+ Sodium.crypto_box_boxzerobytes())
        var temp_plainInit = ByteArray(Sodium.crypto_box_zerobytes())
        Arrays.fill(temp_plainInit,0)
        System.arraycopy(temp_plainInit, 0, temp_plain, 0, Sodium.crypto_box_zerobytes())
        System.arraycopy(src_msg, 0, temp_plain, Sodium.crypto_box_zerobytes(), src_msg.size)

        var crypto_box_afternm_result = Sodium.crypto_box_afternm(temp_encrypted,temp_plain,src_msg.size+ Sodium.crypto_box_zerobytes(),src_nonce,src_key)

        if(crypto_box_afternm_result == 0)
        {
            var encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes())
            System.arraycopy(temp_encrypted, Sodium.crypto_box_boxzerobytes(), encrypted,0 , src_msg.size+ Sodium.crypto_box_macbytes())
            return encrypted
        }else
        {
            return ByteArray(0)
        }
    }

    /**
     * 加密，参数都是String
     */
    fun encrypt_data_symmetric_string(src_msgStr:String, src_nonceStr:String, src_keyStr:String):String
    {
        var src_msg = src_msgStr.toByteArray()
        var src_nonce = src_nonceStr.toByteArray()
        var src_key = RxEncodeTool.base64Decode(src_keyStr)
        val temp_plain = ByteArray(src_msg.size+ Sodium.crypto_box_zerobytes())
        val temp_encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes()+ Sodium.crypto_box_boxzerobytes())
        var temp_plainInit = ByteArray(Sodium.crypto_box_zerobytes())
        Arrays.fill(temp_plainInit,0)
        System.arraycopy(temp_plainInit, 0, temp_plain, 0, Sodium.crypto_box_zerobytes())
        System.arraycopy(src_msg, 0, temp_plain, Sodium.crypto_box_zerobytes(), src_msg.size)

        var crypto_box_afternm_result = Sodium.crypto_box_afternm(temp_encrypted,temp_plain,src_msg.size+ Sodium.crypto_box_zerobytes(),src_nonce,src_key)

        if(crypto_box_afternm_result == 0)
        {
            var encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes())
            System.arraycopy(temp_encrypted, Sodium.crypto_box_boxzerobytes(), encrypted,0 , src_msg.size+ Sodium.crypto_box_macbytes())
            return StringUitl.bytesToString(encrypted)
        }else
        {
            return ""
        }
    }
    /**
     *解密，参数都是ByteArray
     */
    fun decrypt_data_symmetric(encrypted:ByteArray, src_nonce:ByteArray, src_key:ByteArray):String
    {
        var temp_plainafter = ByteArray(encrypted.size+Sodium.crypto_box_zerobytes())
        val temp_encryptedAfter = ByteArray(encrypted.size+Sodium.crypto_box_boxzerobytes())
        var temp_plainInitAfter = ByteArray(Sodium.crypto_box_boxzerobytes())
        Arrays.fill(temp_plainInitAfter,0)
        System.arraycopy(temp_plainInitAfter, 0, temp_encryptedAfter, 0, Sodium.crypto_box_boxzerobytes())
        System.arraycopy(encrypted, 0, temp_encryptedAfter, Sodium.crypto_box_boxzerobytes(), encrypted.size)

        var crypto_box_open_afternm_result = Sodium.crypto_box_open_afternm(temp_plainafter,temp_encryptedAfter,encrypted.size+Sodium.crypto_box_boxzerobytes(),src_nonce,src_key)

        if(crypto_box_open_afternm_result == 0)
        {
            var plain = ByteArray(encrypted.size - Sodium.crypto_box_macbytes())
            System.arraycopy(temp_plainafter, Sodium.crypto_box_zerobytes(), plain,0 , encrypted.size- Sodium.crypto_box_macbytes())

            var souceStr  = String(Base58.decode(String(plain)))
            return souceStr
        }else{
            return ""
        }
    }
    /**
     *解密，参数都是String
     */
    fun decrypt_data_symmetric_string(encryptedStr:String, src_nonceStr:String, src_keyStr:String):String
    {
        var encrypted =  StringUitl.toBytes(encryptedStr)
        var src_nonce = src_nonceStr.toByteArray()
        var src_key = StringUitl.toBytes(src_keyStr)
        var temp_plainafter = ByteArray(encrypted.size+Sodium.crypto_box_zerobytes())
        val temp_encryptedAfter = ByteArray(encrypted.size+Sodium.crypto_box_boxzerobytes())
        var temp_plainInitAfter = ByteArray(Sodium.crypto_box_boxzerobytes())
        Arrays.fill(temp_plainInitAfter,0)
        System.arraycopy(temp_plainInitAfter, 0, temp_encryptedAfter, 0, Sodium.crypto_box_boxzerobytes())
        System.arraycopy(encrypted, 0, temp_encryptedAfter, Sodium.crypto_box_boxzerobytes(), encrypted.size)

        var crypto_box_open_afternm_result = Sodium.crypto_box_open_afternm(temp_plainafter,temp_encryptedAfter,encrypted.size+Sodium.crypto_box_boxzerobytes(),src_nonce,src_key)

        if(crypto_box_open_afternm_result == 0)
        {
            var plain = ByteArray(encrypted.size - Sodium.crypto_box_macbytes())
            System.arraycopy(temp_plainafter, Sodium.crypto_box_zerobytes(), plain,0 , encrypted.size- Sodium.crypto_box_macbytes())

            var souceStr  = String(Base58.decode(String(plain)))
            return souceStr
        }else{
            return ""
        }

    }
}
