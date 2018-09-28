package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterInfoActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterInfoModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterInfoActivity
 * @date 2018/09/27 16:07:17
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterInfoModule::class))
interface RouterInfoComponent {
    fun inject(RouterInfoActivity: RouterInfoActivity): RouterInfoActivity
}