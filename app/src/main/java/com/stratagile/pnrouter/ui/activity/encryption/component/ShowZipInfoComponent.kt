package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.ShowZipInfoActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.ShowZipInfoModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for ShowZipInfoActivity
 * @date 2019/12/30 10:42:25
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ShowZipInfoModule::class))
interface ShowZipInfoComponent {
    fun inject(ShowZipInfoActivity: ShowZipInfoActivity): ShowZipInfoActivity
}