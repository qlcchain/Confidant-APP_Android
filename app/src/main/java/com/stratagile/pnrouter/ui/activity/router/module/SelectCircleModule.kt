package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.SelectCircleActivity
import com.stratagile.pnrouter.ui.activity.router.contract.SelectCircleContract
import com.stratagile.pnrouter.ui.activity.router.presenter.SelectCirclePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of SelectCircleActivity, provide field for SelectCircleActivity
 * @date 2019/03/28 13:52:55
 */
@Module
class SelectCircleModule (private val mView: SelectCircleContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectCirclePresenter(httpAPIWrapper: HttpAPIWrapper) :SelectCirclePresenter {
        return SelectCirclePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectCircleActivity() : SelectCircleActivity {
        return mView as SelectCircleActivity
    }
}