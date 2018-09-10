package com.stratagile.pnrouter.ui.activity.login.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.login.module.LoginActivityModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The component for LoginActivityActivity
 * @date 2018/09/10 15:05:29
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(LoginActivityModule::class))
interface LoginActivityComponent {
    fun inject(LoginActivityActivity: LoginActivityActivity): LoginActivityActivity
}