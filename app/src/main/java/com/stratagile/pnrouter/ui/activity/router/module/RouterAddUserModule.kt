package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterAddUserActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAddUserContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterAddUserPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterAddUserActivity, provide field for RouterAddUserActivity
 * @date 2018/12/06 11:43:15
 */
@Module
class RouterAddUserModule (private val mView: RouterAddUserContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterAddUserPresenter(httpAPIWrapper: HttpAPIWrapper) :RouterAddUserPresenter {
        return RouterAddUserPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterAddUserActivity() : RouterAddUserActivity {
        return mView as RouterAddUserActivity
    }
}