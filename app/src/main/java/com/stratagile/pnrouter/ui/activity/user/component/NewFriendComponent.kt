package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.module.NewFriendModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for NewFriendActivity
 * @date 2018/09/13 21:25:01
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(NewFriendModule::class))
interface NewFriendComponent {
    fun inject(NewFriendActivity: NewFriendActivity): NewFriendActivity
}