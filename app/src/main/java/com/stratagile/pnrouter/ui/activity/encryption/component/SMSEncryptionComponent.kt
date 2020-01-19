package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for SMSEncryptionActivity
 * @date 2020/01/17 14:47:42
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SMSEncryptionModule::class))
interface SMSEncryptionComponent {
    fun inject(SMSEncryptionActivity: SMSEncryptionActivity): SMSEncryptionActivity
}