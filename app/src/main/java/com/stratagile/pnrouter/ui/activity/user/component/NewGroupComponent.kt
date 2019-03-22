package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewGroupFragment
import com.stratagile.pnrouter.ui.activity.user.module.NewGroupModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for NewGroupFragment
 * @date 2019/03/21 19:41:45
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(NewGroupModule::class))
interface NewGroupComponent {
    fun inject(NewGroupFragment: NewGroupFragment): NewGroupFragment
}