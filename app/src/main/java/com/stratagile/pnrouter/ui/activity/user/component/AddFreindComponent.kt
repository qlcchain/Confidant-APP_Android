package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.AddFreindActivity
import com.stratagile.pnrouter.ui.activity.user.module.AddFreindModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for AddFreindActivity
 * @date 2018/09/13 17:42:11
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AddFreindModule::class))
interface AddFreindComponent {
    fun inject(AddFreindActivity: AddFreindActivity): AddFreindActivity
}