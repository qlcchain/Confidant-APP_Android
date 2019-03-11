package com.message

import android.os.Environment
import android.os.Handler
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.FriendEntityDao
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FriendAvatarChange
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.entity.events.UnReadContactCount
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import org.greenrobot.eventbus.EventBus
import org.libsodium.jni.Sodium
import java.io.File
import java.util.*

class UserProvider : PNRouterServiceMessageReceiver.UserControlleCallBack {
    override fun updateAvatarReq(jUpdateAvatarRsp: JUpdateAvatarRsp) {
        if(jUpdateAvatarRsp.params.retCode == 0)
        {

            var filePath = jUpdateAvatarRsp.params.fileName
            var fileBase58Name = filePath.substring(8,filePath.length)
            var fileName = String(Base58.decode(fileBase58Name));
            val filledUri = "https://" + ConstantValue.currentIp + ConstantValue.port + filePath
            fileName = fileName.replace("__Avatar","")
            var fileSavePath  = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/"
            if (ConstantValue.isWebsocketConnected) {
                var msgId = Calendar.getInstance().timeInMillis /1000
                FileDownloadUtils.doDownLoadWork(filledUri, fileSavePath, AppConfig.instance, msgId.toInt(), handlerDown, "")
            }else{
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var msgData = PullFileReq(jUpdateAvatarRsp.params.toId, selfUserId!!, fileBase58Name, 0, 4, 3)
                var baseData = BaseData(msgData)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

        }
    }
    internal var handlerDown: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                }
                0x55 -> {
                    //有头像更新
                    EventBus.getDefault().post(FriendAvatarChange())
                }
            }//goMain();
            //goMain();
        }
    }
    override fun changeRemarksRsp(jChangeRemarksRsp: JChangeRemarksRsp) {
        friendOperateListener?.changeRemarksRsp(jChangeRemarksRsp.params.retCode)
    }

    var friendOperateListener : FriendOperateListener? = null
    var addFriendDelListener : AddFriendDelListener? = null

    /**
     * APP添加新好友，发送好友请求，等待好友同意
     * 添加好友第一步的返回
     */
    override fun addFriendBack(addFriendRsp: JAddFreindRsp) {
        KLog.i("添加好友请求的返回")
        friendOperateListener?.addFriendRsp(addFriendRsp.params.retCode)
    }

    /**
     * 7.	好友请求处理
     * APP上显示有新的好友请求，用户可以选择是否允许对方添加自己好友
     * 第三步，我处理对方给我的好友请求
     */
    override fun addFriendDealRsp(jAddFriendDealRsp: JAddFriendDealRsp) {
        /*userList.forEach {
            if (it.userId.equals(jAddFriendDealRsp.params.friendId)) {
                if (jAddFriendDealRsp.params.result == 0) {
                    it.friendStatus = 0
                } else if (jAddFriendDealRsp.params.result == 1) {
                    it.friendStatus = 2
                }
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                return@forEach
            }
        }*/

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jAddFriendDealRsp.params.friendId.equals(j.friendId)) {
                    if (jAddFriendDealRsp.params.result == 0) {
                        j.friendLocalStatus = 0
                    } else if (jAddFriendDealRsp.params.result == 1) {
                        j.friendLocalStatus = 2
                    }
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    break
                }

            }
        }

        addFriendDelListener?.addFriendDealRsp(jAddFriendDealRsp.params.retCode)
        refreshFriend("")
    }

    /**
     * APP发起，用户删除目标好友
     *
     */
    override fun delFriendCmdRsp(jDelFriendCmdRsp: JDelFriendCmdRsp) {
        if (jDelFriendCmdRsp.params.retCode == 0) {
            /* userList.forEach {
                 if (it.userId.equals(jDelFriendCmdRsp.params.friendId)) {
                     it.friendStatus = 6
                     AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                     refreshFriend(jDelFriendCmdRsp.params.friendId)
                     return@forEach
                 }
             }*/

            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
            for (j in localFriendStatusList) {
                if (j.userId.equals(userId)) {
                    if (jDelFriendCmdRsp.params.friendId.equals(j.friendId)) {
                        j.friendLocalStatus = 6
                        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                        refreshFriend(jDelFriendCmdRsp.params.friendId)
                        break
                    }

                }
            }
            friendOperateListener?.delFriendRsp(jDelFriendCmdRsp.params.retCode)
        }
    }

    /**
     * 对方添加我为好友，推送给我，等待我同意
     * 添加好友第二步
     */
    override fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp) {
        KLog.i("推送过来了添加好友的请求")

        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var addFriendPushReq = AddFriendPushReq(0,userId!!, "")
        var sendAddFriendPushReq = BaseData(addFriendPushReq,jAddFriendPushRsp.msgid)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendAddFriendPushReq = BaseData(4,addFriendPushReq,jAddFriendPushRsp.msgid)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendAddFriendPushReq)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendAddFriendPushReq
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
        var filePath  = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
        var fileMD5 = FileUtil.getFileMD5(File(filePath))
        if(fileMD5 == null)
        {
            fileMD5 = ""
        }
        val updateAvatarReq = UpdateAvatarReq(userId!!, jAddFriendPushRsp.params.friendId, fileMD5)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, updateAvatarReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        //关系表，我和其他用户的关系
        var ifhasFriend = false
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jAddFriendPushRsp.params.friendId.equals(j.friendId))
                {
                    ifhasFriend = true
                    if(j.friendLocalStatus == 0)//自动同意
                    {
                        KLog.i("对方删除我，对方又加我为好友，自动同意为好友")
                        LogUtil.addLog("对方删除我，对方又加我为好友，自动同意为好友")
                        var itUserEntity = UserEntity()
                        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(j.friendId)).list()
                        if (localFriendList.size > 0)
                            itUserEntity = localFriendList.get(0)
                        if(itUserEntity.nickName == null)
                            break
                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var nickName = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, itUserEntity.nickName, selfUserId!!, itUserEntity.userId, ConstantValue.publicRAS!!, itUserEntity.signPublicKey,signBase64,0)
                        var sendData = BaseData(addFriendDealReq)
                        if(ConstantValue.encryptionType.equals("1"))
                        {
                            addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, itUserEntity.nickName, selfUserId!!, itUserEntity.userId, ConstantValue.libsodiumpublicSignKey!!, itUserEntity.signPublicKey,signBase64,0)
                            sendData = BaseData(4,addFriendDealReq)
                        }
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.messageSender!!.send(sendData)
                        }else if (ConstantValue.isToxConnected) {
                            var baseData = sendData
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            if (ConstantValue.isAntox) {
                                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            }
                        }
                        j.friendLocalStatus = 0
                        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                        userList.forEach {
                            if (it.userId.equals(jAddFriendPushRsp.params.friendId)) {
                                KLog.i("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                                LogUtil.addLog("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                                it.nickName = jAddFriendPushRsp.params.nickName;
                                it.signPublicKey = jAddFriendPushRsp.params.userKey
                                it.routeName = jAddFriendPushRsp.params.routerName
                                it.routeId = jAddFriendPushRsp.params.routerId
                                var dst_public_MiKey_Friend = ByteArray(32)
                                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
                                if(crypto_sign_ed25519_pk_to_curve25519_result == 0)
                                {
                                    it.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                                }
                                it.timestamp = jAddFriendPushRsp.timestamp
                                it.validationInfo = jAddFriendPushRsp.params.msg
                                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                it.routerUserId = selfUserId
                                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                                return@forEach

                                /*
                                  EventBus.getDefault().post(FriendChange())
                                  return@forEach*/
                            }
                        }
                        return
                    }else
                    {
                        KLog.i("以前是好友，后来我删除了，现在对方又想加我为好友")
                        LogUtil.addLog("以前是好友，后来我删除了，现在对方又想加我为好友")
                        j.friendLocalStatus = 3
                        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                        userList.forEach {
                            if (it.userId.equals(jAddFriendPushRsp.params.friendId)) {
                                KLog.i("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                                LogUtil.addLog("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                                it.nickName = jAddFriendPushRsp.params.nickName;
                                it.signPublicKey = jAddFriendPushRsp.params.userKey
                                it.routeName = jAddFriendPushRsp.params.routerName
                                it.routeId = jAddFriendPushRsp.params.routerId
                                var dst_public_MiKey_Friend = ByteArray(32)
                                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
                                if(crypto_sign_ed25519_pk_to_curve25519_result == 0)
                                {
                                    it.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                                }
                                it.timestamp = jAddFriendPushRsp.timestamp
                                it.validationInfo = jAddFriendPushRsp.params.msg
                                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                it.routerUserId = selfUserId
                                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                                return@forEach

                                /*
                                  EventBus.getDefault().post(FriendChange())
                                  return@forEach*/
                            }
                        }
                        EventBus.getDefault().post(FriendChange())
                        return
                    }

                }

            }
        }
        KLog.i("更新好友关系，关系为等待我处理好友请求")
        LogUtil.addLog("更新好友关系，关系为等待我处理好友请求")
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId),FriendEntityDao.Properties.FriendId.eq(jAddFriendPushRsp.params.friendId)).list()
        if (localFriendList.size > 0)
        {
            KLog.i("删除：" + localFriendList.size + "个好友关系")
            LogUtil.addLog("删除：" + localFriendList.size + "个好友关系")
            AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.deleteInTx(localFriendList)
        }
        KLog.i("清零，再生成一个")
        var newFriendStatus = FriendEntity()
        newFriendStatus.userId = userId
        newFriendStatus.friendId = jAddFriendPushRsp.params.friendId
        newFriendStatus.friendLocalStatus = 3
        newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
        LogUtil.addLog("新增好友关系：" + newFriendStatus.toString())
        var ifHas = false
        userList.forEach {
            if (it.userId.equals(jAddFriendPushRsp.params.friendId)) {
                KLog.i("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                LogUtil.addLog("好友以前存在，不做新增，只是更新用户信息，这里不更新好友状态")
                ifHas = true
                it.nickName = jAddFriendPushRsp.params.nickName;
                it.signPublicKey = jAddFriendPushRsp.params.userKey
                it.routeName = jAddFriendPushRsp.params.routerName
                it.routeId = jAddFriendPushRsp.params.routerId
                var dst_public_MiKey_Friend = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
                if(crypto_sign_ed25519_pk_to_curve25519_result == 0)
                {
                    it.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                }
                it.timestamp = jAddFriendPushRsp.timestamp
                it.validationInfo = jAddFriendPushRsp.params.msg
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                it.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                return@forEach

                /*
                  EventBus.getDefault().post(FriendChange())
                  return@forEach*/
            }
        }

        if(!ifHas)
        {
            KLog.i("以前没有这个好友，现在新增用户信息")
            LogUtil.addLog("以前没有这个好友，现在新增用户信息")
            var newFriend = UserEntity();
            newFriend.nickName = jAddFriendPushRsp.params.nickName
            //newFriend.friendStatus = 3
            newFriend.userId = jAddFriendPushRsp.params.friendId
            newFriend.routeName = jAddFriendPushRsp.params.routerName
            newFriend.routeId = jAddFriendPushRsp.params.routerId
            newFriend.addFromMe = false
            newFriend.timestamp = jAddFriendPushRsp.timestamp
            newFriend.noteName = ""
            newFriend.validationInfo = jAddFriendPushRsp.params.msg
            newFriend.signPublicKey = jAddFriendPushRsp.params.userKey

            var dst_public_MiKey_Friend = ByteArray(32)
            var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
            if(crypto_sign_ed25519_pk_to_curve25519_result == 0)
            {
                newFriend.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
            }
            userList.add(newFriend)

            var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            newFriend.routerUserId = selfUserId
            AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)
        }

