package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailFileAttachmentShowActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailFileAttachmentShowModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailFileAttachmentShowActivity
 * @date 2020/05/14 10:45:17
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailFileAttachmentShowModule::class))
interface EmailFileAttachmentShowComponent {
    fun inject(EmailFileAttachmentShowActivity: EmailFileAttachmentShowActivity): EmailFileAttachmentShowActivity
}