package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.ContactsEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.ContactsEncryptionModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for ContactsEncryptionActivity
 * @date 2020/01/07 15:46:53
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ContactsEncryptionModule::class))
interface ContactsEncryptionComponent {
    fun inject(ContactsEncryptionActivity: ContactsEncryptionActivity): ContactsEncryptionActivity
}