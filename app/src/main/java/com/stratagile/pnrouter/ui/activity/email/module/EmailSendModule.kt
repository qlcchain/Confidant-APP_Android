package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSendContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailSendPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailSendActivity, provide field for EmailSendActivity
 * @date 2019/07/25 11:21:29
 */
@Module
class EmailSendModule (private val mView: EmailSendContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailSendPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailSendPresenter {
        return EmailSendPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailSendActivity() : EmailSendActivity {
        return mView as EmailSendActivity
    }
}