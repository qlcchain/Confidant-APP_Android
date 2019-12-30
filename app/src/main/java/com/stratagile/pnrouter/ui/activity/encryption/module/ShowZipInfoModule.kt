package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.ShowZipInfoActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.ShowZipInfoContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ShowZipInfoPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of ShowZipInfoActivity, provide field for ShowZipInfoActivity
 * @date 2019/12/30 10:42:25
 */
@Module
class ShowZipInfoModule (private val mView: ShowZipInfoContract.View) {

    @Provides
    @ActivityScope
    fun provideShowZipInfoPresenter(httpAPIWrapper: HttpAPIWrapper) :ShowZipInfoPresenter {
        return ShowZipInfoPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideShowZipInfoActivity() : ShowZipInfoActivity {
        return mView as ShowZipInfoActivity
    }
}