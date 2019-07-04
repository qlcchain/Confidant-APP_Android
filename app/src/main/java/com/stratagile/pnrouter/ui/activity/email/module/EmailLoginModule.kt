package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailLoginActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailLoginContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailLoginPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailLoginActivity, provide field for EmailLoginActivity
 * @date 2019/07/02 15:20:41
 */
@Module
class EmailLoginModule (private val mView: EmailLoginContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailLoginPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailLoginPresenter {
        return EmailLoginPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailLoginActivity() : EmailLoginActivity {
        return mView as EmailLoginActivity
    }
}