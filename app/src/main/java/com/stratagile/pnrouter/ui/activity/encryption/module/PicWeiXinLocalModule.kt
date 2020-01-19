package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicWeiXinLocalFragment
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicWeiXinLocalContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicWeiXinLocalPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicWeiXinLocalFragment, provide field for PicWeiXinLocalFragment
 * @date 2019/12/03 17:30:10
 */
@Module
class PicWeiXinLocalModule (private val mView: PicWeiXinLocalContract.View) {

    @Provides
    @ActivityScope
    fun providePicWeiXinLocalPresenter(httpAPIWrapper: HttpAPIWrapper) :PicWeiXinLocalPresenter {
        return PicWeiXinLocalPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicWeiXinLocalFragment() : PicWeiXinLocalFragment {
        return mView as PicWeiXinLocalFragment
    }
}