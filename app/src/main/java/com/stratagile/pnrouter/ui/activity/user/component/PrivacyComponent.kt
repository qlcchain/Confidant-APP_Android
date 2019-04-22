package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.PrivacyActivity
import com.stratagile.pnrouter.ui.activity.user.module.PrivacyModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for PrivacyActivity
 * @date 2019/04/22 18:22:12
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PrivacyModule::class))
interface PrivacyComponent {
    fun inject(PrivacyActivity: PrivacyActivity): PrivacyActivity
}