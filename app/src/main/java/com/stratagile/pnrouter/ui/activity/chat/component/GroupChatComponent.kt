package com.stratagile.pnrouter.ui.activity.chat.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.chat.module.GroupChatModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: The component for GroupChatActivity
 * @date 2019/03/18 15:06:56
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(GroupChatModule::class))
interface GroupChatComponent {
    fun inject(GroupChatActivity: GroupChatActivity): GroupChatActivity
}