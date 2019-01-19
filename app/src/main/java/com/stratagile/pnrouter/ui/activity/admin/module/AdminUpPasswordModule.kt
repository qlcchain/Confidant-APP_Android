package com.stratagile.pnrouter.ui.activity.admin.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminUpPasswordActivity
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminUpPasswordContract
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminUpPasswordPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The moduele of AdminUpPasswordActivity, provide field for AdminUpPasswordActivity
 * @date 2019/01/19 15:30:48
 */
@Module
class AdminUpPasswordModule (private val mView: AdminUpPasswordContract.View) {

    @Provides
    @ActivityScope
    fun provideAdminUpPasswordPresenter(httpAPIWrapper: HttpAPIWrapper) :AdminUpPasswordPresenter {
        return AdminUpPasswordPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideAdminUpPasswordActivity() : AdminUpPasswordActivity {
        return mView as AdminUpPasswordActivity
    }
}