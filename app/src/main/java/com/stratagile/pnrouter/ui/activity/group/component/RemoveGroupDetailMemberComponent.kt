package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupDetailMemberActivity
import com.stratagile.pnrouter.ui.activity.group.module.RemoveGroupDetailMemberModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for RemoveGroupDetailMemberActivity
 * @date 2019/03/21 10:15:05
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RemoveGroupDetailMemberModule::class))
interface RemoveGroupDetailMemberComponent {
    fun inject(RemoveGroupDetailMemberActivity: RemoveGroupDetailMemberActivity): RemoveGroupDetailMemberActivity
}