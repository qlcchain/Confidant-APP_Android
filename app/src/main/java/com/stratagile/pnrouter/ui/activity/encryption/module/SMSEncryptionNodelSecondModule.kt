package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelSecondActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelSecondContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelSecondPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of SMSEncryptionNodelSecondActivity, provide field for SMSEncryptionNodelSecondActivity
 * @date 2020/02/07 23:33:10
 */
@Module
class SMSEncryptionNodelSecondModule (private val mView: SMSEncryptionNodelSecondContract.View) {

    @Provides
    @ActivityScope
    fun provideSMSEncryptionNodelSecondPresenter(httpAPIWrapper: HttpAPIWrapper) :SMSEncryptionNodelSecondPresenter {
        return SMSEncryptionNodelSecondPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSMSEncryptionNodelSecondActivity() : SMSEncryptionNodelSecondActivity {
        return mView as SMSEncryptionNodelSecondActivity
    }
}