package com.stratagile.pnrouter.ui.activity.add.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.add.addFriendOrGroupActivity
import com.stratagile.pnrouter.ui.activity.add.module.addFriendOrGroupModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.add
 * @Description: The component for addFriendOrGroupActivity
 * @date 2019/04/02 16:08:05
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(addFriendOrGroupModule::class))
interface addFriendOrGroupComponent {
    fun inject(addFriendOrGroupActivity: addFriendOrGroupActivity): addFriendOrGroupActivity
}