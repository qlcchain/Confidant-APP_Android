package com.stratagile.pnrouter.ui.activity.admin.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginSuccessActivity
import com.stratagile.pnrouter.ui.activity.admin.module.AdminLoginSuccessModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The component for AdminLoginSuccessActivity
 * @date 2019/01/19 17:18:45
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AdminLoginSuccessModule::class))
interface AdminLoginSuccessComponent {
    fun inject(AdminLoginSuccessActivity: AdminLoginSuccessActivity): AdminLoginSuccessActivity
}