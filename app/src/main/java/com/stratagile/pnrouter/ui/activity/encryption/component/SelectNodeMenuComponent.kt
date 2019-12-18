package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SelectNodeMenuActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.SelectNodeMenuModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for SelectNodeMenuActivity
 * @date 2019/12/18 09:47:54
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectNodeMenuModule::class))
interface SelectNodeMenuComponent {
    fun inject(SelectNodeMenuActivity: SelectNodeMenuActivity): SelectNodeMenuActivity
}