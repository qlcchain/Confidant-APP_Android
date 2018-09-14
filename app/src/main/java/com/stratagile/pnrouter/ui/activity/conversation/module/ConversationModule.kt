package com.stratagile.pnrouter.ui.activity.conversation.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.ConversationActivity
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationContract
import com.stratagile.pnrouter.ui.activity.conversation.presenter.ConversationPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The moduele of ConversationActivity, provide field for ConversationActivity
 * @date 2018/09/13 16:38:48
 */
@Module
class ConversationModule (private val mView: ConversationContract.View) {

    @Provides
    @ActivityScope
    fun provideConversationPresenter(httpAPIWrapper: HttpAPIWrapper) :ConversationPresenter {
        return ConversationPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideConversationActivity() : ConversationActivity {
        return mView as ConversationActivity
    }
}