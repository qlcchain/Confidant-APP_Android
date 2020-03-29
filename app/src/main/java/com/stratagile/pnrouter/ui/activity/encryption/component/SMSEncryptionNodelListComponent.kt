package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionNodelListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for SMSEncryptionNodelListActivity
 * @date 2020/02/05 14:49:08
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SMSEncryptionNodelListModule::class))
interface SMSEncryptionNodelListComponent {
    fun inject(SMSEncryptionNodelListActivity: SMSEncryptionNodelListActivity): SMSEncryptionNodelListActivity
}