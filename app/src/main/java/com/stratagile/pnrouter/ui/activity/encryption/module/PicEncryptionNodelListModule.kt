package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionNodelListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicEncryptionNodelListActivity, provide field for PicEncryptionNodelListActivity
 * @date 2019/12/23 16:07:44
 */
@Module
class PicEncryptionNodelListModule (private val mView: PicEncryptionNodelListContract.View) {

    @Provides
    @ActivityScope
    fun providePicEncryptionNodelListPresenter(httpAPIWrapper: HttpAPIWrapper) :PicEncryptionNodelListPresenter {
        return PicEncryptionNodelListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicEncryptionNodelListActivity() : PicEncryptionNodelListActivity {
        return mView as PicEncryptionNodelListActivity
    }
}