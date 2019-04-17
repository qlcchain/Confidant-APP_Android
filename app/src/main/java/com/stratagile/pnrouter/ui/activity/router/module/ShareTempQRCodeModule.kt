package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.ShareTempQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.contract.ShareTempQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.presenter.ShareTempQRCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of ShareTempQRCodeActivity, provide field for ShareTempQRCodeActivity
 * @date 2019/04/17 14:04:59
 */
@Module
class ShareTempQRCodeModule (private val mView: ShareTempQRCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideShareTempQRCodePresenter(httpAPIWrapper: HttpAPIWrapper) :ShareTempQRCodePresenter {
        return ShareTempQRCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideShareTempQRCodeActivity() : ShareTempQRCodeActivity {
        return mView as ShareTempQRCodeActivity
    }
}