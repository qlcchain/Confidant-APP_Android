package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.ShareTempQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.module.ShareTempQRCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for ShareTempQRCodeActivity
 * @date 2019/04/17 14:04:59
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ShareTempQRCodeModule::class))
interface ShareTempQRCodeComponent {
    fun inject(ShareTempQRCodeActivity: ShareTempQRCodeActivity): ShareTempQRCodeActivity
}