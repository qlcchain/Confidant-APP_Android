package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskManagementActivity
import com.stratagile.pnrouter.ui.activity.router.module.DiskManagementModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for DIsManagementActivity
 * @date 2019/01/28 11:29:37
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(DiskManagementModule::class))
interface DiskManagementComponent {
    fun inject(DIsManagementActivity: DiskManagementActivity): DiskManagementActivity
}