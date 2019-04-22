package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.TermsOfServiceFragment
import com.stratagile.pnrouter.ui.activity.user.module.TermsOfServiceModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for TermsOfServiceFragment
 * @date 2019/04/22 18:23:24
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TermsOfServiceModule::class))
interface TermsOfServiceComponent {
    fun inject(TermsOfServiceFragment: TermsOfServiceFragment): TermsOfServiceFragment
}