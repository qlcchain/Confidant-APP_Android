package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.CircleMemberDetailActivity
import com.stratagile.pnrouter.ui.activity.user.module.CircleMemberDetailModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for CircleMemberDetailActivity
 * @date 2019/04/17 10:21:23
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(CircleMemberDetailModule::class))
interface CircleMemberDetailComponent {
    fun inject(CircleMemberDetailActivity: CircleMemberDetailActivity): CircleMemberDetailActivity
}