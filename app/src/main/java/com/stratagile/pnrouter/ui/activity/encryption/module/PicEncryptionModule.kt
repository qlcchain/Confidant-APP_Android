package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicEncryptionActivity, provide field for PicEncryptionActivity
 * @date 2019/11/21 15:26:11
 */
@Module
class PicEncryptionModule (private val mView: PicEncryptionContract.View) {

    @Provides
    @ActivityScope
    fun providePicEncryptionPresenter(httpAPIWrapper: HttpAPIWrapper) :PicEncryptionPresenter {
        return PicEncryptionPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicEncryptionActivity() : PicEncryptionActivity {
        return mView as PicEncryptionActivity
    }
}