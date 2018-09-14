package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.AddFreindActivity
import com.stratagile.pnrouter.ui.activity.user.contract.AddFreindContract
import com.stratagile.pnrouter.ui.activity.user.presenter.AddFreindPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of AddFreindActivity, provide field for AddFreindActivity
 * @date 2018/09/13 17:42:11
 */
@Module
class AddFreindModule (private val mView: AddFreindContract.View) {

    @Provides
    @ActivityScope
    fun provideAddFreindPresenter(httpAPIWrapper: HttpAPIWrapper) :AddFreindPresenter {
        return AddFreindPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideAddFreindActivity() : AddFreindActivity {
        return mView as AddFreindActivity
    }
}