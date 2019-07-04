package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailLoginActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailLoginModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailLoginActivity
 * @date 2019/07/02 15:20:41
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailLoginModule::class))
interface EmailLoginComponent {
    fun inject(EmailLoginActivity: EmailLoginActivity): EmailLoginActivity
}