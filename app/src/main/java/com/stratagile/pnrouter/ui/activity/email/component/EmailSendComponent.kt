package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailSendModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailSendActivity
 * @date 2019/07/25 11:21:29
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailSendModule::class))
interface EmailSendComponent {
    fun inject(EmailSendActivity: EmailSendActivity): EmailSendActivity
}