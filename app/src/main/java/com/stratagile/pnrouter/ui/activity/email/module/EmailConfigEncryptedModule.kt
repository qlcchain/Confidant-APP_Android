package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailConfigEncryptedActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailConfigEncryptedContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailConfigEncryptedPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailConfigEncryptedActivity, provide field for EmailConfigEncryptedActivity
 * @date 2019/08/20 17:26:16
 */
@Module
class EmailConfigEncryptedModule (private val mView: EmailConfigEncryptedContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailConfigEncryptedPresenter(httpAPIWrapper: HttpAPIWrapper) :EmailConfigEncryptedPresenter {
        return EmailConfigEncryptedPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailConfigEncryptedActivity() : EmailConfigEncryptedActivity {
        return mView as EmailConfigEncryptedActivity
    }
}