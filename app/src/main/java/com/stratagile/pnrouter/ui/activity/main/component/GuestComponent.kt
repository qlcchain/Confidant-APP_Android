package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.GuestActivity
import com.stratagile.pnrouter.ui.activity.main.module.GuestModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for GuestActivity
 * @date 2018/09/18 14:25:55
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GuestModule::class))
interface GuestComponent {
    fun inject(GuestActivity: GuestActivity): GuestActivity
}