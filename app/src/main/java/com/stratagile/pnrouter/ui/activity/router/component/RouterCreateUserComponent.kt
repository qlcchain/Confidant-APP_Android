package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterCreateUserModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterCreateUserActivity
 * @date 2018/12/06 17:59:39
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterCreateUserModule::class))
interface RouterCreateUserComponent {
    fun inject(RouterCreateUserActivity: RouterCreateUserActivity): RouterCreateUserActivity
}