package com.stratagile.pnrouter.ui.activity.chat.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.module.ChatModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: The component for ChatActivity
 * @date 2018/09/13 13:18:46
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ChatModule::class))
interface ChatComponent {
    fun inject(ChatActivity: ChatActivity): ChatActivity
}