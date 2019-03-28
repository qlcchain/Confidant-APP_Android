package com.stratagile.pnrouter.ui.activity.router.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.SelectCircleActivity
import com.stratagile.pnrouter.ui.activity.router.module.SelectCircleModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The component for SelectCircleActivity
 * @date 2019/03/28 13:52:55
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectCircleModule::class))
interface SelectCircleComponent {
    fun inject(SelectCircleActivity: SelectCircleActivity): SelectCircleActivity
}