package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskConfigureActivity
import com.stratagile.pnrouter.ui.activity.router.contract.DiskConfigureContract
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskConfigurePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of DiskConfigureActivity, provide field for DiskConfigureActivity
 * @date 2019/01/28 14:51:22
 */
@Module
class DiskConfigureModule (private val mView: DiskConfigureContract.View) {

    @Provides
    @ActivityScope
    fun provideDiskConfigurePresenter(httpAPIWrapper: HttpAPIWrapper) :DiskConfigurePresenter {
        return DiskConfigurePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideDiskConfigureActivity() : DiskConfigureActivity {
        return mView as DiskConfigureActivity
    }
}