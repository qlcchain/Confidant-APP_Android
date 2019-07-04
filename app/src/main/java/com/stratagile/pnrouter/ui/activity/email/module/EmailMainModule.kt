package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailMainActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailMainContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailMainPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailMainActivity, provide field for EmailMainActivity
 * @date 2019/07/02 15:22:53
 */
@Module
class EmailMainModule (private val mView: EmailMainContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailMainPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailMainPresenter {
        return EmailMainPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailMainActivity() : EmailMainActivity {
        return mView as EmailMainActivity
    }
}