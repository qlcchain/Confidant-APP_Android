package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.TermsOfServiceFragment
import com.stratagile.pnrouter.ui.activity.user.contract.TermsOfServiceContract
import com.stratagile.pnrouter.ui.activity.user.presenter.TermsOfServicePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of TermsOfServiceFragment, provide field for TermsOfServiceFragment
 * @date 2019/04/22 18:23:24
 */
@Module
class TermsOfServiceModule (private val mView: TermsOfServiceContract.View) {

    @Provides
    @ActivityScope
    fun provideTermsOfServicePresenter(httpAPIWrapper: HttpAPIWrapper) :TermsOfServicePresenter {
        return TermsOfServicePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideTermsOfServiceFragment() : TermsOfServiceFragment {
        return mView as TermsOfServiceFragment
    }
}