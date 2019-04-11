package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.FriendAvatarActivity
import com.stratagile.pnrouter.ui.activity.user.module.FriendAvatarModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for FriendAvatarActivity
 * @date 2019/04/11 18:10:07
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FriendAvatarModule::class))
interface FriendAvatarComponent {
    fun inject(FriendAvatarActivity: FriendAvatarActivity): FriendAvatarActivity
}