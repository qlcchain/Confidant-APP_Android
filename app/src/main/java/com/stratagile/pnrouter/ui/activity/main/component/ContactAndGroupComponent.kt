package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ContactAndGroupFragment
import com.stratagile.pnrouter.ui.activity.main.module.ContactAndGroupModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for ContactAndGroupFragment
 * @date 2019/03/26 11:19:29
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ContactAndGroupModule::class))
interface ContactAndGroupComponent {
    fun inject(ContactAndGroupFragment: ContactAndGroupFragment): ContactAndGroupFragment
}