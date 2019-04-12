/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.ProgressBar
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey

import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.model.EaseCompat
import com.hyphenate.easeui.model.EaseImageCache
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.widget.photoview.EasePhotoView
import com.hyphenate.util.EMLog
import com.hyphenate.util.ImageUtils
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.main.WebViewActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.view.CustomPopWindow

import org.greenrobot.eventbus.EventBus

import java.io.File
import java.util.ArrayList
import java.util.Calendar

import javax.xml.transform.Result

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.alibaba.fastjson.JSONObject
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.RxDialogLoading
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import events.ToxFriendStatusEvent
import events.ToxSendInfoEvent
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import scalaz.Alpha

/**
 * download and show original image
 *
 */
class EaseShowBigImageActivity : EaseBaseActivity() , PNRouterServiceMessageReceiver.BigImageBack {
    private var pd: ProgressDialog? = null
    private var image: EasePhotoView? = null
    private var default_res = R.drawable.image_defalut_bg
    private var fileUrl: String? = null
    private var localFilePath: String? = null
    private var bitmap: Bitmap? = null
    private var isDownloaded: Boolean = false
    private var hasQRCode: String? = ""
    lateinit var progressDialog: RxDialogLoading
    private lateinit var standaloneCoroutine : Job
    var routerId = ""
    var userSn = ""
    var userId = ""
    var username = ""
    var dataFileVersion = 0
    var isScanSwitch = false

