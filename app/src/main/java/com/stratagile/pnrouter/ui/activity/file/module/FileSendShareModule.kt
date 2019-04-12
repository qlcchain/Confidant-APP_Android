package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileSendShareActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileSendShareContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileSendSharePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of FileSendShareActivity, provide field for FileSendShareActivity
 * @date 2019/04/12 15:17:33
 */
@Module
class FileSendShareModule (private val mView: FileSendShareContract.View) {

    @Provides
    @ActivityScope
    fun provideFileSendSharePresenter(httpAPIWrapper: HttpAPIWrapper) :FileSendSharePresenter {
        return FileSendSharePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileSendShareActivity() : FileSendShareActivity {
        return mView as FileSendShareActivity
    }
}