package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ShareFileActivity
import com.stratagile.pnrouter.ui.activity.main.module.ShareFileModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for ShareFileActivity
 * @date 2020/05/12 14:06:39
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ShareFileModule::class))
interface ShareFileComponent {
    fun inject(ShareFileActivity: ShareFileActivity): ShareFileActivity
}