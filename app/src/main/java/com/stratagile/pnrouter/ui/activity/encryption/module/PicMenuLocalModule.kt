package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicMenuLocalFragment
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicMenuLocalContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicMenuLocalPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicMenuLocalFragment, provide field for PicMenuLocalFragment
 * @date 2019/12/02 16:00:46
 */
@Module
class PicMenuLocalModule (private val mView: PicMenuLocalContract.View) {

    @Provides
    @ActivityScope
    fun providePicMenuLocalPresenter(httpAPIWrapper: HttpAPIWrapper) :PicMenuLocalPresenter {
        return PicMenuLocalPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicMenuLocalFragment() : PicMenuLocalFragment {
        return mView as PicMenuLocalFragment
    }
}