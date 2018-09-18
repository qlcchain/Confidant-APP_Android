package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.ui.activity.main.contract.LogContract
import com.stratagile.pnrouter.ui.activity.main.presenter.LogPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of LogActivity, provide field for LogActivity
 * @date 2018/09/18 09:45:46
 */
@Module
class LogModule (private val mView: LogContract.View) {

    @Provides
    @ActivityScope
    fun provideLogPresenter(httpAPIWrapper: HttpAPIWrapper) :LogPresenter {
        return LogPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideLogActivity() : LogActivity {
        return mView as LogActivity
    }
}