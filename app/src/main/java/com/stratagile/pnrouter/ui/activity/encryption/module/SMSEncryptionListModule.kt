package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SMSEncryptionListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of SMSEncryptionListActivity, provide field for SMSEncryptionListActivity
 * @date 2020/02/05 14:48:11
 */
@Module
class SMSEncryptionListModule (private val mView: SMSEncryptionListContract.View) {

    @Provides
    @ActivityScope
    fun provideSMSEncryptionListPresenter(httpAPIWrapper: HttpAPIWrapper) :SMSEncryptionListPresenter {
        return SMSEncryptionListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSMSEncryptionListActivity() : SMSEncryptionListActivity {
        return mView as SMSEncryptionListActivity
    }
}