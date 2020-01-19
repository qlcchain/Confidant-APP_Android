package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailConfigActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailConfigContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailConfigPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailConfigActivity, provide field for EmailConfigActivity
 * @date 2019/08/20 16:58:53
 */
@Module
class EmailConfigModule (private val mView: EmailConfigContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailConfigPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailConfigPresenter {
        return EmailConfigPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailConfigActivity() : EmailConfigActivity {
        return mView as EmailConfigActivity
    }
}