package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskReconfigureActivity
import com.stratagile.pnrouter.ui.activity.router.module.DiskReconfigureModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for DiskReconfigureActivity
 * @date 2019/02/18 17:31:04
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(DiskReconfigureModule::class))
interface DiskReconfigureComponent {
    fun inject(DiskReconfigureActivity: DiskReconfigureActivity): DiskReconfigureActivity
}