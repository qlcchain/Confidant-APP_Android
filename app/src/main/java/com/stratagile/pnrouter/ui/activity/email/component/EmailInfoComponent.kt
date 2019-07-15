package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailInfoActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailInfoModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailInfoActivity
 * @date 2019/07/15 15:18:54
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailInfoModule::class))
interface EmailInfoComponent {
    fun inject(EmailInfoActivity: EmailInfoActivity): EmailInfoActivity
}