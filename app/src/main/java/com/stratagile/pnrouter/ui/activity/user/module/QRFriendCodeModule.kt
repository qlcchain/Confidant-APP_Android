package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.QRFriendCodeActivity
import com.stratagile.pnrouter.ui.activity.user.contract.QRFriendCodeContract
import com.stratagile.pnrouter.ui.activity.user.presenter.QRFriendCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of QRFriendCodeActivity, provide field for QRFriendCodeActivity
 * @date 2018/12/25 11:45:06
 */
@Module
class QRFriendCodeModule (private val mView: QRFriendCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideQRFriendCodePresenter(httpAPIWrapper: HttpAPIWrapper) :QRFriendCodePresenter {
        return QRFriendCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideQRFriendCodeActivity() : QRFriendCodeActivity {
        return mView as QRFriendCodeActivity
    }
}