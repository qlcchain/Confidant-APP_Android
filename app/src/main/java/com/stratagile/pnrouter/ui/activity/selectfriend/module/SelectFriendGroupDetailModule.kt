package com.stratagile.pnrouter.ui.activity.selectfriend.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendGroupDetailActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendGroupDetailContract
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.SelectFriendGroupDetailPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The moduele of SelectFriendGroupDetailActivity, provide field for SelectFriendGroupDetailActivity
 * @date 2019/03/21 10:15:49
 */
@Module
class SelectFriendGroupDetailModule (private val mView: SelectFriendGroupDetailContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectFriendGroupDetailPresenter(httpAPIWrapper: HttpAPIWrapper) :SelectFriendGroupDetailPresenter {
        return SelectFriendGroupDetailPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectFriendGroupDetailActivity() : SelectFriendGroupDetailActivity {
        return mView as SelectFriendGroupDetailActivity
    }
}