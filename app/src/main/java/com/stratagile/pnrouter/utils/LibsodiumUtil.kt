package com.stratagile.pnrouter.utils

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
        var remote_public_key = base64Decode(remote_public_keyStr)
        var local_private_key= base64Decode(local_private_keyStr)
        var dst_shared_key  = ByteArray(32)
        var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,remote_public_key,local_private_key)
        return base64Encode2String(dst_shared_key)
    }
    /**
     * 加密，参数都是ByteArray
     */
    fun encrypt_data_symmetric(src_msg:ByteArray, src_nonce:ByteArray, src_key:ByteArray):ByteArray
    {
        val temp_plain = ByteArray(src_msg.size+ Sodium.crypto_box_zerobytes())
        val temp_encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes()+ Sodium.crypto_box_boxzerobytes())
        var temp_plainInit = ByteArray(Sodium.crypto_box_zerobytes(),{0})
        //Arrays.fill(temp_plainInit,0)
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
        var ddd = base64Encode2String(src_msgStr.toByteArray())
//        var src_msg = base64Encode2String(src_msgStr.toByteArray()).toByteArray()
        var src_msg = src_msgStr.toByteArray()
        var src_nonce = base64Decode(src_nonceStr)
        var src_key = base64Decode(src_keyStr)
        val temp_plain = ByteArray(src_msg.size+ Sodium.crypto_box_zerobytes())
        val temp_encrypted = ByteArray(src_msg.size + Sodium.crypto_box_macbytes()+ Sodium.crypto_box_boxzerobytes())
        var temp_plainInit = ByteArray(Sodium.crypto_box_zerobytes(),{0})
        //Arrays.fill(temp_plainInit,0)
        System.arraycopy(temp_plainInit, 0, temp_plain, 0, Sodium.crypto_box_zerobytes())
        System.arraycopy(src_msg, 0, temp_plain, Sodium.crypto_box_zerobytes(), src_msg.size)

        var crypto_box_afternm_result = Sodium.crypto_box_afternm(temp_encrypted,temp_plain,src_msg.size+ Sodium.crypto_box_zerobytes(),src_nonce,src_key)

        if(crypto_box_afternm_result == 0)
        {
            var encrypted = ByteArray(src_msg.size+ Sodium.crypto_box_macbytes())
            System.arraycopy(temp_encrypted, Sodium.crypto_box_boxzerobytes(), encrypted,0 , src_msg.size+ Sodium.crypto_box_macbytes())
            return base64Encode2String(encrypted)
        }else
        {
            return ""
        }
    }
    /**
     *解密普通消息，参数都是ByteArray
     */
    fun decrypt_data_symmetric(encrypted:ByteArray, src_nonce:ByteArray, src_key:ByteArray):String
    {
        var temp_plainafter = ByteArray(encrypted.size+Sodium.crypto_box_zerobytes())
        val temp_encryptedAfter = ByteArray(encrypted.size+Sodium.crypto_box_boxzerobytes())
        var temp_plainInitAfter = ByteArray(Sodium.crypto_box_boxzerobytes(),{0})
        //Arrays.fill(temp_plainInitAfter,0)
        System.arraycopy(temp_plainInitAfter, 0, temp_encryptedAfter, 0, Sodium.crypto_box_boxzerobytes())
        System.arraycopy(encrypted, 0, temp_encryptedAfter, Sodium.crypto_box_boxzerobytes(), encrypted.size)

        var crypto_box_open_afternm_result = Sodium.crypto_box_open_afternm(temp_plainafter,temp_encryptedAfter,encrypted.size+Sodium.crypto_box_boxzerobytes(),src_nonce,src_key)

        if(crypto_box_open_afternm_result == 0)
        {
            var plain = ByteArray(encrypted.size - Sodium.crypto_box_macbytes())
            System.arraycopy(temp_plainafter, Sodium.crypto_box_zerobytes(), plain,0 , encrypted.size- Sodium.crypto_box_macbytes())

            var souceStr  = String(plain)
            return souceStr
        }else{
            return ""
        }
    }
    /**
     *解密，参数都是ByteArray
     */
    fun decrypt_Filedata_symmetric(encrypted:ByteArray, src_nonce:ByteArray, src_key:ByteArray):ByteArray
    {
        var temp_plainafter = ByteArray(encrypted.size+Sodium.crypto_box_zerobytes())
        val temp_encryptedAfter = ByteArray(encrypted.size+Sodium.crypto_box_boxzerobytes())
        var temp_plainInitAfter = ByteArray(Sodium.crypto_box_boxzerobytes(),{0})
        //Arrays.fill(temp_plainInitAfter,0)
        System.arraycopy(temp_plainInitAfter, 0, temp_encryptedAfter, 0, Sodium.crypto_box_boxzerobytes())
        System.arraycopy(encrypted, 0, temp_encryptedAfter, Sodium.crypto_box_boxzerobytes(), encrypted.size)

        var crypto_box_open_afternm_result = Sodium.crypto_box_open_afternm(temp_plainafter,temp_encryptedAfter,encrypted.size+Sodium.crypto_box_boxzerobytes(),src_nonce,src_key)

        if(crypto_box_open_afternm_result == 0)
        {
            var plain = ByteArray(encrypted.size - Sodium.crypto_box_macbytes())
            System.arraycopy(temp_plainafter, Sodium.crypto_box_zerobytes(), plain,0 , encrypted.size- Sodium.crypto_box_macbytes())
            return plain
        }else{
            return ByteArray(0)
        }
    }
    /**
     *解密，参数都是String
     */
    fun decrypt_data_symmetric_string(encryptedStr:String, src_nonceStr:String, src_keyStr:String):String
    {
        var encrypted =  base64Decode(encryptedStr)
        var src_nonce = base64Decode(src_nonceStr)
        var src_key = base64Decode(src_keyStr)
        var temp_plainafter = ByteArray(encrypted.size+Sodium.crypto_box_zerobytes())
        val temp_encryptedAfter = ByteArray(encrypted.size+Sodium.crypto_box_boxzerobytes())
        var temp_plainInitAfter = ByteArray(Sodium.crypto_box_boxzerobytes(),{0})
        //Arrays.fill(temp_plainInitAfter,0)
        System.arraycopy(temp_plainInitAfter, 0, temp_encryptedAfter, 0, Sodium.crypto_box_boxzerobytes())
        System.arraycopy(encrypted, 0, temp_encryptedAfter, Sodium.crypto_box_boxzerobytes(), encrypted.size)

        var crypto_box_open_afternm_result = Sodium.crypto_box_open_afternm(temp_plainafter,temp_encryptedAfter,encrypted.size+Sodium.crypto_box_boxzerobytes(),src_nonce,src_key)

        if(crypto_box_open_afternm_result == 0)
        {
            var plain = ByteArray(encrypted.size - Sodium.crypto_box_macbytes())
            System.arraycopy(temp_plainafter, Sodium.crypto_box_zerobytes(), plain,0 , encrypted.size- Sodium.crypto_box_macbytes())

//            var souceStr  = String( base64Decode(plain))
            var souceStr  = String(plain)
            return souceStr
        }else{
            return ""
        }

    }
    fun cryptoSign(souceStr:String,libsodiumprivateSignKey:String):String
    {
        var signBase64 = ""
        try {
            if(souceStr == null || souceStr.equals(""))
            {
                return ""
            }
            var souceStrByte = souceStr.toByteArray()
            var mySignPrivate  = base64Decode(libsodiumprivateSignKey)
            var dst_signed_msg = ByteArray(souceStrByte.size +64)
            var signed_msg_len = IntArray(1)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,souceStrByte,souceStrByte.size,mySignPrivate)
            signBase64 = base64Encode2String(dst_signed_msg)//自己固定签名私钥->签名souceStr->转base64
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        finally {
            return signBase64;
        }

    }
    /**
     * 得到最后发送消息数据包
     */
    fun EncryptSendMsg(Msg:String,friendMiPublic:ByteArray,libsodiumprivateSignKey:String,libsodiumprivateTemKey:String,libsodiumpublicTemKey:String,libsodiumpublicMiKey:String):HashMap<String, String>
    {
        var hashMap = HashMap<String, String>()
        try {
            var mySignPrivate  = base64Decode(libsodiumprivateSignKey)
            var myTempPrivate = base64Decode(libsodiumprivateTemKey)
            var myTempPublic = base64Decode(libsodiumpublicTemKey)
            val random = org.libsodium.jni.crypto.Random()
            var NonceBase64 =  base64Encode2String(random.randomBytes(24))
            //开始加密
            var dst_shared_key  = ByteArray(32)
            var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,friendMiPublic,myTempPrivate) //自己临时私钥和好友加解密公钥->生成对称密钥
            var shared_keyBase64 =  base64Encode2String(dst_shared_key)
            var encryptedBase64 = LibsodiumUtil.encrypt_data_symmetric_string(Msg,NonceBase64,shared_keyBase64)//消息原文用对称密码加密后转base64

            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(encryptedBase64, NonceBase64, shared_keyBase64)

            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,myTempPublic,myTempPublic.size,mySignPrivate)
            var signBase64 = base64Encode2String(dst_signed_msg)//自己固定签名私钥->签名自己临时公钥->转base64
            var dst_shared_key_Mi_My = ByteArray(32 + 48)
            var crypto_box_seal= Sodium.crypto_box_seal(dst_shared_key_Mi_My,dst_shared_key,dst_shared_key.size,base64Decode(libsodiumpublicMiKey))
            var dst_shared_key_Mi_My64 =  base64Encode2String(dst_shared_key_Mi_My) //非对称加密方式crypto_box_seal用自己的加密公钥加密对称密钥
            hashMap.put("encryptedBase64",encryptedBase64)
            hashMap.put("signBase64",signBase64)
            hashMap.put("NonceBase64",NonceBase64)
            hashMap.put("dst_shared_key_Mi_My64",dst_shared_key_Mi_My64)
            return hashMap
        }catch (e:Exception)
        {
            return hashMap
        }

    }

    /**
     * 用aeskey进行加密，方便网络传输不被破解，对方收到后需要解密才能拿到秘钥
     */
    fun EncryptShareKey(shareKey:String,pulicMiKey:String):ByteArray
    {
        var dst_shared_key= shareKey.toByteArray()
        var dst_shared_key_Mi_My = ByteArray(32 + 48)
        var crypto_box_seal= Sodium.crypto_box_seal(dst_shared_key_Mi_My,dst_shared_key,dst_shared_key.size,base64Decode(pulicMiKey))
        return dst_shared_key_Mi_My
    }

    /**
     * 解密获取加密秘钥aeskey
     */
    fun DecryptShareKey(shareMiKey:String,libsodiumpublicMiKey:String,libsodiumprivateMiKey:String):String
    {
        var dst_shared_key_Mi_My = base64Decode(shareMiKey)
        var dst_shared_key_Soucre_My = ByteArray(32)
        var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,base64Decode(libsodiumpublicMiKey),base64Decode(libsodiumprivateMiKey))
        var shareKey16 =  ByteArray(16)
        System.arraycopy(dst_shared_key_Soucre_My, 0, shareKey16,0 , 16)
        var shareKey16Str = String(shareKey16)
        return shareKey16Str
    }
    fun DecryptShareKeyBySign(shareMiKey:String,libsodiumpublicSignKey:String,libsodiumprivateSignKey:String):String
    {
        var dst_shared_key_Mi_My = base64Decode(shareMiKey)
        var dst_shared_key_Soucre_My = ByteArray(32)
        var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,base64Decode(libsodiumpublicSignKey),base64Decode(libsodiumprivateSignKey))
        var shareKey16 =  ByteArray(16)
        System.arraycopy(dst_shared_key_Soucre_My, 0, shareKey16,0 , 16)
        var shareKey16Str = String(shareKey16)
        return shareKey16Str
    }
    /**
     * 得到最后发送文件数据
     */
    fun EncryptSendFile(fileData:ByteArray,shareKey:String,libsodiumprivateSignKey:String,libsodiumprivateTemKey:String,libsodiumpublicTemKey:String,fileNonce:String):ByteArray
    {
        var byteArray = ByteArray(0)
        try {
            var mySignPrivate  = base64Decode(libsodiumprivateSignKey)
            var myTempPrivate = base64Decode(libsodiumprivateTemKey)
            var myTempPublic = base64Decode(libsodiumpublicTemKey)

            var Nonce =  base64Decode(fileNonce)//固定不随机
            //开始加密
            var dst_shared_key  = shareKey.toByteArray()
            var encryptedFile = LibsodiumUtil.encrypt_data_symmetric(fileData,Nonce,dst_shared_key)//消息原文用对称密码加密
            return encryptedFile
        }catch (e:Exception)
        {
            return byteArray
        }

    }
    /**
     * 解密文件数据
     */
    fun DecryptFile(fileData:ByteArray,shareKey:ByteArray,fileNonce:String):ByteArray
    {
        var byteArray = ByteArray(0)
        try {
            var Nonce =  base64Decode(fileNonce)//固定不随机
            var encryptedFile = LibsodiumUtil.decrypt_Filedata_symmetric(fileData,Nonce,shareKey)//消息原文用对称密码加密
            return encryptedFile
        }catch (e:Exception)
        {
            return byteArray
        }

    }
    /**
     * 解密好友的消息
     */
    fun DecryptFriendMsg(msg:String,nonce:String,From:String,Sign:String,libsodiumprivateMiKey:String,friendSignPublicKey:String):String
    {
        try {
            val myMiPrivateBase64 = libsodiumprivateMiKey
            val dst_signed_msg = base64Decode(Sign)

            val dst_Friend_TempPublicKey = ByteArray(32)
            val msg_len = IntArray(1)
            val crypto_sign_open = Sodium.crypto_sign_open(dst_Friend_TempPublicKey, msg_len, dst_signed_msg, dst_signed_msg.size, base64Decode(friendSignPublicKey))

            val dst_share_key = ByteArray(32)
            val crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_share_key, dst_Friend_TempPublicKey, base64Decode(myMiPrivateBase64))

            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(msg, nonce, base64Encode2String(dst_share_key))
            return msgSouce
        }catch (e:Exception)
        {
            return ""

        }

    }

    /**
     * 解密自己的消息
     */
    fun DecryptMyMsg(msg:String,nonce:String,priKey:String,libsodiumpublicMiKey:String,libsodiumprivateMiKey:String):String
    {
        try {
            var dst_shared_key_Mi_My = base64Decode(priKey)
            //非对称解密方式crypto_box_seal_open解密出对称密钥
            var dst_shared_key_Soucre_My = ByteArray(32)
            var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,base64Decode(libsodiumpublicMiKey),base64Decode(libsodiumprivateMiKey))
            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(msg, nonce, base64Encode2String(dst_shared_key_Soucre_My))
            return msgSouce
        }catch (e:Exception)
        {
            return ""

        }

    }


    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    fun base64Encode2String(input: ByteArray): String {
        return Base64.getEncoder().encodeToString(input)
    }

    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    fun base64Decode(input: String?): ByteArray {
        if(input == null)
        {
            return Base64.getDecoder().decode("")
        }
        try {
            return Base64.getDecoder().decode(input)
        } catch (e: Exception) {
            return Base64.getDecoder().decode("")
        }

    }
}
