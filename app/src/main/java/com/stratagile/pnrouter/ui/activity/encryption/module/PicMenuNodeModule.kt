package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicMenuNodeFragment
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicMenuNodeContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicMenuNodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicMenuNodeFragment, provide field for PicMenuNodeFragment
 * @date 2019/12/02 16:04:58
 */
@Module
class PicMenuNodeModule (private val mView: PicMenuNodeContract.View) {

    @Provides
    @ActivityScope
    fun providePicMenuNodePresenter(httpAPIWrapper: HttpAPIWrapper) :PicMenuNodePresenter {
        return PicMenuNodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicMenuNodeFragment() : PicMenuNodeFragment {
        return mView as PicMenuNodeFragment
    }
}