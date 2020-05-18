package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailSelectAttachmentActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSelectAttachmentContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailSelectAttachmentPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailSelectAttachmentActivity, provide field for EmailSelectAttachmentActivity
 * @date 2020/05/13 15:04:52
 */
@Module
class EmailSelectAttachmentModule (private val mView: EmailSelectAttachmentContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailSelectAttachmentPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailSelectAttachmentPresenter {
        return EmailSelectAttachmentPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailSelectAttachmentActivity() : EmailSelectAttachmentActivity {
        return mView as EmailSelectAttachmentActivity
    }
}