package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterManagementActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterManagementContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterManagementPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterManagementActivity, provide field for RouterManagementActivity
 * @date 2018/09/26 10:29:17
 */
@Module
class RouterManagementModule (private val mView: RouterManagementContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterManagementPresenter(httpAPIWrapper: HttpAPIWrapper) :RouterManagementPresenter {
        return RouterManagementPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterManagementActivity() : RouterManagementActivity {
        return mView as RouterManagementActivity
    }
}