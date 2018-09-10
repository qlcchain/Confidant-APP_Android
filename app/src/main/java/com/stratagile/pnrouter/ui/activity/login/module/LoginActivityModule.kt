package com.stratagile.pnrouter.ui.activity.login.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.presenter.LoginActivityPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: The moduele of LoginActivityActivity, provide field for LoginActivityActivity
 * @date 2018/09/10 15:05:29
 */
@Module
class LoginActivityModule (private val mView: LoginActivityContract.View) {

    @Provides
    @ActivityScope
    fun provideLoginActivityPresenter(httpAPIWrapper: HttpAPIWrapper, mActivity : LoginActivityActivity) :LoginActivityPresenter {
        return LoginActivityPresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideLoginActivityActivity() : LoginActivityActivity {
        return mView as LoginActivityActivity
    }
}