package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileSendShareActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileSendShareModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for FileSendShareActivity
 * @date 2019/04/12 15:17:33
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileSendShareModule::class))
interface FileSendShareComponent {
    fun inject(FileSendShareActivity: FileSendShareActivity): FileSendShareActivity
}