package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.NewGroupFragment
import com.stratagile.pnrouter.ui.activity.user.contract.NewGroupContract
import com.stratagile.pnrouter.ui.activity.user.presenter.NewGroupPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of NewGroupFragment, provide field for NewGroupFragment
 * @date 2019/03/21 19:41:45
 */
@Module
class NewGroupModule (private val mView: NewGroupContract.View) {

    @Provides
    @ActivityScope
    fun provideNewGroupPresenter(httpAPIWrapper: HttpAPIWrapper) :NewGroupPresenter {
        return NewGroupPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideNewGroupFragment() : NewGroupFragment {
        return mView as NewGroupFragment
    }
}