package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ChatAndEmailFragment
import com.stratagile.pnrouter.ui.activity.main.module.ChatAndEmailModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for ChatAndEmailFragment
 * @date 2019/07/08 14:57:30
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ChatAndEmailModule::class))
interface ChatAndEmailComponent {
    fun inject(ChatAndEmailFragment: ChatAndEmailFragment): ChatAndEmailFragment
}