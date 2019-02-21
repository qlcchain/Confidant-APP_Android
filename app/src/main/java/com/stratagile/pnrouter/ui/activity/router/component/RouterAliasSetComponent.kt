package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterAliasSetActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterAliasSetModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterAliasSetActivity
 * @date 2019/02/21 11:00:31
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterAliasSetModule::class))
interface RouterAliasSetComponent {
    fun inject(RouterAliasSetActivity: RouterAliasSetActivity): RouterAliasSetActivity
}