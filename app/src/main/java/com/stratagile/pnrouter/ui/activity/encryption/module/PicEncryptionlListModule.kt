package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionlListActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionlListContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionlListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicEncryptionlListActivity, provide field for PicEncryptionlListActivity
 * @date 2019/11/21 15:27:22
 */
@Module
class PicEncryptionlListModule (private val mView: PicEncryptionlListContract.View) {

    @Provides
    @ActivityScope
    fun providePicEncryptionlListPresenter(httpAPIWrapper: HttpAPIWrapper) :PicEncryptionlListPresenter {
        return PicEncryptionlListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicEncryptionlListActivity() : PicEncryptionlListActivity {
        return mView as PicEncryptionlListActivity
    }
}