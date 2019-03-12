package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.CreateGroupActivity
import com.stratagile.pnrouter.ui.activity.group.module.CreateGroupModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for CreateGroupActivity
 * @date 2019/03/12 15:29:49
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(CreateGroupModule::class))
interface CreateGroupComponent {
    fun inject(CreateGroupActivity: CreateGroupActivity): CreateGroupActivity
}