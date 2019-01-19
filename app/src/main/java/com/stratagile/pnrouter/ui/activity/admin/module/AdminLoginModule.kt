package com.stratagile.pnrouter.ui.activity.admin.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginContract
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminLoginPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The moduele of AdminLoginActivity, provide field for AdminLoginActivity
 * @date 2019/01/19 15:30:16
 */
@Module
class AdminLoginModule (private val mView: AdminLoginContract.View) {

    @Provides
    @ActivityScope
    fun provideAdminLoginPresenter(httpAPIWrapper: HttpAPIWrapper) :AdminLoginPresenter {
        return AdminLoginPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideAdminLoginActivity() : AdminLoginActivity {
        return mView as AdminLoginActivity
    }
}