package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailFileAttachmentShowActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailFileAttachmentShowContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailFileAttachmentShowPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailFileAttachmentShowActivity, provide field for EmailFileAttachmentShowActivity
 * @date 2020/05/14 10:45:17
 */
@Module
class EmailFileAttachmentShowModule (private val mView: EmailFileAttachmentShowContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailFileAttachmentShowPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailFileAttachmentShowPresenter {
        return EmailFileAttachmentShowPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailFileAttachmentShowActivity() : EmailFileAttachmentShowActivity {
        return mView as EmailFileAttachmentShowActivity
    }
}