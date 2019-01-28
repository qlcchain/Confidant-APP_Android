package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskInformationActivity
import com.stratagile.pnrouter.ui.activity.router.module.DiskInformationModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for DiskInformationActivity
 * @date 2019/01/28 15:21:12
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(DiskInformationModule::class))
interface DiskInformationComponent {
    fun inject(DiskInformationActivity: DiskInformationActivity): DiskInformationActivity
}