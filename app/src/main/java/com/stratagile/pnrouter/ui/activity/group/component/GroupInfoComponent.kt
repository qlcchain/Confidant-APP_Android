package com.stratagile.pnrouter.ui.activity.group.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupInfoActivity
import com.stratagile.pnrouter.ui.activity.group.module.GroupInfoModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The component for GroupInfoActivity
 * @date 2019/03/20 11:44:58
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GroupInfoModule::class))
interface GroupInfoComponent {
    fun inject(GroupInfoActivity: GroupInfoActivity): GroupInfoActivity
}