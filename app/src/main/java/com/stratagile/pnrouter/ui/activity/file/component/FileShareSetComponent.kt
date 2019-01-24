package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileShareSetActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileShareSetModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for FileShareSetActivity
 * @date 2019/01/24 10:26:38
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileShareSetModule::class))
interface FileShareSetComponent {
    fun inject(FileShareSetActivity: FileShareSetActivity): FileShareSetActivity
}