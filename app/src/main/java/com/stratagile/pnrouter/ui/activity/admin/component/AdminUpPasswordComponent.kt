package com.stratagile.pnrouter.ui.activity.admin.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminUpPasswordActivity
import com.stratagile.pnrouter.ui.activity.admin.module.AdminUpPasswordModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The component for AdminUpPasswordActivity
 * @date 2019/01/19 15:30:48
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AdminUpPasswordModule::class))
interface AdminUpPasswordComponent {
    fun inject(AdminUpPasswordActivity: AdminUpPasswordActivity): AdminUpPasswordActivity
}