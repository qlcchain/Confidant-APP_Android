package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.GuestActivity
import com.stratagile.pnrouter.ui.activity.main.contract.GuestContract
import com.stratagile.pnrouter.ui.activity.main.presenter.GuestPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of GuestActivity, provide field for GuestActivity
 * @date 2018/09/18 14:25:55
 */
@Module
class GuestModule (private val mView: GuestContract.View) {

    @Provides
    @ActivityScope
    fun provideGuestPresenter(httpAPIWrapper: HttpAPIWrapper) :GuestPresenter {
        return GuestPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideGuestActivity() : GuestActivity {
        return mView as GuestActivity
    }
}