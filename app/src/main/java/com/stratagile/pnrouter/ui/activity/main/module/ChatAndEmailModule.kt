package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ChatAndEmailFragment
import com.stratagile.pnrouter.ui.activity.main.contract.ChatAndEmailContract
import com.stratagile.pnrouter.ui.activity.main.presenter.ChatAndEmailPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of ChatAndEmailFragment, provide field for ChatAndEmailFragment
 * @date 2019/07/08 14:57:30
 */
@Module
class ChatAndEmailModule (private val mView: ChatAndEmailContract.View) {

    @Provides
    @ActivityScope
    fun provideChatAndEmailPresenter(httpAPIWrapper: HttpAPIWrapper) :ChatAndEmailPresenter {
        return ChatAndEmailPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideChatAndEmailFragment() : ChatAndEmailFragment {
        return mView as ChatAndEmailFragment
    }
}