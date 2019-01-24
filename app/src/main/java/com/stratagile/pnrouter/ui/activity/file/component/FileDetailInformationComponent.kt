package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileDetailInformationActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileDetailInformationModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for FileDetailInformationActivity
 * @date 2019/01/23 17:49:28
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileDetailInformationModule::class))
interface FileDetailInformationComponent {
    fun inject(FileDetailInformationActivity: FileDetailInformationActivity): FileDetailInformationActivity
}