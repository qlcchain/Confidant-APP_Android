package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupMemberActivity
import com.stratagile.pnrouter.ui.activity.group.module.RemoveGroupMemberModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for RemoveGroupMemberActivity
 * @date 2019/03/14 10:20:11
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RemoveGroupMemberModule::class))
interface RemoveGroupMemberComponent {
    fun inject(RemoveGroupMemberActivity: RemoveGroupMemberActivity): RemoveGroupMemberActivity
}