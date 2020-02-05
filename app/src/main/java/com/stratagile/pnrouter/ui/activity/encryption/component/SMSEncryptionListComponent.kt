package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.SMSEncryptionListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for SMSEncryptionListActivity
 * @date 2020/02/05 14:48:11
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SMSEncryptionListModule::class))
interface SMSEncryptionListComponent {
    fun inject(SMSEncryptionListActivity: SMSEncryptionListActivity): SMSEncryptionListActivity
}