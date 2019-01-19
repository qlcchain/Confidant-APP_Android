package com.stratagile.pnrouter.ui.activity.admin.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginSuccessActivity
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginSuccessContract
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminLoginSuccessPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The moduele of AdminLoginSuccessActivity, provide field for AdminLoginSuccessActivity
 * @date 2019/01/19 17:18:46
 */
@Module
class AdminLoginSuccessModule (private val mView: AdminLoginSuccessContract.View) {

    @Provides
    @ActivityScope
    fun provideAdminLoginSuccessPresenter(httpAPIWrapper: HttpAPIWrapper) :AdminLoginSuccessPresenter {
        return AdminLoginSuccessPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideAdminLoginSuccessActivity() : AdminLoginSuccessActivity {
        return mView as AdminLoginSuccessActivity
    }
}