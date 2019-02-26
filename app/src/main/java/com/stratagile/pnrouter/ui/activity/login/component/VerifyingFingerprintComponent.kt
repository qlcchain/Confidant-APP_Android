package com.stratagile.pnrouter.ui.activity.login.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.VerifyingFingerprintActivity
import com.stratagile.pnrouter.ui.activity.login.module.VerifyingFingerprintModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The component for VerifyingFingerprintActivity
 * @date 2019/02/26 14:40:52
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(VerifyingFingerprintModule::class))
interface VerifyingFingerprintComponent {
    fun inject(VerifyingFingerprintActivity: VerifyingFingerprintActivity): VerifyingFingerprintActivity
}