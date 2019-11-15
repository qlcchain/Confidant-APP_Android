package com.stratagile.pnrouter.ui.activity.main

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.BADGE_ICON_SMALL
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import cn.jpush.android.api.JPushInterface
import com.alibaba.fastjson.JSONObject
import com.stratagile.pnrouter.screencapture.FloatWindowsService
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.gson.Gson
import com.huawei.android.hms.agent.HMSAgent
import com.huawei.android.hms.agent.common.handler.ConnectHandler
import com.huxq17.floatball.libarary.FloatBallManager
import com.huxq17.floatball.libarary.floatball.FloatBallCfg
import com.huxq17.floatball.libarary.menu.FloatMenuCfg
import com.huxq17.floatball.libarary.menu.MenuItem
import com.huxq17.floatball.libarary.utils.BackGroudSeletor
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.PathUtils
import com.hyphenate.util.DensityUtil
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.message.Message
import com.message.MessageProvider
import com.pawegio.kandroid.notificationManager
import com.pawegio.kandroid.runDelayed
import com.floatball.FloatPermissionManager
import com.pawegio.kandroid.setHeight
import com.pawegio.kandroid.toast
import com.smailnet.eamil.Callback.GmailAuthCallback
import com.smailnet.eamil.EmailCount
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.eamil.Utils.AESCipher
import com.smailnet.islands.Islands
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.data.service.BackGroundService
import com.stratagile.pnrouter.data.service.FileDownloadUploadService
import com.stratagile.pnrouter.data.service.FileTransformService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.*
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.gmail.GmailQuickstart
import com.stratagile.pnrouter.reciver.WinqMessageReceiver
import com.stratagile.pnrouter.ui.activity.add.addFriendOrGroupActivity
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.email.EmailChooseActivity
import com.stratagile.pnrouter.ui.activity.email.EmailEditActivity
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.ui.activity.file.FileSendShareActivity
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.group.CreateGroupActivity
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.login.VerifyingFingerprintActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.activity.user.QRCodeActivity
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiConfigChooseAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.ActiveTogglePopWindow
import com.stratagile.pnrouter.view.CustomPopWindow
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import com.tencent.bugly.crashreport.CrashReport
import com.xiaomi.mipush.sdk.MiPushClient
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View, PNRouterServiceMessageReceiver.MainInfoBack, MessageProvider.MessageListener, ActiveTogglePopWindow.OnItemClickListener, PNRouterServiceMessageReceiver.BakMailsNumCallback {
    override fun sysMsgPushRsp(jSysMsgPushRsp: JSysMsgPushRsp) {

    }

    override fun BakMailsNumBack(JBakMailsNumRsp: JBakMailsNumRsp) {
        if (JBakMailsNumRsp.params.retCode == 0) {
            runOnUiThread {
                if (nodebackedup != null) {
                    nodebackedup.setCount(JBakMailsNumRsp.params.num)
                }

            }

        }

    }

    companion object {
        var isForeground = false
        val MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION"
        val KEY_MESSAGE = "message"
        val KEY_EXTRAS = "extras"
        const val REQUEST_ACCOUNT_PICKER = 1000
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
        var isGmailToken = false;
    }
    var REQUEST_MEDIA_PROJECTION = 1008
    private lateinit var standaloneCoroutine: Job
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
    private var loginGoMain: Boolean = false
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
    var adminUserSn: String? = null
    var hasFinger = false
    var name: Long = 0;
    var emaiConfigChooseAdapter: EmaiConfigChooseAdapter? = null

    private var exitTime: Long = 0
    lateinit var viewModel: MainViewModel
    private var conversationListFragment: EaseConversationListFragment? = null
    private var chatAndEmailFragment: ChatAndEmailFragment? = null
    private var contactFragment: ContactFragment? = null
    private var isAddEmail = true
    var this_: Activity? = null;
    var routerEntityAddMembers: RouterEntity? = null

    private var mFloatballManager: FloatBallManager? = null
    private var mFloatPermissionManager: FloatPermissionManager? = null
    private val mActivityLifeCycleListener = ActivityLifeCycleListener()
    private var resumed: Int = 0


    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null

    private var mResultData: Intent? = null


    private var mImageReader: ImageReader? = null
    private var mWindowManager: WindowManager? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mGestureDetector: GestureDetector? = null


    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mScreenDensity: Int = 0

    override fun registerBack(registerRsp: JRegisterRsp) {
        if (!isScanSwitch) {

            return;
        }
        runOnUiThread {
            closeProgressDialog()
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
            ConstantValue.isNewUser = true;
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
            var localData: java.util.ArrayList<MyRouter> = LocalRouterUtils.localAssetsList
            newRouterEntity.routerName = String(RxEncodeTool.base64Decode(registerRsp.params!!.routerName))
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            var sign = ByteArray(32)
            var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
            System.arraycopy(time, 0, sign, 0, time.size)
            var dst_signed_msg = ByteArray(96)
            var signed_msg_len = IntArray(1)
            var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
            var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
            var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
            val NickName = RxEncodeTool.base64Encode2String(ConstantValue.localUserName!!.toByteArray())
            //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
            //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
            var login = LoginReq_V4(registerRsp.params.routeId, registerRsp.params.userSn, registerRsp.params.userId, signBase64, registerRsp.params.dataFileVersion, NickName)
            islogining = true
            ConstantValue.loginReq = login
            if (ConstantValue.isWebsocketConnected) {

                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, login))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4, login)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(registerRsp.params.routeId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
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
        if (!isScanSwitch) {
            return;
        }
        runOnUiThread {
            closeProgressDialog()
        }
        islogining = false
        ConstantValue.unSendMessage.remove("login")
        ConstantValue.unSendMessageFriendId.remove("login")
        ConstantValue.unSendMessageSendCount.remove("login")
        KLog.i(loginRsp.toString())
        LogUtil.addLog("loginBack:" + loginRsp.params.retCode, "LoginActivityActivity")
        if (standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        if (loginRsp.params.retCode != 0) {
            if (loginRsp.params.retCode == 1) {
                runOnUiThread {
                    toast(R.string.need_Verification)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else if (loginRsp.params.retCode == 2) {
                runOnUiThread {
                    toast(R.string.rid_error)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else if (loginRsp.params.retCode == 3) {
                runOnUiThread {
                    toast(R.string.uid_error)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else if (loginRsp.params.retCode == 4) {
                runOnUiThread {
                    toast(R.string.Validation_failed)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else if (loginRsp.params.retCode == 5) {
                runOnUiThread {
                    toast(R.string.Verification_code_error)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else if (loginRsp.params.retCode == 7) {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                    closeProgressDialog()
                    gotoLogin()
                }
            } else {
                runOnUiThread {
                    toast(R.string.other_error)
                    closeProgressDialog()
                    gotoLogin()
                }
            }
            return
        }
        if ("".equals(loginRsp.params.userId)) {
            runOnUiThread {
                toast(R.string.userId_is_empty)
                closeProgressDialog()
                gotoLogin()
            }
        } else {
            islogining = false
            ConstantValue.loginOut = false
            ConstantValue.logining = true
            LogUtil.addLog("loginBack:" + "begin", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userId, "userid")
            //FileUtil.saveUserData2Local(loginRsp.params!!.index,"userIndex")
            LogUtil.addLog("loginBack:" + "a", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.userSn, "usersn")
            LogUtil.addLog("loginBack:" + "b", "LoginActivityActivity")
            FileUtil.saveUserData2Local(loginRsp.params!!.routerid, "routerid")
            LogUtil.addLog("loginBack:" + "c", "LoginActivityActivity")
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
            if (loginRsp.params.nickName != null)
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
            LogUtil.addLog("loginBack:" + "d", "LoginActivityActivity")
            var needUpdate: java.util.ArrayList<MyRouter> = java.util.ArrayList();
            routerList.forEach {
                it.lastCheck = false
                var myRouter: MyRouter = MyRouter();
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
            newRouterEntity.dataFilePay = ""
            newRouterEntity.adminId = loginRsp.params!!.adminId
            newRouterEntity.adminName = loginRsp.params!!.adminName
            newRouterEntity.adminKey = loginRsp.params!!.adminKey
            ConstantValue.currentRouterSN = loginRsp.params!!.userSn
            ConstantValue.isCurrentRouterAdmin = loginRsp.params!!.userSn.indexOf("01") == 0
            if (contains) {
                KLog.i("数据局中已经包含了这个userSn")
                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(newRouterEntity)
            } else {

                AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
            }
            LogUtil.addLog("loginBack:" + "e", "LoginActivityActivity")
            //更新sd卡路由器数据begin
            val myRouter = MyRouter()
            myRouter.setType(0)
            myRouter.setRouterEntity(newRouterEntity)
            LocalRouterUtils.insertLocalAssets(myRouter)
            runOnUiThread {
                closeProgressDialog()
                KLog.i("333")
            }
            LogUtil.addLog("loginBack:" + "f", "LoginActivityActivity")
            loginOk = true
            isToxLoginOverTime = false
            ConstantValue.hasLogin = true
            ConstantValue.isHeart = true
            resetUnCompleteFileRecode()
            /* if(loginGoMain)
                 return*/
            isScanSwitch = false
            /*startActivity(Intent(this, MainActivity::class.java))
            loginGoMain  = true*/
            initSwitchData()
            LogUtil.addLog("loginBack:" + "g", "LoginActivityActivity")
            //finish()
        }
    }

    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {
        if (!isScanSwitch) {

            return;
        }
        runOnUiThread {
            closeProgressDialog()
        }
        if (standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        KLog.i("222")
        ConstantValue.unSendMessage.remove("recovery")
        ConstantValue.unSendMessageFriendId.remove("recovery")
        ConstantValue.unSendMessageSendCount.remove("recovery")
        when (recoveryRsp.params.retCode) {
            0 -> {
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
                var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(ConstantValue.localUserName!!.toByteArray())
                //var LoginKeySha = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var login = LoginReq(  registerRsp.params.routeId,registerRsp.params.userSn, registerRsp.params.userId,LoginKeySha, registerRsp.params.dataFileVersion)
                var login = LoginReq_V4(recoveryRsp.params.routeId, recoveryRsp.params.userSn, recoveryRsp.params.userId, signBase64, recoveryRsp.params.dataFileVersion, NickName)
                islogining = true
                ConstantValue.loginReq = login
                if (ConstantValue.isWebsocketConnected) {

                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, login))
                } else if (ConstantValue.isToxConnected) {
                    var baseData = BaseData(4, login)
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

                val NickName = RxEncodeTool.base64Encode2String(ConstantValue.localUserName!!.toByteArray())
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                var regeister = RegeisterReq_V4(recoveryRsp.params.routeId, recoveryRsp.params.userSn, signBase64, pulicMiKey, NickName)
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, regeister))
                } else if (ConstantValue.isToxConnected) {
                    var baseData = BaseData(4, regeister)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        //var friendKey: FriendKey = FriendKey(recoveryRsp.params.routeId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    } else {
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                    }
                }
            }
            2 -> {
                runOnUiThread {
                    toast(R.string.rid_error)
                    gotoLogin()
                }

            }
            3 -> {
                ConstantValue.lastNetworkType = "";
                /*val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
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
                }else{*/
                /*  AppConfig.instance.messageReceiver!!.loginBackListener = null
                  var intent = Intent(this, RegisterActivity::class.java)
                  intent.putExtra("flag", 1)
                  startActivity(intent)*/
                runOnUiThread {
                    showProgressDialog("waiting...")
                }

                val NickName = RxEncodeTool.base64Encode2String(ConstantValue.localUserName!!.toByteArray())
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                //var LoginKey = RxEncryptTool.encryptSHA256ToString(userName3.text.toString())
                //var regeister = RegeisterReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN, IdentifyCode.text.toString(),LoginKey,NickName)
                var regeister = RegeisterReq_V4(recoveryRsp.params.routeId, recoveryRsp.params.userSn, signBase64, pulicMiKey, NickName)
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, regeister))
                } else if (ConstantValue.isToxConnected) {
                    var baseData = BaseData(4, regeister)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        //var friendKey: FriendKey = FriendKey(recoveryRsp.params.routeId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    } else {
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, recoveryRsp.params.routeId.substring(0, 64))
                    }
                }
                //}

            }
            4 -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
            5 -> {
                runOnUiThread {
                    toast(R.string.The_QR_code_has_been_occupied_by_others)
                }
            }
            6 -> {
                runOnUiThread {
                    toast(R.string.The_account_has_expired)
                }
            }
            else -> {
                runOnUiThread {
                    toast(R.string.other_error)
                }
            }
        }
    }

    private fun startTox(startToxFlag: Boolean) {
        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if (!ConstantValue.isToxConnected && startToxFlag) {
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
            if (ConstantValue.isAntox) {
                //intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        } else {
            runOnUiThread {
                KLog.i("666")
                closeProgressDialog()
            }
        }
    }

    private fun startLogin() {

        isloginOutTime = false
        isStartLogin = true
        if (!ConstantValue.lastNetworkType.equals("")) {
            isFromScan = false
            ConstantValue.curreantNetworkType = ConstantValue.lastNetworkType
            ConstantValue.currentRouterIp = ConstantValue.lastRouterIp
            ConstantValue.port = ConstantValue.lastPort
            ConstantValue.filePort = ConstantValue.lastFilePort
            ConstantValue.currentRouterId = ConstantValue.lastRouterId
            ConstantValue.currentRouterSN = ConstantValue.lastRouterSN
            ConstantValue.lastRouterId = ""
            ConstantValue.lastPort = ""
            ConstantValue.lastFilePort = ""
            ConstantValue.lastRouterId = ""
            ConstantValue.lastRouterSN = ""
            ConstantValue.lastNetworkType = ""
        }
        /* if (loginKey.text.toString().equals("")) {
             toast(getString(R.string.please_type_your_password))
             return
         }*/
        if (ConstantValue.curreantNetworkType.equals("TOX")) {

            if (ConstantValue.isToxConnected) {
                isToxLoginOverTime = true
                //var friendKey:FriendKey = FriendKey(routerId.substring(0, 64))

                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId, userSn, userId, signBase64, dataFileVersion, NickName)
                ConstantValue.loginReq = login
                var baseData = BaseData(4, login)
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
                    if (ConstantValue.freindStatus == 1) {
                        tips = getString(R.string.login_)
                    } else {
                        tips = "Circle connecting..."
                    }
                    showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            EventBus.getDefault().post(StopTox())
                            gotoLogin()
                            false
                        } else false
                    })
                }
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                }
            } else {
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
                LogUtil.addLog("P2P启动连接:", "LoginActivityActivity")

                if (ConstantValue.isAntox) {
                    /* var intent = Intent(AppConfig.instance, ToxService::class.java)
                    startService(intent)*/
                } else {
                    var intent = Intent(AppConfig.instance, KotlinToxService::class.java)
                    startService(intent)
                }

            }

        } else {
            isClickLogin = true
            if (!ConstantValue.isWebsocketConnected) {
                if (intent.hasExtra("flag")) {
                    if (ConstantValue.isHasWebsocketInit) {
                        KLog.i("已经初始化了，走重连逻辑")
                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                    } else {
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
                                if (AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                } else {
                    KLog.i("走不带flag")
                    if (ConstantValue.isHasWebsocketInit) {
                        KLog.i("已经初始化了，走重连逻辑")
                        KLog.i("已经初始化了。。走重连逻辑" + this + "##" + AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true).reConnect()
                    } else {
                        KLog.i("没有初始化。。")
                        ConstantValue.isHasWebsocketInit = true
                        KLog.i("没有初始化。。设置loginBackListener前" + this + "##" + AppConfig.instance.messageReceiver)
                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    }
                    KLog.i("没有初始化。。设置loginBackListener前" + this + "##" + AppConfig.instance.name)
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!!.loginBackListener + "##" + AppConfig.instance.name)
                    KLog.i("没有初始化。。设置loginBackListener 后" + AppConfig.instance.messageReceiver!! + "##" + AppConfig.instance.name)
                    standaloneCoroutine = launch(CommonPool) {
                        delay(6000)
                        runOnUiThread {
                            closeProgressDialog()
                            if (!ConstantValue.isWebsocketConnected) {
                                if (AppConfig.instance.messageReceiver != null)
                                    AppConfig.instance.messageReceiver!!.close()
                                toast("Server connection timeout")
                                gotoLogin()
                            }
                        }
                    }
                }
            } else {
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var sign = ByteArray(32)
                var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                System.arraycopy(time, 0, sign, 0, time.size)
                var dst_signed_msg = ByteArray(96)
                var signed_msg_len = IntArray(1)
                var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                var login = LoginReq_V4(routerId, userSn, userId, signBase64, dataFileVersion, NickName)
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
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, login))
            }
        }

    }

    override fun groupListPull(jGroupListPullRsp: JGroupListPullRsp) {
        when (jGroupListPullRsp.params.retCode) {
            0 -> {
                //AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.deleteAll()
                for (item in jGroupListPullRsp.params.payload) {
                    var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(item.gId)).list()
                    if (groupList.size > 0) {
                        var GroupLocal = groupList.get(0)
                        GroupLocal.userKey = item.userKey
                        GroupLocal.remark = item.remark
                        GroupLocal.gId = item.gId
                        GroupLocal.gAdmin = item.gAdmin
                        GroupLocal.gName = item.gName
                        GroupLocal.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.update(GroupLocal);
                    } else {
                        item.routerId = ConstantValue.currentRouterId
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.insert(item);
                    }

                }
            }
        }
    }

    override fun droupSysPushRsp(jGroupSysPushRsp: JGroupSysPushRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(jGroupSysPushRsp.params.gId)) {
            KLog.i("已经在群聊天窗口了，不处理该条数据！")
        } else {
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgData = GroupSysPushRsp(0, userId!!)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, msgData, jGroupSysPushRsp.msgid))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(4, msgData, jGroupSysPushRsp.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

            when (jGroupSysPushRsp.params.type) {

                1 -> {

                }
                2 -> {

                }
                3, 4 -> {
                    var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    val keyMap = SpUtil.getAll(AppConfig.instance)
                    for (key in keyMap.keys) {

                        if (key.contains(ConstantValue.message) && key.contains(userId!! + "_")) {
                            val tempkey = key.replace(ConstantValue.message, "")
                            val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                            if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null" && toChatUserId.equals(jGroupSysPushRsp.params.gId)) {
                                val cachStr = SpUtil.getString(AppConfig.instance, key, "")
                                if ("" != cachStr) {
                                    val gson = GsonUtil.getIntGson()
                                    val MessageLocal = gson.fromJson(cachStr, Message::class.java)
                                    if (MessageLocal.from.equals(jGroupSysPushRsp.params.from)) {
                                        var gson = Gson()
                                        var Message = Message()
                                        Message.setMsg(resources.getString(R.string.withdrawn))
                                        Message.setMsgId(jGroupSysPushRsp.getParams().getMsgId())
                                        Message.setFrom(jGroupSysPushRsp.getParams().from)
                                        Message.setTo(jGroupSysPushRsp.getParams().gId)
                                        Message.msgType = 0
                                        Message.sender = 1
                                        Message.status = 2
                                        Message.timeStamp = jGroupSysPushRsp?.timestamp
                                        Message.msgId = jGroupSysPushRsp?.params.msgId
                                        Message.chatType = EMMessage.ChatType.GroupChat
                                        var unReadCount = MessageLocal.unReadCount
                                        if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                            unReadCount = MessageLocal.unReadCount
                                        }
                                        if (unReadCount > 0) {
                                            Message.unReadCount = unReadCount - 1;
                                        } else {
                                            Message.unReadCount = 0;
                                        }

                                        var baseDataJson = gson.toJson(Message)
                                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                        SpUtil.putString(AppConfig.instance, key, baseDataJson)
                                        if (ConstantValue.isInit) {
                                            runOnUiThread {
                                                var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
                                                if (Message.unReadCount > 0) {
                                                    UnReadMessageCount = UnReadMessageCount(1)
                                                }
                                                controlleMessageUnReadCount(UnReadMessageCount)
                                            }
                                            if (isAddEmail) {
                                                chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                                            } else {
                                                conversationListFragment?.refresh()
                                            }

                                            ConstantValue.isRefeshed = true
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                241 -> {

                }
                242 -> {
                    if (jGroupSysPushRsp.params.from.equals(userId))//如果是自己
                    {
                        var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(jGroupSysPushRsp.params.gId)).list()
                        if (groupList.size > 0) {
                            var GroupLocal = groupList.get(0)
                            AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.delete(GroupLocal);
                        }
                        //需要细化处理 ，弹窗告知详情等
                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jGroupSysPushRsp.params.gId, "");//移除临时会话UI
                    }
                }
                243 -> {//有人被移除群
                    if (jGroupSysPushRsp.params.to.equals(userId))//如果是自己
                    {
                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var verifyList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.queryBuilder().where(GroupVerifyEntityDao.Properties.Aduit.eq(selfUserId)).list()
                        var hasVerify = false
                        verifyList.forEach {
                            if (it.gId.equals(jGroupSysPushRsp.params.gId) && it.userId.equals(selfUserId) && it.verifyType == 3) {
                                //存在
                                hasVerify = true
                                it.userId = selfUserId
                                it.verifyType = 3
                                AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.update(it)
                            }
                        }
                        if (!hasVerify) {
                            var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(jGroupSysPushRsp.params.gId)).list()
                            var groupVerifyEntity = GroupVerifyEntity()
                            groupVerifyEntity.verifyType = 3
                            groupVerifyEntity.from = jGroupSysPushRsp.params.from
                            groupVerifyEntity.gId = jGroupSysPushRsp.params.gId
                            groupVerifyEntity.userId = selfUserId
                            if (groupList != null && groupList.size > 0) {
                                groupVerifyEntity.gname = groupList[0].gName
                            }
                            groupVerifyEntity.fromUserName = jGroupSysPushRsp.params.fromUserName
                            AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.insert(groupVerifyEntity)
                        }
                        var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(jGroupSysPushRsp.params.gId)).list()
                        if (groupList.size > 0) {
                            var GroupLocal = groupList.get(0)
                            AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.delete(GroupLocal);
                        }
                        //需要细化处理 ，弹窗告知详情等
                        SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jGroupSysPushRsp.params.gId, "");//移除临时会话UI
                        if (ConstantValue.isInit) {
                            runOnUiThread {
                                if (isAddEmail) {
                                    chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                                } else {
                                    conversationListFragment?.refresh()
                                }

                            }
                            ConstantValue.isRefeshed = true
                        }
                    } else {//是别人

                    }
                }
                244 -> {//群主解散
                    var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(jGroupSysPushRsp.params.gId)).list()
                    if (groupList.size > 0) {
                        var GroupLocal = groupList.get(0)
                        AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.delete(GroupLocal);
                    }
                    //需要细化处理 ，弹窗告知详情等
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jGroupSysPushRsp.params.gId, "");//移除临时会话UI
                    runOnUiThread {

                        toast(R.string.Group_disbanded)
                        if (isAddEmail) {
                            chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                        } else {
                            conversationListFragment?.refresh()
                        }

                    }
                }
            }
        }
    }

    override fun uploadAvatarReq(jUploadAvatarRsp: JUploadAvatarRsp) {
        when (jUploadAvatarRsp.params.retCode) {
            0 -> {
                runOnUiThread {
                    //toast(getString(R.string.Avatar_Update_Successful))
                }
                var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
                var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + "__Avatar.jpg"
                var files_dir = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
                FileUtil.copySdcardFile(filePath, files_dir)
                AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance, files_dir, System.currentTimeMillis())
            }
            1 -> {
                runOnUiThread {
                    //toast(getString(R.string.User_ID_error))
                }
            }
            2 -> {
                runOnUiThread {
                    //toast(getString(R.string.file_error))
                }
            }
            3 -> {
                runOnUiThread {
                    toast(getString(R.string.file_hasnot_changed))
                }
            }
            else -> {
                runOnUiThread {
                    //toast(getString(R.string.Other_mistakes))
                }
            }
        }
    }

    override fun pushLogoutRsp(jPushLogoutRsp: JPushLogoutRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = PushLogoutRsp(0, userId!!, "")
        var msgId: String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jPushLogoutRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jPushLogoutRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        if (jPushLogoutRsp.params.reason == 1) {
            runOnUiThread {
                toast(R.string.Other_devices)
                ConstantValue.isHasWebsocketInit = true
                if (AppConfig.instance.messageReceiver != null)
                    AppConfig.instance.messageReceiver!!.close()

                ConstantValue.loginOut = true
                ConstantValue.logining = false
                ConstantValue.isHeart = false
                ConstantValue.currentRouterIp = ""
                resetUnCompleteFileRecode()
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
                } else {
                    val intentTox = Intent(this, KotlinToxService::class.java)
                    this.stopService(intentTox)
                }
                ConstantValue.isWebsocketConnected = false
                onLogOutSuccess()
            }
        } else if (jPushLogoutRsp.params.reason == 2) {
            runOnUiThread {
                toast(R.string.System_Upgrade)

            }
        } else {
            runOnUiThread {
                toast(R.string.Users_are_deleted)

            }
        }
        runOnUiThread {
            ConstantValue.isHasWebsocketInit = true
            if (AppConfig.instance.messageReceiver != null)
                AppConfig.instance.messageReceiver!!.close()

            ConstantValue.loginOut = true
            ConstantValue.logining = false
            ConstantValue.isHeart = false
            ConstantValue.currentRouterIp = ""
            resetUnCompleteFileRecode()
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
            } else {
                val intentTox = Intent(this, KotlinToxService::class.java)
                this.stopService(intentTox)
            }
            ConstantValue.isWebsocketConnected = false
            onLogOutSuccess()
        }
    }

    fun onLogOutSuccess() {
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(this, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }

    override fun readMsgPushRsp(jReadMsgPushRsp: JReadMsgPushRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = ReadMsgPushReq(0, "", userId!!)
        var msgId: String = ""
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jReadMsgPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jReadMsgPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    override fun OnlineStatusPush(jOnlineStatusPushRsp: JOnlineStatusPushRsp) {

    }

    override fun userInfoPushRsp(jUserInfoPushRsp: JUserInfoPushRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()

        for (j in localFriendList) {
            if (jUserInfoPushRsp.params.friendId.equals(j.userId)) {
                j.nickName = jUserInfoPushRsp.params.nickName
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                break
            }
        }
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var msgData = UserInfoPushRsp(0, userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2, msgData, jUserInfoPushRsp.msgid))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2, msgData, jUserInfoPushRsp.msgid)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        runOnUiThread {
            contactFragment?.updataUI()
        }
    }

    override fun unReadCount(unReadCOunt: Int) {
        runOnUiThread {
            if (unread_count != null) {
                if (unReadCOunt == 0) {
                    unread_count.visibility = View.INVISIBLE
                    unread_count.text = ""
                } else {
                    unread_count.visibility = View.VISIBLE
                    unread_count.text = unReadCOunt.toString()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFileSendFinished(toxSendFileFinishedEvent: ToxSendFileFinishedEvent) {
        var fileNumber = toxSendFileFinishedEvent.fileNumber
        var key = toxSendFileFinishedEvent.key
        val toxFileData = ConstantValue.sendToxFileDataMap[fileNumber.toString() + ""]
        if (toxFileData != null) {//点对点聊天
            if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(toxFileData.toId)) {
                KLog.i("已经在聊天窗口了，不处理该条数据！")
            } else {
                val sendToxFileNotice = SendToxFileNotice(toxFileData.fromId, toxFileData.toId, toxFileData.fileName, toxFileData.fileMD5, toxFileData.widthAndHeight, toxFileData.fileSize, toxFileData.fileType.value(), toxFileData.fileId, toxFileData.srcKey, toxFileData.dstKey, "SendFile")
                val baseData = BaseData(sendToxFileNotice)
                val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().sendMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }
        val toxFileGroupChatData = ConstantValue.sendToxFileInGroupChapDataMap[fileNumber.toString() + ""]
        if (toxFileGroupChatData != null)//群聊
        {
            if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(toxFileGroupChatData.toId)) {
                KLog.i("已经在聊天窗口了，不处理该条数据！")
            } else {
                val fileBase58Name = toxFileGroupChatData.getFileName()
                val fileMD5 = FileUtil.getFileMD5(File(toxFileGroupChatData.getFilePath()))
                var fileInfo = ""
                if (toxFileGroupChatData.getFileType().value() == 1) {
                    if (toxFileGroupChatData.getWidthAndHeight() != null && !toxFileGroupChatData.getWidthAndHeight().equals("")) {
                        fileInfo = toxFileGroupChatData.getWidthAndHeight()
                    } else {
                        fileInfo = "200.0000000*200.0000000"
                    }

                } else if (toxFileGroupChatData.getFileType().value() == 4) {
                    fileInfo = "200.0000000*200.0000000"
                }
                val size = toxFileGroupChatData.getFileSize()
                val groupSendFileDone = GroupSendFileDoneReq(toxFileGroupChatData.getFromId(), toxFileGroupChatData.getToId(), fileBase58Name, fileMD5!!, fileInfo, size, toxFileGroupChatData.getFileType().value(), toxFileGroupChatData.getFileId().toString() + "", "GroupSendFileDone")
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, groupSendFileDone))
                } else if (ConstantValue.isToxConnected) {
                    val baseData = BaseData(4, groupSendFileDone)
                    val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }

    }

    override fun pushFileMsgRsp(jPushFileMsgRsp: JPushFileMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(jPushFileMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgDataPushFileRsp = PushFileRespone(0, jPushFileMsgRsp.params.fromId, jPushFileMsgRsp.params.toId, jPushFileMsgRsp.params.msgId)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(5, msgDataPushFileRsp, jPushFileMsgRsp.msgid))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(5, msgDataPushFileRsp, jPushFileMsgRsp.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
            val gson = Gson()
            val Message = Message()
            Message.msg = ""
            Message.msgId = jPushFileMsgRsp.params.msgId
            Message.from = jPushFileMsgRsp.params.fromId
            Message.to = jPushFileMsgRsp.params.toId
            Message.msgType = jPushFileMsgRsp.params.fileType
            Message.sender = 1
            Message.status = 1
            Message.chatType = EMMessage.ChatType.Chat
            Message.fileName = jPushFileMsgRsp.params.fileName
            Message.timeStamp = jPushFileMsgRsp.timestamp

            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, "")
            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
            var unReadCount = 0
            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                unReadCount = MessageLocal.unReadCount
            }
            Message.unReadCount = unReadCount + 1;

            val baseDataJson = gson.toJson(Message)
            if (Message.sender == 0) {
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
            } else {
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + jPushFileMsgRsp.params.fromId, baseDataJson)
            }
            if (ConstantValue.isInit) {
                runOnUiThread {
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                    controlleMessageUnReadCount(UnReadMessageCount)
                }
                if (isAddEmail) {
                    chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                } else {
                    conversationListFragment?.refresh()
                }

                ConstantValue.isRefeshed = true
            }
//        }else{
            /* var ipAddress = WiFiUtil.getGateWay(AppConfig.instance);
             var filledUri = "https://" + ipAddress + ConstantValue.port +jPushFileMsgRsp.params.filePath
             var files_dir = this.filesDir.absolutePath + "/image/"
             FileDownloadUtils.doDownLoadWork(filledUri, files_dir, this, jPushFileMsgRsp.params.deleteMsgId, handler)*/
        }
    }

    override fun pushDelMsgRsp(delMsgPushRsp: JDelMsgPushRsp) {

        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(delMsgPushRsp.params.friendId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            var msgData = DelMsgRsp(0, "", delMsgPushRsp.params.friendId)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData, delMsgPushRsp.msgid))
            } else if (ConstantValue.isToxConnected) {
                var baseData = BaseData(msgData, delMsgPushRsp.msgid)
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            val keyMap = SpUtil.getAll(AppConfig.instance)
            for (key in keyMap.keys) {

                if (key.contains(ConstantValue.message) && key.contains(userId!! + "_")) {
                    val tempkey = key.replace(ConstantValue.message, "")
                    val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                    if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null") {
                        val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list()
                        if (localFriendList.size == 0) {
                            continue
                        }
                        val cachStr = SpUtil.getString(AppConfig.instance, key, "")
                        if ("" != cachStr) {
                            val gson = GsonUtil.getIntGson()
                            val MessageLocal = gson.fromJson(cachStr, Message::class.java)
                            if (MessageLocal.from.equals(delMsgPushRsp.params.userId)) {
                                var gson = Gson()
                                var Message = Message()
                                Message.setMsg(resources.getString(R.string.withdrawn))
                                Message.setMsgId(delMsgPushRsp.getParams().getMsgId())
                                Message.setFrom(delMsgPushRsp.getParams().userId)
                                Message.setTo(delMsgPushRsp.getParams().friendId)
                                Message.msgType = 0
                                Message.sender = 1
                                Message.status = 2
                                Message.timeStamp = delMsgPushRsp?.timestamp
                                Message.msgId = delMsgPushRsp?.params.msgId
                                Message.chatType = EMMessage.ChatType.Chat
                                var unReadCount = MessageLocal.unReadCount
                                if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                    unReadCount = MessageLocal.unReadCount
                                }
                                if (unReadCount > 0) {
                                    Message.unReadCount = unReadCount - 1;
                                } else {
                                    Message.unReadCount = 0;
                                }

                                var baseDataJson = gson.toJson(Message)
                                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                SpUtil.putString(AppConfig.instance, key, baseDataJson)
                                if (ConstantValue.isInit) {
                                    runOnUiThread {
                                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
                                        if (Message.unReadCount > 0) {
                                            UnReadMessageCount = UnReadMessageCount(1)
                                        }
                                        controlleMessageUnReadCount(UnReadMessageCount)
                                    }
                                    if (isAddEmail) {
                                        chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                                    } else {
                                        conversationListFragment?.refresh()
                                    }

                                    ConstantValue.isRefeshed = true
                                }
                                break
                            }
                        }
                    }
                }
            }


        }

    }

    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (i in jPullFriendRsp.params.payload) {
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    j.nickName = i.name
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.mails = i.mails
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }

            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var isLocalFriendStatus = false
            for (j in localFriendStatusList) {
                if (i.id.equals(j.friendId) && j.userId.equals(userId)) {
                    isLocalFriendStatus = true
                    j.friendLocalStatus = 0
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriendStatus) {
                var friendEntity = FriendEntity()
                friendEntity.userId = userId
                friendEntity.friendId = i.id
                friendEntity.friendLocalStatus = 0
                friendEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(friendEntity)
            }
        }
        if (!ConstantValue.isRefeshed) {
            if (isAddEmail) {
                chatAndEmailFragment!!.getConversationListFragment()?.refresh()
            } else {
                conversationListFragment?.refresh()
            }

            ConstantValue.isRefeshed = true
        }
    }


    /**
     * 播放系统默认提示音
     * 铃声的实现方式
     * @return MediaPlayer对象
     *
     * @throws Exception
     */
    fun defaultMediaPlayer() {
        var notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var r = RingtoneManager.getRingtone(this, notification)
        r.play()
    }

    /**
     * 音乐的实现方式
     */
    fun defaultMedia() {
        KLog.i("播放通知声音")
        var notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var bgmediaPlayer = MediaPlayer.create(this, notification)
        bgmediaPlayer.start()
        bgmediaPlayer.setVolume(0.7f, 0.7f)
    }

    fun defaultNotification() {
        KLog.i("播放通知声音")
        var mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder: NotificationCompat.Builder? = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            var channel = NotificationChannel("通知渠道ID", "通知渠道名称", NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true); //设置开启指示灯，如果设备有的话

            channel.setLightColor(Color.RED); //设置指示灯颜色

            channel.setShowBadge(true); //设置是否显示角标

            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知

//            channel.setDescription("通知渠道描述");//设置渠道描述


            channel.setBypassDnd(true);//设置是否绕过免打扰模式

            mNotificationManager.createNotificationChannel(channel);

//            createNotificationChannelGroups();
//
//            setNotificationChannelGroups(channel);

            builder = NotificationCompat.Builder(this, "通知渠道ID");

            builder.setBadgeIconType(BADGE_ICON_SMALL);//设置显示角标的样式

            builder.setNumber(3);//设置显示角标的数量

            builder.setTimeoutAfter(0);//设置通知被创建多长时间之后自动取消通知栏的通知。

        } else {

            builder = NotificationCompat.Builder(this);

        }

