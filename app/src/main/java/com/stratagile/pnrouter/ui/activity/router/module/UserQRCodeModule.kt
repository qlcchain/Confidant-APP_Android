package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.UserQRCodeActivity
import com.stratagile.pnrouter.ui.activity.router.contract.UserQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.presenter.UserQRCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of UserQRCodeActivity, provide field for UserQRCodeActivity
 * @date 2018/12/10 17:38:20
 */
@Module
class UserQRCodeModule (private val mView: UserQRCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideUserQRCodePresenter(httpAPIWrapper: HttpAPIWrapper) :UserQRCodePresenter {
        return UserQRCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideUserQRCodeActivity() : UserQRCodeActivity {
        return mView as UserQRCodeActivity
    }
}