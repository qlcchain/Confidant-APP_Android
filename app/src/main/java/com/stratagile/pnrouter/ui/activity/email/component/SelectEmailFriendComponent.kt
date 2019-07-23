package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.SelectEmailFriendActivity
import com.stratagile.pnrouter.ui.activity.email.module.SelectEmailFriendModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for SelectEmailFriendActivity
 * @date 2019/07/23 17:37:47
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectEmailFriendModule::class))
interface SelectEmailFriendComponent {
    fun inject(SelectEmailFriendActivity: SelectEmailFriendActivity): SelectEmailFriendActivity
}