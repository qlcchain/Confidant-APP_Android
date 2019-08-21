package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailConfigEncryptedActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailConfigEncryptedModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailConfigEncryptedActivity
 * @date 2019/08/20 17:26:16
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailConfigEncryptedModule::class))
interface EmailConfigEncryptedComponent {
    fun inject(EmailConfigEncryptedActivity: EmailConfigEncryptedActivity): EmailConfigEncryptedActivity
}