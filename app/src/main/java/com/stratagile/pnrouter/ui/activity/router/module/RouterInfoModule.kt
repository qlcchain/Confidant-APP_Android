package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterInfoActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterInfoContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterInfoPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterInfoActivity, provide field for RouterInfoActivity
 * @date 2018/09/27 16:07:17
 */
@Module
class RouterInfoModule (private val mView: RouterInfoContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterInfoPresenter(httpAPIWrapper: HttpAPIWrapper) :RouterInfoPresenter {
        return RouterInfoPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterInfoActivity() : RouterInfoActivity {
        return mView as RouterInfoActivity
    }
}