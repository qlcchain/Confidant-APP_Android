package com.stratagile.pnrouter.ui.activity.admin.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.admin.AdminUpCodeActivity
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminUpCodeContract
import com.stratagile.pnrouter.ui.activity.admin.presenter.AdminUpCodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: The moduele of AdminUpCodeActivity, provide field for AdminUpCodeActivity
 * @date 2019/01/19 15:31:09
 */
@Module
class AdminUpCodeModule (private val mView: AdminUpCodeContract.View) {

    @Provides
    @ActivityScope
    fun provideAdminUpCodePresenter(httpAPIWrapper: HttpAPIWrapper) :AdminUpCodePresenter {
        return AdminUpCodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideAdminUpCodeActivity() : AdminUpCodeActivity {
        return mView as AdminUpCodeActivity
    }
}