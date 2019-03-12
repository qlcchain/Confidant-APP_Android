package com.stratagile.pnrouter.ui.activity.selectfriend.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.module.SelectFriendCreateGroupModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The component for SelectFriendCreateGroupActivity
 * @date 2019/03/12 17:49:51
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectFriendCreateGroupModule::class))
interface SelectFriendCreateGroupComponent {
    fun inject(SelectFriendCreateGroupActivity: SelectFriendCreateGroupActivity): SelectFriendCreateGroupActivity
}