package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ChatAndEmailSearchFragment
import com.stratagile.pnrouter.ui.activity.main.module.ChatAndEmailSearchModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for ChatAndEmailSearchFragment
 * @date 2019/08/13 15:32:23
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ChatAndEmailSearchModule::class))
interface ChatAndEmailSearchComponent {
    fun inject(ChatAndEmailSearchFragment: ChatAndEmailSearchFragment): ChatAndEmailSearchFragment
}