//setContentTitle 通知栏通知的标题

//        builder.setContentTitle("内容标题");

//setContentText 通知栏通知的详细内容

//        builder.setContentText("内容文本信息");

//setAutoCancel 点击通知的清除按钮是否清除该消息（true/false）

        builder.setAutoCancel(true);

//setLargeIcon 通知消息上的大图标

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

//setSmallIcon 通知上面的小图标

        builder.setSmallIcon(R.mipmap.ic_launcher);//小图标

//创建一个意图

        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com"));

        var pIntent = PendingIntent.getActivity(this, 1, intent, 0);

//setContentIntent 将意图设置到通知上

        builder.setContentIntent(pIntent);

//通知默认的声音 震动 呼吸灯

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

//构建通知

        var notification = builder.build();

//将构建好的通知添加到通知管理器中，执行通知

        mNotificationManager.notify(0, notification);

        ivQrCode.postDelayed({ mNotificationManager.cancel(0) }, 50)
    }

    override fun pushGroupMsgRsp(pushMsgRsp: JGroupMsgPushRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(pushMsgRsp.params.gId)) {
            KLog.i("已经在群聊天窗口了，不处理该条数据！")
        } else {
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if (pushMsgRsp.params.point == 1 || pushMsgRsp.params.point == 2) {
                SpUtil.putString(AppConfig.instance, ConstantValue.messageAT + userId + "_" + pushMsgRsp.params.gId, "1")
            }
            if (pushMsgRsp.params.from != userId) {
                val userList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(pushMsgRsp.params.from)).list()
                if (userList.size == 0)   //群聊非好友成员数据
                {
                    val UserEntityLocal = UserEntity()
                    UserEntityLocal.nickName = pushMsgRsp.params.userName
                    UserEntityLocal.userId = pushMsgRsp.params.from
                    UserEntityLocal.index = ""
                    UserEntityLocal.signPublicKey = pushMsgRsp.params.userKey
                    UserEntityLocal.routeId = ""
                    UserEntityLocal.routeName = ""
                    val dst_public_MiKey_Friend = ByteArray(32)
                    val crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(pushMsgRsp.params.userKey))
                    if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                        UserEntityLocal.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                    }
                    UserEntityLocal.remarks = ""
                    UserEntityLocal.timestamp = Calendar.getInstance().timeInMillis
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(UserEntityLocal)
                }
                val friendList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(pushMsgRsp.params.from)).list()
                if (friendList.size == 0) {
                    //判断非好友头像是否需要更新
                    var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(pushMsgRsp.params.userKey))
                    var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
                    var fileMD5 = FileUtil.getFileMD5(File(filePath))
                    if (fileMD5 == null) {
                        fileMD5 = ""
                    }
                    val updateAvatarReq = UpdateAvatarReq(userId!!, pushMsgRsp.params.from, fileMD5)
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
                    } else if (ConstantValue.isToxConnected) {
                        val baseData = BaseData(4, updateAvatarReq)
                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                        if (ConstantValue.isAntox) {
                            //val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        } else {
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                        }
                    }
                }
            }
            var groupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(pushMsgRsp.params.gId)).list()
            if (groupList.size > 0) {
                var GroupLocal = groupList.get(0)
                GroupLocal.userKey = pushMsgRsp.params.selfKey
                GroupLocal.remark = ""
                GroupLocal.gId = pushMsgRsp.params.gId
                GroupLocal.gAdmin = pushMsgRsp.params.gAdmin
                GroupLocal.gName = pushMsgRsp.params.groupName
                GroupLocal.routerId = ConstantValue.currentRouterId
                AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.update(GroupLocal);
            } else {
                var GroupLocal = GroupEntity()
                GroupLocal.userKey = pushMsgRsp.params.selfKey
                GroupLocal.remark = ""
                GroupLocal.gId = pushMsgRsp.params.gId
                GroupLocal.gAdmin = pushMsgRsp.params.gAdmin
                GroupLocal.gName = pushMsgRsp.params.groupName
                GroupLocal.routerId = ConstantValue.currentRouterId
                AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.insert(GroupLocal);
            }
            if (!AppConfig.instance.isBackGroud) {
                defaultMediaPlayer()
            }

            var msgData = GroupMsgPushRsp(0, userId!!, pushMsgRsp.params.gId, "")

            var sendData = BaseData(5, msgData, pushMsgRsp?.msgid)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(5, msgData, pushMsgRsp?.msgid)
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            } else if (ConstantValue.isToxConnected) {
                var baseData = sendData
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }

            when (pushMsgRsp.params.msgType) {
                0 -> {
                    val aesKey = LibsodiumUtil.DecryptShareKey(pushMsgRsp.params.selfKey, ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                    val base64Scoure = RxEncodeTool.base64Decode(pushMsgRsp.getParams().getMsg())
                    var msgSouce: String? = ""
                    try {
                        msgSouce = String(AESCipher.aesDecryptBytes(base64Scoure, aesKey.toByteArray()))
                        var message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.from)
                        if (msgSouce != null && msgSouce != "") {
                            message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.from)
                        }
                        try {
                            message.setDirection(EMMessage.Direct.RECEIVE)
                            message.msgId = "" + pushMsgRsp?.params.msgId
                            message.from = pushMsgRsp.params.from
                            message.to = pushMsgRsp.params.gId
                            message.isUnread = true
                            message.isAcked = true
                            message.setStatus(EMMessage.Status.SUCCESS)
                            //if (conversation != null){
                            var gson = Gson()
                            var Message = Message()
                            Message.setMsg(msgSouce)
                            Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                            Message.setFrom(pushMsgRsp.getParams().from)
                            Message.setTo(pushMsgRsp.getParams().gId)
                            Message.msgType = 0
                            Message.sender = 1
                            Message.status = 1
                            Message.timeStamp = pushMsgRsp?.timestamp
                            Message.chatType = EMMessage.ChatType.GroupChat
                            Message.msgId = pushMsgRsp?.params.msgId
                            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, "")
                            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                            var unReadCount = 0
                            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                unReadCount = MessageLocal.unReadCount
                            }
                            Message.unReadCount = unReadCount + 1;


                            var baseDataJson = gson.toJson(Message)
                            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, baseDataJson)
                            KLog.i("insertMessage:" + "MainActivity" + "_pushMsgRsp")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        //conversation.insertMessage(message)
                        //}
                        if (ConstantValue.isInit) {
                            runOnUiThread {
                                var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                                controlleMessageUnReadCount(UnReadMessageCount)
                            }
                            if (isAddEmail) {
                                chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                            } else {
                                conversationListFragment?.refresh()
                            }

                            ConstantValue.isRefeshed = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    val gson = Gson()
                    val Message = Message()
                    Message.msg = ""
                    Message.msgId = pushMsgRsp.params.msgId
                    Message.from = pushMsgRsp.params.from
                    Message.to = pushMsgRsp.params.gId
                    Message.msgType = pushMsgRsp.params.msgType
                    Message.sender = 1
                    Message.status = 1
                    Message.chatType = EMMessage.ChatType.GroupChat
                    Message.fileName = pushMsgRsp.params.fileName
                    Message.timeStamp = pushMsgRsp.timestamp

                    var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, "")
                    val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                    var unReadCount = 0
                    if (MessageLocal != null && MessageLocal.unReadCount != null) {
                        unReadCount = MessageLocal.unReadCount
                    }
                    Message.unReadCount = unReadCount + 1;

                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.gId, baseDataJson)
                    if (ConstantValue.isInit) {
                        runOnUiThread {
                            var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                            controlleMessageUnReadCount(UnReadMessageCount)
                        }
                        if (isAddEmail) {
                            chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                        } else {
                            conversationListFragment?.refresh()
                        }

                        ConstantValue.isRefeshed = true
                    }
                }
            }

        }
    }

    override fun pushMsgRsp(pushMsgRsp: JPushMsgRsp) {
        if (AppConfig.instance.isChatWithFirend != null && AppConfig.instance.isChatWithFirend.equals(pushMsgRsp.params.fromId)) {
            KLog.i("已经在聊天窗口了，不处理该条数据！")
        } else {
            if (!AppConfig.instance.isBackGroud) {
                defaultMediaPlayer()
            }
            var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), userId!!, 0, "")

            var sendData = BaseData(3, msgData, pushMsgRsp?.msgid)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(3, msgData, pushMsgRsp?.msgid)
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            } else if (ConstantValue.isToxConnected) {
                var baseData = sendData
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

            //var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            var msgSouce = "";

            if (ConstantValue.encryptionType.equals("1")) {
                var friendEntity = UserEntity()
                val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(pushMsgRsp.getParams().getFrom())).list()
                if (localFriendList.size > 0)
                    friendEntity = localFriendList[0]
                msgSouce = LibsodiumUtil.DecryptFriendMsg(pushMsgRsp.getParams().getMsg(), pushMsgRsp.getParams().getNonce(), pushMsgRsp.getParams().getFrom(), pushMsgRsp.getParams().getSign(), ConstantValue.libsodiumprivateMiKey!!, friendEntity.signPublicKey)
            } else {
                msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
            }
            var message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
            if (msgSouce != null && msgSouce != "") {
                message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
            }
            message.setDirection(EMMessage.Direct.RECEIVE)

            message.msgId = "" + pushMsgRsp?.params.msgId
            message.from = pushMsgRsp.params.fromId
            message.to = pushMsgRsp.params.toId
            message.isUnread = true
            message.isAcked = true
            message.setStatus(EMMessage.Status.SUCCESS)
            //if (conversation != null){
            var gson = Gson()
            var Message = Message()
            Message.setMsg(msgSouce)
            Message.setMsgId(pushMsgRsp.getParams().getMsgId())
            Message.setFrom(pushMsgRsp.getParams().getFromId())
            Message.setTo(pushMsgRsp.getParams().getToId())
            Message.msgType = 0
            Message.sender = 1
            Message.status = 1
            Message.chatType = EMMessage.ChatType.Chat
            Message.timeStamp = pushMsgRsp?.timestamp
            Message.msgId = pushMsgRsp?.params.msgId


            var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, "")
            val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
            var unReadCount = 0
            if (MessageLocal != null && MessageLocal.unReadCount != null) {
                unReadCount = MessageLocal.unReadCount
            }
            Message.unReadCount = unReadCount + 1;


            var baseDataJson = gson.toJson(Message)
            SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, baseDataJson)
            KLog.i("insertMessage:" + "MainActivity" + "_pushMsgRsp")
            //conversation.insertMessage(message)
            //}
            if (ConstantValue.isInit) {
                runOnUiThread {
                    var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(1)
                    controlleMessageUnReadCount(UnReadMessageCount)
                }
                if (isAddEmail) {
                    chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                } else {
                    conversationListFragment?.refresh()
                }

                ConstantValue.isRefeshed = true
            }
        }
    }

    //别人删除我，服务器给我的推送
    override fun delFriendPushRsp(jDelFriendPushRsp: JDelFriendPushRsp) {

        /*var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (jDelFriendPushRsp.params.friendId.equals(i.userId) || jDelFriendPushRsp.params.userId.equals(i.userId) ) {
                i.friendStatus = 4
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
            }
        }*/
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId)) {
                if (jDelFriendPushRsp.params.friendId.equals(j.friendId) || jDelFriendPushRsp.params.userId.equals(j.friendId)) {
                    j.friendLocalStatus = 4
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                }
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
            }
        }

        var delFriendPushReq = DelFriendPushReq(0, userId!!, "")
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(delFriendPushReq))
        } else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(delFriendPushReq)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        EventBus.getDefault().post(FriendChange(jDelFriendPushRsp.params.friendId, jDelFriendPushRsp.params.userId))
    }

    /**
     * 目标好友处理完成好友请求操作，由router推送消息给好友请求发起方，本次好友请求的结果
     */
    override fun addFriendReplyRsp(jAddFriendReplyRsp: JAddFriendReplyRsp) {
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId) && jAddFriendReplyRsp.params.userId.equals(j.friendId)) {
                if (jAddFriendReplyRsp.params.result == 0) {
                    j.friendLocalStatus = 0
                } else if (jAddFriendReplyRsp.params.result == 1) {
                    j.friendLocalStatus = 2
                }
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                break
            }
        }

        var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            //jAddFriendReplyRsp.params.userId==对方的id
            if (i.userId.equals(jAddFriendReplyRsp.params.userId)) {
                /* if (jAddFriendReplyRsp.params.result == 0) {
                     i.friendStatus = 0
                 } else if (jAddFriendReplyRsp.params.result == 1) {
                     i.friendStatus = 2
                 }*/
                i.nickName = jAddFriendReplyRsp.params.nickname
                i.signPublicKey = jAddFriendReplyRsp.params.userKey
                i.routeId = jAddFriendReplyRsp.params.routeId
                i.routeName = jAddFriendReplyRsp.params.routeName

                var dst_public_MiKey_Friend = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(jAddFriendReplyRsp.params.userKey))
                if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                    i.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                }
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var addFriendReplyReq = AddFriendReplyReq(0, userId!!, "")
                var sendData = BaseData(addFriendReplyReq, jAddFriendReplyRsp.msgid)
                if (ConstantValue.encryptionType.equals("1")) {
                    sendData = BaseData(4, addFriendReplyReq, jAddFriendReplyRsp.msgid)
                }
                if (ConstantValue.isWebsocketConnected) {
                    AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                } else if (ConstantValue.isToxConnected) {
                    var baseData = sendData
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    if (ConstantValue.isAntox) {
                        //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                        //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                    } else {
                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                    }
                }
                runOnUiThread {
                    viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                }
                return
            }
        }

    }

    /**
     * 当一个用户A被其他用户B请求添加好友，router推送消息到A
     */
    override fun addFriendPushRsp(jAddFriendPushRsp: JAddFriendPushRsp) {
        var newFriend = UserEntity()

        /*var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        for (i in useEntityList) {
            if (i.userId.equals(jAddFriendPushRsp.params.friendId)) {
                if (i.friendStatus == 0) {
                    return
                } else {
                    i.friendStatus = 3
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                    runOnUiThread {
                        viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                    }
                    return
                }
            }
        }*/
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        for (j in localFriendStatusList) {
            if (j.userId.equals(userId) && jAddFriendPushRsp.params.friendId.equals(j.friendId)) {
                if (j.friendLocalStatus == 0) {
                    return
                } else {
                    j.friendLocalStatus = 3
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    runOnUiThread {
                        viewModel.freindChange.value = Calendar.getInstance().timeInMillis
                    }
                    return
                }
            }
        }
        newFriend.nickName = jAddFriendPushRsp.params.nickName
        //newFriend.friendStatus = 3
        newFriend.userId = jAddFriendPushRsp.params.friendId
        newFriend.addFromMe = false
        newFriend.timestamp = Calendar.getInstance().timeInMillis
        newFriend.noteName = ""
        newFriend.signPublicKey = jAddFriendPushRsp.params.userKey

        var dst_public_MiKey_Friend = ByteArray(32)
        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(jAddFriendPushRsp.params.userKey))
        if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
            newFriend.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
        }
        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
        newFriend.routerUserId = selfUserId
        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(newFriend)

        var newFriendStatus = FriendEntity()
        newFriendStatus.userId = userId;
        newFriendStatus.friendId = jAddFriendPushRsp.params.friendId
        newFriendStatus.friendLocalStatus = 3
        newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)

        var addFriendPushReq = AddFriendPushReq(0, userId!!, "")
        runOnUiThread {
            viewModel.freindChange.value = Calendar.getInstance().timeInMillis
        }
        var sendData = BaseData(addFriendPushReq, jAddFriendPushRsp.msgid)
        if (ConstantValue.encryptionType.equals("1")) {
            sendData = BaseData(4, addFriendPushReq, jAddFriendPushRsp.msgid)
        }

        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        } else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }


    override fun showToast() {
        showProgressDialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNikNameChange(editnickName: EditNickName) {
        contactFragment?.initData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun friendChange(friendChange: FriendChange) {
        if (friendChange.userId != null && !friendChange.userId.equals("")) {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendChange.userId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation != null) {
                conversation.clearAllMessages()
                if (ConstantValue.isInit) {
                    if (isAddEmail) {
                        var count = chatAndEmailFragment!!.getConversationListFragment()?.removeFriend()
                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
                        controlleMessageUnReadCount(UnReadMessageCount)
                        ConstantValue.isRefeshed = true

                    } else {
                        var count = conversationListFragment?.removeFriend()
                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
                        controlleMessageUnReadCount(UnReadMessageCount)
                        ConstantValue.isRefeshed = true
                    }

                }
            }
        }
        if (friendChange.firendId != null && !friendChange.firendId.equals("")) {
            var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(friendChange.firendId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
            if (conversation != null) {
                conversation.clearAllMessages()
                if (ConstantValue.isInit) {
                    if (isAddEmail) {
                        var count = chatAndEmailFragment!!.getConversationListFragment()?.removeFriend()
                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
                        controlleMessageUnReadCount(UnReadMessageCount)
                        ConstantValue.isRefeshed = true
                    } else {
                        var count = conversationListFragment?.removeFriend()
                        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(count!!)
                        controlleMessageUnReadCount(UnReadMessageCount)
                        ConstantValue.isRefeshed = true
                    }

                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: MainPresenter

    override fun setPresenter(presenter: MainContract.MainContractPresenter) {
        mPresenter = presenter as MainPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        KLog.i("弹窗：closeProgressDialog")
        progressDialog.hide()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        KLog.i("调试mac onWebSocketConnected" + connectStatus.status)
        if (connectStatus.status != 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        when (connectStatus.status) {
            0 -> {
                if (standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                ConstantValue.isHasWebsocketInit = true
                if (isFromScanAdmim) {
                    if (!isScanSwitch) {
                        runOnUiThread {
                            closeProgressDialog()
                        }
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
                } else if (isFromScan) {
                    runOnUiThread {
                        if (!isScanSwitch) {
                            closeProgressDialog()
                            showProgressDialog("wait...")
                        }
                    }
                    standaloneCoroutine = launch(CommonPool) {
                        delay(10000)
                        if (!loginBack) {
                            runOnUiThread {
                                closeProgressDialog()
                                gotoLogin()
                                toast("time out")
                            }
                        }
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq(ConstantValue.currentRouterId, ConstantValue.currentRouterSN, pulicMiKey)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, recovery))
                    isFromScan = false
                } else {
                    if (isClickLogin) {
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
                        var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        KLog.i("没有初始化。。登录接口设置loginBackListener" + "##" + AppConfig.instance.name + "##" + this.name + "##" + AppConfig.instance.messageReceiver)
                        //AppConfig.instance.messageReceiver!!.loginBackListener = this
                        var login = LoginReq_V4(routerId, userSn, userId, signBase64, dataFileVersion, NickName)
                        ConstantValue.loginReq = login
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, login))
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
                if (isScanSwitch) {
                    isScanSwitch = false
                    gotoLogin()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxSendInfoEvent(toxSendInfoEvent: ToxSendInfoEvent) {
        LogUtil.addLog("Tox发送消息：" + toxSendInfoEvent.info)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSortEmailConfig(sortEmailConfig: SortEmailConfig) {
        initLeftSubMenuName()
        initLeftMenu(ConstantValue.chooseFragMentMenu, true, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChooseEmailConfig(chooseEmailConfig: ChooseEmailConfig) {
        initLeftSubMenuName()
        initLeftMenu(ConstantValue.chooseFragMentMenu, false, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddEmailConfig(changeEmailConfig: ChangeEmailConfig) {
        initLeftMenu(ConstantValue.chooseFragMentMenu, true, true)
        initLeftSubMenuName()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxFriendStatusEvent(toxFriendStatusEvent: ToxFriendStatusEvent) {

        KLog.i("tox好友状态MainActivity:" + toxFriendStatusEvent.status)
        if (toxFriendStatusEvent.status == 0) {
            resetUnCompleteFileRecode()
            EventBus.getDefault().post(AllFileStatus())
        }
        if (toxFriendStatusEvent.status == 1) {

            ConstantValue.freindStatus = 1
            if (!threadInit) {
                Thread(Runnable() {
                    run() {

                        while (true) {
                            if (ConstantValue.unSendMessage.size > 0) {
                                for (key in ConstantValue.unSendMessage.keys) {
                                    var sendData = ConstantValue.unSendMessage.get(key)
                                    var friendId = ConstantValue.unSendMessageFriendId.get(key)
                                    var sendCount: Int = ConstantValue.unSendMessageSendCount.get(key) as Int
                                    if (sendCount < 5) {
                                        if (ConstantValue.isAntox) {
                                            //var friendKey: FriendKey = FriendKey(routerId.substring(0, 64))
                                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, sendData, ToxMessageType.NORMAL)
                                        } else {
                                            ToxCoreJni.getInstance().senToxMessage(sendData, friendId)
                                        }
                                        ConstantValue.unSendMessageSendCount.put(key, sendCount++)
                                    } else {
                                        closeProgressDialog()
                                        break
                                    }
                                }

                            } else {
                                closeProgressDialog()
                                break
                            }
                            Thread.sleep(2000)
                        }

                    }
                }).start()
                threadInit = true
            }


            LogUtil.addLog("P2P检测路由好友上线，可以发消息:", "LoginActivityActivity")
        } else {
            ConstantValue.freindStatus = 0;
            LogUtil.addLog("P2P检测路由好友未上线，不可以发消息:", "LoginActivityActivity")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopTox(stopTox: StopTox) {
        try {
            //MessageHelper.clearAllMessage()
        } catch (e: Exception) {
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
                reConnect.visibility = View.GONE
                KLog.i("P2P连接成功")
                LogUtil.addLog("P2P连接成功:", "LoginActivityActivity")
                runOnUiThread {
                    KLog.i("444")
                    closeProgressDialog()
                    //toast("login time out")
                }
                ConstantValue.isToxConnected = true

                if (ConstantValue.curreantNetworkType.equals("TOX")) {
                    ConstantValue.isHasWebsocketInit = true
                    AppConfig.instance.getPNRouterServiceMessageReceiver()
                    KLog.i("没有初始化。。设置loginBackListener")
                    //AppConfig.instance.messageReceiver!!.loginBackListener = this
                }
                if (stopTox || ConstantValue.curreantNetworkType.equals("WIFI"))
                    return
                if (isFromScan) {

                    if (ConstantValue.isAntox) {
                        //InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
                    } else {
                        ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
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
                        if (ConstantValue.freindStatus == 1) {
                            tips = "wait..."
                        } else {
                            tips = "circle connecting..."
                        }
                        showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if (standaloneCoroutine != null)
                                    standaloneCoroutine.cancel()
                                EventBus.getDefault().post(StopTox())
                                gotoLogin()
                                false
                            } else false
                        })
                    }
                    var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
                    var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN, pulicMiKey)
                    var baseData = BaseData(4, recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")

                    ConstantValue.unSendMessage.put("recovery", baseDataJson)
                    ConstantValue.unSendMessageFriendId.put("recovery", ConstantValue.scanRouterId.substring(0, 64))
                    ConstantValue.unSendMessageSendCount.put("recovery", 0)
                    //ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
                    isFromScan = false
                } else {
                    if (ConstantValue.isAntox) {
                        //InterfaceScaleUtil.addFriend(routerId,this)
                    } else {
                        ToxCoreJni.getInstance().addFriend(routerId)
                    }
                    if (isClickLogin) {
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
                            if (ConstantValue.freindStatus == 1) {
                                tips = getString(R.string.login_)
                            } else {
                                tips = "Circle connecting..."
                            }
                            showProgressDialog(tips, DialogInterface.OnKeyListener { dialog, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    if (standaloneCoroutine != null)
                                        standaloneCoroutine.cancel()
                                    EventBus.getDefault().post(StopTox())
                                    gotoLogin()
                                    false
                                } else false
                            })
                        }
                        var sign = ByteArray(32)
                        var time = (System.currentTimeMillis() / 1000).toString().toByteArray()
                        System.arraycopy(time, 0, sign, 0, time.size)
                        var dst_signed_msg = ByteArray(96)
                        var signed_msg_len = IntArray(1)
                        var mySignPrivate = RxEncodeTool.base64Decode(ConstantValue.libsodiumprivateSignKey)
                        var crypto_sign = Sodium.crypto_sign(dst_signed_msg, signed_msg_len, sign, sign.size, mySignPrivate)
                        var signBase64 = RxEncodeTool.base64Encode2String(dst_signed_msg)
                        val NickName = RxEncodeTool.base64Encode2String(username.toByteArray())
                        //var login = LoginReq( routerId,userSn, userId,LoginKeySha, dataFileVersion)
                        var login = LoginReq_V4(routerId, userSn, userId, signBase64, dataFileVersion, NickName)
                        ConstantValue.loginReq = login
                        var baseData = BaseData(4, login)
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        ConstantValue.unSendMessage.put("login", baseDataJson)
                        ConstantValue.unSendMessageFriendId.put("login", routerId.substring(0, 64))
                        ConstantValue.unSendMessageSendCount.put("login", 0)
                        //ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerId.substring(0, 64))
                        ////MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        isClickLogin = false;
                    }

                }
            }
            1 -> {
                LogUtil.addLog("P2P连接中Reconnecting:", "LoginActivityActivity")
            }
        }
    }

    private fun getToken() {
        HMSAgent.Push.getToken {
            KLog.i("华为推送 get token: end" + it)
            LogUtil.addLog("华为推送 get token: end" + it)
            //ConstantValue.mHuaWeiRegId = "" + it

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openSceen(screen: Sceen) {
        var screenshotsSettingFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
        if (screenshotsSettingFlag.equals("1")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
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

    override fun onResume() {
//        AppShortCutUtil.clearBadge(this)
        isForeground = true
        WinqMessageReceiver.count = 0
        ShortcutBadger.removeCount(this)
        exitTime = System.currentTimeMillis() - 2001

        super.onResume()
        notificationManager?.cancelAll()
        var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
        controlleMessageUnReadCount(UnReadMessageCount)

    }

    override fun onPause() {
        isForeground = false
        ShortcutBadger.removeCount(this)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlleContactUnReadCount(unReadContactCount: UnReadContactCount) {
        if (unReadContactCount.messageCount == 0) {
            new_contact.visibility = View.INVISIBLE
            new_contact.text = ""
        } else {
            new_contact.visibility = View.VISIBLE
            //new_contact.text = "" + unReadContactCount.messageCount
            new_contact.text = ""
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlleMessageUnReadCount(unReadMessageZero: UnReadMessageZero) {
        unread_count.visibility = View.INVISIBLE
        unread_count.text = ""
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun controlleMessageUnReadCount(unReadMessageCount: UnReadMessageCount) {

        var hasUnReadMsgCount = 0
        var hasUnReadMsg: Boolean = false;
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val keyMap = SpUtil.getAll(AppConfig.instance)
        for (key in keyMap.keys) {

            if (key.contains(ConstantValue.message) && key.contains(userId + "_")) {
                val tempkey = key.replace(ConstantValue.message, "")
                val toChatUserId = tempkey.substring(tempkey.indexOf("_") + 1, tempkey.length)
                if (toChatUserId != null && toChatUserId != "" && toChatUserId != "null") {
                    if (toChatUserId.indexOf("group") == 0)//这里处理群聊
                    {
                        val localGroupListss = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.loadAll()
                        val localGroupList = AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.queryBuilder().where(GroupEntityDao.Properties.GId.eq(toChatUserId)).list()
                        if (localGroupList.size == 0)
                        //如果找不到用户
                        {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }

                    } else {//这里是普通聊天

                        val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list()
                        if (localFriendList.size == 0)
                        //如果找不到用户
                        {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }
                        var freindStatusData = FriendEntity()
                        freindStatusData.friendLocalStatus = 7
                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(userId), FriendEntityDao.Properties.FriendId.eq(toChatUserId)).list()
                        if (localFriendStatusList.size > 0) freindStatusData = localFriendStatusList[0]
                        if (freindStatusData.friendLocalStatus != 0) {
                            SpUtil.putString(AppConfig.instance, key, "")
                            continue
                        }
                    }

                    val cachStr = SpUtil.getString(AppConfig.instance, key, "")

                    if ("" != cachStr) {
                        val gson = GsonUtil.getIntGson()
                        val Message = gson.fromJson(cachStr, Message::class.java)
                        hasUnReadMsgCount += Message.unReadCount
                        if (hasUnReadMsgCount > 0) {
                            hasUnReadMsg = true
                        }
                    }
                }

            }
        }
        if (unread_count != null) {
            if (hasUnReadMsgCount <= 0) {
                unread_count.visibility = View.INVISIBLE
                unread_count.text = ""
            } else if (hasUnReadMsgCount > 99) {
                unread_count.visibility = View.VISIBLE
                unread_count.text = "99+"
            } else {
                unread_count.visibility = View.VISIBLE
                unread_count.text = hasUnReadMsgCount.toString()
            }
        }
    }


    override fun onItemClick(id: Int) {
        when (id) {
//            R.id.rl_detail -> startActivity(Intent(this, FreeConnectActivity::class.java))
//            R.id.rl_rank -> startActivity(Intent(this, RankActivity::class.java))
            else -> {
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                reConnect.visibility = View.GONE
            }
            1 -> {

            }
            2 -> {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                reConnect.visibility = View.VISIBLE
            }
            3 -> {
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                reConnect.visibility = View.VISIBLE
            }
        }
    }

    private var isCanShotNetCoonect = true
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun connectNetWorkStatusChange(statusChange: ConnectStatus) {
        when (statusChange.status) {
            0 -> {
                if (!isScanSwitch) {
                    closeProgressDialog()
                }

                isCanShotNetCoonect = true
            }
            1 -> {

            }
            2 -> {
                if (isCanShotNetCoonect) {
                    if (!ConstantValue.loginOut) {
                        if (!isScanSwitch) {
                            closeProgressDialog()
                        }
                        //showProgressDialog(getString(R.string.network_reconnecting))
                    }
                    isCanShotNetCoonect = false
                }
            }
            3 -> {
                if (isCanShotNetCoonect) {
                    if (!isScanSwitch) {
                        closeProgressDialog()
                    }
                    //showProgressDialog(getString(R.string.network_reconnecting))
                    isCanShotNetCoonect = false
                }
            }
        }
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
        stopVirtual()

        tearDownMediaProjection()
        //注册ActivityLifeCyclelistener以后要记得注销，以防内存泄漏。
        //application.unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener)
        EventBus.getDefault().unregister(this)
//        exitToast()
        super.onDestroy()
    }

    fun clear(content: String) {
        var preferences = getSharedPreferences(content, Context.MODE_PRIVATE)
        var editor = preferences.edit()
        editor.clear()
        editor.apply()
    }


    fun setToNews() {
        rl1.setBackgroundResource(R.color.headmainColor)
        tvTitle.visibility = View.GONE
        tvTitle.text = getString(R.string.app_name)
        mainIv1.visibility = View.GONE
        fileLook.visibility = View.GONE
        emailLook.visibility = View.VISIBLE
        rootTitleParent.visibility = View.VISIBLE
        ivQrCode.visibility = View.GONE
        ivNewGroup.visibility = View.VISIBLE
        searchBtn.visibility = View.VISIBLE
    }

    fun setToFile() {
        rl1.setBackgroundResource(R.color.mainColor)
        tvTitle.text = getString(R.string.file_)
        tvTitle.visibility = View.VISIBLE
        mainIv1.visibility = View.VISIBLE
        ivQrCode.visibility = View.GONE
        emailLook.visibility = View.GONE
        rootTitleParent.visibility = View.GONE
        fileLook.visibility = View.VISIBLE
        ivNewGroup.visibility = View.GONE
        searchBtn.visibility = View.GONE
    }

    fun setToContact() {
        rl1.setBackgroundResource(R.color.mainColor)
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.contacts)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.VISIBLE
        emailLook.visibility = View.GONE
        rootTitleParent.visibility = View.GONE
        fileLook.visibility = View.GONE
        ivNewGroup.visibility = View.GONE
        searchBtn.visibility = View.GONE
        //contactFragment?.updata()
    }

    fun setToMy() {
        rl1.setBackgroundResource(R.color.mainColor)
        tvTitle.visibility = View.VISIBLE
        tvTitle.text = getString(R.string.my)
        mainIv1.visibility = View.GONE
        ivQrCode.visibility = View.GONE
        fileLook.visibility = View.GONE
        emailLook.visibility = View.GONE
        rootTitleParent.visibility = View.GONE
        ivNewGroup.visibility = View.GONE
        searchBtn.visibility = View.GONE
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        StatusBarUtil.setColor(this, resources.getColor(R.color.mainColor), 0)
        tvTitle.text = getString(R.string.news)
        tvTitle.visibility = View.GONE
//        val llp = RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
//        statusBar.setLayoutParams(llp)
        val llp1 = RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        reConnect.setLayoutParams(llp1)
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        chatAndEmailFragment = ChatAndEmailFragment()
        contactFragment = ContactFragment()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
        //initFlatBall()

    }

    fun initFlatBall()
    {
        //1 初始化悬浮球配置，定义好悬浮球大小和icon的drawable
        val ballSize = DensityUtil.dip2px(this, 45f)
        val ballIcon = BackGroudSeletor.getdrawble("ic_floatball", this)
        //可以尝试使用以下几种不同的config。
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_CENTER,false);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_BOTTOM, -100);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_TOP, 100);
        val ballCfg = FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER)
        //设置悬浮球不半隐藏
        ballCfg.setHideHalfLater(false)
        //2 需要显示悬浮菜单
        //2.1 初始化悬浮菜单配置，有菜单item的大小和菜单item的个数
        val menuSize = DensityUtil.dip2px(this, 180f)
        val menuItemSize = DensityUtil.dip2px(this, 40f)
        val menuCfg = FloatMenuCfg(menuSize, menuItemSize)
        //3 生成floatballManager
        mFloatballManager = FloatBallManager(applicationContext, ballCfg, menuCfg)
        addFloatMenuItem()
        setFloatPermission()

        //5 如果没有添加菜单，可以设置悬浮球点击事件
        if (mFloatballManager!!.getMenuItemSize() == 0) {
            mFloatballManager!!.setOnFloatBallClickListener(FloatBallManager.OnFloatBallClickListener { toast("点击了悬浮球") })
        }
        //6 如果想做成应用内悬浮球，可以添加以下代码。
        //application.registerActivityLifecycleCallbacks(mActivityLifeCycleListener)
        createFloatView()
        createImageReader()
        requestCapturePermission()
    }

    private fun createFloatView() {
        mGestureDetector = GestureDetector(applicationContext, FloatGestrueTouchListener())
        mLayoutParams = WindowManager.LayoutParams()
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        mWindowManager!!.getDefaultDisplay().getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels

        mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        mLayoutParams!!.format = PixelFormat.RGBA_8888
        // 设置Window flag
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        mLayoutParams!!.x = mScreenWidth
        mLayoutParams!!.y = 100
        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT


        //startScreenShot();
    }

    private inner class FloatGestrueTouchListener : GestureDetector.OnGestureListener {
        internal var lastX: Int = 0
        internal var lastY: Int = 0
        internal var paramX: Int = 0
        internal var paramY: Int = 0

        override fun onDown(event: MotionEvent): Boolean {
            lastX = event.rawX.toInt()
            lastY = event.rawY.toInt()
            paramX = mLayoutParams!!.x
            paramY = mLayoutParams!!.y
            return true
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            startScreenShot()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val dx = e2.rawX.toInt() - lastX
            val dy = e2.rawY.toInt() - lastY
            mLayoutParams!!.x = paramX + dx
            mLayoutParams!!.y = paramY + dy
            // 更新悬浮窗位置
            // 更新悬浮窗位置

            return true
        }

        override fun onLongPress(e: MotionEvent) {

        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }
    private fun createImageReader() {

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1)

    }

    fun startScreenShot() {
        /*val handler1 = Handler()
        handler1.postDelayed({
            //start virtual
            startVirtual()
        }, 5)

        handler1.postDelayed({
            //capture the screen
            startCapture()
        }, 30)*/
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
        val handler1 = Handler()
        handler1.postDelayed({
            startVirtual()
            startCapture()
        }, 5)

    }

    fun startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay()
        } else {
            setUpMediaProjection()
            virtualDisplay()
        }
    }

    fun setUpMediaProjection() {
        if (mResultData == null) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData)
        }
    }

    private fun getMediaProjectionManager(): MediaProjectionManager {

        return getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private fun virtualDisplay() {
        if (mMediaProjection != null) {
            mVirtualDisplay = mMediaProjection!!.createVirtualDisplay("screen-mirror",
                    mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader!!.getSurface(), null, null)
        }
    }

    private fun startCapture() {

        val image = mImageReader!!.acquireLatestImage()

        if (image == null) {
            startScreenShot()
        } else {
            val mSaveTask = SaveTask()
            mSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image)
            //AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    inner class SaveTask : AsyncTask<Image, Void, Bitmap>() {

        override fun doInBackground(vararg params: Image): Bitmap? {

            if (params == null || params.size < 1 || params[0] == null) {

                return null
            }

            val image = params[0]

            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer = planes[0].buffer
            //每个像素的间距
            val pixelStride = planes[0].pixelStride
            //总的间距
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            var bitmap: Bitmap? = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
            bitmap!!.copyPixelsFromBuffer(buffer)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            image.close()
            var fileImage: File? = null
            if (bitmap != null) {
                try {
                    fileImage = File(com.stratagile.pnrouter.screencapture.FileUtil.getScreenShotsName(applicationContext))
                    if (!fileImage.exists()) {
                        fileImage.createNewFile()
                    }
                    val out = FileOutputStream(fileImage)
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        out.flush()
                        out.close()
                        val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val contentUri = Uri.fromFile(fileImage)
                        media.data = contentUri
                        sendBroadcast(media)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    fileImage = null
                } catch (e: IOException) {
                    e.printStackTrace()
                    fileImage = null
                }

            }

            return if (fileImage != null) {
                bitmap
            } else null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            //预览图片
            if (bitmap != null) {

                (application as AppConfig).setmScreenCaptureBitmap(bitmap)
                Log.e("ryze", "获取图片成功")
                if(mFloatballManager != null)
                {
                    mFloatballManager!!.show()
                }
                toast(R.string.screenshots_success)
                //startActivity(PreviewPictureActivity.newIntent(applicationContext))
            }

        }
    }


    private fun tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }

    private fun stopVirtual() {
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
        mVirtualDisplay = null
    }
    override fun initData() {
        standaloneCoroutine = launch(CommonPool) {
            delay(10000)
        }
        var autoLoginRouterSn = SpUtil.getString(this, ConstantValue.autoLoginRouterSn, "")
        if(!autoLoginRouterSn.equals("no") && !autoLoginRouterSn.equals(""))
        {
            SpUtil.putString(this, ConstantValue.autoLoginRouterSn, ConstantValue.currentRouterSN)
        }
        var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.loadAll()
        var localEmailContactAcount = ""
        var localEmailContactAcountDelete = ""
        for (item in localEmailContacts)
        {
            var account = item.account.toLowerCase()
            if(!localEmailContactAcount.contains(account))
            {
                localEmailContactAcount += account +","
            }else{
                localEmailContactAcountDelete += account +","
            }
        }
        if(localEmailContactAcountDelete.length > 0)
        {
            var  localEmailContactAcountDeleteArr = localEmailContactAcountDelete.split(",")
            for (item in localEmailContactAcountDeleteArr)
            {
                if(item!="")
                {
                    var localEmailContacts = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.queryBuilder().where(EmailContactsEntityDao.Properties.Account.eq(item)).list()
                    if(localEmailContacts != null && localEmailContacts.size >0)
                    {
                        var localEmailContactsItem = localEmailContacts.get(0)
                        AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.delete(localEmailContactsItem)
                    }
                }
            }
        }
        var emailConfigEntityChooseALL = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
        for (item in emailConfigEntityChooseALL)
        {
            var account = item.account
            var pulicSignKey = ConstantValue.libsodiumpublicSignKey!!
            var accountBase64 = String(RxEncodeTool.base64Encode(account))
            var saveEmailConf = SaveEmailConf(1,1,accountBase64 ,"", pulicSignKey)
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))
        }
        if (VersionUtil.getDeviceBrand() == 3) {
            HMSAgent.connect(this, ConnectHandler {
                KLog.i("华为推送 HMS connect end: " + it)
                LogUtil.addLog("华为推送 HMS connect end: " + it)
            })
            getToken()
        }
        this_ = this
        var isStartWebsocket = false
        handler = object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_UPD_DATA -> {
                        var obj:String = msg.obj.toString()
                        KLog.i("调试mac "+obj)
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
                                            println("调试mac :"+udpRouterArray[1] +" ip: "+udpRouterArray[0])
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
                                    KLog.i("调试mac "+ConstantValue.currentRouterIp+ConstantValue.port)
                                    //AppConfig.instance.messageReceiver!!.loginBackListener = this_
                                }

                            }
                        }

                    }
                }
            }
        }
        SpUtil.putBoolean(this, ConstantValue.isUnLock, true)

        FileMangerUtil.init()
        FileMangerDownloadUtils.init()
        try {
            AppConfig.instance.getPNRouterServiceMessageReceiver().mainInfoBack = this
            AppConfig.instance.messageReceiver!!.bakMailsNumCallback = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val backGroundService = Intent(this, BackGroundService::class.java)
        this.startService(backGroundService)
        var intent = Intent(this, FileTransformService::class.java)
        startService(intent)
        ConstantValue.mainActivity = this
        var startFileDownloadUploadService = Intent(this, FileDownloadUploadService::class.java)
        startService(startFileDownloadUploadService)
        Thread(Runnable() {
            run() {
                while (isSendRegId) {
                    Thread.sleep(10 * 1000)
                    ConstantValue.mJiGuangRegId = JPushInterface.getRegistrationID(applicationContext)
                    var aa = ConstantValue.mHuaWeiRegId
                    var map: HashMap<String, String> = HashMap()
                    var os = VersionUtil.getDeviceBrand()
                    map.put("os", os.toString())
                    map.put("appversion", BuildConfig.VERSION_NAME)
                    if (os == 3) {
                        map.put("regid", ConstantValue.mJiGuangRegId)
                        map.put("token", ConstantValue.mHuaWeiRegId)
                    } else {
                        map.put("regid", ConstantValue.mRegId)
                    }
                    map.put("topicid", "")
                    var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
                    map.put("routerid", ConstantValue.currentRouterId)
                    map.put("userid", selfUserId!!)
                    var lastLoginUserSn = FileUtil.getLocalUserData("usersn")
                    map.put("usersn", lastLoginUserSn)
                    KLog.i("小米推送注册RegId= " + ConstantValue.mRegId)
                    KLog.i("华为推送注册RegId= " + ConstantValue.mHuaWeiRegId)
                    KLog.i("极光推送注册RegId= " + ConstantValue.mJiGuangRegId)
                    LogUtil.addLog("小米推送注册RegId= " + ConstantValue.mRegId, "MainActivity")
                    KLog.i(map)
                    OkHttpUtils.getInstance().doPost(ConstantValue.pushURL, map, object : OkHttpUtils.OkCallback {
                        override fun onFailure(e: Exception) {
                            isSendRegId = true
                            KLog.i(e.printStackTrace())
                            LogUtil.addLog("小米推送注册失败:", "MainActivity")
                            KLog.i("小米推送注册失败:MainActivity")
                        }

                        override fun onResponse(json: String) {
                            isSendRegId = false
                            LogUtil.addLog("小米推送注册成功:", "MainActivity")
                            KLog.i("小米推送注册成功:MainActivity" + json)
                            //Toast.makeText(AppConfig.instance,"成功",Toast.LENGTH_SHORT).show()
                        }
                    });
                    Thread.sleep(10 * 1000)
                }

            }
        }).start()
        MessageProvider.getInstance().messageListenter = this
        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
        tvTitle.setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.toAddUserId.observe(this, android.arch.lifecycle.Observer<String> { toAddUserId ->
            KLog.i(toAddUserId)
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            if (toAddUserId!!.contains(selfUserId!!)) {
                runOnUiThread {
                    toast(R.string.The_same_user)
                }
                return@Observer
            }
            if (!"".equals(toAddUserId)) {
                var toAddUserIdTemp = toAddUserId!!.substring(0, toAddUserId!!.indexOf(","))
                var intent = Intent(this, SendAddFriendActivity::class.java)
                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                for (i in useEntityList) {
                    if (i.userId.equals(toAddUserIdTemp)) {
                        var nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                        i.nickName = nickName
                        AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
                        var freindStatusData = FriendEntity()
                        freindStatusData.friendLocalStatus = 7
                        val localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list()
                        if (localFriendStatusList.size > 0)
                            freindStatusData = localFriendStatusList[0]

                        if (freindStatusData.friendLocalStatus == 0) {
                            intent.putExtra("user", i)
                            startActivity(intent)
                        } else {
                            intent = Intent(this, SendAddFriendActivity::class.java)
                            intent.putExtra("user", i)
                            startActivity(intent)
                        }
                        return@Observer
                    }
                }
                intent = Intent(this, SendAddFriendActivity::class.java)
                var userEntity = UserEntity()
                //userEntity.friendStatus = 7
                userEntity.userId = toAddUserId!!.substring(0, toAddUserId!!.indexOf(","))
                userEntity.nickName = toAddUserId!!.substring(toAddUserId!!.indexOf(",") + 1, toAddUserId.lastIndexOf(","))
                userEntity.signPublicKey = toAddUserId!!.substring(toAddUserId!!.lastIndexOf(",") + 1, toAddUserId.length)
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)


                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var newFriendStatus = FriendEntity()
                newFriendStatus.userId = userId;
                newFriendStatus.friendId = toAddUserId
                newFriendStatus.friendLocalStatus = 7
                newFriendStatus.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(newFriendStatus)
                intent.putExtra("user", userEntity)
                startActivity(intent)
            }
        })
        if( ConstantValue.waitAddFreind!= null &&  ConstantValue.waitAddFreind!="")
        {
            doWaitAddFreind(ConstantValue.waitAddFreind)
            ConstantValue.waitAddFreind = "";
        }
        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
        if (messageEntityList != null) {
            KLog.i("开始添加本地数据到重发列表" + messageEntityList.size)
            LogUtil.addLog("开始添加本地数据到重发列表" + messageEntityList.size)
            messageEntityList.sortBy { it.sendTime }
            for (i in messageEntityList) {
                if (i.type.equals("0")) {
                    KLog.i("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    //文本消息
                    AppConfig.instance.getPNRouterServiceMessageSender().addDataFromSql(i.userId, i.baseData)
                } else {
                    //文件消息
                    var SendFileInfo = SendFileInfo();
                    SendFileInfo.userId = i.userId
                    SendFileInfo.friendId = i.friendId
                    SendFileInfo.files_dir = i.filePath
                    SendFileInfo.msgId = i.msgId
                    SendFileInfo.friendSignPublicKey = i.friendSignPublicKey
                    SendFileInfo.friendMiPublicKey = i.friendMiPublicKey
                    SendFileInfo.voiceTimeLen = i.voiceTimeLen
                    SendFileInfo.type = i.type
                    SendFileInfo.sendTime = i.sendTime
                    SendFileInfo.widthAndHeight = i.widthAndHeight
                    SendFileInfo.porperty = i.porperty
                    KLog.i("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    AppConfig.instance.getPNRouterServiceMessageSender().addFileDataFromSql(i.userId, SendFileInfo)
                }

            }
        }
        var shelfId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupListPullReq(shelfId!!, ConstantValue.currentRouterId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
        setToNews()
        ivQrCode.setOnClickListener {
            //            val morePopWindow = ActiveTogglePopWindow(this)
//            morePopWindow.setOnItemClickListener(this@MainActivity)
//            morePopWindow.showPopupWindow(ivQrCode)
            startActivityForResult(Intent(this, addFriendOrGroupActivity::class.java), add_activity)
//            mPresenter.getScanPermission()
        }
        ivNewGroup.setOnClickListener {
            //startActivityForResult(Intent(this, addFriendOrGroupActivity::class.java), add_activity)

            var menuArray = arrayListOf<String>()
            var iconArray = arrayListOf<String>()
            menuArray = arrayListOf<String>(getString(R.string.New_Email),getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends),getString(R.string.Add_Members))
            iconArray = arrayListOf<String>("tabbar_email_selected","add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends","tabbar_circle_add_members")

            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            routerList.forEach {
                if (it.lastCheck) {
                    routerEntityAddMembers = it
                    if(ConstantValue.currentRouterSN != null && ConstantValue.isCurrentRouterAdmin && ConstantValue.currentRouterSN.equals(routerEntityAddMembers!!.userSn))
                    {
                        //menuArray = arrayListOf<String>(getString(R.string.New_Email),getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends),getString(R.string.Add_Members))
                        //iconArray = arrayListOf<String>("tabbar_email_selected","add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends","tabbar_circle_add_members")
                        if(AppConfig.instance.emailConfig().account != null)
                        {
                            menuArray = arrayListOf<String>(getString(R.string.New_Email),getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends),getString(R.string.Add_Members))
                            iconArray = arrayListOf<String>("tabbar_email_selected","add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends","tabbar_circle_add_members")
                        }else{
                            menuArray = arrayListOf<String>(getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends),getString(R.string.Add_Members))
                            iconArray = arrayListOf<String>("add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends","tabbar_circle_add_members")
                        }

                    }else{
                        //menuArray = arrayListOf<String>(getString(R.string.New_Email),getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends))
                        //iconArray = arrayListOf<String>("tabbar_email_selected","add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends")
                        if(AppConfig.instance.emailConfig().account != null)
                        {
                            menuArray = arrayListOf<String>(getString(R.string.New_Email),getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends))
                            iconArray = arrayListOf<String>("tabbar_email_selected","add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends")
                        }else{
                            menuArray = arrayListOf<String>(getString(R.string.Create_a_Group),getString(R.string.Add_Contacts),getString(R.string.Invite_Friends))
                            iconArray = arrayListOf<String>("add_contacts","tabbar_circle_selected","tabbar_circle_invite_friends")
                        }

                    }
                    return@forEach
                }
            }
            PopWindowUtil.showPopAddMenuWindow(this@MainActivity, ivNewGroup,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.name) {
                        "New Email" -> {
                            onClickSendEmail()
                        }
                        "New Chat" -> {
                            onClickCreateGroup()
                        }
                        "Add Contacts" -> {
                            mPresenter.getScanPermission()
                        }
                        "Invite Friends" -> {
                            onClickInviteFriendEmail()
                        }
                        "Add Members" -> {
                            onClickAddMembers()
                        }
                    }
                }

            })
            /* var list = arrayListOf<GroupEntity>()
             startActivityForResult(Intent(this, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), create_group)*/
        }
        searchBtn.setOnClickListener {
            startActivityForResult(Intent(this, SearchActivity::class.java), add_activity)
        }
        fileLook.setOnClickListener {
            startActivity(Intent(this, FileTaskListActivity::class.java))
        }
        emailLook.setOnClickListener {
            startActivity(Intent(this, FileTaskListActivity::class.java))
        }
        newAccount.setOnClickListener {
            startActivity(Intent(this, EmailChooseActivity::class.java))
            /*if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }*/
        }
        newCricle.setOnClickListener {
            //startActivity(Intent(this, EmailChooseActivity::class.java))
            /*if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }*/
        }
        editBtn.setOnClickListener {
            startActivity(Intent(this, EmailEditActivity::class.java))
        }
        mDrawer.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(arg0: Int) {

                //Log.i("zhangshuli", "statechange")
            }

            override fun onDrawerSlide(arg0: View, arg1: Float) {

                //Log.i("zhangshuli", "slide")

            }

            override fun onDrawerOpened(arg0: View) {
                if(AppConfig.instance.emailConfig().account != null)
                {
                    var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                    var bakMailsNum = BakMailsNum(accountBase64)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,bakMailsNum))
                }
                //Log.i("zhangshuli", "open")
            }

            override fun onDrawerClosed(arg0: View) {
                var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                if(emailConfigEntityChoose.size > 0)
                {
                    var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
                    AppConfig.instance.emailConfig()
                            .setSmtpHost(emailConfigEntity.smtpHost)
                            .setSmtpPort(emailConfigEntity.smtpPort)
                            .setPopHost(emailConfigEntity.popHost)
                            .setPopPort(emailConfigEntity.popPort)
                            .setImapHost(emailConfigEntity.imapHost)
                            .setImapPort(emailConfigEntity.imapPort)
                            .setAccount(emailConfigEntity.account)
                            .setPassword(emailConfigEntity.password)
                            .setName(emailConfigEntity.name)
                            .setEmailType(emailConfigEntity.emailType)
                            .setImapEncrypted(emailConfigEntity.imapEncrypted)
                            .setSmtpEncrypted(emailConfigEntity.smtpEncrypted)
                    ConstantValue.currentEmailConfigEntity = emailConfigEntity;
                    EventBus.getDefault().post(SortEmailConfig())
                }
                EventBus.getDefault().post(OnDrawerOpened())
                //Log.i("zhangshuli", "colse")
            }
        })
        if(ConstantValue.chooseEmailMenu == 0)
        {
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
        }
        Inbox.setOnClickListener {
            ConstantValue.chooseEmailMenu = 0;
            rootTitle.text = getString(R.string.Inbox)
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }

            EventBus.getDefault().post(ChangEmailMenu("Inbox",ConstantValue.currentEmailConfigEntity!!.inboxMenu))
        }
        nodebackedup.setOnClickListener {
            rootTitle.text = getString(R.string.Node_back_up)
            ConstantValue.chooseEmailMenu = 1;
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Nodebackedup",ConstantValue.currentEmailConfigEntity!!.nodeMenu))
        }
        starred.setOnClickListener {
            rootTitle.text = getString(R.string.Starred)
            ConstantValue.chooseEmailMenu = 2;
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Starred",ConstantValue.currentEmailConfigEntity!!.starMenu))
        }
        drafts.setOnClickListener {
            rootTitle.text = getString(R.string.Drafts)
            ConstantValue.chooseEmailMenu = 3;
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Drafts",ConstantValue.currentEmailConfigEntity!!.drafMenu))
        }
        sent.setOnClickListener {
            rootTitle.text = getString(R.string.Sent)
            ConstantValue.chooseEmailMenu = 4;
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Sent",ConstantValue.currentEmailConfigEntity!!.sendMenu))
        }
        spam.setOnClickListener {
            rootTitle.text = getString(R.string.Spam)
            ConstantValue.chooseEmailMenu = 5;
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Spam",ConstantValue.currentEmailConfigEntity!!.garbageMenu))
        }
        trash.setOnClickListener {
            ConstantValue.chooseEmailMenu = 6;
            rootTitle.text = getString(R.string.Trash)
            ConstantValue.chooseEmailMenuName =  rootTitle.text.toString()
            Inbox.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            nodebackedup.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            starred.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            drafts.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            sent.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            spam.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item))
            trash.setBackGroundResource(getResources().getDrawable(R.drawable.shape_menu_item_select))
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START)
            }
            EventBus.getDefault().post(ChangEmailMenu("Trash",ConstantValue.currentEmailConfigEntity!!.deleteMenu))
        }
        mainIv1.setOnClickListener {
            PopWindowUtil.showFileUploadPopWindow(this@MainActivity, recyclerView, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    when (position) {
                        0 -> {
                            PictureSelector.create(this@MainActivity)
                                    .openGallery(PictureMimeType.ofImage())
                                    .maxSelectNum(100)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
                                    .imageFormat(PictureMimeType.PNG)
                                    .isZoomAnim(true)
                                    .sizeMultiplier(0.5f)
                                    .setOutputCameraPath("/CustomPath")
                                    .enableCrop(false)
                                    .compress(false)
                                    .glideOverride(160, 160)
                                    .hideBottomControls(false)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .minimumCompressSize(100)
                                    .synOrAsy(true)
                                    .rotateEnabled(true)
                                    .scaleEnabled(true)
                                    .videoMaxSecond(60 * 60 * 3)
                                    .videoMinSecond(1)
                                    .isDragFrame(false)
                                    .forResult(SELECT_PHOTO)
                        }
                        1 -> {
                            PictureSelector.create(this@MainActivity)
                                    .openGallery(PictureMimeType.ofVideo())
//                                    .theme()
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(3)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .previewImage(false)
                                    .previewVideo(false)
                                    .enablePreviewAudio(false)
                                    .isCamera(true)
                                    .imageFormat(PictureMimeType.PNG)
                                    .isZoomAnim(true)
                                    .sizeMultiplier(0.5f)
                                    .setOutputCameraPath("/CustomPath")
                                    .enableCrop(false)
                                    .compress(false)
                                    .glideOverride(160, 160)
                                    .hideBottomControls(false)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .minimumCompressSize(100)
                                    .synOrAsy(true)
                                    .rotateEnabled(true)
                                    .scaleEnabled(true)
                                    .videoMaxSecond(60 * 60 * 3)
                                    .videoMinSecond(1)
                                    .isDragFrame(false)
                                    .forResult(SELECT_VIDEO)
                        }
                        2 -> {
                            startActivityForResult(Intent(this@MainActivity, FileChooseActivity::class.java).putExtra("fileType", 2), SELECT_DEOCUMENT)
                        }
                    }
                }

            })
        }
        if (!ConstantValue.isInit) {
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            var pullFriend = PullFriendReq_V4(selfUserId!!)
            var sendData = BaseData(pullFriend)
            if (ConstantValue.encryptionType.equals("1")) {
                sendData = BaseData(6, pullFriend)
            }
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
            } else if (ConstantValue.isToxConnected) {
                var baseData = sendData
                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }

            ConstantValue.isInit = true
        }
        if(!isAddEmail)
        {
            conversationListFragment?.setConversationListItemClickListener(
                    EaseConversationListFragment.EaseConversationListItemClickListener
                    { userid, chatType ->
                        if (chatType.equals("Chat")) {
                            startActivity(Intent(this@MainActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userid))
                        } else {

                            val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
                            intent.putExtra(EaseConstant.EXTRA_USER_ID, userid)
                            intent.putExtra(EaseConstant.EXTRA_CHAT_GROUP, UserDataManger.currentGroupData)
                            startActivity(intent)
                        }
                        KLog.i("进入聊天页面，好友id为：" + userid)
                    })
        }

        if (AppConfig.instance.tempPushMsgList.size != 0) {
            Thread(Runnable() {
                run() {
                    Thread.sleep(1000);
                    for (pushMsgRsp in AppConfig.instance.tempPushMsgList) {
                        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                        var msgData = PushMsgReq(Integer.valueOf(pushMsgRsp?.params.msgId), userId!!, 0, "")
                        var sendData = BaseData(3,msgData, pushMsgRsp?.msgid)
                        if (ConstantValue.encryptionType.equals("1")) {
                            sendData = BaseData(3, msgData, pushMsgRsp?.msgid)
                        }
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                        } else if (ConstantValue.isToxConnected) {
                            var baseData = sendData
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            if (ConstantValue.isAntox) {
                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            } else {
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                            }
                        }
                        var conversation: EMConversation = EMClient.getInstance().chatManager().getConversation(pushMsgRsp.params.fromId, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true)
                        var msgSouce = ""
                        if (ConstantValue.encryptionType.equals("1")) {
                            var friendEntity = UserEntity()
                            val localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(pushMsgRsp.getParams().getFrom())).list()
                            if (localFriendList.size > 0)
                                friendEntity = localFriendList[0]
                            msgSouce = LibsodiumUtil.DecryptFriendMsg(pushMsgRsp.getParams().getMsg(), pushMsgRsp.getParams().getNonce(), pushMsgRsp.getParams().getFrom(), pushMsgRsp.getParams().getSign(),ConstantValue.libsodiumprivateMiKey!!,friendEntity.signPublicKey)
                        } else {
                            msgSouce = RxEncodeTool.RestoreMessage(pushMsgRsp.params.dstKey, pushMsgRsp.params.msg)
                        }
                        try {
                            val message = EMMessage.createTxtSendMessage(msgSouce, pushMsgRsp.params.fromId)
                            message.setDirection(EMMessage.Direct.RECEIVE)
                            message.msgId = pushMsgRsp?.params.msgId.toString()
                            message.from = pushMsgRsp.params.fromId
                            message.to = pushMsgRsp.params.toId
                            message.isUnread = true
                            message.isAcked = true
                            message.setStatus(EMMessage.Status.SUCCESS)
                            if (conversation != null) {
                                var gson = Gson()
                                var Message = Message()
                                Message.setMsg(pushMsgRsp.getParams().getMsg())
                                Message.setMsgId(pushMsgRsp.getParams().getMsgId())
                                Message.setFrom(pushMsgRsp.getParams().getFromId())
                                Message.setTo(pushMsgRsp.getParams().getToId())
                                Message.chatType = EMMessage.ChatType.Chat

                                var cachStr = SpUtil.getString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, "")
                                val MessageLocal = gson.fromJson<Message>(cachStr, com.message.Message::class.java)
                                var unReadCount = 0
                                if (MessageLocal != null && MessageLocal.unReadCount != null) {
                                    unReadCount = MessageLocal.unReadCount
                                }
                                Message.unReadCount = unReadCount + AppConfig.instance.tempPushMsgList.size;

                                var baseDataJson = gson.toJson(Message)
                                var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + pushMsgRsp.params.fromId, baseDataJson)
                                KLog.i("insertMessage:" + "MainActivity" + "_tempPushMsgList")
                                //conversation.insertMessage(message)
                            }
                        }catch (e:Exception)
                        {
                            e.printStackTrace()
                        }


                        if (ConstantValue.isInit) {
                            if(isAddEmail)
                            {
                                chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                            }else{
                                conversationListFragment?.refresh()
                            }

                            ConstantValue.isRefeshed = true
                        }
                    }
                    AppConfig.instance.tempPushMsgList = ArrayList<JPushMsgRsp>()
                }
            }).start()


        }
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> {
                        if(isAddEmail)
                        {
                            return chatAndEmailFragment!!
                        }else{
                            return conversationListFragment!!
                        }
                    }
                    1 -> return FileListFragment()
                    2 -> return contactFragment!!
                    else -> return MyFragment()
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        // 为ViewPager添加页面改变事件
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                // 将当前的页面对应的底部标签设为选中状态
//                bottomNavigation.getMenu().getItem(position).setChecked(true)
                inputMethodManager?.hideSoftInputFromWindow(viewPager.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
                when (position) {
                    0 -> setToNews()
                    1 -> setToFile()
                    2 -> setToContact()
                    3 -> setToMy()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        alphaIndicator.setViewPager(viewPager)

        // 为bnv设置选择监听事件
//        bottomNavigation.setOnNavigationItemSelectedListener {
//            when (it.getItemId()) {
//                R.id.item_news -> viewPager.setCurrentItem(0, false)
//                R.id.item_file -> viewPager.setCurrentItem(1, false)
//                R.id.item_contacts -> viewPager.setCurrentItem(2, false)
//                R.id.item_my -> viewPager.setCurrentItem(3, false)
//            }
//            true
//        }
//        tv_hello.text = "hahhaha"
//        tv_hello.setOnClickListener {
//            mPresenter.showToast()
//            startActivity(Intent(this, TestActivity::class.java))
//        }
//        tv_hello.typeface.style
        viewPager.offscreenPageLimit = 4


        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
        try
        {
            var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
            var fileMD5 = FileUtil.getFileMD5(File(filePath))
            if (fileMD5 == null) {
                fileMD5 = ""
            }
            val updateAvatarReq = UpdateAvatarReq(userId!!, userId!!, fileMD5)
            if (ConstantValue.isWebsocketConnected) {
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
            } else if (ConstantValue.isToxConnected) {
                val baseData = BaseData(4, updateAvatarReq)
                val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                if (ConstantValue.isAntox) {
                    //val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                    //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                }
            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        if(!ConstantValue.shareFromLocalPath.equals(""))
        {
            var startIntent = Intent(this, FileSendShareActivity::class.java)
            startIntent.putExtra("fileLocalPath",ConstantValue.shareFromLocalPath)
            startActivity(startIntent)
            ConstantValue.shareFromLocalPath = ""
        }
        initEvent()
    }
    fun doWaitAddFreind(waitAddFreind:String)
    {

        KLog.i("doWaitAddFreind")
        var userId = waitAddFreind!!.substring(0, waitAddFreind!!.indexOf(","))
        var nickName = SpUtil.getString(this, ConstantValue.username, "")
        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        if (waitAddFreind!!.contains(selfUserId!!)) {
            return
        }
        KLog.i("doWaitAddFreind2")
        var emailId= ""
        var AddFriendsAutoReq = AddFriendsAutoReq(1, selfUserId!!, userId,emailId)
        var sendData = BaseData(6,AddFriendsAutoReq);
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }
    fun onClickSendEmail()
    {
        var intent = Intent(this, EmailSendActivity::class.java)
        intent.putExtra("flag",0)
        intent.putExtra("menu","")
        startActivity(intent)
    }
    fun onClickInviteFriendEmail()
    {
        var intent = Intent(this, EmailSendActivity::class.java)
        intent.putExtra("flag",100)
        intent.putExtra("menu","")
        startActivity(intent)
    }
    fun onClickCreateGroup()
    {
        var list = arrayListOf<GroupEntity>()
        startActivityForResult(Intent(this, SelectFriendCreateGroupActivity::class.java).putParcelableArrayListExtra("person", list), create_group)
    }
    fun onClickAddMembers()
    {
        var intent = Intent(this, RouterCreateUserActivity::class.java)
        intent.putExtra("routerUserEntity", routerEntityAddMembers!!)
        startActivity(intent)
    }
    fun initLeftMenu(fragmentMenu:String,sort:Boolean,refresh:Boolean)
    {

        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChoose.size > 0)
        {
            editBtn.visibility = View.VISIBLE
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
            if(emailConfigEntity.userId != null && emailConfigEntity.userId !="")
            {
                AppConfig.instance.credential!!.setSelectedAccountName(emailConfigEntity.account)
            }
            AppConfig.instance.emailConfig()
                    .setSmtpHost(emailConfigEntity.smtpHost)
                    .setSmtpPort(emailConfigEntity.smtpPort)
                    .setPopHost(emailConfigEntity.popHost)
                    .setPopPort(emailConfigEntity.popPort)
                    .setImapHost(emailConfigEntity.imapHost)
                    .setImapPort(emailConfigEntity.imapPort)
                    .setAccount(emailConfigEntity.account)
                    .setPassword(emailConfigEntity.password)
                    .setName(emailConfigEntity.name)
                    .setEmailType(emailConfigEntity.emailType)
                    .setImapEncrypted(emailConfigEntity.imapEncrypted)
                    .setSmtpEncrypted(emailConfigEntity.smtpEncrypted)
            Inbox.setCount(emailConfigEntity.unReadCount)
            spam.setCount(emailConfigEntity.garbageUnReadCount)
            if(AppConfig.instance.emailConfig().account != null)
            {
                var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                var bakMailsNum = BakMailsNum(accountBase64)
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,bakMailsNum))
            }
            ConstantValue.currentEmailConfigEntity = emailConfigEntity;
        }else{
            editBtn.visibility = View.GONE
            AppConfig.instance.initEmailConfig()
        }
        if(fragmentMenu == "Circle")
        {
            recyclerViewleftParent.setHeight(0)
        }else{
            var aa = recyclerViewleftParent.height
            var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
            if(emailConfigEntityList.size == 0)
            {
                recyclerViewleftParent.setHeight(0)
            }else if(emailConfigEntityList.size == 1)
            {
                recyclerViewleftParent.setHeight(resources.getDimension(R.dimen.x110).toInt())
            }else if(emailConfigEntityList.size == 2){
                recyclerViewleftParent.setHeight(resources.getDimension(R.dimen.x220).toInt())
            }else{
                recyclerViewleftParent.setHeight(resources.getDimension(R.dimen.x270).toInt())
            }
            if(refresh)
            {
                var emailConfigEntityListNew = mutableListOf<EmailConfigEntity>()
                if(sort)
                {
                    var emailConfigChoose:EmailConfigEntity? = null
                    for(emailConfig in emailConfigEntityList)
                    {
                        if(!emailConfig.choose)
                        {
                            emailConfigEntityListNew.add(emailConfig)
                        }else{
                            emailConfigChoose = emailConfig
                        }
                    }
                    if(emailConfigChoose != null)
                    {
                        emailConfigEntityListNew.add(0,emailConfigChoose!!)
                    }
                    emaiConfigChooseAdapter = EmaiConfigChooseAdapter(emailConfigEntityListNew)
                }else{
                    emaiConfigChooseAdapter = EmaiConfigChooseAdapter(emailConfigEntityList)
                }
                emaiConfigChooseAdapter!!.setOnItemLongClickListener { adapter, view, position ->
                    /*val floatMenu = FloatMenu(this)
                    floatMenu.items("菜单1", "菜单2", "菜单3")
                    floatMenu.show((activity!! as BaseActivity).point,0,0)*/
                    true
                }
                recyclerViewleft.adapter = emaiConfigChooseAdapter
                emaiConfigChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
                    /* var intent = Intent(activity!!, ConversationActivity::class.java)
                     intent.putExtra("user", coversationListAdapter!!.getItem(position)!!.userEntity)
                     startActivity(intent)*/
                    var dataList  = emaiConfigChooseAdapter!!.data
                    for (item in dataList)
                    {
                        item.choose = false
                    }
                    var accountData = emaiConfigChooseAdapter!!.getItem(position)
                    var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.Account.eq(accountData!!.account)).list()
                    var hasVerify = false
                    if(emailConfigEntityList.size > 0)
                    {
                        var localemailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
                        for (j in localemailConfigEntityList) {
                            j.choose = false
                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(j)
                        }
                        var emailConfigEntity: EmailConfigEntity = emailConfigEntityList.get(0);
                        if(emailConfigEntity.userId != null && emailConfigEntity.userId !="")
                        {
                            AppConfig.instance.credential!!.setSelectedAccountName(emailConfigEntity.account)
                            isGmailToken = false;
                        }
                        emailConfigEntity.choose = true
                        AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                        AppConfig.instance.emailConfig()
                                .setSmtpHost(emailConfigEntity.smtpHost)
                                .setSmtpPort(emailConfigEntity.smtpPort)
                                .setPopHost(emailConfigEntity.popHost)
                                .setPopPort(emailConfigEntity.popPort)
                                .setImapHost(emailConfigEntity.imapHost)
                                .setImapPort(emailConfigEntity.imapPort)
                                .setAccount(emailConfigEntity.account)
                                .setPassword(emailConfigEntity.password)
                                .setName(emailConfigEntity.name)
                                .setEmailType(emailConfigEntity.emailType)
                                .setImapEncrypted(emailConfigEntity.imapEncrypted)
                                .setSmtpEncrypted(emailConfigEntity.smtpEncrypted)
                    }
                    accountData.choose = true
                    emaiConfigChooseAdapter!!.notifyDataSetChanged()
                    EventBus.getDefault().post(ChooseEmailConfig())
                    /* if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                        mDrawer.closeDrawer(GravityCompat.START)
                    }*/
                    if(sort)
                    {
                        recyclerViewleft!!.scrollToPosition(0)
                    }
                }
            }

        }

    }
    fun initSwitchData()
    {
        var this_ = this
        var isStartWebsocket = false
        SpUtil.putBoolean(this, ConstantValue.isUnLock, true)

        FileMangerUtil.init()
        FileMangerDownloadUtils.init()
        try {
            AppConfig.instance.getPNRouterServiceMessageReceiver().mainInfoBack = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
        if (messageEntityList != null) {
            KLog.i("开始添加本地数据到重发列表" + messageEntityList.size)
            LogUtil.addLog("开始添加本地数据到重发列表" + messageEntityList.size)
            messageEntityList.sortBy { it.sendTime }
            for (i in messageEntityList) {
                if (i.type.equals("0")) {
                    KLog.i("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文本" + messageEntityList.size)
                    //文本消息
                    AppConfig.instance.getPNRouterServiceMessageSender().addDataFromSql(i.userId, i.baseData)
                } else {
                    //文件消息
                    var SendFileInfo = SendFileInfo();
                    SendFileInfo.userId = i.userId
                    SendFileInfo.friendId = i.friendId
                    SendFileInfo.files_dir = i.filePath
                    SendFileInfo.msgId = i.msgId
                    SendFileInfo.friendSignPublicKey = i.friendSignPublicKey
                    SendFileInfo.friendMiPublicKey = i.friendMiPublicKey
                    SendFileInfo.voiceTimeLen = i.voiceTimeLen
                    SendFileInfo.type = i.type
                    SendFileInfo.sendTime = i.sendTime
                    SendFileInfo.widthAndHeight = i.widthAndHeight
                    SendFileInfo.porperty = i.porperty
                    KLog.i("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    LogUtil.addLog("开始添加本地数据到重发列表 文件" + messageEntityList.size)
                    AppConfig.instance.getPNRouterServiceMessageSender().addFileDataFromSql(i.userId, SendFileInfo)
                }

            }
        }
        var shelfId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        val GroupListPullReq = GroupListPullReq(shelfId!!, ConstantValue.currentRouterId)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, GroupListPullReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, GroupListPullReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
        }
        //setToNews()

        var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
        var pullFriend = PullFriendReq_V4(selfUserId!!)
        var sendData = BaseData(pullFriend)
        if (ConstantValue.encryptionType.equals("1")) {
            sendData = BaseData(6, pullFriend)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        } else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        val userId = SpUtil.getString(this, ConstantValue.userId, "")
        var fileBase58Name = Base58.encode(RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))
        var filePath = PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name + ".jpg"
        var fileMD5 = FileUtil.getFileMD5(File(filePath))
        if (fileMD5 == null) {
            fileMD5 = ""
        }
        val updateAvatarReq = UpdateAvatarReq(userId!!, userId!!, fileMD5)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(4, updateAvatarReq))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(4, updateAvatarReq)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                //val friendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

        if (ConstantValue.isInit) {
            runOnUiThread {
                var UnReadMessageCount: UnReadMessageCount = UnReadMessageCount(0)
                controlleMessageUnReadCount(UnReadMessageCount)
                if(isAddEmail)
                {
                    chatAndEmailFragment!!.getConversationListFragment()?.refresh()
                }else{
                    conversationListFragment?.refresh()
                }

            }
            ConstantValue.isRefeshed = true
        }
        if( ConstantValue.waitAddFreind!= null &&  ConstantValue.waitAddFreind!="")
        {
            doWaitAddFreind(ConstantValue.waitAddFreind)
            ConstantValue.waitAddFreind = "";
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addEmailEvent(AddEmailEvent: AddEmailEvent) {
        chatAndEmailFragment!!.setCurrentItem(1)
        if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.openDrawer(GravityCompat.START)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun fromChat(fromChat: FromChat) {
        alphaIndicator.setSelectItem(0)
        //viewPager.setCurrentItem(0, true)
        setToNews()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changFragmentMenu(changFragmentMenu: ChangFragmentMenu) {


        var menu = changFragmentMenu.menu
        ConstantValue.chooseFragMentMenu = menu
        rootTitle.text = menu;
        mainTitle.text = menu;
        if(menu== "Circle")
        {
            if(unread_count.text == "")
            {
                unread_count.visibility = View.INVISIBLE
            }else{
                unread_count.visibility = View.VISIBLE
            }
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            routerList.forEach {
                if (it.lastCheck) {
                    rootName.text = it.routerName
                    return@forEach
                }
            }
            rootName.visibility = View.VISIBLE
            emailMenu.visibility = View.GONE
            newAccount.visibility = View.GONE
            newCricle.visibility = View.VISIBLE
        }else{
            unread_count.visibility = View.INVISIBLE
            newAccount.visibility = View.VISIBLE
            newCricle.visibility = View.GONE
            initLeftSubMenuName()
        }
        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChoose.size > 0) {
            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
            if(emailConfigEntity.userId != null && emailConfigEntity.userId !="")
            {
                if(menu== "Email" &&  !isGmailToken)
                {
                    isGmailToken = false;
                    var gmailService = GmailQuickstart.getGmailService(AppConfig.instance,"");
                    Islands.circularProgress(this)
                            .setCancelable(false)
                            .setMessage(getString(R.string.waiting))
                            .run { progressDialog ->
                                val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                                emailReceiveClient
                                        .gmaiApiToken(this, object : GmailAuthCallback {
                                            override fun googlePlayFailure(availabilityException: GooglePlayServicesAvailabilityIOException?) {
                                                progressDialog.dismiss()
                                            }
                                            override fun authFailure(userRecoverableException: UserRecoverableAuthIOException?) {
                                                progressDialog.dismiss()
                                                this_!!.startActivityForResult(
                                                        userRecoverableException!!.getIntent(),
                                                        REQUEST_AUTHORIZATION);
                                            }

                                            override fun gainSuccess(messageList: List<EmailCount>, count: Int) {
                                                progressDialog.dismiss()
                                            }

                                            override fun gainFailure(errorMsg: String) {
                                                progressDialog.dismiss()
                                            }
                                        },gmailService,"me")
                            }
                }
            }
        }

        initLeftMenu(menu,true,true)
    }
    fun initLeftSubMenuName()
    {
        if(AppConfig.instance.emailConfig().account != null)
        {
            rootName.visibility = View.VISIBLE
            rootName.text = AppConfig.instance.emailConfig().account
            emailMenu.visibility = View.VISIBLE
            if(AppConfig.instance.emailConfig().emailType == "255" || AppConfig.instance.emailConfig().emailType == "6")
            {
                drafts.visibility = View.GONE
                sent.visibility = View.GONE
                spam.visibility = View.GONE
                trash.visibility = View.GONE
            }else{
                drafts.visibility = View.VISIBLE
                sent.visibility = View.VISIBLE
                spam.visibility = View.VISIBLE
                trash.visibility = View.VISIBLE
            }
            when(ConstantValue.chooseEmailMenu)
            {
                0->
                {
                    rootTitle.text = getString(R.string.Inbox)
                }
                1->
                {
                    rootTitle.text = getString(R.string.Node_back_up)
                }
                2->
                {
                    rootTitle.text = getString(R.string.Star)
                }
                3->
                {
                    rootTitle.text = getString(R.string.Drafts)
                }
                4->
                {
                    rootTitle.text = getString(R.string.Sent)
                }
                5->
                {
                    rootTitle.text = getString(R.string.Spam)
                }
                6->
                {
                    rootTitle.text = getString(R.string.Trash)
                }
                else ->
                {

                }
            }
        }else{
            rootName.text  = ""
            rootName.visibility = View.GONE
            emailMenu.visibility = View.GONE
            rootTitle.text = getString(R.string.AddEmail)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun startVerify(startVerify: StartVerify) {
        KLog.i("要进入验证页面")
        if ((AppConfig.instance.mAppActivityManager.currentActivity() as Activity) is VerifyingFingerprintActivity) {
            return
        }
        runDelayed(50, {
            SpUtil.putBoolean(this, ConstantValue.isUnLock, false)
            val intent = Intent(AppConfig.instance, VerifyingFingerprintActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out_1)
        })
    }

    override fun setupActivityComponent() {
        DaggerMainComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isScanSwitch = false
        if (savedInstanceState != null) {
            KLog.i("保存的东西不为空," + savedInstanceState.getString("save"))
            LogUtil.addLog("保存的东西不为空," + savedInstanceState.getString("save"))

        }
        needFront = true
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("save", "保存的东西")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        KLog.i("HELLO:如果应用进程被系统咔嚓，则再次打开应用的时候会进入")
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initEvent() {
        emailLook.setOnClickListener(View.OnClickListener {
            if(ConstantValue.chooseFragMentMenu== "Circle")
            {
                return@OnClickListener
            }
            if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.START)
            }
        })
        rootTitleParent.setOnClickListener(View.OnClickListener {
            if(ConstantValue.chooseFragMentMenu== "Circle")
            {
                return@OnClickListener
            }
            if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.START)
            }
        })
        /* mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i(TAG, "onTabChanged: tabId -- " + tabId);
                mTvTitle.setText(tabId);
                mIvAdd.setVisibility(mTabTexts[0].equals(tabId) ? View.VISIBLE : View.GONE);
                mTvAdd.setVisibility(mTabTexts[1].equals(tabId) ? View.VISIBLE : View.GONE);
                mTvMore.setVisibility(mTabTexts[2].equals(tabId) ? View.VISIBLE : View.GONE);
            }
        });*/

        var mColorShades = ColorShades()

        mDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //设置主布局随菜单滑动而滑动
                val drawerViewWidth = drawerView.width
                mLlContentMain.setTranslationX(drawerViewWidth * slideOffset)

                //设置控件最先出现的位置
                val padingLeft = drawerViewWidth.toDouble() * (1 - 0.618) * (1 - slideOffset).toDouble()
                mRlMenu.setPadding(padingLeft.toInt(), 0, 0, 0)

                //设置Title颜色渐变
                mColorShades.setFromColor("#001AA7F2")
                        .setToColor(Color.WHITE)
                        .setShade(slideOffset)
                //mRlTitle.setBackgroundColor(mColorShades.generate())
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })
    }
    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
        moveTaskToBack(true)
    }

    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            /*   var resultType0 = data!!.getStringExtra("result")
               if (!resultType0.contains("type_0")) {
                   toast(getString(R.string.codeerror))
                   return;
               }
               viewModel.toAddUserId.value = resultType0.substring(7, resultType0.length)
               return*/
            try {
                var result = data!!.getStringExtra("result");
                if (result!!.indexOf("http://") > -1 || result!!.indexOf("https://") > -1) {
                    /*val intent = Intent(AppConfig.instance, WebViewActivity::class.java)
                    intent.putExtra("url", hasQRCode)
                    intent.putExtra("title", "Other websites")
                    startActivity(intent)*/
                    val intent = Intent()
                    intent.action = "android.intent.action.VIEW"
                    val url = Uri.parse(result)
                    intent.data = url
                    startActivity(intent)
                    return;
                }
                if(!result.contains("type_"))
                {
                    if (NetUtils.isMacAddress(result)) {
                        SweetAlertDialog(this_, SweetAlertDialog.BUTTON_NEUTRAL)
                                .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                .setConfirmClickListener {
                                    var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                                    var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                    if (ConstantValue.isWebsocketConnected) {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                    } else if (ConstantValue.isToxConnected) {
                                        val baseData = BaseData(2,msgData)
                                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                        ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                    }
                                    ConstantValue.loginReq = null
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
                                                            /* if(ConstantValue.currentRouterMac.equals(""))
                                                             {
                                                                 runOnUiThread {
                                                                     closeProgressDialog()
                                                                     RouterMacStr = ""
                                                                     isFromScanAdmim = false
                                                                     gotoLogin()
                                                                     toast(R.string.Unable_to_connect_to_router)
                                                                 }
                                                             }
                                                             Thread.currentThread().interrupt(); //方法调用终止线程
                                                             break;*/
                                                            if(!ConstantValue.currentRouterMac.equals(""))
                                                            {
                                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                                                break;
                                                            }else{
                                                                getMacFromRemote()
                                                                break;
                                                            }
                                                        }/*else if(!ConstantValue.currentRouterMac.equals(""))
                                                        {
                                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                                            break;
                                                        }*/
                                                        count ++;
                                                        MobileSocketClient.getInstance().init(handler,this)
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
                                            /*runOnUiThread {
                                                closeProgressDialog()
                                                gotoLogin()
                                                toast(R.string.Please_connect_to_WiFi)
                                            }*/
                                            getMacFromRemote()
                                        }
                                    }else{
                                        runOnUiThread {
                                            closeProgressDialog()
                                            toast(R.string.code_error)
                                        }
                                    }

                                }
                                .show()

                        return
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
                var type = result.substring(0,6);
                var data = result.substring(7,result.length);
                var soureData:ByteArray =  ByteArray(0)
                if(!type.equals("type_0"))
                {
                    soureData =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
                }
                if(type.equals("type_0"))
                {
                    viewModel.toAddUserId.value = result.substring(7, result.length)
                }
                else if(type.equals("type_1"))
                {
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
                            toast(R.string.The_same_circle_without_switching)
                            return
                        }
                        runOnUiThread {
                            SweetAlertDialog(this_, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                    .setConfirmClickListener {
                                        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                                        var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                        if (ConstantValue.isWebsocketConnected) {
                                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                        } else if (ConstantValue.isToxConnected) {
                                            val baseData = BaseData(2,msgData)
                                            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                        ConstantValue.loginReq = null
                                        showProgressDialog("wait...")
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
                                                                                if(httpData != null  && httpData!!.retCode == 0 && httpData!!.connStatus == 1)
                                                                                {
                                                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                                                    ConstantValue.currentRouterIp = httpData!!.serverHost
                                                                                    ConstantValue.port = ":"+httpData!!.serverPort.toString()
                                                                                    ConstantValue.filePort = ":"+(httpData!!.serverPort +1).toString()
                                                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                    if(ConstantValue.isHasWebsocketInit)
                                                                                    {
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                    }else{
                                                                                        ConstantValue.isHasWebsocketInit = true
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                    }
                                                                                    KLog.i("没有初始化。。设置loginBackListener"+this_)
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
                                                        MobileSocketClient.getInstance().init(handler,this)
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
                                            showProgressDialog("wait...")
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
                                                                    if(httpData != null  && httpData!!.retCode == 0 && httpData!!.connStatus == 1)
                                                                    {
                                                                        ConstantValue.curreantNetworkType = "WIFI"
                                                                        ConstantValue.currentRouterIp = httpData!!.serverHost
                                                                        ConstantValue.port = ":"+httpData!!.serverPort.toString()
                                                                        ConstantValue.filePort = ":"+(httpData!!.serverPort +1).toString()
                                                                        ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                        ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                        if(ConstantValue.isHasWebsocketInit)
                                                                        {
                                                                            AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                        }else{
                                                                            ConstantValue.isHasWebsocketInit = true
                                                                            AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                        }
                                                                        KLog.i("没有初始化。。设置loginBackListener"+this_)
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
                }else  if(type.equals("type_2"))
                {
                    scanType = 0;
                    RouterMacStr = String(soureData)
                    if(RouterMacStr != null && !RouterMacStr.equals(""))
                    {
                        runOnUiThread {
                            SweetAlertDialog(this_, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                    .setConfirmClickListener {
                                        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                                        var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                        if (ConstantValue.isWebsocketConnected) {
                                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                        } else if (ConstantValue.isToxConnected) {
                                            val baseData = BaseData(2,msgData)
                                            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                        isScanSwitch = true
                                        ConstantValue.loginReq = null
                                        showProgressDialog("wait...")
                                        if(AppConfig.instance.messageReceiver != null)
                                            AppConfig.instance.messageReceiver!!.close()
                                        if(WiFiUtil.isWifiConnect())
                                        {
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
                                                        MobileSocketClient.getInstance().init(handler,this)
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
                }else if (type!!.contains("type_3"))
                {
                    var left = result.substring(7,result.length)
                    var signprivatek = left.substring(0,left.indexOf(","))
                    left = left.substring(signprivatek.length+1,left.length)
                    var usersn = left.substring(0,left.indexOf(","))
                    left = left.substring(usersn.length+1,left.length)
                    var username = left.substring(0,left.length)
                    username = String(RxEncodeTool.base64Decode(username))
                    SpUtil.putString(this, ConstantValue.username, username)
                    var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()

                    if(signprivatek.equals(ConstantValue.libsodiumprivateSignKey))
                    {
                        toast(R.string.Same_account_no_need_to_import)
                        return;
                    }else{
                        runOnUiThread {
                            SweetAlertDialog(this_, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Do_you_leave_the_circle_to_import_new_accounts))
                                    .setConfirmClickListener {
                                        var type = result.substring(0,6);
                                        var left = result.substring(7,result.length)
                                        var signprivatek = left.substring(0,left.indexOf(","))
                                        left = left.substring(signprivatek.length+1,left.length)
                                        var usersn = left.substring(0,left.indexOf(","))
                                        left = left.substring(usersn.length+1,left.length)
                                        var username = left.substring(0,left.length)
                                        username = String(RxEncodeTool.base64Decode(username))
                                        val localSignArrayList: java.util.ArrayList<CryptoBoxKeypair>
                                        val localMiArrayList: java.util.ArrayList<CryptoBoxKeypair>
                                        val gson = Gson()

                                        var strSignPublicSouce = ByteArray(32)
                                        var  signprivatekByteArray = RxEncodeTool.base64Decode(signprivatek)
                                        System.arraycopy(signprivatekByteArray, 32, strSignPublicSouce, 0, 32)


                                        var dst_public_SignKey = strSignPublicSouce
                                        var dst_private_Signkey = signprivatekByteArray
                                        val strSignPrivate:String =  signprivatek
                                        val strSignPublic =  RxEncodeTool.base64Encode2String(strSignPublicSouce)
                                        ConstantValue.libsodiumprivateSignKey = strSignPrivate
                                        ConstantValue.libsodiumpublicSignKey = strSignPublic
                                        ConstantValue.localUserName = username
                                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateSignKeySp, ConstantValue.libsodiumprivateSignKey!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicSignKeySp, ConstantValue.libsodiumpublicSignKey!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                        localSignArrayList = java.util.ArrayList()
                                        var SignData: CryptoBoxKeypair = CryptoBoxKeypair()
                                        SignData.privateKey = strSignPrivate
                                        SignData.publicKey = strSignPublic
                                        SignData.userName = username
                                        localSignArrayList.add(SignData)
                                        FileUtil.saveKeyData(gson.toJson(localSignArrayList),"libsodiumdata_sign")


                                        var dst_public_MiKey = ByteArray(32)
                                        var dst_private_Mikey = ByteArray(32)
                                        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKey)
                                        var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkey)

                                        val strMiPrivate:String =  RxEncodeTool.base64Encode2String(dst_private_Mikey)
                                        val strMiPublic =  RxEncodeTool.base64Encode2String(dst_public_MiKey)
                                        ConstantValue.libsodiumprivateMiKey = strMiPrivate
                                        ConstantValue.libsodiumpublicMiKey = strMiPublic
                                        ConstantValue.localUserName = username
                                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumprivateMiKeySp, ConstantValue.libsodiumprivateMiKey!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.libsodiumpublicMiKeySp, ConstantValue.libsodiumpublicMiKey!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.localUserNameSp, ConstantValue.localUserName!!)
                                        SpUtil.putString(AppConfig.instance, ConstantValue.username, ConstantValue.localUserName!!)
                                        localMiArrayList = java.util.ArrayList()
                                        var RSAData: CryptoBoxKeypair = CryptoBoxKeypair()
                                        RSAData.privateKey = strMiPrivate
                                        RSAData.publicKey = strMiPublic
                                        RSAData.userName = username
                                        localMiArrayList.add(RSAData)
                                        FileUtil.saveKeyData(gson.toJson(localMiArrayList),"libsodiumdata_mi")
                                        FileUtil.deleteFile(Environment.getExternalStorageDirectory().getPath()+ConstantValue.localPath + "/RouterList/routerData.json")
                                        AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.deleteAll()
                                        ConstantValue.loginReq = null
                                        AppConfig.instance.deleteEmailData()
                                        runOnUiThread {
                                            toast("Import success")
                                            startActivity(Intent(this, LoginActivityActivity::class.java))
                                            finish()
                                        }
                                    }
                                    .show()
                        }
                    }

                }else if(type.equals("type_4"))
                {
                    var beginIndex = result.lastIndexOf(",")
                    ConstantValue.waitAddFreind = result.substring(7,beginIndex);
                    var data = result.substring(beginIndex+1,result.length);
                    var soureData:ByteArray =  AESCipher.aesDecryptByte(data,"welcometoqlc0101")
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
                            if( ConstantValue.waitAddFreind!= null &&  ConstantValue.waitAddFreind!="")
                            {
                                doWaitAddFreind(ConstantValue.waitAddFreind)
                                ConstantValue.waitAddFreind = "";
                            }
                            toast(R.string.The_same_circle_without_switching)
                            return
                        }
                        runOnUiThread {
                            SweetAlertDialog(this_, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Are_you_sure_you_want_to_leave_the_circle))
                                    .setConfirmClickListener {
                                        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                                        var msgData = LogOutReq(ConstantValue.currentRouterId,selfUserId!!,ConstantValue.currentRouterSN)
                                        if (ConstantValue.isWebsocketConnected) {
                                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                                        } else if (ConstantValue.isToxConnected) {
                                            val baseData = BaseData(2,msgData)
                                            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                        ConstantValue.loginReq = null
                                        showProgressDialog("wait...")
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
                                                                                if(httpData != null  && httpData!!.retCode == 0 && httpData!!.connStatus == 1)
                                                                                {
                                                                                    ConstantValue.curreantNetworkType = "WIFI"
                                                                                    ConstantValue.currentRouterIp = httpData!!.serverHost
                                                                                    ConstantValue.port = ":"+httpData!!.serverPort.toString()
                                                                                    ConstantValue.filePort = ":"+(httpData!!.serverPort +1).toString()
                                                                                    ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                                    ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                                    if(ConstantValue.isHasWebsocketInit)
                                                                                    {
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                                    }else{
                                                                                        ConstantValue.isHasWebsocketInit = true
                                                                                        AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                                    }
                                                                                    KLog.i("没有初始化。。设置loginBackListener"+this_)
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
                                                        MobileSocketClient.getInstance().init(handler,this)
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
                                            showProgressDialog("wait...")
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
                                                                    if(httpData != null  && httpData!!.retCode == 0 && httpData!!.connStatus == 1)
                                                                    {
                                                                        ConstantValue.curreantNetworkType = "WIFI"
                                                                        ConstantValue.currentRouterIp = httpData!!.serverHost
                                                                        ConstantValue.port = ":"+httpData!!.serverPort.toString()
                                                                        ConstantValue.filePort = ":"+(httpData!!.serverPort +1).toString()
                                                                        ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                                        ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                                        if(ConstantValue.isHasWebsocketInit)
                                                                        {
                                                                            AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                                        }else{
                                                                            ConstantValue.isHasWebsocketInit = true
                                                                            AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                                        }
                                                                        KLog.i("没有初始化。。设置loginBackListener"+this_)
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
                }else{
                    runOnUiThread {
                        closeProgressDialog()
                        toast(R.string.code_error)
                    }
                }
            }catch (e:Exception)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.code_error)
                }
            }
            return
        } else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            var list = data?.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == SELECT_DEOCUMENT && resultCode == Activity.RESULT_OK) {
            var list = ArrayList<LocalMedia>()
            var localMedia = LocalMedia()
            localMedia.path = data!!.getStringExtra("path")
            list.add(localMedia)
            KLog.i(list)
            var startIntent = Intent(this, FileTaskListActivity::class.java)
            startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
            startActivity(startIntent)
        } else if (requestCode == create_group && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var contactSelectedList: java.util.ArrayList<GroupEntity> = data.getParcelableArrayListExtra("person")
                if (contactSelectedList.size > 0) {
                    var intent = Intent(this, CreateGroupActivity::class.java)
                    intent.putExtra("personList", contactSelectedList)
                    startActivity(intent)
                }
            }
        }else if (requestCode == add_activity && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var result = data!!.getStringExtra("result")
                when(result)
                {
                    "0" ->
                    {
                        onClickCreateGroup()
                    }
                    "1" ->
                    {
                        mPresenter.getScanPermission()
                    }
                    "2" ->
                    {
                        var intent = Intent(this, QRCodeActivity::class.java)
                        intent.putExtra("flag",0)
                        startActivity(intent)
                    }
                    "3" ->
                    {
                        onClickSendEmail()
                    }
                    "4" ->
                    {
                        onClickInviteFriendEmail()
                    }
                }

            }
        }else if (requestCode == REQUEST_AUTHORIZATION) {
            if(resultCode == Activity.RESULT_OK)//授权成功
            {
                toast(R.string.Authorizedsuccess)
            }else{//授权失败
                toast(R.string.Authorizedfail)
            }
        }else if (requestCode == REQUEST_MEDIA_PROJECTION) {
            mResultData = data;
            /* FloatWindowsService.setResultData(data)
             startService(Intent(applicationContext, FloatWindowsService::class.java))*/
        }

    }
    private fun startToxAndRecovery() {

        ConstantValue.curreantNetworkType = "TOX"
        stopTox = false
        if (!ConstantValue.isToxConnected) {
            runOnUiThread {
                closeProgressDialog()
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
                //intent = Intent(AppConfig.instance, ToxService::class.java)
            }
            startService(intent)
        } else {
            ////var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
            runOnUiThread {
                closeProgressDialog()
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
                //InterfaceScaleUtil.addFriend( ConstantValue.scanRouterId,this)
            }else{
                ToxCoreJni.getInstance().addFriend(ConstantValue.scanRouterId)
            }
            var pulicMiKey = ConstantValue.libsodiumpublicSignKey!!
            var recovery = RecoveryReq(ConstantValue.scanRouterId, ConstantValue.scanRouterSN,pulicMiKey)
            var baseData = BaseData(4, recovery)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.scanRouterId.substring(0, 64))
            }
        }
    }
    private fun getContacts(): Map<String, EaseUser> {
        val contacts = HashMap<String, EaseUser>()
        val aa = arrayOf("aa", "cc", "ff", "gg", "kk", "ll", "bb", "jj", "oo", "zz", "mm")
        for (i in 1..10) {
            val user = EaseUser(aa[i])
            contacts[aa[i]] = user
        }
        return contacts
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!CustomPopWindow.onBackPressed()) {
                moveTaskToBack(true)
//\                exitToast()
            }
        }
        return false
    }


    fun exitToast(): Boolean {
        CrashReport.closeBugly()
        CrashReport.closeCrashReport()
        MiPushClient.unregisterPush(this)
        AppConfig.instance.stopAllService()
        //android进程完美退出方法。
//            AppConfig.instance.mAppActivityManager.AppExit()
        var intent = Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
//        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        //System.runFinalizersOnExit(true);
//        System.exit(0)
        return false
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
    fun gotoActivity(index:Int)
    {
        runOnUiThread {
            closeProgressDialog()
        }
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
    internal var handlerDown: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x12 -> {

                }
                0x16 -> {
                }
            }//goMain();
            //goMain();
        }
    }
    fun getMacFromRemote()
    {
        var RouterMacData = RouterMacStr.replace(":","")
        var httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        if(!BuildConfig.DEBUG)
        {
            httpUrlData = ConstantValue.httpMacUrl +"CheckByMac?mac="
        }
        OkHttpUtils.getInstance().doGet(httpUrlData + RouterMacData,  object : OkHttpUtils.OkCallback {
            override fun onFailure( e :Exception) {
                runOnUiThread {
                    closeProgressDialog()
                    RouterMacStr = ""
                    isFromScanAdmim = false
                    gotoLogin()
                    toast(R.string.Unable_to_connect_to_router)
                }
                Thread.currentThread().interrupt(); //方法调用终止线程
            }
            override fun  onResponse(json:String ) {

                val gson = GsonUtil.getIntGson()
                var httpDataMac: HttpData? = null
                try {
                    if (json != null) {
                        httpDataMac = gson.fromJson<HttpData>(json, HttpData::class.java)
                        if(httpDataMac != null  && httpDataMac.retCode == 0 && httpDataMac.connStatus == 1)
                        {
                            ConstantValue.curreantNetworkType = "WIFI"
                            ConstantValue.currentRouterIp = httpDataMac.serverHost
                            ConstantValue.port = ":"+httpDataMac.serverPort.toString()
                            ConstantValue.filePort = ":"+(httpDataMac.serverPort +1).toString()
                            ConstantValue.currentRouterMac = RouterMacStr
                            /*ConstantValue.currentRouterId = ConstantValue.scanRouterId
                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN*/
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
                            runOnUiThread {
                                closeProgressDialog()
                                RouterMacStr = ""
                                isFromScanAdmim = false
                                gotoLogin()
                                toast(R.string.Unable_to_connect_to_router)
                            }
                            Thread.currentThread().interrupt(); //方法调用终止线程
                        }

                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        closeProgressDialog()
                        RouterMacStr = ""
                        isFromScanAdmim = false
                        gotoLogin()
                        toast(R.string.Unable_to_connect_to_router)
                    }
                    Thread.currentThread().interrupt(); //方法调用终止线程
                }
            }
        })
    }

    inner class ActivityLifeCycleListener : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            ++resumed
            setFloatballVisible(true)
        }

        override fun onActivityPaused(activity: Activity) {
            --resumed
            if (!isApplicationInForeground()) {
                //setFloatballVisible(false);
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    private fun setFloatPermission() {
        // 设置悬浮球权限，用于申请悬浮球权限的，这里用的是别人写好的库，可以自己选择
        //如果不设置permission，则不会弹出悬浮球
        mFloatPermissionManager = FloatPermissionManager()
        mFloatballManager!!.setPermission(object : FloatBallManager.IFloatBallPermission {
            override fun onRequestFloatBallPermission(): Boolean {
                requestFloatBallPermission(this@MainActivity)
                return true
            }

            override fun hasFloatBallPermission(context: Context): Boolean {
                return mFloatPermissionManager!!.checkPermission(context)
            }

            override fun requestFloatBallPermission(activity: Activity) {
                mFloatPermissionManager!!.applyPermission(activity)
            }

        })
    }
    private fun addFloatMenuItem() {
        val personItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_desktop", this)) {
            override fun action() {
                goToDesktop(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        val walletItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_screenshot", this)) {
            override fun action() {

                startScreenShot()
                mFloatballManager!!.closeMenu()
            }
        }
        val settingItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_return", this)) {
            override fun action() {
                goToApp(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        mFloatballManager!!.addMenuItem(personItem)
                .addMenuItem(settingItem)
                .addMenuItem(walletItem)
                .buildMenu()
    }

    fun requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图

            return
        }

        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION)
    }
    private fun setFloatballVisible(visible: Boolean) {
        if (visible) {
            mFloatballManager!!.show()
        } else {
            mFloatballManager!!.hide()
        }
    }

    fun isApplicationInForeground(): Boolean {
        return resumed > 0
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.show()
        }
        //mFloatballManager!!.onFloatBallClick()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
    }

    /**
     * 跳转到桌面
     */
    fun goToDesktop(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(intent)
    }
    /**
     * 跳转到桌面
     */
    fun goToApp(context: Context) {
        val intent = Intent("android.intent.action.MAIN")
        intent.component = ComponentName(applicationContext.packageName, MainActivity::class.java.name)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }
}
