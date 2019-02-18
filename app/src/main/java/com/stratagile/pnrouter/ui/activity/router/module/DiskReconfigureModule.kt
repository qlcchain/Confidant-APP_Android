package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskReconfigureActivity
import com.stratagile.pnrouter.ui.activity.router.contract.DiskReconfigureContract
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskReconfigurePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of DiskReconfigureActivity, provide field for DiskReconfigureActivity
 * @date 2019/02/18 17:31:04
 */
@Module
class DiskReconfigureModule (private val mView: DiskReconfigureContract.View) {

    @Provides
    @ActivityScope
    fun provideDiskReconfigurePresenter(httpAPIWrapper: HttpAPIWrapper) :DiskReconfigurePresenter {
        return DiskReconfigurePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideDiskReconfigureActivity() : DiskReconfigureActivity {
        return mView as DiskReconfigureActivity
    }
}