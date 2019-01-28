package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskConfigureActivity
import com.stratagile.pnrouter.ui.activity.router.module.DiskConfigureModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for DiskConfigureActivity
 * @date 2019/01/28 14:51:22
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(DiskConfigureModule::class))
interface DiskConfigureComponent {
    fun inject(DiskConfigureActivity: DiskConfigureActivity): DiskConfigureActivity
}