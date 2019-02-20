package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.CreateLocalAccountActivity
import com.stratagile.pnrouter.ui.activity.user.contract.CreateLocalAccountContract
import com.stratagile.pnrouter.ui.activity.user.presenter.CreateLocalAccountPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of CreateLocalAccountActivity, provide field for CreateLocalAccountActivity
 * @date 2019/02/20 14:14:35
 */
@Module
class CreateLocalAccountModule (private val mView: CreateLocalAccountContract.View) {

    @Provides
    @ActivityScope
    fun provideCreateLocalAccountPresenter(httpAPIWrapper: HttpAPIWrapper) :CreateLocalAccountPresenter {
        return CreateLocalAccountPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideCreateLocalAccountActivity() : CreateLocalAccountActivity {
        return mView as CreateLocalAccountActivity
    }
}