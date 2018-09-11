package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.QRCodeActivity
import com.stratagile.pnrouter.ui.activity.user.module.QRCodeModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for QRCodeActivity
 * @date 2018/09/11 14:01:23
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(QRCodeModule::class))
interface QRCodeComponent {
    fun inject(QRCodeActivity: QRCodeActivity): QRCodeActivity
}