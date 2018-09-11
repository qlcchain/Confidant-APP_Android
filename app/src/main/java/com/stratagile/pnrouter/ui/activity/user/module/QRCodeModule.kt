package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.QRCodeActivity
import com.stratagile.pnrouter.ui.activity.user.contract.QRCodeContract
import com.stratagile.pnrouter.ui.activity.user.presenter.QRCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of QRCodeActivity, provide field for QRCodeActivity
 * @date 2018/09/11 14:01:23
 */
@Module
class QRCodeModule (private val mView: QRCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideQRCodePresenter(httpAPIWrapper: HttpAPIWrapper) :QRCodePresenter {
        return QRCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideQRCodeActivity() : QRCodeActivity {
        return mView as QRCodeActivity
    }
}