package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.UserFragment
import com.stratagile.pnrouter.ui.activity.router.module.UserModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for UserFragment
 * @date 2018/12/06 14:25:43
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(UserModule::class))
interface UserComponent {
    fun inject(UserFragment: UserFragment): UserFragment
}