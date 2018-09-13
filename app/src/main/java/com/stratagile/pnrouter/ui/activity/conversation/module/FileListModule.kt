package com.stratagile.pnrouter.ui.activity.conversation.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileListContract
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileListPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The moduele of FileListFragment, provide field for FileListFragment
 * @date 2018/09/13 15:32:14
 */
@Module
class FileListModule (private val mView: FileListContract.View) {

    @Provides
    @ActivityScope
    fun provideFileListPresenter(httpAPIWrapper: HttpAPIWrapper) :FileListPresenter {
        return FileListPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileListFragment() : FileListFragment {
        return mView as FileListFragment
    }
}