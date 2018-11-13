package com.stratagile.pnrouter.ui.activity.register.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.register.module.RegisterModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.register
 * @Description: The component for RegisterActivity
 * @date 2018/11/12 11:53:06
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RegisterModule::class))
interface RegisterComponent {
    fun inject(RegisterActivity: RegisterActivity): RegisterActivity
}