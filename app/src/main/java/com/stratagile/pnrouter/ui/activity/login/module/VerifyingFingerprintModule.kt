package com.stratagile.pnrouter.ui.activity.login.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.VerifyingFingerprintActivity
import com.stratagile.pnrouter.ui.activity.login.contract.VerifyingFingerprintContract
import com.stratagile.pnrouter.ui.activity.login.presenter.VerifyingFingerprintPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The moduele of VerifyingFingerprintActivity, provide field for VerifyingFingerprintActivity
 * @date 2019/02/26 14:40:52
 */
@Module
class VerifyingFingerprintModule (private val mView: VerifyingFingerprintContract.View) {

    @Provides
    @ActivityScope
    fun provideVerifyingFingerprintPresenter(httpAPIWrapper: HttpAPIWrapper) :VerifyingFingerprintPresenter {
        return VerifyingFingerprintPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideVerifyingFingerprintActivity() : VerifyingFingerprintActivity {
        return mView as VerifyingFingerprintActivity
    }
}