package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.UserAccoutCodeActivity
import com.stratagile.pnrouter.ui.activity.user.module.UserAccoutCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for UserAccoutCodeActivity
 * @date 2019/04/12 19:25:26
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(UserAccoutCodeModule::class))
interface UserAccoutCodeComponent {
    fun inject(UserAccoutCodeActivity: UserAccoutCodeActivity): UserAccoutCodeActivity
}