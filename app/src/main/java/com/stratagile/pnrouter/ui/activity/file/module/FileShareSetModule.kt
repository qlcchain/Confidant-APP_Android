package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileShareSetActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileShareSetContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileShareSetPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of FileShareSetActivity, provide field for FileShareSetActivity
 * @date 2019/01/24 10:26:38
 */
@Module
class FileShareSetModule (private val mView: FileShareSetContract.View) {

    @Provides
    @ActivityScope
    fun provideFileShareSetPresenter(httpAPIWrapper: HttpAPIWrapper) :FileShareSetPresenter {
        return FileShareSetPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileShareSetActivity() : FileShareSetActivity {
        return mView as FileShareSetActivity
    }
}