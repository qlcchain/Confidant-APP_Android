package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupChatsActivity
import com.stratagile.pnrouter.ui.activity.group.module.GroupChatsModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for GroupChatsActivity
 * @date 2019/03/12 15:05:01
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GroupChatsModule::class))
interface GroupChatsComponent {
    fun inject(GroupChatsActivity: GroupChatsActivity): GroupChatsActivity
}