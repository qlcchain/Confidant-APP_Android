package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterAddUserActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterAddUserModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterAddUserActivity
 * @date 2018/12/06 11:43:15
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterAddUserModule::class))
interface RouterAddUserComponent {
    fun inject(RouterAddUserActivity: RouterAddUserActivity): RouterAddUserActivity
}