package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileTaskListContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileTaskListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of FileTaskListActivity, provide field for FileTaskListActivity
 * @date 2019/01/25 16:21:04
 */
@Module
class FileTaskListModule (private val mView: FileTaskListContract.View) {

    @Provides
    @ActivityScope
    fun provideFileTaskListPresenter(httpAPIWrapper: HttpAPIWrapper) :FileTaskListPresenter {
        return FileTaskListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileTaskListActivity() : FileTaskListActivity {
        return mView as FileTaskListActivity
    }
}