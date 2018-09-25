package com.stratagile.pnrouter.ui.activity.selectfriend.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The component for selectFriendActivity
 * @date 2018/09/25 14:58:33
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(selectFriendModule::class))
interface selectFriendComponent {
    fun inject(Activity: selectFriendActivity): selectFriendActivity
}