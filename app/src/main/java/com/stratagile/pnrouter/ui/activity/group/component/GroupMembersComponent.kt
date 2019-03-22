package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupMembersActivity
import com.stratagile.pnrouter.ui.activity.group.module.GroupMembersModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for GroupMembersActivity
 * @date 2019/03/22 15:19:37
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GroupMembersModule::class))
interface GroupMembersComponent {
    fun inject(GroupMembersActivity: GroupMembersActivity): GroupMembersActivity
}