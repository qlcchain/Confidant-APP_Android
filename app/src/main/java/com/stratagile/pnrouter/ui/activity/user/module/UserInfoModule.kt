package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import com.stratagile.pnrouter.ui.activity.user.contract.UserInfoContract
import com.stratagile.pnrouter.ui.activity.user.presenter.UserInfoPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of UserInfoActivity, provide field for UserInfoActivity
 * @date 2018/09/13 22:03:00
 */
@Module
class UserInfoModule (private val mView: UserInfoContract.View) {

    @Provides
    @ActivityScope
    fun provideUserInfoPresenter(httpAPIWrapper: HttpAPIWrapper) :UserInfoPresenter {
        return UserInfoPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideUserInfoActivity() : UserInfoActivity {
        return mView as UserInfoActivity
    }
}