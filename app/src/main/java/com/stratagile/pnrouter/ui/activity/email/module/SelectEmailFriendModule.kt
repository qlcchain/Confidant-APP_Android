package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.SelectEmailFriendActivity
import com.stratagile.pnrouter.ui.activity.email.contract.SelectEmailFriendContract
import com.stratagile.pnrouter.ui.activity.email.presenter.SelectEmailFriendPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of SelectEmailFriendActivity, provide field for SelectEmailFriendActivity
 * @date 2019/07/23 17:37:47
 */
@Module
class SelectEmailFriendModule (private val mView: SelectEmailFriendContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectEmailFriendPresenter(httpAPIWrapper: HttpAPIWrapper) :SelectEmailFriendPresenter {
        return SelectEmailFriendPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectEmailFriendActivity() : SelectEmailFriendActivity {
        return mView as SelectEmailFriendActivity
    }
}