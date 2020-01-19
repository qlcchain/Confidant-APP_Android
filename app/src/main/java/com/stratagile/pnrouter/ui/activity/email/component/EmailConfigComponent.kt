package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailConfigActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailConfigModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailConfigActivity
 * @date 2019/08/20 16:58:53
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailConfigModule::class))
interface EmailConfigComponent {
    fun inject(EmailConfigActivity: EmailConfigActivity): EmailConfigActivity
}