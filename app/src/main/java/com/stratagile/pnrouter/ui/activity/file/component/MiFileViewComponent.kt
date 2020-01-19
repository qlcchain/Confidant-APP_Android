package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.MiFileViewActivity
import com.stratagile.pnrouter.ui.activity.file.module.MiFileViewModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for MiFileViewActivity
 * @date 2019/12/23 18:07:51
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MiFileViewModule::class))
interface MiFileViewComponent {
    fun inject(MiFileViewActivity: MiFileViewActivity): MiFileViewActivity
}