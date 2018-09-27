package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.RouterQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.contract.RouterQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterQRCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of RouterQRCodeActivity, provide field for RouterQRCodeActivity
 * @date 2018/09/26 11:53:16
 */
@Module
class RouterQRCodeModule (private val mView: RouterQRCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideRouterQRCodePresenter(httpAPIWrapper: HttpAPIWrapper) :RouterQRCodePresenter {
        return RouterQRCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRouterQRCodeActivity() : RouterQRCodeActivity {
        return mView as RouterQRCodeActivity
    }
}