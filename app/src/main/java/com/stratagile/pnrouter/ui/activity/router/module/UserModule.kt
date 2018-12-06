package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.UserFragment
import com.stratagile.pnrouter.ui.activity.router.contract.UserContract
import com.stratagile.pnrouter.ui.activity.router.presenter.UserPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of UserFragment, provide field for UserFragment
 * @date 2018/12/06 14:25:43
 */
@Module
class UserModule (private val mView: UserContract.View) {

    @Provides
    @ActivityScope
    fun provideUserPresenter(httpAPIWrapper: HttpAPIWrapper) :UserPresenter {
        return UserPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideUserFragment() : UserFragment {
        return mView as UserFragment
    }
}