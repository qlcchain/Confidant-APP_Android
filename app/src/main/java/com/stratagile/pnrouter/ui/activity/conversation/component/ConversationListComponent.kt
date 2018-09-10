package com.stratagile.pnrouter.ui.activity.conversation.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.ConversationListFragment
import com.stratagile.pnrouter.ui.activity.conversation.module.ConversationListModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The component for ConversationListFragment
 * @date 2018/09/10 17:25:57
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ConversationListModule::class))
interface ConversationListComponent {
    fun inject(ConversationListFragment: ConversationListFragment): ConversationListFragment
}