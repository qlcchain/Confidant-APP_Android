package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicWeiXinNodeFragment
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicWeiXinNodeContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicWeiXinNodePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of PicWeiXinNodeFragment, provide field for PicWeiXinNodeFragment
 * @date 2019/12/03 17:30:27
 */
@Module
class PicWeiXinNodeModule (private val mView: PicWeiXinNodeContract.View) {

    @Provides
    @ActivityScope
    fun providePicWeiXinNodePresenter(httpAPIWrapper: HttpAPIWrapper) :PicWeiXinNodePresenter {
        return PicWeiXinNodePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePicWeiXinNodeFragment() : PicWeiXinNodeFragment {
        return mView as PicWeiXinNodeFragment
    }
}