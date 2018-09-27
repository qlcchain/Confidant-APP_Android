package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterManagementActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterManagementModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterManagementActivity
 * @date 2018/09/26 10:29:17
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterManagementModule::class))
interface RouterManagementComponent {
    fun inject(RouterManagementActivity: RouterManagementActivity): RouterManagementActivity
}