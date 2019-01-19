package com.stratagile.pnrouter.ui.activity.admin.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.admin.module.AdminLoginModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The component for AdminLoginActivity
 * @date 2019/01/19 15:30:16
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AdminLoginModule::class))
interface AdminLoginComponent {
    fun inject(AdminLoginActivity: AdminLoginActivity): AdminLoginActivity
}