    var SELECT_PHOTO = 2
    var SELECT_VIDEO = 3
    var SELECT_DEOCUMENT = 4
    var create_group = 5
    var add_activity = 6
    var isSendRegId = true
    private var handler: Handler? = null
    var newRouterEntity = RouterEntity()
    private var loginGoMain:Boolean = false
    var loginBack = false
    var isFromScan = false
    var isFromScanAdmim = false
    //是否点击了登陆按钮
    //是否点击了登陆按钮
    var isClickLogin = false
    //是否正在登陆
    var isStartLogin = false
    var stopTox = false;
    var loginOk = false
    var isToxLoginOverTime = false;
    var maxLogin = 0
    var threadInit = false
    var RouterMacStr = ""
    var islogining = false
    var isloginOutTime = false
    var scanType = 0 // 0 admin   1 其他
    var adminUserSn:String?  = null
    var hasFinger = false
    var name:Long  = 0;
    override fun registerBack(registerRsp: JRegisterRsp) {
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        if (registerRsp.params.retCode != 0) {
            if (registerRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("RouterId Error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            if (registerRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("QR code has been activated by other users.")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            if (registerRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("Error Verification Code")
                    gotoLogin()
                    closeProgressDialog()
                }
            }
            if (registerRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Other Error")
                    gotoLogin()
                    closeProgressDialog()
                }
            }
            return
        }
        if ("".equals(registerRsp.params.userId)) {
            runOnUiThread {
                toast("Too many users")
                gotoLogin()
                closeProgressDialog()
            }
        } else {
            runOnUiThread {
                closeProgressDialog()
                KLog.i("1111")
            }
            var newRouterEntity = RouterEntity()
            newRouterEntity.routerId = registerRsp.params.routeId
            newRouterEntity.userSn = registerRsp.params.userSn
            newRouterEntity.username = ConstantValue.localUserName
            newRouterEntity.userId = registerRsp.params.userId
            newRouterEntity.loginKey = "";
            newRouterEntity.dataFileVersion = registerRsp.params.dataFileVersion
            newRouterEntity.dataFilePay = registerRsp.params.dataFilePay
            newRouterEntity.adminId = registerRsp.params!!.adminId
            newRouterEntity.adminName = registerRsp.params!!.adminName
            newRouterEntity.adminKey = registerRsp.params!!.adminKey
            var localData: java.util.ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(registerRsp.params!!.routerName))
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() /1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
            //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
            var login = LoginReq_V4(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,signBase64, registerRsp.params.dataFileVersion,NickName)
            islogining = true
            ConstantValue.loginReq = login
            if(ConstantValue.isWebsocketConnected)
            {

                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
            else if(ConstantValue.isToxConnected)
            {
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(registerRsp.params.routeId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, registerRsp.params.routeId.substring(0, 64))
                }
            }
        }
    }

    override fun loginBack(loginRsp: JLoginRsp) {
//        if (!loginRsp.params.userId.equals(userId)) {
//            KLog.i("过滤掉userid错误的请求")
//            return
//        }
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        islogining = false
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        KLog.i(loginRsp.toString())
        LogUtil.addLog("loginBack:"+loginRsp.params.retCode,"LoginActivityActivity")
        if(standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast("need Verification")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast("rid error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast("uid error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast("Validation failed")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast("Verification code error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            else{
                runOnUiThread {
                    toast("other error")
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast("userId is empty")
                closeProgressDialog()
                gotoLogin()
            }
        } else {
            islogining = false
            ConstantValue.loginOut = false
            ConstantValue.logining = true
            LogUtil.addLog("loginBack:"+"begin","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userId,"userid")
            //FileUtil.saveUserData2Local(loginRsp.params!!.index,"userIndex")
            LogUtil.addLog("loginBack:"+"a","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn,"usersn")
            LogUtil.addLog("loginBack:"+"b","LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid,"routerid")
            LogUtil.addLog("loginBack:"+"c","LoginActivityActivity")
            KLog.i("服务器返回的userId：${loginRsp.params!!.userId}")
            ConstantValue.currentRouterId = loginRsp.params!!.routerid
            newRouterEntity.userId = loginRsp.params!!.userId
            newRouterEntity.index = ""
            SpUtil.putString(this, ConstantValue.userId, loginRsp.params!!.userId)
            //SpUtil.putString(this, ConstantValue.userIndex, loginRsp.params!!.index)
            //SpUtil.putString(this, ConstantValue.username,ConstantValue.localUserName!!)
            SpUtil.putString(this, ConstantValue.routerId, loginRsp.params!!.routerid)
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            newRouterEntity.routerId = loginRsp.params!!.routerid
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            if(loginRsp.params.nickName != null)
                newRouterEntity.username = String(RxEncodeTool.base64Decode(loginRsp.params.nickName))
            newRouterEntity.lastCheck = true
            newRouterEntity.userSn = loginRsp.params!!.userSn
            newRouterEntity.loginKey = ""
            var myUserData = UserEntity()
            myUserData.userId = loginRsp.params!!.userId
            myUserData.nickName = loginRsp.params!!.nickName
            UserDataManger.myUserData = myUserData
            var contains = false
            for (i in routerList) {
                if (i.userSn.equals(loginRsp.params!!.userSn)) {
                    contains = true
                    newRouterEntity = i

                    break
                }
            }
            LogUtil.addLog("loginBack:"+"d","LoginActivityActivity")
            var needUpdate : java.util.ArrayList<MyRouter> = java.util.ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter:MyRouter = MyRouter();
                myRouter.setType(0)
                myRouter.setRouterEntity(it)
                needUpdate.add(myRouter);
            }
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.updateInTx(routerList)
            LocalRouterUtils.updateList(needUpdate)
            newRouterEntity.lastCheck = true
            newRouterEntity.loginKey = ""
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(loginRsp.params!!.routerName))
            newRouterEntity.dataFileVersion = 0
            newRouterEntity.dataFilePay =  ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            LogUtil.addLog("loginBack:"+"e","LoginActivityActivity")
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
                KLog.i("333")
            }
            LogUtil.addLog("loginBack:"+"f","LoginActivityActivity")
            loginOk = true
            isToxLoginOverTime = false
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            resetUnCompleteFileRecode()
             if(loginGoMain)
                 return
            isScanSwitch = false
            ConstantValue.isInit = false
            AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
            startActivity(Intent(this, MainActivity::class.java))
            loginGoMain  = true
            LogUtil.addLog("loginBack:"+"g","LoginActivityActivity")
            finish()
        }
    }

    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        if(!isScanSwitch)
        {
            runOnUiThread {
                closeProgressDialog()
            }
            return;
        }
        closeProgressDialog()
        KLog.i("222")
        ConstantValue.unSendMessage.remove("recovery")
        ConstantValue.unSendMessageFriendId.remove("recovery")
        ConstantValue.unSendMessageSendCount.remove("recovery")
        when (recoveryRsp.params.retCode) {
            0 ->{
                ConstantValue.lastNetworkType = "";
                /*val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    var routerEntity:RouterEntity = routerEntityList[0]
                    routerId = routerEntity.routerId
                    userSn = routerEntity.userSn
                    userId = routerEntity.userId
                    username = routerEntity.username
                    dataFileVersion = routerEntity.dataFileVersion
                    *//*runOnUiThread {
                        routerNameTips.text = newRouterEntity.routerName
                    }*//*
                    EventBus.getDefault().post(NameChange(routerEntity.routerName,routerEntity.loginKey))
                }else{
                    var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.loginKey = ""
                    newRouterEntity.dataFilePay = ""
                    var localData: java.util.ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
                    newRouterEntity.routerName = String(RxEncodeTool.base64Decode(recoveryRsp.params!!.routerName))
                    routerId = recoveryRsp.params.routeId
                    userSn = recoveryRsp.params.userSn
                    userId = recoveryRsp.params.userId
                    username =String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    dataFileVersion =recoveryRsp.params.dataFileVersion
                    //routerNameTips.text = newRouterEntity.routerName
                    EventBus.getDefault().post(NameChange(newRouterEntity.routerName))
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }*/
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
                var login = LoginReq_V4(  recoveryRsp.params.routeId,recoveryRsp.params.userSn, recoveryRsp.params.userId,signBase64, recoveryRsp.params.dataFileVersion,NickName)
                islogining = true
                ConstantValue.loginReq = login
                if(ConstantValue.isWebsocketConnected)
                {

                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,login)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                }
            }
            1 -> {
                /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                  startActivity(Intent(this, RegisterActivity::class.java))*/

                runOnUiThread {
                    showProgressDialog("waiting...")
                }

                val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                var regeister = RegeisterReq_V4( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, signBase64,pulicMiKey,NickName)
                if(ConstantValue.isWebsocketConnected)
                {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                }
                else if(ConstantValue.isToxConnected)
                {
                    var baseData = BaseData(4,regeister)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                        MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    }else{
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    }
                }
            }
            2 -> {
                runOnUiThread {
                    toast("Rid error")
                    gotoLogin()
                }

            }
            3-> {
                ConstantValue.lastNetworkType = "";
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {
                    for( i in routerEntityList)
                    {

                        if(i!= null && !i.userId.equals(""))
                        {
                            routerId = i.routerId
                            userSn = i.userSn
                            userId = i.userId
                            username = i.username
                            if(i.dataFileVersion == null)
                            {
                                dataFileVersion = 0
                            }else{
                                dataFileVersion = i.dataFileVersion
                            }

                            runOnUiThread()
                            {
                                routerNameTips.text = i.routerName
                                ivAvatar.setText(i.routerName)
                                loginKey.setText(i.loginKey)
                            }
                            ConstantValue.currentRouterIp = ""
                            //ConstantValue.scanRouterId = routerId;
                            isClickLogin = false
                            isStartLogin = true
                            getServer(routerId,userSn,true,true)
                        }
                    }
                }else{
                    /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                      var intent = Intent(this, RegisterActivity::class.java)
                      intent.putExtra("flag", 1)
                      startActivity(intent)*/
                    runOnUiThread {
                        showProgressDialog("waiting...")
                    }

                    val NickName = RxEncodeTool.base64Encode2String( ConstantValue.localUserName!!.toByteArray())
                    var sign = ByteArray(32)
                    var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                    System.arraycopy(time, 0, sign, 0, time.size)
                    var dst_signed_msg = ByteArray(96)
                    var signed_msg_len = IntArray(1)
                    var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                    var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                    var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                    //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                    var regeister = RegeisterReq_V4( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, signBase64,pulicMiKey,NickName)
                    if(ConstantValue.isWebsocketConnected)
                    {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,regeister))
                    }
                    else if(ConstantValue.isToxConnected)
                    {
                        var baseData = BaseData(4,regeister)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        if (ConstantValue.isAntox) {
                            var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }else{
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                        }
                    }
                }

            }
            4 -> {
                runOnUiThread {
                    toast("Other mistakes")
                    gotoLogin()
                }
            }
            else -> {
                runOnUiThread {
                    toast("QR code has been activated by other users.")
                    gotoLogin()
                }
            }
        }
    }



    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.ease_activity_show_big_image)
        super.onCreate(savedInstanceState)
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
        }
        AppConfig.instance.messageReceiver?.bigImageBack = this
        progressDialog = RxDialogLoading(this)
        progressDialog.setmDialogColor(resources.getColor(R.color.mainColor))
        progressDialog.setDialogText(resources.getString(R.string.apploading))
        image = findViewById<View>(R.id.image) as EasePhotoView
        val loadLocalPb = findViewById<View>(R.id.pb_load_local) as ProgressBar
        default_res = intent.getIntExtra("default_image", R.drawable.ease_default_avatar)
        val uri = intent.getParcelableExtra<Uri>("uri")
        fileUrl = intent.extras!!.getString("fileUrl")
        localFilePath = intent.extras!!.getString("localUrl")
        val msgId = intent.extras!!.getString("messageId")
        //EMLog.d(TAG, "show big deleteMsgId:" + msgId!!)
        EventBus.getDefault().register(this)
        var this_ = this
        var isStartWebsocket = false
        handler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_UPD_DATA -> {
                        KLog.i("收到了组播的回复了 ")
                        var obj:String = msg.obj.toString()
                        if(!obj.equals(""))
                        {
                            var objArray = obj.split("##")
                            var index = 0;
                            for(item in objArray)
                            {
                                if(!item.equals(""))
                                {
                                    try {
                                        var udpData = AESCipher.aesDecryptString(objArray[index],"slph\$%*&^@-78231")
                                        var udpRouterArray = udpData.split(";")

                                        if(udpRouterArray.size > 1)
                                        {
                                            println("ipdizhi:"+udpRouterArray[1] +" ip: "+udpRouterArray[0])
                                            //ConstantValue.updRouterData.put(udpRouterArray[1],udpRouterArray[0])
                                            if(scanType == 1)//不是admin二维码
                                            {
                                                if(!ConstantValue.scanRouterId.equals("") && ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                                {
                                                    ConstantValue.currentRouterIp = udpRouterArray[0]
                                                    ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                    ConstantValue.port= ":18006"
                                                    ConstantValue.filePort = ":18007"
                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                    break;
                                                }else if(!routerId.equals("") && routerId.equals(udpRouterArray[1]))
                                                {
                                                    ConstantValue.currentRouterIp = udpRouterArray[0]
                                                    ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                    ConstantValue.port= ":18006"
                                                    ConstantValue.filePort = ":18007"
                                                    ConstantValue.currentRouterId = routerId
                                                    ConstantValue.currentRouterSN =  userSn
                                                    break;
                                                }
                                            }else{
                                                ConstantValue.curreantNetworkType = "WIFI"
                                                ConstantValue.currentRouterIp = udpRouterArray[0]
                                                ConstantValue.localCurrentRouterIp = ConstantValue.currentRouterIp
                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                ConstantValue.port= ":18006"
                                                ConstantValue.filePort = ":18007"
                                                ConstantValue.currentRouterMac = RouterMacStr
                                                break;
                                            }


                                        }
                                    }catch (e:Exception)
                                    {
                                        e.printStackTrace()
                                    }

                                }
                                index ++

                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                                if(isFromScan || isFromScanAdmim)
                                {
                                    if(ConstantValue.isHasWebsocketInit)
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                    }else{
                                        ConstantValue.isHasWebsocketInit = true
                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                    }
                                    isStartWebsocket = true
                                    KLog.i("没有初始化。。设置loginBackListener"+this_)
                                    //AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                }

                            }
                        }

                    }
                }
            }
        }
        //show the image if it exist in local path
        if (uri != null && File(uri.path).exists()) {
            EMLog.d(TAG, "showbigimage file exists. directly show it")
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = EaseImageCache.getInstance().get(uri.path)
            //			if (bitmap == null) {
            //				EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
            //						ImageUtils.SCALE_IMAGE_HEIGHT);
            //				if (android.os.Build.VERSION.SDK_INT > 10) {
            //					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //				} else {
            //					task.execute();
            //				}
            //			} else {
            //				Bitmap bigData = EaseImageUtils.getBitmap(new File(uri.getPath()));
            //				int degree = EaseImageUtils.readPictureDegree(uri.getPath());
            //				if(degree != 0)
            //				{
            //					Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree);
            //					image.setImageBitmap(bmpOk);
            //				}else{
            //					image.setImageBitmap(bigData);
            //				}
            //
            //			}
            val bigData = EaseImageUtils.getBitmap(File(uri.path))
            val degree = EaseImageUtils.readPictureDegree(uri.path)
            if (degree != 0) {
                val bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree.toFloat())
                image!!.setImageBitmap(bmpOk)
            } else {
                image!!.setImageBitmap(bigData)
            }
        } else if (msgId != null) {
            downloadImage(msgId)
        } else {
            image!!.setImageResource(default_res)
        }

        image!!.setOnClickListener {
            finish()
        }
        val obmp = (image!!.getDrawable() as BitmapDrawable).bitmap
        Thread(Runnable {
            if(hasQRCode == null || hasQRCode.equals(""))
            {
                val list = ArrayList<String>()
                list.add("Save Image")
                hasQRCode = QRCodeDecoder.syncDecodeQRCode(obmp)
                if (hasQRCode != null && hasQRCode != "") {
                    EventBus.getDefault().post(AddMenu())
                }
            }
        }).start()
        var _this = this
        image!!.setOnLongClickListener {
            val list = ArrayList<String>()
            list.add("Save Image")
            if (hasQRCode != null && hasQRCode != "") {
                list.add("Scan QR Code in Image")
            }else{
                Thread(Runnable {
                    if(hasQRCode == null || hasQRCode.equals(""))
                    {
                        EventBus.getDefault().post(AddMenu())
                    }
                }).start()
            }
            PopWindowUtil.showSelecMenuPopWindow(this@EaseShowBigImageActivity, image!!, list, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {

                    val choose = obj.toString()
                    when (choose) {
                        "Save Image" -> {
                            var galleryPath = (Environment.getExternalStorageDirectory().toString()
                                    + File.separator + Environment.DIRECTORY_DCIM
                                    + File.separator + "Confidant" + File.separator)
                            val galleryPathFile = File(galleryPath)
                            if (!galleryPathFile.exists()) {
                                galleryPathFile.mkdir()
                            }
                            var imagePath = localFilePath
                            if (fileUrl != null) {
                                imagePath = fileUrl
                            }
                            galleryPath += System.currentTimeMillis().toString() + imagePath!!.substring(imagePath.lastIndexOf("."), imagePath.length)
                            val result = FileUtil.copyAppFileToSdcard(imagePath, galleryPath)
                            if (result == 1) {

                                AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, galleryPath, System.currentTimeMillis())
                            }
                            EventBus.getDefault().post(SaveMsgEvent("", result))
                        }
                        "Scan QR Code in Image" ->
                        {
                            var result = hasQRCode
                            var type = result!!.substring(0,6);
                            var data = result!!.substring(7,result!!.length);
                            var soureData:ByteArray =  ByteArray(0)
                            if(!type.equals("type_0"))
                            {
                                soureData =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
                            }
                            if (hasQRCode!!.indexOf("http://") > -1 || hasQRCode!!.indexOf("https://") > -1) {
                                /*val intent = Intent(AppConfig.instance, WebViewActivity::class.java)
                                intent.putExtra("url", hasQRCode)
                                intent.putExtra("title", "Other websites")
                                startActivity(intent)*/
                                val intent = Intent()
                                intent.action = "android.intent.action.VIEW"
                                val url = Uri.parse(hasQRCode)
                                intent.data = url
                                startActivity(intent)
                            } else if (!result!!.contains("type_")){
                                if (NetUtils.isMacAddress(result)) {
                                    runOnUiThread {
                                        SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                                .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                                .setConfirmClickListener {
                                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                    var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                    if (ConstantValue.isWebsocketConnected) {
                                                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                    } else if (ConstantValue.isToxConnected) {
                                                        val baseData = BaseData(2,msgData)
                                                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                    }
                                                    isScanSwitch = true
                                                    scanType = 0;
                                                    RouterMacStr = result
                                                    if(RouterMacStr != null && !RouterMacStr.equals(""))
                                                    {
                                                        if(AppConfig.instance.messageReceiver != null)
                                                            AppConfig.instance.messageReceiver!!.close()
                                                        if(WiFiUtil.isWifiConnect())
                                                        {
                                                            showProgressDialog("wait...")
                                                            ConstantValue.currentRouterMac  = ""
                                                            isFromScanAdmim = true
                                                            var count =0;
                                                            KLog.i("测试计时器Mac" + count)
                                                            Thread(Runnable() {
                                                                run() {
                                                                    Thread.sleep(1500)
                                                                    while (true)
                                                                    {
                                                                        if(count >=3)
                                                                        {
                                                                            if(ConstantValue.currentRouterMac.equals(""))
                                                                            {
                                                                                runOnUiThread {
                                                                                    closeProgressDialog()
                                                                                    RouterMacStr = ""
                                                                                    isFromScanAdmim = false
                                                                                    toast(R.string.Unable_to_connect_to_router)
                                                                                }
                                                                            }
                                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                                            break;
                                                                        }else if(!ConstantValue.currentRouterMac.equals(""))
                                                                        {
                                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                                            break;
                                                                        }
                                                                        count ++;
                                                                        MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                        var macAddress  = ""
                                                                        for (i in 0..5) {
                                                                            macAddress = macAddress + RouterMacStr.substring(i * 2, (i + 1) * 2) + ":"
                                                                        }
                                                                        macAddress = macAddress.subSequence(0, macAddress.length - 1).toString()
                                                                        var toMacMi = AESCipher.aesEncryptString(macAddress,"slph\$%*&^@-78231")
                                                                        MobileSocketClient.getInstance().destroy()
                                                                        MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                                                        MobileSocketClient.getInstance().receive()
                                                                        KLog.i("测试计时器Mac" + count)
                                                                        Thread.sleep(1000)
                                                                    }

                                                                }
                                                            }).start()

                                                        }else{
                                                            runOnUiThread {
                                                                closeProgressDialog()
                                                                toast(R.string.Please_connect_to_WiFi)
                                                            }
                                                        }
                                                    }else{
                                                        runOnUiThread {
                                                            closeProgressDialog()
                                                            toast(R.string.code_error)
                                                        }
                                                    }
                                                }
                                                .show()
                                    }
                                    return;

                                } else {
                                    runOnUiThread {
                                        toast(R.string.code_error)
                                    }
                                    return
                                }
                                runOnUiThread {
                                    toast(R.string.code_error)
                                }

                                return;
                            }
                            else if (hasQRCode!!.contains("type_0")) {
                                val toAddUserId = hasQRCode!!.substring(7, hasQRCode!!.length)
                                val selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                if (toAddUserId.contains(selfUserId!!)) {
                                    runOnUiThread {
                                        runOnUiThread {
                                            toast(R.string.The_same_user)
                                        }

                                    }
                                    return
                                }
                                if ("" != toAddUserId) {
                                    val toAddUserIdTemp = toAddUserId.substring(0, toAddUserId.indexOf(","))
                                    var intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                    val useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                                    for (i in useEntityList) {
                                        if (i.userId == toAddUserIdTemp) {
                                            var freindStatusData = FriendEntity()
                                            freindStatusData.friendLocalStatus = 7
                                            val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
                                            if (localFriendStatusList.size > 0)
                                                freindStatusData = localFriendStatusList[0]

                                            if (freindStatusData.friendLocalStatus == 0) {
                                                intent.putExtra("user", i)
                                                startActivity(intent)
                                            } else {
                                                intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                                intent.putExtra("user", i)
                                                startActivity(intent)
                                            }

                                            return
                                        }
                                    }
                                    intent = Intent(AppConfig.instance, SendAddFriendActivity::class.java)
                                    val userEntity = UserEntity()
                                    //userEntity.friendStatus = 7
                                    userEntity.userId = toAddUserId.substring(0, toAddUserId.indexOf(","))
                                    userEntity.nickName = toAddUserId.substring(toAddUserId.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                                    userEntity.signPublicKey = toAddUserId.substring(toAddUserId.lastIndexOf(",") + 1, toAddUserId.length)
                                    userEntity.timestamp = Calendar.getInstance().timeInMillis

                                    userEntity.routerUserId = selfUserId
                                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)


                                    val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                    val newFriendStatus = FriendEntity()
                                    newFriendStatus.userId = userId
                                    newFriendStatus.friendId = toAddUserId
                                    newFriendStatus.friendLocalStatus = 7
                                    newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
                                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
                                    intent.putExtra("user", userEntity)
                                    startActivity(intent)
                                }
                            } else if (hasQRCode!!.contains("type_1")) {

                                scanType = 1
                                val keyId:ByteArray = ByteArray(6) //密钥ID
                                val RouterId:ByteArray = ByteArray(76) //路由器id
                                val UserSn:ByteArray = ByteArray(32)  //用户SN
                                System.arraycopy(soureData, 0, keyId, 0, 6)
                                System.arraycopy(soureData, 6, RouterId, 0, 76)
                                System.arraycopy(soureData, 82, UserSn, 0, 32)
                                var keyIdStr = String(keyId)
                                var RouterIdStr = String(RouterId)
                                var UserSnStr = String(UserSn)

                                ConstantValue.scanRouterId = RouterIdStr
                                ConstantValue.scanRouterSN = UserSnStr
                                if(RouterIdStr != null && !RouterIdStr.equals("")&& UserSnStr != null && !UserSnStr.equals(""))
                                {
                                    if(ConstantValue.currentRouterId.equals(RouterIdStr))
                                    {
                                        runOnUiThread {
                                            toast(R.string.The_same_circle_without_switching)
                                        }

                                        return
                                    }
                                    runOnUiThread {
                                        SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                                .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                                .setConfirmClickListener {
                                                    showProgressDialog("switch...")
                                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                    var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                    if (ConstantValue.isWebsocketConnected) {
                                                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                    } else if (ConstantValue.isToxConnected) {
                                                        val baseData = BaseData(2,msgData)
                                                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                    }
                                                    isScanSwitch = true
                                                    if(AppConfig.instance.messageReceiver != null)
                                                        AppConfig.instance.messageReceiver!!.close()
                                                    ConstantValue.lastNetworkType = ConstantValue.curreantNetworkType

                                                    ConstantValue.lastRouterIp =  ConstantValue.currentRouterIp
                                                    ConstantValue.lastPort=  ConstantValue.port
                                                    ConstantValue.lastFilePort= ConstantValue.filePort
                                                    ConstantValue.lastRouterId =   ConstantValue.currentRouterId
                                                    ConstantValue.lastRouterSN =  ConstantValue.currentRouterSN

                                                    isFromScan = true
                                                    ConstantValue.currentRouterIp = ""
                                                    if(WiFiUtil.isWifiConnect())
                                                    {
                                                        showProgressDialog("wait...")
                                                        var count =0;
                                                        KLog.i("测试计时器" + count)
                                                        Thread(Runnable() {
                                                            run() {
                                                                Thread.sleep(1500)
                                                                while (true)
                                                                {
                                                                    if(count >=3)
                                                                    {
                                                                        if(!ConstantValue.currentRouterIp.equals(""))
                                                                        {
                                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                                            break;
                                                                        }else{

                                                                            OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                                                                override fun onFailure( e :Exception) {
                                                                                    startToxAndRecovery()
                                                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                                                }
                                                                                override fun  onResponse(json:String ) {

                                                                                    val gson = GsonUtil.getIntGson()
                                                                                    var httpData: HttpData? = null
                                                                                    try {
                                                                                        if (json != null) {
                                                                                            httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                                                            if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                                                            {
                                                                                                ConstantValue.curreantNetworkType = "WIFI"
                                                                                                ConstantValue.currentRouterIp = httpData.serverHost
                                                                                                ConstantValue.port = ":"+httpData.serverPort.toString()
                                                                                                ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                                if(ConstantValue.isHasWebsocketInit)
                                                                                                {
                                                                                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                                }else{
                                                                                                    ConstantValue.isHasWebsocketInit = true
                                                                                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                                }
                                                                                                //KLog.i("没有初始化。。设置loginBackListener"+this_)
                                                                                                //AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                                                                                Thread.currentThread().interrupt() //方法调用终止线程
                                                                                            }else{
                                                                                                startToxAndRecovery()
                                                                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                                                                            }

                                                                                        }
                                                                                    } catch (e: Exception) {
                                                                                        startToxAndRecovery()
                                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                                    }
                                                                                }
                                                                            })
                                                                            break;
                                                                        }

                                                                    }
                                                                    count ++;
                                                                    MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                    var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                                                                    MobileSocketClient.getInstance().destroy()
                                                                    MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                                                                    MobileSocketClient.getInstance().receive()
                                                                    KLog.i("测试计时器" + count)
                                                                    Thread.sleep(1000)
                                                                }

                                                            }
                                                        }).start()
                                                    }else{
                                                        showProgressDialog("switch...")
                                                        Thread(Runnable() {
                                                            run() {
                                                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + RouterIdStr,  object : OkHttpUtils.OkCallback {
                                                                    override fun onFailure( e :Exception) {
                                                                        startToxAndRecovery()
                                                                    }

                                                                    override fun  onResponse(json:String ) {

                                                                        val gson = GsonUtil.getIntGson()
                                                                        var httpData: HttpData? = null
                                                                        try {
                                                                            if (json != null) {
                                                                                var  httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                                                if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                                                {
                                                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                                                    ConstantValue.currentRouterIp = httpData.serverHost
                                                                                    ConstantValue.port = ":"+httpData.serverPort.toString()
                                                                                    ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                    if(ConstantValue.isHasWebsocketInit)
                                                                                    {
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                    }else{
                                                                                        ConstantValue.isHasWebsocketInit = true
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                    }
                                                                                    //KLog.i("没有初始化。。设置loginBackListener"+this_)
                                                                                    //AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                                                                }else{
                                                                                    startToxAndRecovery()
                                                                                }

                                                                            }
                                                                        } catch (e: Exception) {
                                                                            startToxAndRecovery()
                                                                        }
                                                                    }
                                                                })
                                                            }
                                                        }).start()
                                                    }
                                                }
                                                .show()
                                    }


                                }else{
                                    runOnUiThread {
                                        toast(R.string.code_error)
                                    }
                                }
                            }else if (hasQRCode!!.contains("type_2")) {
                                scanType = 0;
                                RouterMacStr = String(soureData)
                                if(RouterMacStr != null && !RouterMacStr.equals(""))
                                {
                                    runOnUiThread {
                                        SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                                .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                                .setConfirmClickListener {
                                                    showProgressDialog("switch...")
                                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                                    var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                                    if (ConstantValue.isWebsocketConnected) {
                                                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                                    } else if (ConstantValue.isToxConnected) {
                                                        val baseData = BaseData(2,msgData)
                                                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                                    }
                                                    isScanSwitch = true
                                                    if(AppConfig.instance.messageReceiver != null)
                                                        AppConfig.instance.messageReceiver!!.close()
                                                    if(WiFiUtil.isWifiConnect())
                                                    {
                                                        showProgressDialog("wait...")
                                                        ConstantValue.currentRouterMac  = ""
                                                        isFromScanAdmim = true
                                                        var count =0;
                                                        KLog.i("测试计时器Mac" + count)
                                                        Thread(Runnable() {
                                                            run() {
                                                                Thread.sleep(1500)
                                                                while (true)
                                                                {
                                                                    if(count >=3)
                                                                    {
                                                                        if(ConstantValue.currentRouterMac.equals(""))
                                                                        {
                                                                            runOnUiThread {
                                                                                closeProgressDialog()
                                                                                RouterMacStr = ""
                                                                                isFromScanAdmim = false
                                                                                toast(R.string.Unable_to_connect_to_router)
                                                                            }
                                                                        }
                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                        break;
                                                                    }else if(!ConstantValue.currentRouterMac.equals(""))
                                                                    {
                                                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                                                        break;
                                                                    }
                                                                    count ++;
                                                                    MobileSocketClient.getInstance().init(handler,AppConfig.instance)
                                                                    var toMacMi = AESCipher.aesEncryptString(RouterMacStr,"slph\$%*&^@-78231")
                                                                    MobileSocketClient.getInstance().destroy()
                                                                    MobileSocketClient.getInstance().send("MAC"+toMacMi)
                                                                    MobileSocketClient.getInstance().receive()
                                                                    KLog.i("测试计时器Mac" + count)
                                                                    Thread.sleep(1000)
                                                                }

                                                            }
                                                        }).start()

                                                    }else{
                                                        runOnUiThread {
                                                            closeProgressDialog()
                                                            toast(R.string.Please_connect_to_WiFi)
                                                        }
                                                    }
                                                }
                                                .show()
                                    }


                                }else{
                                    runOnUiThread {
                                        closeProgressDialog()
                                        toast(R.string.code_error)
                                    }
                                }
                            }else if (hasQRCode!!.contains("type_3")) {
                                runOnUiThread {
                                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Do_you_leave_the_circle_to_import_new_accounts))
                                            .setConfirmClickListener {
                                                gotoLogin()
                                            }
                                            .show()
                                }
                            }else{
                                runOnUiThread {
                                    closeProgressDialog()
                                    toast(R.string.code_error)
                                }
                            }
                        }
                        else -> {

                        }
                    }
                }
            })
            true
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun finishEvent(finishEvent: FinishEvent) {
        finish()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addMenu(addMenu: AddMenu) {
        Thread(Runnable {
            val obmp = (image!!.getDrawable() as BitmapDrawable).bitmap
            val list = ArrayList<String>()
            list.add("Save Image")
            hasQRCode = QRCodeDecoder.syncDecodeQRCode(obmp)
            if (hasQRCode != null && hasQRCode != "") {
                list.add("Scan QR Code in Image")
                runOnUiThread {
                    PopWindowUtil.showSelecMenuPopWindowNotice(list);
                }

            }
        }).start()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("websocket状态MainActivity:" + connectStatus.status)
        if (connectStatus.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        when (connectStatus.status) {
            0 -> {
                if(standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                ConstantValue.isHasWebsocketInit = true
                if(isFromScanAdmim)
                {
                    runOnUiThread {
                        closeProgressDialog()
                    }
                    gotoActivity(1)
                    /* var intent = Intent(this, AdminLoginActivity::class.java)
                     startActivity(intent)*/
                    /*closeProgressDialog()
                    showProgressDialog("wait...")
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))*/
                    isFromScanAdmim = false
//                    finish()
                }
                else if(isFromScan)
                {
                    runOnUiThread {
                        closeProgressDialog()
                        showProgressDialog("wait...")
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN,pulicMiKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,recovery))
                    isFromScan = false
                }else{
                    if(isClickLogin)
                    {
                        KLog.i("开始用websocket登录路由器")
                        loginBack = false
                        runOnUiThread {
                            showProgressDialog(getString(R.string.login_))
                        }
                        standaloneCoroutine = launch(CommonPool) {
                            delay(10000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                    gotoLogin()
                                }
                            }
                        }
//            standaloneCoroutine.cancel()

                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        KLog.i("没有初始化。。登录接口设置loginBackListener"+"##" +AppConfig.instance.name +"##"+this.name+"##"+AppConfig.instance.messageReceiver)
                        //AppConfig.instance.messageReceiver!!.loginBackListener = this
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
                    }

                }

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Network_error)
                }

            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息："+toxSendInfoEvent.info)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {

        if(toxFriendStatusEvent.status == 1)
        {

            ConstantValue.freindStatus = 1
            if(!threadInit)
            {
                Thread(Runnable() {
                    run() {

                        while (true)
                        {
                            if(ConstantValue.unSendMessage.size >0)
                            {
                                for (key in ConstantValue.unSendMessage.keys)
                                {
                                    var sendData = ConstantValue.unSendMessage.get(key)
                                    var friendId = ConstantValue.unSendMessageFriendId.get(key)
                                    var sendCount:Int = ConstantValue.unSendMessageSendCount.get(key) as Int
                                    if(sendCount < 5)
                                    {
                                        if (ConstantValue.isAntox) {
                                            var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, sendData, ToxMessageType.NORMAL)
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(sendData, friendId)
                                        }
                                        ConstantValue.unSendMessageSendCount.put(key,sendCount++)
                                    }else{
                                        closeProgressDialog()
                                        break
                                    }
                                }

                            }else{
                                closeProgressDialog()
                                break
                            }
                            Thread.sleep(2000)
                        }

                    }
                }).start()
                threadInit = true
            }


            LogUtil.addLog("P2P检测路由好友上线，可以发消息:","LoginActivityActivity")
        }else{
            ConstantValue.freindStatus = 0;
            LogUtil.addLog("P2P检测路由好友未上线，不可以发消息:","LoginActivityActivity")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopTox(stopTox: StopTox) {
        try {
            MessageHelper.clearAllMessage()
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        KLog.i("tox状态MainActivity:" + toxStatusEvent.status)
        if (toxStatusEvent.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        when (toxStatusEvent.status) {
            0 -> {
                KLog.i("P2P连接成功")
                LogUtil.addLog("P2P连接成功:","LoginActivityActivity")
                runOnUiThread {
                    KLog.i("444")
                    closeProgressDialog()
                    //toast("login time out")
                }
                ConstantValue.isToxConnected = true

                if(ConstantValue.curreantNetworkType.equals("TOX"))
                {
                    ConstantValue.isHasWebsocketInit = true
                    AppConfig.instance.getPNRouterServiceMessageReceiver()
                    KLog.i("没有初始化。。设置loginBackListener")
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                }
                if( stopTox ||  ConstantValue.curreantNetworkType.equals("WIFI"))
                    return
                if(isFromScan)
                {

                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend( ConstantValue.scanRouterId)
                    }
                    standaloneCoroutine = launch(CommonPool) {
                        delay(60000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                toast("time out")
                                gotoLogin()
                            }
                        }
                    }

                    runOnUiThread {
                        var tips = getString(R.string.login_)
                        if(ConstantValue.freindStatus == 1)
                        {
                            tips = "wait..."
                        }else{
                            tips = "circle connecting..."
                        }
                        showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if(standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                EventBus.getDefault().post(StopTox())
                                gotoLogin()
                                false
                            } else false
                        })
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
                    var baseData = BaseData(4,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                    ConstantValue.unSendMessage.put("recovery",baseDataJson)
                    ConstantValue.unSendMessageFriendId.put("recovery",ConstantValue.scanRouterId.substring(0, 64))
                    ConstantValue.unSendMessageSendCount.put("recovery",0)
                    //ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    isFromScan = false
                }else{
                    if (ConstantValue.isAntox) {
                        InterfaceScaleUtil.addFriend(routerId,this)
                    }else{
                        ToxCoreJni.getInstance().addFriend(routerId)
                    }
                    if(isClickLogin)
                    {
                        //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))
                        loginBack = false

                        standaloneCoroutine = launch(CommonPool) {
                            delay(60000)
                            if (!loginBack) {
                                runOnUiThread {
                                    closeProgressDialog()
                                    isloginOutTime = true
                                    toast("login time out")
                                    gotoLogin()
                                }
                            }
                        }
                        runOnUiThread {
                            var tips = getString(R.string.login_)
                            if(ConstantValue.freindStatus == 1)
                            {
                                tips = getString(R.string.login_)
                            }else{
                                tips = "Circle connecting..."
                            }
                            showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    if(standaloneCoroutine != null)
                                        standaloneCoroutine.cancel()
                                    EventBus.getDefault().post(StopTox())
                                    gotoLogin()
                                    false
                                } else false
                            })
                        }
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                        ConstantValue.loginReq = login
                        var baseData = BaseData(4,login)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        ConstantValue.unSendMessage.put("login",baseDataJson)
                        ConstantValue.unSendMessageFriendId.put("login",routerId.substring(0, 64))
                        ConstantValue.unSendMessageSendCount.put("login",0)
                        //ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        isClickLogin = false;
                    }

                }
            }
            1 -> {
                LogUtil.addLog("P2P连接中Reconnecting:","LoginActivityActivity")
            }
        }
    }
    fun initSwitchData() {


    }
    fun gotoLogin()
    {
        closeProgressDialog()
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        ConstantValue.isHasWebsocketInit = true
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.close()

        ConstantValue.loginOut = true
        ConstantValue.logining = false
        ConstantValue.currentRouterIp = ""
        if (ConstantValue.isWebsocketConnected) {
            FileMangerDownloadUtils.init()
            ConstantValue.webSockeFileMangertList.forEach {
                it.disconnect(true)
                //ConstantValue.webSockeFileMangertList.remove(it)
            }
            ConstantValue.webSocketFileList.forEach {
                it.disconnect(true)
                //ConstantValue.webSocketFileList.remove(it)
            }
        }else{
            val intentTox = Intent(AppConfig.instance, KotlinToxService::class.java)
            AppConfig.instance.stopService(intentTox)
        }
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        ConstantValue.hasLogin = false
        ConstantValue.isHeart = false
        resetUnCompleteFileRecode()
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(AppConfig.instance, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }
    private fun getServer(routerId:String ,userSn:String,startToxFlag:Boolean,autoLogin:Boolean)
    {
        ConstantValue.currentRouterIp = ""
        islogining = false
        runOnUiThread {
            KLog.i("777")
            closeProgressDialog()
            showProgressNoCanelDialog("Connecting...")
        }
        if(WiFiUtil.isWifiConnect())
        {
            var count =0;
            KLog.i("测试计时器" + count)
            Thread(Runnable() {
                run() {

                    while (true)
                    {
                        KLog.i("currentRouterIp== " + ConstantValue.currentRouterIp)
                        if(count >=3)
                        {
                            //如果本地收到广播了，这个 currentRouterIp 肯定有值了。
                            if(!ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoInner
                                KLog.i("走本地：" + ConstantValue.currentRouterIp)
                                var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                {
                                    runOnUiThread {
                                        startLogin()
                                    }

                                }
                                Thread.currentThread().interrupt(); //方法调用终止线程
                                break;
                            }else{
                                // 通过http看是否有远程的路由器可以登录
                                KLog.i("通过http看是否有远程的路由器可以登录")
                                OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + routerId,  object : OkHttpUtils.OkCallback {
                                    override fun onFailure( e :Exception) {
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                    }

                                    override fun  onResponse(json:String ) {

                                        val gson = GsonUtil.getIntGson()
                                        var httpData: HttpData? = null
                                        try {
                                            if (json != null) {
                                                httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                                KLog.i("http的返回为：" + httpData.toString())
                                                if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                                {
                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                    ConstantValue.currentRouterIp = httpData.serverHost
                                                    ConstantValue.port = ":"+httpData.serverPort.toString()
                                                    ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                    ConstantValue.currentRouterId = routerId
                                                    ConstantValue.currentRouterSN =  userSn
                                                    ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoOuterNet
                                                    KLog.i("走远程：这个远程websocket如果连不上，会一直重连下去" + ConstantValue.currentRouterIp+ConstantValue.port)
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                                    {
                                                        runOnUiThread {
                                                            startLogin()
                                                        }

                                                    }
                                                    Thread.currentThread().interrupt() //方法调用终止线程
                                                }else{
                                                    //没有远程，开启tox
                                                    KLog.i("没有远程，开启tox")
                                                    startTox(startToxFlag)
                                                    var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                                    if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                                    {
                                                        runOnUiThread {
                                                            startLogin()
                                                        }

                                                    }
                                                    Thread.currentThread().interrupt(); //方法调用终止线程
                                                }

                                            }
                                        } catch (e: Exception) {
                                            startTox(startToxFlag)
                                            var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                            if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                            {
                                                runOnUiThread {
                                                    startLogin()
                                                }

                                            }
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                        }
                                    }
                                })
                                break
                            }

                        }
                        // 走广播，本地的路由器
                        count ++;
                        MobileSocketClient.getInstance().init(handler,this)
                        var toxIdMi = AESCipher.aesEncryptString(routerId,"slph\$%*&^@-78231")
                        MobileSocketClient.getInstance().destroy()
                        MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                        MobileSocketClient.getInstance().receive()
                        KLog.i("测试计时器" + count)
                        Thread.sleep(1000)
                    }

                }
            }).start()
        }else{

            Thread(Runnable() {
                run() {

                    OkHttpUtils.getInstance().doGet(ConstantValue.httpUrl + routerId,  object : OkHttpUtils.OkCallback {
                        override fun onFailure( e :Exception) {
                            startTox(startToxFlag)
                            var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                            if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                            {
                                runOnUiThread {
                                    startLogin()
                                }

                            }
                            Thread.currentThread().interrupt(); //方法调用终止线程
                        }

                        override fun  onResponse(json:String ) {

                            val gson = GsonUtil.getIntGson()
                            var httpData: HttpData? = null
                            try {
                                if (json != null) {
                                    var  httpData = gson.fromJson<HttpData>(json, HttpData::class.java)
                                    if(httpData != null  && httpData.retCode == 0 && httpData.connStatus == 1)
                                    {
                                        ConstantValue.curreantNetworkType = "WIFI"
                                        ConstantValue.currentRouterIp = httpData.serverHost
                                        ConstantValue.port = ":"+httpData.serverPort.toString()
                                        ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                        ConstantValue.currentRouterId = routerId
                                        ConstantValue.currentRouterSN =  userSn
                                        ConstantValue.sendFileSizeMax = ConstantValue.sendFileSizeMaxoOuterNet
                                        KLog.i("走远程：" + ConstantValue.currentRouterIp+ConstantValue.port)
                                        /* AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                         AppConfig.instance.messageReceiver!!.loginBackListener = this*/
                                        runOnUiThread {
                                            KLog.i("555")
//                                            standaloneCoroutine.cancel()
//                                            closeProgressDialog()
                                        }
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt() //方法调用终止线程
                                    }else{
                                        startTox(startToxFlag)
                                        var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                        if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                        {
                                            runOnUiThread {
                                                startLogin()
                                            }

                                        }
                                        Thread.currentThread().interrupt(); //方法调用终止线程
                                    }

                                }
                            } catch (e: Exception) {
                                startTox(startToxFlag)
                                var autoLoginRouterSn = SpUtil.getString(AppConfig.instance, ConstantValue.autoLoginRouterSn, "")
                                if(!autoLoginRouterSn.equals("") && !isStartLogin || autoLogin)
                                {
                                    runOnUiThread {
                                        startLogin()
                                    }

                                }
                                Thread.currentThread().interrupt(); //方法调用终止线程
                            }
                        }
                    })
                }
            }).start()

        }
    }
    private fun startTox(startToxFlag:Boolean)
    {
        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if(!ConstantValue.isToxConnected && startToxFlag)
        {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        gotoLogin()
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:","LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        }else{
            runOnUiThread {
                KLog.i("666")
                closeProgressDialog()
            }
        }
    }
    private fun startLogin()
    {

        isloginOutTime = false
        isStartLogin = true
        if(!ConstantValue.lastNetworkType.equals(""))
        {
            isFromScan = false
            ConstantValue.curreantNetworkType = ConstantValue.lastNetworkType
            ConstantValue.currentRouterIp = ConstantValue.lastRouterIp
            ConstantValue.port = ConstantValue.lastPort
            ConstantValue.filePort = ConstantValue.lastFilePort
            ConstantValue.currentRouterId = ConstantValue.lastRouterId
            ConstantValue.currentRouterSN =  ConstantValue.lastRouterSN
            ConstantValue.lastRouterId=""
            ConstantValue.lastPort=""
            ConstantValue.lastFilePort=""
            ConstantValue.lastRouterId=""
            ConstantValue.lastRouterSN=""
            ConstantValue.lastNetworkType =""
        }
        /* if (loginKey.text.toString().equals("")) {
             toast(getString(R.string.please_type_your_password))
             return
         }*/
        if( ConstantValue.curreantNetworkType.equals("TOX"))
        {

            if(ConstantValue.isToxConnected)
            {
                isToxLoginOverTime = true
                //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))

                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                var baseData = BaseData(4,login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                KLog.i("没有初始化。。设置loginBackListener")
                //AppConfig.instance.messageReceiver!!.loginBackListener = this
                standaloneCoroutine = launch(CommonPool) {
                    delay(60000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                            gotoLogin()
                        }
                    }
                }
                runOnUiThread {
                    var tips = getString(R.string.login_)
                    if(ConstantValue.freindStatus == 1)
                    {
                        tips = getString(R.string.login_)
                    }else{
                        tips = "Circle connecting..."
                    }
                    showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if(standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            EventBus.getDefault().post(StopTox())
                            gotoLogin()
                            false
                        } else false
                    })
                }
                if (ConstantValue.isAntox) {
                    var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }else{
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                }
            }else{
//                    if (!ConstantValue.isToxConnected) {
//                        loadLibrary()
//                    }
                isToxLoginOverTime = true
                isClickLogin = true
                stopTox = false
                ConstantValue.curreantNetworkType = "TOX"
                runOnUiThread {
                    showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            stopTox = true
                            gotoLogin()
                            false
                        } else false
                    })
                }
                LogUtil.addLog("P2P启动连接:","LoginActivityActivity")

                if(ConstantValue.isAntox)
                {
                    var intent = Intent(AppConfig.instance, ToxService::class.java)
                    startService(intent)
                }else{
                    var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
                    startService(intent)
                }

            }

        }else{
            isClickLogin = true
            if(!ConstantValue.isWebsocketConnected)
            {
                if (intent.hasExtra("flag")) {
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener")
                    //AppConfig.instance.getPNRouterServiceMessageReceiver().loginBackListener = this
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                } else {
                    KLog.i("走不带flag")
                    if(ConstantValue.isHasWebsocketInit)
                    {
                        KLog.i("已经初始化了，走重连逻辑")
                        KLog.i("已经初始化了。。走重连逻辑" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true).reConnect()
                    }else{
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener前" +this+ "##" +AppConfig.instance.name)
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!!.loginBackListener +"##" +AppConfig.instance.name)
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!! + "##" +AppConfig.instance.name)
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if(AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                }
            }else{
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() /1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate  = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg,signed_msg_len,sign,sign.size,mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId,userSn, userId,signBase64, dataFileVersion,NickName)
                ConstantValue.loginReq = login
                standaloneCoroutine = launch(CommonPool) {
                    delay(10000)
                    if (!loginBack) {
                        runOnUiThread {
                            closeProgressDialog()
                            isloginOutTime = true
                            toast("login time out")
                            gotoLogin()
                        }
                    }
                }
                runOnUiThread {
                    showProgressDialog(getString(R.string.login_))
                }
                KLog.i("没有初始化。。设置loginBackListener")
                //AppConfig.instance.messageReceiver!!.loginBackListener = this
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4,login))
            }
        }

    }
    fun gotoActivity(index:Int)
    {
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        ConstantValue.isHasWebsocketInit = true

        if (ConstantValue.isWebsocketConnected) {
            FileMangerDownloadUtils.init()
            ConstantValue.webSockeFileMangertList.forEach {
                it.disconnect(true)
                //ConstantValue.webSockeFileMangertList.remove(it)
            }
            ConstantValue.webSocketFileList.forEach {
                it.disconnect(true)
                //ConstantValue.webSocketFileList.remove(it)
            }
        }else{
            val intentTox = Intent(AppConfig.instance, KotlinToxService::class.java)
            AppConfig.instance.stopService(intentTox)
        }
        resetUnCompleteFileRecode()
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        when(index)
        {
            0->{
                var intent = Intent(AppConfig.instance, LoginActivityActivity::class.java)
                intent.putExtra("flag", "logout")
                startActivity(intent)
            }
            1->
            {
                ConstantValue.loginOut = true
                ConstantValue.logining = false
                ConstantValue.isHeart = false
                var intent = Intent(this, AdminLoginActivity::class.java)
                startActivity(intent)
            }

        }

        finish()
    }
    fun resetUnCompleteFileRecode() {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList) {
            if (myFie.upLoadFile.isComplete == false) {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.isStop = "1"
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
    }
    /**
     * download image
     *
     * @param
     */
    @SuppressLint("NewApi")
    private fun downloadImage(msgId: String) {
        EMLog.e(TAG, "download with messageId: $msgId")
        val str1 = resources.getString(R.string.Download_the_pictures)
        pd = ProgressDialog(this)
        pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd!!.setCanceledOnTouchOutside(false)
        pd!!.setMessage(str1)
        pd!!.show()
        val temp = File(localFilePath!!)
        val tempPath = temp.parent + "/temp_" + temp.name
        val callback = object : EMCallBack {
            override fun onSuccess() {
                EMLog.e(TAG, "onSuccess")
                runOnUiThread(Runnable {
                    File(tempPath).renameTo(File(localFilePath!!))

                    val metrics = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(metrics)
                    val screenWidth = metrics.widthPixels
                    val screenHeight = metrics.heightPixels

                    bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight)
                    if (bitmap == null) {
                        image!!.setImageResource(default_res)
                    } else {
                        val degree = EaseImageUtils.readPictureDegree(localFilePath)
                        if (degree != 0) {
                            val bmpOk = EaseImageUtils.rotateToDegrees(bitmap, degree.toFloat())
                            image!!.setImageBitmap(bmpOk)
                            EaseImageCache.getInstance().put(localFilePath, bmpOk)
                        } else {
                            image!!.setImageBitmap(bitmap)
                            EaseImageCache.getInstance().put(localFilePath, bitmap)
                        }

                        isDownloaded = true
                    }
                    if (isFinishing || isDestroyed) {
                        return@Runnable
                    }
                    if (pd != null) {
                        pd!!.dismiss()
                    }
                })
            }

            override fun onError(error: Int, msg: String) {
                EMLog.e(TAG, "offline file transfer error:$msg")
                val file = File(tempPath)
                if (file.exists() && file.isFile) {
                    file.delete()
                }
                runOnUiThread(Runnable {
                    if (this@EaseShowBigImageActivity.isFinishing || this@EaseShowBigImageActivity.isDestroyed) {
                        return@Runnable
                    }
                    image!!.setImageResource(default_res)
                    pd!!.dismiss()
                })
            }

            override fun onProgress(progress: Int, status: String) {
                EMLog.d(TAG, "Progress: $progress")
                val str2 = resources.getString(R.string.Download_the_pictures_new)
                runOnUiThread(Runnable {
                    if (this@EaseShowBigImageActivity.isFinishing || this@EaseShowBigImageActivity.isDestroyed) {
                        return@Runnable
                    }
                    pd!!.setMessage("$str2$progress%")
                })
            }
        }

        val msg = EMClient.getInstance().chatManager().getMessage(msgId)
        msg.setMessageStatusCallback(callback)

        EMLog.e(TAG, "downloadAttachement")
        EMClient.getInstance().chatManager().downloadAttachment(msg)
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            if (isDownloaded)
                setResult(Activity.RESULT_OK)
            finish()
        }
    }
    private fun startToxAndRecovery() {

        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if (!ConstantValue.isToxConnected) {
            runOnUiThread {
                showProgressDialog("p2p connecting...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        stopTox = true
                        gotoLogin()
                        false
                    } else false
                })
            }
            LogUtil.addLog("P2P启动连接:", "LoginActivityActivity")
            var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
            if(ConstantValue.isAntox)
            {
                intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        } else {
            //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
            runOnUiThread {
                showProgressDialog("wait...", DialogInterface.OnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        EventBus.getDefault().post(StopTox())
                        gotoLogin()
                        false
                    } else false
                })
            }
            KLog.i("没有初始化。。设置loginBackListener")
            //AppConfig.instance.messageReceiver!!.loginBackListener = this
            if (ConstantValue.isAntox) {
                InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
            }else{
                ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
            }
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
            var baseData = BaseData(4, recovery)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
            }
        }
    }
    fun showProgressDialog(text: String) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setOnTouchOutside(false)
    }
    fun showProgressDialog(text: String, onKeyListener: DialogInterface.OnKeyListener) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setCanceledOnBack(false, onKeyListener)
    }
    fun showProgressNoCanelDialog(text: String) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setNoCanceledOnTouchOutside(false)
    }
    fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
//        reRegesterMiPush()
        KLog.i("onDestroy")
        if (handler != null) {
            handler?.removeCallbacksAndMessages(null)
        }
        if (handler != null) {
            handler = null
        }
        AppConfig.instance.messageReceiver?.bigImageBack = null
        EventBus.getDefault().unregister(this)
//        exitToast()
        super.onDestroy()
    }
    companion object {
        private val TAG = "ShowBigImage"
    }
}
