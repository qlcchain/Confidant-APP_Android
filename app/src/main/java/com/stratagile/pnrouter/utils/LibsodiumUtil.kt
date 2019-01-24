package com.stratagile.pnrouter.utils

import android.content.Context
import android.content.SharedPreferences
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
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
        var remote_public_key = RxEncodeTool.base64Decode(remote_public_keyStr)
        var local_private_key= RxEncodeTool.base64Decode(local_private_keyStr)
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
        var src_msg = RxEncodeTool.base64Encode2String(src_msgStr.toByteArray()).toByteArray()
        var src_nonce = RxEncodeTool.base64Decode(src_nonceStr)
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
            return RxEncodeTool.base64Encode2String(encrypted)
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

            var souceStr  = String(plain)
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
        var encrypted =  RxEncodeTool.base64Decode(encryptedStr)
        var src_nonce = RxEncodeTool.base64Decode(src_nonceStr)
        var src_key = RxEncodeTool.base64Decode(src_keyStr)
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

            var souceStr  = String( RxEncodeTool.base64Decode(plain))
            return souceStr
        }else{
            return ""
        }

    }

    /**
     * 得到最后发送消息数据包
     */
    fun EncryptSendMsg(Msg:String,friendMiPublic:ByteArray):HashMap<String, String>
    {
        var hashMap = HashMap<String, String>()
        try {
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var myTempPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateTemKey)
            var myTempPublic = RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicTemKey)
            val random = org.libsodium.jni.crypto.Random()
            var NonceBase64 =  RxEncodeTool.base64Encode2String(random.randomBytes(24))
            //开始加密
            var dst_shared_key  = ByteArray(32)
            var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,friendMiPublic,myTempPrivate) //自己临时私钥和好友加解密公钥->生成对称密钥
            var shared_keyBase64 =  RxEncodeTool.base64Encode2String(dst_shared_key)
            var encryptedBase64 = LibsodiumUtil.encrypt_data_symmetric_string(Msg,NonceBase64,shared_keyBase64)//消息原文用对称密码加密后转base64

            KLog.i("shared_keyBase64:"+shared_keyBase64)
            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(encryptedBase64, NonceBase64, shared_keyBase64)

            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,myTempPublic,myTempPublic.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)//自己固定签名私钥->签名自己临时公钥->转base64

            var dst_shared_key_Mi_My = ByteArray(32 + 48)
            var crypto_box_seal= Sodium.crypto_box_seal(dst_shared_key_Mi_My,dst_shared_key,dst_shared_key.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicMiKey))
            var dst_shared_key_Mi_My64 =  RxEncodeTool.base64Encode2String(dst_shared_key_Mi_My) //非对称加密方式crypto_box_seal用自己的加密公钥加密对称密钥
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
     * 解密好友的消息
     */
    fun DecryptFriendMsg(msg:String,nonce:String,From:String,Sign:String):String
    {
        try {
            val myMiPrivateBase64 = ConstantValue.libsodiumprivateMiKey
            var friendEntity = UserEntity()
            val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(From)).list()
            if (localFriendList.size > 0)
                friendEntity = localFriendList[0]

            val dst_signed_msg = RxEncodeTool.base64Decode(Sign)

            val dst_Friend_TempPublicKey = ByteArray(32)
            val msg_len = IntArray(1)
            val crypto_sign_open = Sodium.crypto_sign_open(dst_Friend_TempPublicKey, msg_len, dst_signed_msg, dst_signed_msg.size, RxEncodeTool.base64Decode(friendEntity.signPublicKey))

            val dst_share_key = ByteArray(32)
            val crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_share_key, dst_Friend_TempPublicKey, RxEncodeTool.base64Decode(myMiPrivateBase64))

            KLog.i("shared_keyBase64:_receive" + RxEncodeTool.base64Encode2String(dst_share_key))
            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(msg, nonce, RxEncodeTool.base64Encode2String(dst_share_key))
            return msgSouce
        }catch (e:Exception)
        {
            return ""

        }

    }

    /**
     * 解密自己的消息
     */
    fun DecryptMyMsg(msg:String,nonce:String,priKey:String):String
    {
        try {
            var dst_shared_key_Mi_My = RxEncodeTool.base64Decode(priKey)
            //非对称解密方式crypto_box_seal_open解密出对称密钥
            var dst_shared_key_Soucre_My = ByteArray(32)
            var crypto_box_seal_open = Sodium.crypto_box_seal_open(dst_shared_key_Soucre_My,dst_shared_key_Mi_My,dst_shared_key_Mi_My.size,RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicMiKey),RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateMiKey))
            KLog.i("shared_keyBase64:_receive" + RxEncodeTool.base64Encode2String(dst_shared_key_Soucre_My))
            val msgSouce = LibsodiumUtil.decrypt_data_symmetric_string(msg, nonce, RxEncodeTool.base64Encode2String(dst_shared_key_Soucre_My))
            return msgSouce
        }catch (e:Exception)
        {
            return ""

        }

    }

}
