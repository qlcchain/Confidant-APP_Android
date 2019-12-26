package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeXinEncryptionNodelListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of WeXinEncryptionNodelListActivity, provide field for WeXinEncryptionNodelListActivity
 * @date 2019/12/26 10:33:40
 */
@Module
class WeXinEncryptionNodelListModule (private val mView: WeXinEncryptionNodelListContract.View) {

    @Provides
    @ActivityScope
    fun provideWeXinEncryptionNodelListPresenter(httpAPIWrapper: HttpAPIWrapper) :WeXinEncryptionNodelListPresenter {
        return WeXinEncryptionNodelListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideWeXinEncryptionNodelListActivity() : WeXinEncryptionNodelListActivity {
        return mView as WeXinEncryptionNodelListActivity
    }
}