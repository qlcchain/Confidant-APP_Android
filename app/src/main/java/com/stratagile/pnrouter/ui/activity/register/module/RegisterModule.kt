package com.stratagile.pnrouter.ui.activity.register.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.register.contract.RegisterContract
import com.stratagile.pnrouter.ui.activity.register.presenter.RegisterPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.register
 * @Description: The moduele of RegisterActivity, provide field for RegisterActivity
 * @date 2018/11/12 11:53:06
 */
@Module
class RegisterModule (private val mView: RegisterContract.View) {

    @Provides
    @ActivityScope
    fun provideRegisterPresenter(httpAPIWrapper: HttpAPIWrapper) :RegisterPresenter {
        return RegisterPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRegisterActivity() : RegisterActivity {
        return mView as RegisterActivity
    }
}