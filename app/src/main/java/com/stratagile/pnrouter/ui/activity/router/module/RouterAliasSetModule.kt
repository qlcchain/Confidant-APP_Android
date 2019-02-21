package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterAliasSetActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAliasSetContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAliasSetPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterAliasSetActivity, provide field for RouterAliasSetActivity
 * @date 2019/02/21 11:00:31
 */
@Module
class RouterAliasSetModule (private val mView: RouterAliasSetContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterAliasSetPresenter(httpAPIWrapper: HttpAPIWrapper) :RouterAliasSetPresenter {
        return RouterAliasSetPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterAliasSetActivity() : RouterAliasSetActivity {
        return mView as RouterAliasSetActivity
    }
}