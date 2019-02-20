package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.CreateLocalAccountActivity
import com.stratagile.pnrouter.ui.activity.user.module.CreateLocalAccountModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for CreateLocalAccountActivity
 * @date 2019/02/20 14:14:35
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(CreateLocalAccountModule::class))
interface CreateLocalAccountComponent {
    fun inject(CreateLocalAccountActivity: CreateLocalAccountActivity): CreateLocalAccountActivity
}