package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailChooseActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailChooseModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailChooseActivity
 * @date 2019/07/10 17:41:08
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailChooseModule::class))
interface EmailChooseComponent {
    fun inject(EmailChooseActivity: EmailChooseActivity): EmailChooseActivity
}