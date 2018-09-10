package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SplashActivity
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of SplashActivity, provide field for SplashActivity
 * @date 2018/09/10 22:25:34
 */
@Module
class SplashModule (private val mView: SplashContract.View) {

    @Provides
    @ActivityScope
    fun provideSplashPresenter(httpAPIWrapper: HttpAPIWrapper) :SplashPresenter {
        return SplashPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSplashActivity() : SplashActivity {
        return mView as SplashActivity
    }
}