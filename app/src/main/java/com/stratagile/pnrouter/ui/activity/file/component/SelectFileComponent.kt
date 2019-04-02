package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.SelectFileActivity
import com.stratagile.pnrouter.ui.activity.file.module.SelectFileModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for SelectFileActivity
 * @date 2019/04/02 17:51:39
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectFileModule::class))
interface SelectFileComponent {
    fun inject(SelectFileActivity: SelectFileActivity): SelectFileActivity
}