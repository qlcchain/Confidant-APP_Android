package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of SMSEncryptionActivity, provide field for SMSEncryptionActivity
 * @date 2020/01/17 14:47:42
 */
@Module
class SMSEncryptionModule (private val mView: SMSEncryptionContract.View) {

    @Provides
    @ActivityScope
    fun provideSMSEncryptionPresenter(httpAPIWrapper: HttpAPIWrapper) :SMSEncryptionPresenter {
        return SMSEncryptionPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSMSEncryptionActivity() : SMSEncryptionActivity {
        return mView as SMSEncryptionActivity
    }
}