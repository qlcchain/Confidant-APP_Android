package com.stratagile.pnrouter.ui.activity.chat.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.chat.contract.GroupChatContract
import com.stratagile.pnrouter.ui.activity.chat.presenter.GroupChatPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: The moduele of GroupChatActivity, provide field for GroupChatActivity
 * @date 2019/03/18 15:06:56
 */
@Module
class GroupChatModule (private val mView: GroupChatContract.View) {

    @Provides
    @ActivityScope
    fun provideGroupChatPresenter(httpAPIWrapper: HttpAPIWrapper) :GroupChatPresenter {
        return GroupChatPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideGroupChatActivity() : GroupChatActivity {
        return mView as GroupChatActivity
    }
}