//        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId),FriendEntityDao.Properties.FriendId.eq(jAddFriendPushRsp.params.friendId)).list()
//        if (localFriendList.size > 0)
//        {
//            KLog.i("好友关系大于1，清零，再生成一个")
//            AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.deleteInTx(localFriendList)
//            var newFriendStatus = FriendEntity()
//            newFriendStatus.userId = selfUserId;
//            newFriendStatus.friendId = jAddFriendPushRsp.params.friendId
//            newFriendStatus.friendLocalStatus = 3
//            newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
//            AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
//        }else{
//            KLog.i("好友关系小于1")
//        }
        KLog.i("更新ui")
        EventBus.getDefault().post(FriendChange())
    }

    fun refreshFriend(friendId:String) {
        userList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll().MutableListToArrayList()
        var newFriendCount = 0
        /*for (i in userList) {
            if (i.userId.equals(selfUserId)) {
                continue
            }
            if (i.friendStatus == 3) {
                newFriendCount++
            }
        }*/

        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(selfUserId)) {
                if (j.friendLocalStatus == 3) {
                    newFriendCount++
                }
            }
        }
        EventBus.getDefault().post(UnReadContactCount(newFriendCount))
        EventBus.getDefault().post(FriendChange(friendId))
    }


    /**
     * 目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友请求的结果
     * 第四步，对方处理完我发出的好友请求，给我推送处理结果
     */
    override fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp) {
        if (jAddFriendReplyRsp.params.result == 0) {
            var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(jAddFriendReplyRsp.params.userKey))
            var filePath  = Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath + "/Avatar/" + fileBase58Name + ".jpg"
            var fileMD5 = FileUtil.getFileMD5(File(filePath))
            if(fileMD5 == null)
            {
                fileMD5 = ""
            }
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            val updateAvatarReq = UpdateAvatarReq(userId!!, jAddFriendReplyRsp.params.userId, fileMD5)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
            } else if (ConstantValue.isToxConnected) {
                val baseData = BaseData(4, updateAvatarReq)
                val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                if (ConstantValue.isAntox) {
                    val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        userList.forEach {
            if (it.userId.equals(jAddFriendReplyRsp.params.friendId)) {
                /*if (jAddFriendReplyRsp.params.result == 0) {
                    it.friendStatus = 0
                } else if (jAddFriendReplyRsp.params.result == 1) {
                    it.friendStatus = 2
                }*/
                it.nickName = jAddFriendReplyRsp.params.nickname
                it.signPublicKey = jAddFriendReplyRsp.params.userKey
                it.routeId = jAddFriendReplyRsp.params.routeId
                it.routeName = jAddFriendReplyRsp.params.routeName
                var dst_public_MiKey_Friend = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend,RxEncodeTool.base64Decode(jAddFriendReplyRsp.params.userKey))
                if(crypto_sign_ed25519_pk_to_curve25519_result == 0)
                {
                    it.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                }
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)

                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendReplyReq = AddFriendReplyReq(0,userId!!, "")
                var sendData = BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid)
                if(ConstantValue.encryptionType.equals("1"))
                {
                    sendData = BaseData(4,addFriendReplyReq,jAddFriendReplyRsp.msgid)
                }
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                }else if (ConstantValue.isToxConnected) {
                    var baseData = sendData
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
                refreshFriend("")
                return
            }
        }
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jAddFriendReplyRsp.params.friendId.equals(j.friendId)) {
                    if (jAddFriendReplyRsp.params.result == 0) {
                        j.friendLocalStatus = 0
                    } else if (jAddFriendReplyRsp.params.result == 1) {
                        j.friendLocalStatus = 2
                    }
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    refreshFriend("")
                    return
                }
            }
        }
        var userEntity = UserEntity()
        userEntity.userId = jAddFriendReplyRsp.params.friendId
        userEntity.nickName = jAddFriendReplyRsp.params.nickname
        userEntity.routeId = jAddFriendReplyRsp.params.routeId
        userEntity.routeName = jAddFriendReplyRsp.params.routeName
        /* if (jAddFriendReplyRsp.params.result == 0) {
             userEntity.friendStatus = 0
         } else if (jAddFriendReplyRsp.params.result == 1) {
             userEntity.friendStatus = 2
         }*/
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        userEntity.routerUserId = selfUserId
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)

        var newFriendStatus = FriendEntity()
        newFriendStatus.userId = userId;
        newFriendStatus.friendLocalStatus =7;
        newFriendStatus.friendId = jAddFriendReplyRsp.params.friendId
        if (jAddFriendReplyRsp.params.result == 0) {
            newFriendStatus.friendLocalStatus = 0
        } else if (jAddFriendReplyRsp.params.result == 1) {
            newFriendStatus.friendLocalStatus = 2
        }
        newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)


        var addFriendReplyReq = AddFriendReplyReq(0,userId!!, "")
        var sendData = BaseData(addFriendReplyReq,jAddFriendReplyRsp.msgid)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendData = BaseData(4,addFriendReplyReq,jAddFriendReplyRsp.msgid)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        refreshFriend("")
    }

    override fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp) {
        /* userList.forEach {
             if (it.userId.equals(jDelFriendPushRsp.params.friendId)) {
                 it.friendStatus = 6
                 AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(it)
                 MessageProvider.getInstance().deletConversationByDeleteFriend(jDelFriendPushRsp.params.friendId)
                 return@forEach
             }
         }*/
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jDelFriendPushRsp.params.friendId.equals(j.friendId)) {
                    j.friendLocalStatus = 6
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    MessageProvider.getInstance().deletConversationByDeleteFriend(jDelFriendPushRsp.params.friendId)
                    break
                }

            }
        }
        var delFriendPushReq = DelFriendPushReq(0,userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendPushReq,jDelFriendPushRsp.msgid))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendPushReq,jDelFriendPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        EventBus.getDefault().post(FriendChange(jDelFriendPushRsp.params.friendId,jDelFriendPushRsp.params.userId))
    }

    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        KLog.i("firendList")
    }

    var userList = arrayListOf<UserEntity>()
    var selfUserId = ""
    companion object {
        var userProvider : UserProvider? = null
        fun getInstance() : UserProvider{
            if (userProvider == null) {
                userProvider = UserProvider()
                return userProvider!!
            } else {
                return userProvider!!
            }
        }

        fun init() {
            getInstance()
        }
    }

    init {
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")!!
        userList.addAll(list)
        if (!"".equals(selfUserId)) {
            var hasSelf = false
            list.forEach {
                if (selfUserId!!.equals(it.userId)) {
                    hasSelf = true
                }
            }
            if (!hasSelf) {
                var self = UserEntity()
                self.userId = selfUserId
                self.nickName = SpUtil.getString(AppConfig.instance, ConstantValue.username, "")
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(self)
                userList.add(self)
            }
        }
    }

    fun getUserById(userId : String) : UserEntity? {
        userList.forEach{
            if (it.userId.equals(userId)) {
                return it
            }
        }
        getUserFromServer()
        return null
    }

    fun getUserFromServer() {

    }

    fun addFriend(selfUserId : String, nickName : String, toUserId: String) {
        val strBase64 = RxEncodeTool.base64Encode2String(nickName.toByteArray())
        var addFriendReq = AddFriendReq( selfUserId, strBase64, toUserId,ConstantValue.publicRAS!!,"")
        var sendData = BaseData(addFriendReq);
        if(ConstantValue.encryptionType.equals( "1"))
        {
            addFriendReq =  AddFriendReq( selfUserId, strBase64, toUserId,ConstantValue.libsodiumpublicSignKey!!,"")
            sendData = BaseData(4,addFriendReq);
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }

    fun deleteFriend(selfUserId : String, toUserId: String) {
        var delFriendCmdReq = DelFriendCmdReq(selfUserId, toUserId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.messageSender!!.send(BaseData(delFriendCmdReq))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendCmdReq)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }


    }

    fun accepteAddFriend(selfNickName : String, toNickName : String, selfUserId: String, toUserId : String,friendKey :String) {
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(selfNickName!!.toByteArray())
        var sign = ByteArray(32)
        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
        System.arraycopy(time, 0, sign, 0, time.size)
        var dst_signed_msg = ByteArray(96)
        var signed_msg_len = IntArray(1)
        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(toNickName!!.toByteArray())
        var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, ConstantValue.publicRAS!!, friendKey,signBase64,0)
        var sendData = BaseData(addFriendDealReq)
        if(ConstantValue.encryptionType.equals("1"))
        {
            addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, ConstantValue.libsodiumpublicSignKey!!, friendKey,signBase64,0)
            sendData = BaseData(4,addFriendDealReq)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.messageSender!!.send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }

    fun refuseAddFriend(selfNickName : String, toNickName : String, selfUserId: String, toUserId : String,friendKey :String) {
        val selfNickNameBase64 = RxEncodeTool.base64Encode2String(selfNickName!!.toByteArray())
        //val toNickNameBase64 = RxEncodeTool.base64Encode2String(toNickName!!.toByteArray())
        var sign = ByteArray(32)
        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
        System.arraycopy(time, 0, sign, 0, time.size)
        var dst_signed_msg = ByteArray(96)
        var signed_msg_len = IntArray(1)
        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
        var addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, "",friendKey,signBase64,1)

        var sendData = BaseData(addFriendDealReq)
        if(ConstantValue.encryptionType.equals("1"))
        {
            addFriendDealReq = AddFriendDealReq(selfNickNameBase64!!, toNickName, selfUserId, toUserId, "",friendKey,signBase64,1)
            sendData = BaseData(4,addFriendDealReq)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.messageSender!!.send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }

    interface FriendOperateListener {
        fun delFriendRsp(retCode : Int)
        fun accepteFriendRsp(retCode : Int)
        fun refuseFriendRsp(retCode : Int)
        fun addFriendRsp(retCode : Int)
        fun changeRemarksRsp(retCode : Int)
    }

    interface AddFriendDelListener {
        fun addFriendDealRsp(retCode : Int)
    }

}