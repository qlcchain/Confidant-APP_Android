package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SettingsActivity
import com.stratagile.pnrouter.ui.activity.main.contract.SettingsContract
import com.stratagile.pnrouter.ui.activity.main.presenter.SettingsPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of SettingsActivity, provide field for SettingsActivity
 * @date 2019/02/28 14:55:22
 */
@Module
class SettingsModule (private val mView: SettingsContract.View) {

    @Provides
    @ActivityScope
    fun provideSettingsPresenter(httpAPIWrapper: HttpAPIWrapper) :SettingsPresenter {
        return SettingsPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSettingsActivity() : SettingsActivity {
        return mView as SettingsActivity
    }
}