package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileTaskListModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for FileTaskListActivity
 * @date 2019/01/25 16:21:04
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileTaskListModule::class))
interface FileTaskListComponent {
    fun inject(FileTaskListActivity: FileTaskListActivity): FileTaskListActivity
}