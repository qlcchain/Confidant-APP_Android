package com.stratagile.pnrouter.ui.activity.conversation.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.ConversationActivity
import com.stratagile.pnrouter.ui.activity.conversation.module.ConversationModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The component for ConversationActivity
 * @date 2018/09/13 16:38:48
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ConversationModule::class))
interface ConversationComponent {
    fun inject(ConversationActivity: ConversationActivity): ConversationActivity
}