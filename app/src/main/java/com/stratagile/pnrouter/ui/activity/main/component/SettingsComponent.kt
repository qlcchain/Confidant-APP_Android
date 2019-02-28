package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SettingsActivity
import com.stratagile.pnrouter.ui.activity.main.module.SettingsModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for SettingsActivity
 * @date 2019/02/28 14:55:22
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SettingsModule::class))
interface SettingsComponent {
    fun inject(SettingsActivity: SettingsActivity): SettingsActivity
}