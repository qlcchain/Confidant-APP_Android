package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeXinEncryptionListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of WeXinEncryptionListActivity, provide field for WeXinEncryptionListActivity
 * @date 2019/11/21 15:27:44
 */
@Module
class WeXinEncryptionListModule (private val mView: WeXinEncryptionListContract.View) {

    @Provides
    @ActivityScope
    fun provideWeXinEncryptionListPresenter(httpAPIWrapper: HttpAPIWrapper) :WeXinEncryptionListPresenter {
        return WeXinEncryptionListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideWeXinEncryptionListActivity() : WeXinEncryptionListActivity {
        return mView as WeXinEncryptionListActivity
    }
}