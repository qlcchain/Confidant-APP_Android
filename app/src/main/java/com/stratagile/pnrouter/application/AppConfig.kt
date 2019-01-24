package com.stratagile.pnrouter.application

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.support.multidex.MultiDexApplication
import chat.tox.antox.tox.ToxService
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.hyphenate.easeui.EaseUI
import com.message.MessageProvider
import com.message.UserProvider
import com.socks.library.KLog
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.tox.ToxMessageReceiver
import com.stratagile.pnrouter.data.web.*
import com.stratagile.pnrouter.db.DaoMaster
import com.stratagile.pnrouter.db.MySQLiteOpenHelper
import com.stratagile.pnrouter.entity.JPushMsgRsp
import com.stratagile.pnrouter.utils.AppActivityManager
import com.stratagile.pnrouter.utils.GlideCircleTransformMainColor
import com.stratagile.pnrouter.utils.swipeback.BGASwipeBackHelper
import com.stratagile.tox.toxcore.KotlinToxService
import com.tencent.bugly.crashreport.CrashReport
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * 作者：Android on 2017/8/1
 * 邮箱：365941593@qq.com
 * 描述：
 */


class AppConfig : MultiDexApplication() {

    val MI_PUSH_APP_ID = "2882303761517914075"
    val MI_PUSH_APP_KEY = "5221791411075"
    var applicationComponent: AppComponent? = null

    var onToxMessageReceiveListener : WebSocketConnection.OnMessageReceiveListener? = null

    var messageReceiver: PNRouterServiceMessageReceiver? = null

    var messageSender: PNRouterServiceMessageSender? = null
    lateinit var mAppActivityManager: AppActivityManager

    var messageToxReceiver: ToxMessageReceiver? = null

    var mDaoMaster: DaoMaster? = null

    var isChatWithFirend:String? = null
    var tempPushMsgList:ArrayList<JPushMsgRsp> = ArrayList<JPushMsgRsp>()
    var options = RequestOptions()
            .centerCrop()
            .transform(GlideCircleTransformMainColor(this))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .priority(Priority.HIGH)

    override fun onCreate() {
        super.onCreate()
//        CrashHandler.instance.init(this)
        CrashReport.initCrashReport(applicationContext, "22ae8f7fc8", true)
        EaseUI.getInstance().init(this, null)
        //EMClient.getInstance().setDebugMode(true)
        instance = this
        setupApplicationComponent()
        setDatabase()
        MessageProvider.getInstance()
        KLog.init(BuildConfig.LOG_DEBUG)
        BGASwipeBackHelper.init(this, null)
        mAppActivityManager = AppActivityManager(this)
        UserProvider.init()
        initMiPush()
        loadLibrary()
        messageToxReceiver = ToxMessageReceiver()
//        MessageProvider.init()
    }
    fun getMessageReceiverInstance():  PNRouterServiceMessageReceiver{

        return messageReceiver!!
    }
    fun getPNRouterServiceMessageToxReceiver() :  PNRouterServiceMessageReceiver{
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




    fun getPNRouterServiceMessageReceiver(reStart : Boolean) : PNRouterServiceMessageReceiver{
        if (messageReceiver == null) {
            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
            MessageRetrievalService.registerActivityStarted(this)
//            messageReceiver!!.convsationCallBack = MessageProvider.getInstance()
            messageReceiver!!.userControlleCallBack = UserProvider.getInstance()
        } else {
            getPNRouterServiceMessageReceiver()
        }
        return messageReceiver!!
    }
    fun getPNRouterServiceMessageReceiver() :  PNRouterServiceMessageReceiver{
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
    fun getPNRouterServiceMessageSender() :  PNRouterServiceMessageSender{
        if (messageSender == null) {
            messageSender = PNRouterServiceMessageSender(Optional.fromNullable(MessageRetrievalService.getPipe()), Optional.of(SecurityEventListener(this)))
        }
        return messageSender!!
    }

    fun getPNRouterServiceMessageSender(reStart: Boolean) :  PNRouterServiceMessageSender{
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
    fun stopAllService()
    {
        val intent = Intent(this, MessageRetrievalService::class.java)
        this.stopService(intent)
        if(ConstantValue.isAntox)
        {
            val intentAnTox = Intent(this, ToxService::class.java)
            this.stopService(intentAnTox)
        }else{
            val intentTox = Intent(this, KotlinToxService::class.java)
            this.stopService(intentTox)
        }

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
            MiPushClient.registerPush(this, MI_PUSH_APP_ID, MI_PUSH_APP_KEY)
        }
        val newLogger = object : LoggerInterface {
            override fun setTag(tag: String) {
                // ignore
            }

          override  fun log(content: String, t: Throwable) {
                //KLog.i(content, t)
            }

            override  fun log(content: String) {
                //KLog.i(content)
            }
        }
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

    fun loadLibrary() {
        try{
            KLog.i("load tox库")
            System.loadLibrary("tox")
        } catch (exception : java.lang.Exception) {
            exception.printStackTrace()
        }
    }
}
