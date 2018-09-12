package com.stratagile.pnrouter.ui.activity.login.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.SelectRouterActivity
import com.stratagile.pnrouter.ui.activity.login.contract.SelectRouterContract
import com.stratagile.pnrouter.ui.activity.login.presenter.SelectRouterPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The moduele of SelectRouterActivity, provide field for SelectRouterActivity
 * @date 2018/09/12 13:59:14
 */
@Module
class SelectRouterModule (private val mView: SelectRouterContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectRouterPresenter(httpAPIWrapper: HttpAPIWrapper) :SelectRouterPresenter {
        return SelectRouterPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectRouterActivity() : SelectRouterActivity {
        return mView as SelectRouterActivity
    }
}