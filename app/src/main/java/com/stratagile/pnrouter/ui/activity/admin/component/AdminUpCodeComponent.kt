package com.stratagile.pnrouter.ui.activity.admin.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminUpCodeActivity
import com.stratagile.pnrouter.ui.activity.admin.module.AdminUpCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The component for AdminUpCodeActivity
 * @date 2019/01/19 15:31:09
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(AdminUpCodeModule::class))
interface AdminUpCodeComponent {
    fun inject(AdminUpCodeActivity: AdminUpCodeActivity): AdminUpCodeActivity
}