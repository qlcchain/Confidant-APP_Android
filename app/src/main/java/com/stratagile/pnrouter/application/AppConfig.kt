package com.stratagile.pnrouter.application

import android.support.multidex.MultiDexApplication

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

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupApplicationComponent()
    }


    protected fun setupApplicationComponent() {
        applicationComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .aPIModule(APIModule(this))
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
