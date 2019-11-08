package com.stratagile.pnrouter.application

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.IBinder
import android.os.Process
import android.support.multidex.MultiDexApplication
import android.support.v4.app.NotificationCompat
import cn.jpush.android.api.JPushInterface
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.GmailRequestInitializer
import com.google.api.services.gmail.GmailScopes
import com.huawei.android.hms.agent.HMSAgent
import com.hyphenate.easeui.EaseUI
import com.message.MessageProvider
import com.message.UserProvider
import com.smailnet.eamil.EmailConfig
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.service.BackGroundService
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.tox.ToxMessageReceiver
import com.stratagile.pnrouter.data.web.*
import com.stratagile.pnrouter.data.web.Optional
import com.stratagile.pnrouter.db.DaoMaster
import com.stratagile.pnrouter.db.MySQLiteOpenHelper
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.HeartBeatReq
import com.stratagile.pnrouter.entity.JGroupMsgPushRsp
import com.stratagile.pnrouter.entity.JPushMsgRsp
import com.stratagile.pnrouter.entity.events.StartVerify
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.KotlinToxService
import com.tencent.bugly.crashreport.CrashReport
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.MiPushClient
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 作者：Android on 2017/8/1
 * 邮箱：365941593@qq.com
 * 描述：
 */


class AppConfig : MultiDexApplication() {

    var emailConfig: EmailConfig? = null     //设置全局emailConfig
    var name: Long = 0
    val MI_PUSH_APP_ID = "2882303761517914075"
    val MI_PUSH_APP_KEY = "5221791411075"
    val FOREGROUND_ID = 313399
    var applicationComponent: AppComponent? = null

    var isBackGroud = false

    var onToxMessageReceiveListener: WebSocketConnection.OnMessageReceiveListener? = null

    var messageReceiver: PNRouterServiceMessageReceiver? = null

    var messageSender: PNRouterServiceMessageSender? = null
    lateinit var mAppActivityManager: AppActivityManager

    var messageToxReceiver: ToxMessageReceiver? = null

    var mDaoMaster: DaoMaster? = null

    val point = Point()

    var isChatWithFirend: String? = null
    var tempPushMsgList: ArrayList<JPushMsgRsp> = ArrayList<JPushMsgRsp>()
    var tempPushGroupMsgList: ArrayList<JGroupMsgPushRsp> = ArrayList<JGroupMsgPushRsp>()
    var options = RequestOptions()
            .centerCrop()
            .transform(GlideCircleTransformMainColor(this))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .priority(Priority.HIGH)

    var credential: GoogleAccountCredential? = null

    var mService: com.google.api.services.gmail.Gmail? = null
    var SCOPES = arrayOf(GmailScopes.GMAIL_LABELS, GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY)
    var transport = AndroidHttp.newCompatibleTransport()
    var jsonFactory = GsonFactory.getDefaultInstance()

    override fun onCreate() {
        super.onCreate()
        KLog.i("超时调试：10"+this)
//        CrashHandler.instance.init(this)
        credential = GoogleAccountCredential.usingOAuth2(
                applicationContext, Arrays.asList(*SCOPES))
                .setBackOff(ExponentialBackOff())
        mService = com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("com.stratagile.pnrouter")
                .setGmailRequestInitializer(GmailRequestInitializer("873428561545-i01gqi3hsp0rkjs2u21ql0msjgu0qgnv.apps.googleusercontent.com"))
                .build()
        emailConfig = EmailConfig()
        name = System.currentTimeMillis()
        CrashReport.initCrashReport(applicationContext, "22ae8f7fc8", BuildConfig.DEBUG)
        EaseUI.getInstance().init(this, null)
        //EMClient.getInstance().setDebugMode(true)
        instance = this
        setupApplicationComponent()
        setDatabase()
        MessageProvider.getInstance()
        KLog.init(BuildConfig.LOG_DEBUG)
        mAppActivityManager = AppActivityManager(this)
        UserProvider.init()
        if (VersionUtil.getDeviceBrand() == 3) {
            KLog.i("华为推送初始化")
            HMSAgent.init(this)
        }else{
            initMiPush()
        }
        loadLibrary()
        messageToxReceiver = ToxMessageReceiver()
        initResumeListener()
        /*if (TextSecurePreferences.isFcmDisabled(this)) {
           ContextCompat.startForegroundService(this, Intent(this, ForegroundService::class.java))
        }*/
        JPushInterface.setDebugMode(BuildConfig.DEBUG)    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this)            // 初始化 JPush

