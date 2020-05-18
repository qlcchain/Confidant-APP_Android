package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ShareFileActivity
import com.stratagile.pnrouter.ui.activity.main.contract.ShareFileContract
import com.stratagile.pnrouter.ui.activity.main.presenter.ShareFilePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of ShareFileActivity, provide field for ShareFileActivity
 * @date 2020/05/12 14:06:39
 */
@Module
class ShareFileModule (private val mView: ShareFileContract.View) {

    @Provides
    @ActivityScope
    fun provideShareFilePresenter(httpAPIWrapper: HttpAPIWrapper) :ShareFilePresenter {
        return ShareFilePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideShareFileActivity() : ShareFileActivity {
        return mView as ShareFileActivity
    }
}