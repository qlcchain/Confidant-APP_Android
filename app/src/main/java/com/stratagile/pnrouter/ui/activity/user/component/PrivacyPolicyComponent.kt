package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.PrivacyPolicyFragment
import com.stratagile.pnrouter.ui.activity.user.module.PrivacyPolicyModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for PrivacyPolicyFragment
 * @date 2019/04/22 18:24:47
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PrivacyPolicyModule::class))
interface PrivacyPolicyComponent {
    fun inject(PrivacyPolicyFragment: PrivacyPolicyFragment): PrivacyPolicyFragment
}