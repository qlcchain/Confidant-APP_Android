package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionNodelListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of SMSEncryptionNodelListActivity, provide field for SMSEncryptionNodelListActivity
 * @date 2020/02/05 14:49:08
 */
@Module
class SMSEncryptionNodelListModule (private val mView: SMSEncryptionNodelListContract.View) {

    @Provides
    @ActivityScope
    fun provideSMSEncryptionNodelListPresenter(httpAPIWrapper: HttpAPIWrapper) :SMSEncryptionNodelListPresenter {
        return SMSEncryptionNodelListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSMSEncryptionNodelListActivity() : SMSEncryptionNodelListActivity {
        return mView as SMSEncryptionNodelListActivity
    }
}