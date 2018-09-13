package com.stratagile.pnrouter.ui.activity.chat.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.presenter.ChatPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: The moduele of ChatActivity, provide field for ChatActivity
 * @date 2018/09/13 13:18:46
 */
@Module
class ChatModule (private val mView: ChatContract.View) {

    @Provides
    @ActivityScope
    fun provideChatPresenter(httpAPIWrapper: HttpAPIWrapper, mActivity : ChatActivity) :ChatPresenter {
        return ChatPresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideChatActivity() : ChatActivity {
        return mView as ChatActivity
    }
}