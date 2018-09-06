package com.stratagile.pnrouter.application

import android.support.multidex.MultiDexApplication
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.data.web.SignalServiceMessageReceiver
import com.stratagile.pnrouter.data.web.SignalServiceNetworkAccess

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
        private set
    open var messageReceiver: SignalServiceMessageReceiver? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        setupApplicationComponent()
        if (this.messageReceiver == null) {
            this.messageReceiver = SignalServiceMessageReceiver(SignalServiceNetworkAccess(this).getConfiguration(this)!!,
                    APIModule.DynamicCredentialsProvider(this),
                    BuildConfig.USER_AGENT,
                    APIModule.PipeConnectivityListener())
        }
    }


    protected fun setupApplicationComponent() {
        var signalServiceNetworkAccess = SignalServiceNetworkAccess(this)
        applicationComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .aPIModule(APIModule(this, signalServiceNetworkAccess))
                .build()
        applicationComponent!!.inject(this)
    }

    companion object {
        var instance: AppConfig? = null
    }

    @Synchronized
    fun getInstance(): AppConfig {
        if (null == instance) {
            instance = AppConfig()
        }
        return instance as AppConfig
    }


}
