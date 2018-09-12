package com.stratagile.pnrouter.ui.activity.login.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.SelectRouterActivity
import com.stratagile.pnrouter.ui.activity.login.module.SelectRouterModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The component for SelectRouterActivity
 * @date 2018/09/12 13:59:14
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectRouterModule::class))
interface SelectRouterComponent {
    fun inject(SelectRouterActivity: SelectRouterActivity): SelectRouterActivity
}