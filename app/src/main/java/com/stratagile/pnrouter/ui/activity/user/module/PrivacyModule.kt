package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.PrivacyActivity
import com.stratagile.pnrouter.ui.activity.user.contract.PrivacyContract
import com.stratagile.pnrouter.ui.activity.user.presenter.PrivacyPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of PrivacyActivity, provide field for PrivacyActivity
 * @date 2019/04/22 18:22:12
 */
@Module
class PrivacyModule (private val mView: PrivacyContract.View) {

    @Provides
    @ActivityScope
    fun providePrivacyPresenter(httpAPIWrapper: HttpAPIWrapper) :PrivacyPresenter {
        return PrivacyPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePrivacyActivity() : PrivacyActivity {
        return mView as PrivacyActivity
    }
}