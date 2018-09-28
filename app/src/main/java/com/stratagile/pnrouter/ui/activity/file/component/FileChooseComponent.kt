package com.stratagile.pnrouter.ui.activity.file.component


import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileChooseModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: The component for FileChooseActivity
 * @date 2018/09/28 16:46:15
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileChooseModule::class))
interface FileChooseComponent {
    fun inject(Activity: FileChooseActivity): FileChooseActivity
}