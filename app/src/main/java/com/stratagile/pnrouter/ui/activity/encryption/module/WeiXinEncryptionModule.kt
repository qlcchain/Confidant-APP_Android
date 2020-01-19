package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeiXinEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeiXinEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeiXinEncryptionPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of WeiXinEncryptionActivity, provide field for WeiXinEncryptionActivity
 * @date 2019/11/21 15:26:37
 */
@Module
class WeiXinEncryptionModule (private val mView: WeiXinEncryptionContract.View) {

    @Provides
    @ActivityScope
    fun provideWeiXinEncryptionPresenter(httpAPIWrapper: HttpAPIWrapper) :WeiXinEncryptionPresenter {
        return WeiXinEncryptionPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideWeiXinEncryptionActivity() : WeiXinEncryptionActivity {
        return mView as WeiXinEncryptionActivity
    }
}