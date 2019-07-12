package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EmailMessageFragment
import com.stratagile.pnrouter.ui.activity.main.module.EmailMessageModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for EmailMessageFragment
 * @date 2019/07/11 16:19:12
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailMessageModule::class))
interface EmailMessageComponent {
    fun inject(EmailMessageFragment: EmailMessageFragment): EmailMessageFragment
}