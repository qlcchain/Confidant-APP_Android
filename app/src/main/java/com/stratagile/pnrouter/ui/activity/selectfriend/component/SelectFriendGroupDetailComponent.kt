package com.stratagile.pnrouter.ui.activity.selectfriend.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendGroupDetailActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.module.SelectFriendGroupDetailModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The component for SelectFriendGroupDetailActivity
 * @date 2019/03/21 10:15:49
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SelectFriendGroupDetailModule::class))
interface SelectFriendGroupDetailComponent {
    fun inject(SelectFriendGroupDetailActivity: SelectFriendGroupDetailActivity): SelectFriendGroupDetailActivity
}