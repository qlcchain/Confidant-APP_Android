package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailSelectAttachmentActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailSelectAttachmentModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailSelectAttachmentActivity
 * @date 2020/05/13 15:04:52
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailSelectAttachmentModule::class))
interface EmailSelectAttachmentComponent {
    fun inject(EmailSelectAttachmentActivity: EmailSelectAttachmentActivity): EmailSelectAttachmentActivity
}