        /* var intent =  Intent(this, MyService::class.java)
         var sender= PendingIntent.getService(this, 0, intent, 0);
         var alarm= getSystemService(ALARM_SERVICE) as AlarmManager;
         alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5*1000,sender);*/
//        MessageProvider.init()
        var intent = Intent(this, ForegroundService::class.java)
        startService(intent)
    }

    fun getMessageReceiverInstance(): PNRouterServiceMessageReceiver? {

        return messageReceiver
    }

    fun getPNRouterServiceMessageToxReceiver(): PNRouterServiceMessageReceiver {
        if (messageReceiver == null) {
            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
            MessageRetrievalService.registerActivityStarted(this)
//            messageReceiver!!.convsationCallBack = MessageProvider.getInstance()
            messageReceiver!!.userControlleCallBack = UserProvider.getInstance()
        }
        return messageReceiver!!
    }


    fun getPNRouterServiceMessageReceiver(reStart: Boolean): PNRouterServiceMessageReceiver {
        KLog.i("没有初始化。。 getPNRouterServiceMessageReceiver  AAAAA " + this + "##" + messageReceiver)
        if (messageReceiver == null) {
            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
            MessageRetrievalService.registerActivityStarted(this)
            messageReceiver!!.userControlleCallBack = UserProvider.getInstance()
        } else {
            getPNRouterServiceMessageReceiver()
        }
        return messageReceiver!!
    }

    fun getPNRouterServiceMessageReceiver(): PNRouterServiceMessageReceiver {
        KLog.i("超时调试：getPNRouterServiceMessageReceiver"+messageSender)
        if (messageReceiver == null) {
            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
            MessageRetrievalService.registerActivityStarted(this)
//            messageReceiver!!.convsationCallBack = MessageProvider.getInstance()
            messageReceiver!!.userControlleCallBack = UserProvider.getInstance()
        }
        return messageReceiver!!
    }

    fun getPNRouterServiceMessageSender(): PNRouterServiceMessageSender {
        KLog.i("超时调试：getPNRouterServiceMessageSender"+messageSender)
        if (messageSender == null) {
            messageSender = PNRouterServiceMessageSender(Optional.fromNullable(MessageRetrievalService.getPipe()), Optional.of(SecurityEventListener(this)))
        }else{
            messageSender!!.setPipe(Optional.fromNullable(MessageRetrievalService.getPipe()))
        }
        return messageSender!!
    }

    fun getPNRouterServiceMessageSender(reStart: Boolean): PNRouterServiceMessageSender {
        messageSender = PNRouterServiceMessageSender(Optional.fromNullable(MessageRetrievalService.getPipe()), Optional.of(SecurityEventListener(this)))
        return messageSender!!
    }


    protected fun setupApplicationComponent() {
        applicationComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .aPIModule(APIModule(this))
                .build()
        applicationComponent!!.inject(this)
    }

    fun stopAllService() {
        val intent = Intent(this, MessageRetrievalService::class.java)
        this.stopService(intent)
        if (ConstantValue.isAntox) {
            /*val intentAnTox = Intent(this, ToxService::class.java)
            this.stopService(intentAnTox)*/
        } else {
            val intentTox = Intent(this, KotlinToxService::class.java)
            this.stopService(intentTox)
        }
        val backGroundService = Intent(this, BackGroundService::class.java)
        this.stopService(backGroundService)
    }

    companion object {
        lateinit var instance: AppConfig
    }

    @Synchronized
    fun getInstance(): AppConfig {
        if (null == instance) {
            instance = AppConfig()
        }
        return instance as AppConfig
    }

    /**
     * 设置greenDao
     */
    private fun setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        //        mHelper = new DaoMaster.DevOpenHelper(this, "qlink-db", null);
        val helper = MySQLiteOpenHelper(this, "qlink-db", null)
        mDaoMaster = DaoMaster(helper.getWritableDatabase())
        //        db = mHelper.getWritableDatabase();
        //        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        //        mDaoMaster = new DaoMaster(db);
    }

    private fun initMiPush() {
        if (shouldInit()) {
            KLog.i("注册小米推送")
            reRegesterMiPush()
            MiPushClient.registerPush(this, MI_PUSH_APP_ID, MI_PUSH_APP_KEY)
        }
        val newLogger = object : LoggerInterface {
            override fun setTag(tag: String) {
                // ignore
            }

            override fun log(content: String, t: Throwable) {
                KLog.i("小米推送" + content + t)
            }

            override fun log(content: String) {
                KLog.i("小米推送" + content)
            }
        }
//        Logger.setLogger(this, newLogger)
    }

    fun reRegesterMiPush() {
        if (VersionUtil.getAppVersionCode(this) <= 675 && !SpUtil.getBoolean(this, ConstantValue.isReRegesterMiPush, false)) {
            clear("mipush")
            SpUtil.putBoolean(this, ConstantValue.isReRegesterMiPush, true)
            KLog.i("RegId解注册小米推送")
        }
    }

    fun clear(content: String) {
        var preferences = getSharedPreferences(content, Context.MODE_PRIVATE)
        var editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun initResumeListener() {
        ForegroundCallbacks.init(this)
        ForegroundCallbacks.getInstance().addListener(object : ForegroundCallbacks.Listener {
            override fun onBecameForeground() {
                KLog.i("当前程序切换到前台")
                LogUtil.addLog("当前程序切换到前台")
                isBackGroud = false
                var unlockTime = SpUtil.getLong(AppConfig.instance, ConstantValue.unlockTime, 0)
                KLog.i(unlockTime)
                KLog.i(ConstantValue.logining)
                KLog.i(Calendar.getInstance().timeInMillis - unlockTime)
                var isUnlock = SpUtil.getBoolean(this@AppConfig, ConstantValue.isUnLock, false)
                // && !BuildConfig.DEBUG
                var fingerprintSwitchFlag = SpUtil.getString(AppConfig.instance, ConstantValue.fingerprintSetting, "1")
                if ((unlockTime != 0L && Calendar.getInstance().timeInMillis - unlockTime > 5 * 60 * 1000  && !ConstantValue.isShowVerify) || (!isUnlock)) {
                    if(fingerprintSwitchFlag.equals("1") && !BuildConfig.DEBUG )
                    {
                        EventBus.getDefault().post(StartVerify())
                    }

                }
//                if((unlockTime != 0L && Calendar.getInstance().timeInMillis - unlockTime > 5 * 1 * 1000) && !ConstantValue.isShowVerify)
//                {
//                    KLog.i("发送消息，显示验证密码页面")
//                    EventBus.getDefault().post(StartVerify())
//                }
                if (ConstantValue.logining) {
                    var heartBeatReq = HeartBeatReq(SpUtil.getString(instance, ConstantValue.userId, "")!!, 0)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(heartBeatReq))
                }
                MiPushClient.clearNotification(this@AppConfig)
                JPushInterface.clearAllNotifications(this@AppConfig);
            }

            override fun onBecameBackground() {
                KLog.i("当前程序切换到后台")
                LogUtil.addLog("当前程序切换到后台")
//                EventBus.getDefault().post(BackgroudEvent())
                isBackGroud = true
                SpUtil.putLong(AppConfig.instance, ConstantValue.unlockTime, Calendar.getInstance().timeInMillis)
                //EventBus.getDefault().post(ForegroundCallBack(false))
                if (ConstantValue.logining) {
                    var heartBeatReq = HeartBeatReq(SpUtil.getString(instance, ConstantValue.userId, "")!!, 1)
                    AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(heartBeatReq))
                }
            }
        })
    }

    private fun shouldInit(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = packageName
        val myPid = Process.myPid()
        if (processInfos != null) {
            for (info in processInfos) {
                if (info.pid == myPid && mainProcessName == info.processName) {
                    return true
                }
            }
        }
        return false
    }

    class ForegroundService : Service() {

        override fun onBind(intent: Intent): IBinder? {
            return null
        }

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            super.onStartCommand(intent, flags, startId)

            val builder = NotificationCompat.Builder(AppConfig.instance, "other_v2")
            builder.setContentTitle(AppConfig.instance.getString(R.string.app_name))
            builder.setContentText(AppConfig.instance.getString(R.string.MessageRetrievalService_background_connection_enabled))
            builder.priority = NotificationCompat.PRIORITY_MIN
            builder.setWhen(0)
            builder.setSmallIcon(R.mipmap.ic_launcher)
            startForeground(313399, builder.build())

            return Service.START_STICKY
        }
    }

    fun loadLibrary() {
        try {
            KLog.i("load tox库")
            System.loadLibrary("tox")
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }

    /**
     * 获取emailConfig
     *
     * @return
     */
    fun emailConfig(): EmailConfig {
        return emailConfig!!
    }
    fun initEmailConfig()
    {
        emailConfig = EmailConfig()
    }
    fun  deleteEmailData()
    {
        this.mDaoMaster!!.newSession().emailContactsEntityDao.deleteAll()
        this.mDaoMaster!!.newSession().emailAttachEntityDao.deleteAll()
        this.mDaoMaster!!.newSession().emailAttachEntityDao.deleteAll()
        this.mDaoMaster!!.newSession().emailMessageEntityDao.deleteAll()
    }

}
