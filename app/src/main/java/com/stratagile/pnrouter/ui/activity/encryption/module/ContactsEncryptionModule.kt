package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.ContactsEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.ContactsEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ContactsEncryptionPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of ContactsEncryptionActivity, provide field for ContactsEncryptionActivity
 * @date 2020/01/07 15:46:53
 */
@Module
class ContactsEncryptionModule (private val mView: ContactsEncryptionContract.View) {

    @Provides
    @ActivityScope
    fun provideContactsEncryptionPresenter(httpAPIWrapper: HttpAPIWrapper) :ContactsEncryptionPresenter {
        return ContactsEncryptionPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideContactsEncryptionActivity() : ContactsEncryptionActivity {
        return mView as ContactsEncryptionActivity
    }
}