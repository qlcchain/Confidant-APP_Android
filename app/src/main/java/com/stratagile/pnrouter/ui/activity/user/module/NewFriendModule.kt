package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewFriendActivity
import com.stratagile.pnrouter.ui.activity.user.contract.NewFriendContract
import com.stratagile.pnrouter.ui.activity.user.presenter.NewFriendPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of NewFriendActivity, provide field for NewFriendActivity
 * @date 2018/09/13 21:25:01
 */
@Module
class NewFriendModule (private val mView: NewFriendContract.View) {

    @Provides
    @ActivityScope
    fun provideNewFriendPresenter(httpAPIWrapper: HttpAPIWrapper) :NewFriendPresenter {
        return NewFriendPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideNewFriendActivity() : NewFriendActivity {
        return mView as NewFriendActivity
    }
}