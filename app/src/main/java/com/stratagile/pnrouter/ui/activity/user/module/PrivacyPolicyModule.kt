package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.PrivacyPolicyFragment
import com.stratagile.pnrouter.ui.activity.user.contract.PrivacyPolicyContract
import com.stratagile.pnrouter.ui.activity.user.presenter.PrivacyPolicyPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of PrivacyPolicyFragment, provide field for PrivacyPolicyFragment
 * @date 2019/04/22 18:24:47
 */
@Module
class PrivacyPolicyModule (private val mView: PrivacyPolicyContract.View) {

    @Provides
    @ActivityScope
    fun providePrivacyPolicyPresenter(httpAPIWrapper: HttpAPIWrapper) :PrivacyPolicyPresenter {
        return PrivacyPolicyPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePrivacyPolicyFragment() : PrivacyPolicyFragment {
        return mView as PrivacyPolicyFragment
    }
}