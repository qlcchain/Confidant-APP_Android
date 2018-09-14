package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.activity.user.module.UserInfoModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for UserInfoActivity
 * @date 2018/09/13 22:03:00
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(UserInfoModule::class))
interface UserInfoComponent {
    fun inject(UserInfoActivity: UserInfoActivity): UserInfoActivity
}