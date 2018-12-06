package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterCreateUserContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterCreateUserPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterCreateUserActivity, provide field for RouterCreateUserActivity
 * @date 2018/12/06 17:59:39
 */
@Module
class RouterCreateUserModule (private val mView: RouterCreateUserContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterCreateUserPresenter(httpAPIWrapper: HttpAPIWrapper) :RouterCreateUserPresenter {
        return RouterCreateUserPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterCreateUserActivity() : RouterCreateUserActivity {
        return mView as RouterCreateUserActivity
    }
}