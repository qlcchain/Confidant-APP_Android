package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailEditActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailEditContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailEditPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailEditActivity, provide field for EmailEditActivity
 * @date 2019/08/13 09:58:11
 */
@Module
class EmailEditModule (private val mView: EmailEditContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailEditPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailEditPresenter {
        return EmailEditPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailEditActivity() : EmailEditActivity {
        return mView as EmailEditActivity
    }
}