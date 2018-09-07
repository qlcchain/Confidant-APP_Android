package com.stratagile.pnrouter.application

import android.support.multidex.MultiDexApplication
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.web.*

/**
 * 作者：Android on 2017/8/1
 * 邮箱：365941593@qq.com
 * 描述：
 */
/**
 * 当前vpn的使用类型
 * 0， 为不收费，
 * 1， 收费
 */

class AppConfig : MultiDexApplication() {
    var applicationComponent: AppComponent? = null

    var messageReceiver: PNRouterServiceMessageReceiver? = null

    var messageSender: PNRouterServiceMessageSender? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupApplicationComponent()
        if (messageReceiver == null) {
            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
        }
    }

    fun getPNRouterServiceMessageSender() :  PNRouterServiceMessageSender{
        if (messageSender == null) {
            messageSender = PNRouterServiceMessageSender(Optional.fromNullable(MessageRetrievalService.getPipe()), Optional.of(SecurityEventListener(this)))
        }
        return messageSender!!
    }

//    fun getSignalServiceMessageReceiver() : PNRouterServiceMessageReceiver{
//        if (messageReceiver == null) {
//            this.messageReceiver = PNRouterServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this),
//                    APIModule.DynamicCredentialsProvider(this),
//                    BuildConfig.USER_AGENT,
//                    APIModule.PipeConnectivityListener())
//        }
//        return messageReceiver!!
//    }


    protected fun setupApplicationComponent() {
        applicationComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .aPIModule(APIModule(this))
                .build()
        applicationComponent!!.inject(this)
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


}
