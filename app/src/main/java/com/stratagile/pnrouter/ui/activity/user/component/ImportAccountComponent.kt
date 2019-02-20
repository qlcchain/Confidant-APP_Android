package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.ImportAccountActivity
import com.stratagile.pnrouter.ui.activity.user.module.ImportAccountModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for ImportAccountActivity
 * @date 2019/02/20 14:43:29
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ImportAccountModule::class))
interface ImportAccountComponent {
    fun inject(ImportAccountActivity: ImportAccountActivity): ImportAccountActivity
}