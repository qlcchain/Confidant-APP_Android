package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewUserFragment
import com.stratagile.pnrouter.ui.activity.user.contract.NewUserContract
import com.stratagile.pnrouter.ui.activity.user.presenter.NewUserPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of NewUserFragment, provide field for NewUserFragment
 * @date 2019/03/21 19:41:25
 */
@Module
class NewUserModule (private val mView: NewUserContract.View) {

    @Provides
    @ActivityScope
    fun provideNewUserPresenter(httpAPIWrapper: HttpAPIWrapper) :NewUserPresenter {
        return NewUserPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideNewUserFragment() : NewUserFragment {
        return mView as NewUserFragment
    }
}