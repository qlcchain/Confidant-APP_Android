package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EmailMessageFragment
import com.stratagile.pnrouter.ui.activity.main.contract.EmailMessageContract
import com.stratagile.pnrouter.ui.activity.main.presenter.EmailMessagePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of EmailMessageFragment, provide field for EmailMessageFragment
 * @date 2019/07/11 16:19:12
 */
@Module
class EmailMessageModule (private val mView: EmailMessageContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailMessagePresenter(httpAPIWrapper: HttpAPIWrapper) :EmailMessagePresenter {
        return EmailMessagePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailMessageFragment() : EmailMessageFragment {
        return mView as EmailMessageFragment
    }
}