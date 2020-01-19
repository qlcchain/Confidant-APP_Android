package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SearchActivity
import com.stratagile.pnrouter.ui.activity.main.module.SearchModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for SearchActivity
 * @date 2019/08/13 14:06:03
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(SearchModule::class))
interface SearchComponent {
    fun inject(SearchActivity: SearchActivity): SearchActivity
}