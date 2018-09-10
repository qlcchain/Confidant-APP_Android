package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SplashActivity
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for SplashActivity
 * @date 2018/09/10 22:25:34
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SplashModule::class))
interface SplashComponent {
    fun inject(SplashActivity: SplashActivity): SplashActivity
}