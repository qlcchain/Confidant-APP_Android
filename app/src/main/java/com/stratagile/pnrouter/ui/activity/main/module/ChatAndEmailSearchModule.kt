package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ChatAndEmailSearchFragment
import com.stratagile.pnrouter.ui.activity.main.contract.ChatAndEmailSearchContract
import com.stratagile.pnrouter.ui.activity.main.presenter.ChatAndEmailSearchPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of ChatAndEmailSearchFragment, provide field for ChatAndEmailSearchFragment
 * @date 2019/08/13 15:32:23
 */
@Module
class ChatAndEmailSearchModule (private val mView: ChatAndEmailSearchContract.View) {

    @Provides
    @ActivityScope
    fun provideChatAndEmailSearchPresenter(httpAPIWrapper: HttpAPIWrapper) :ChatAndEmailSearchPresenter {
        return ChatAndEmailSearchPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideChatAndEmailSearchFragment() : ChatAndEmailSearchFragment {
        return mView as ChatAndEmailSearchFragment
    }
}