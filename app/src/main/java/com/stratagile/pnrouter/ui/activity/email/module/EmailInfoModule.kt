package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailInfoActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailInfoContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailInfoPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailInfoActivity, provide field for EmailInfoActivity
 * @date 2019/07/15 15:18:54
 */
@Module
class EmailInfoModule (private val mView: EmailInfoContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailInfoPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailInfoPresenter {
        return EmailInfoPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailInfoActivity() : EmailInfoActivity {
        return mView as EmailInfoActivity
    }
}