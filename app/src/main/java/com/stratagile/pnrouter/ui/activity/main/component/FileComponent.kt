package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.FileFragment
import com.stratagile.pnrouter.ui.activity.main.module.FileModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for FileFragment
 * @date 2018/09/10 17:32:58
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileModule::class))
interface FileComponent {
    fun inject(FileFragment: FileFragment): FileFragment
}