package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelSecondActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelSecondModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for SMSEncryptionNodelSecondActivity
 * @date 2020/02/07 23:33:10
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SMSEncryptionNodelSecondModule::class))
interface SMSEncryptionNodelSecondComponent {
    fun inject(SMSEncryptionNodelSecondActivity: SMSEncryptionNodelSecondActivity): SMSEncryptionNodelSecondActivity
}