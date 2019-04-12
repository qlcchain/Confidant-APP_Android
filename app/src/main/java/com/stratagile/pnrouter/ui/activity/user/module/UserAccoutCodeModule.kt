package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.UserAccoutCodeActivity
import com.stratagile.pnrouter.ui.activity.user.contract.UserAccoutCodeContract
import com.stratagile.pnrouter.ui.activity.user.presenter.UserAccoutCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of UserAccoutCodeActivity, provide field for UserAccoutCodeActivity
 * @date 2019/04/12 19:25:26
 */
@Module
class UserAccoutCodeModule (private val mView: UserAccoutCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideUserAccoutCodePresenter(httpAPIWrapper: HttpAPIWrapper) :UserAccoutCodePresenter {
        return UserAccoutCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideUserAccoutCodeActivity() : UserAccoutCodeActivity {
        return mView as UserAccoutCodeActivity
    }
}