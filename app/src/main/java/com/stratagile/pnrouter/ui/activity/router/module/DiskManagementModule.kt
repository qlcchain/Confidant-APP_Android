package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskManagementActivity
import com.stratagile.pnrouter.ui.activity.router.contract.DiskManagementContract
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskManagementPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of DIsManagementActivity, provide field for DIsManagementActivity
 * @date 2019/01/28 11:29:37
 */
@Module
class DiskManagementModule (private val mView: DiskManagementContract.View) {

    @Provides
    @ActivityScope
    fun provideDIsManagementPresenter(httpAPIWrapper: HttpAPIWrapper) :DiskManagementPresenter {
        return DiskManagementPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideDIsManagementActivity() : DiskManagementActivity {
        return mView as DiskManagementActivity
    }
}