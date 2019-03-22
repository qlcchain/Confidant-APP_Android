package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewUserFragment
import com.stratagile.pnrouter.ui.activity.user.module.NewUserModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for NewUserFragment
 * @date 2019/03/21 19:41:25
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(NewUserModule::class))
interface NewUserComponent {
    fun inject(NewUserFragment: NewUserFragment): NewUserFragment
}