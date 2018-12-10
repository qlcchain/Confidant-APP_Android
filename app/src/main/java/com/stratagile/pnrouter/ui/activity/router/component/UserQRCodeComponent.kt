package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.UserQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.module.UserQRCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for UserQRCodeActivity
 * @date 2018/12/10 17:38:20
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(UserQRCodeModule::class))
interface UserQRCodeComponent {
    fun inject(UserQRCodeActivity: UserQRCodeActivity): UserQRCodeActivity
}