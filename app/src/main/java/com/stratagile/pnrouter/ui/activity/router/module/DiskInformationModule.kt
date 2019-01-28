package com.stratagile.pnrouter.ui.activity.router.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.router.DiskInformationActivity
import com.stratagile.pnrouter.ui.activity.router.contract.DiskInformationContract
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskInformationPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: The moduele of DiskInformationActivity, provide field for DiskInformationActivity
 * @date 2019/01/28 15:21:12
 */
@Module
class DiskInformationModule (private val mView: DiskInformationContract.View) {

    @Provides
    @ActivityScope
    fun provideDiskInformationPresenter(httpAPIWrapper: HttpAPIWrapper) :DiskInformationPresenter {
        return DiskInformationPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideDiskInformationActivity() : DiskInformationActivity {
        return mView as DiskInformationActivity
    }
}