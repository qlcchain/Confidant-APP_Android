package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.PdfViewActivity
import com.stratagile.pnrouter.ui.activity.file.module.PdfViewModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for PdfViewActivity
 * @date 2018/10/09 16:03:36
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PdfViewModule::class))
interface PdfViewComponent {
    fun inject(PdfViewActivity: PdfViewActivity): PdfViewActivity
}