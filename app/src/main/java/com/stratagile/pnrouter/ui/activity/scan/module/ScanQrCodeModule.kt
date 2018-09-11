package com.stratagile.pnrouter.ui.activity.scan.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.scan.contract.ScanQrCodeContract
import com.stratagile.pnrouter.ui.activity.scan.presenter.ScanQrCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: The moduele of ScanQrCodeActivity, provide field for ScanQrCodeActivity
 * @date 2018/09/11 15:29:14
 */
@Module
class ScanQrCodeModule (private val mView: ScanQrCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideScanQrCodePresenter(httpAPIWrapper: HttpAPIWrapper, mActivity : ScanQrCodeActivity) :ScanQrCodePresenter {
        return ScanQrCodePresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideScanQrCodeActivity() : ScanQrCodeActivity {
        return mView as ScanQrCodeActivity
    }
}