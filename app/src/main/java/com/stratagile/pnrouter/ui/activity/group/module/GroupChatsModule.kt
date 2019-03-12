package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupChatsActivity
import com.stratagile.pnrouter.ui.activity.group.contract.GroupChatsContract
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupChatsPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of GroupChatsActivity, provide field for GroupChatsActivity
 * @date 2019/03/12 15:05:01
 */
@Module
class GroupChatsModule (private val mView: GroupChatsContract.View) {

    @Provides
    @ActivityScope
    fun provideGroupChatsPresenter(httpAPIWrapper: HttpAPIWrapper) :GroupChatsPresenter {
        return GroupChatsPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideGroupChatsActivity() : GroupChatsActivity {
        return mView as GroupChatsActivity
    }
}