package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.module.RouterQRCodeModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for RouterQRCodeActivity
 * @date 2018/09/26 11:53:16
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RouterQRCodeModule::class))
interface RouterQRCodeComponent {
    fun inject(RouterQRCodeActivity: RouterQRCodeActivity): RouterQRCodeActivity
}