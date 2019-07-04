package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailMainActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailMainModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailMainActivity
 * @date 2019/07/02 15:22:53
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailMainModule::class))
interface EmailMainComponent {
    fun inject(EmailMainActivity: EmailMainActivity): EmailMainActivity
}