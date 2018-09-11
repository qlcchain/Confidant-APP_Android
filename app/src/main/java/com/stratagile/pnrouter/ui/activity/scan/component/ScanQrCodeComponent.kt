package com.stratagile.pnrouter.ui.activity.scan.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.scan.module.ScanQrCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: The component for ScanQrCodeActivity
 * @date 2018/09/11 15:29:14
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ScanQrCodeModule::class))
interface ScanQrCodeComponent {
    fun inject(ScanQrCodeActivity: ScanQrCodeActivity): ScanQrCodeActivity
}