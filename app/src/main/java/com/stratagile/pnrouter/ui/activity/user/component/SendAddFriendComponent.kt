package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.ui.activity.user.module.SendAddFriendModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for SendAddFriendActivity
 * @date 2019/01/02 11:19:43
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SendAddFriendModule::class))
interface SendAddFriendComponent {
    fun inject(SendAddFriendActivity: SendAddFriendActivity): SendAddFriendActivity
}