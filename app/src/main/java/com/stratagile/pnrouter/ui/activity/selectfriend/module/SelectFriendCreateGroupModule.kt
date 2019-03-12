package com.stratagile.pnrouter.ui.activity.selectfriend.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendCreateGroupContract
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.SelectFriendCreateGroupPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The moduele of SelectFriendCreateGroupActivity, provide field for SelectFriendCreateGroupActivity
 * @date 2019/03/12 17:49:51
 */
@Module
class SelectFriendCreateGroupModule (private val mView: SelectFriendCreateGroupContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectFriendCreateGroupPresenter(httpAPIWrapper: HttpAPIWrapper) :SelectFriendCreateGroupPresenter {
        return SelectFriendCreateGroupPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectFriendCreateGroupActivity() : SelectFriendCreateGroupActivity {
        return mView as SelectFriendCreateGroupActivity
    }
}