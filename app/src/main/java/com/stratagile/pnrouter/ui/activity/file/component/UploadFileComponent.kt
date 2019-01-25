package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.UploadFileActivity
import com.stratagile.pnrouter.ui.activity.file.module.UploadFileModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for UploadFileActivity
 * @date 2019/01/25 14:59:07
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(UploadFileModule::class))
interface UploadFileComponent {
    fun inject(UploadFileActivity: UploadFileActivity): UploadFileActivity
}