package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.FriendAvatarActivity
import com.stratagile.pnrouter.ui.activity.user.contract.FriendAvatarContract
import com.stratagile.pnrouter.ui.activity.user.presenter.FriendAvatarPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of FriendAvatarActivity, provide field for FriendAvatarActivity
 * @date 2019/04/11 18:10:07
 */
@Module
class FriendAvatarModule (private val mView: FriendAvatarContract.View) {

    @Provides
    @ActivityScope
    fun provideFriendAvatarPresenter(httpAPIWrapper: HttpAPIWrapper) :FriendAvatarPresenter {
        return FriendAvatarPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFriendAvatarActivity() : FriendAvatarActivity {
        return mView as FriendAvatarActivity
    }
}