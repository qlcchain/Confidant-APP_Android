package com.stratagile.pnrouter.ui.activity.email.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailEditActivity
import com.stratagile.pnrouter.ui.activity.email.module.EmailEditModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The component for EmailEditActivity
 * @date 2019/08/13 09:58:11
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EmailEditModule::class))
interface EmailEditComponent {
    fun inject(EmailEditActivity: EmailEditActivity): EmailEditActivity
}