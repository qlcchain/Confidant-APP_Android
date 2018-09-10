package com.stratagile.pnrouter.ui.activity.conversation.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.ConversationListFragment
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationListContract
import com.stratagile.pnrouter.ui.activity.conversation.presenter.ConversationListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The moduele of ConversationListFragment, provide field for ConversationListFragment
 * @date 2018/09/10 17:25:57
 */
@Module
class ConversationListModule (private val mView: ConversationListContract.View) {

    @Provides
    @ActivityScope
    fun provideConversationListPresenter(httpAPIWrapper: HttpAPIWrapper) :ConversationListPresenter {
        return ConversationListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideConversationListFragment() : ConversationListFragment {
        return mView as ConversationListFragment
    }
}