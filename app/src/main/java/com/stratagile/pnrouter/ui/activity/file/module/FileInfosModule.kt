package com.stratagile.pnrouter.ui.activity.file.module


import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileInfosFragment
import com.stratagile.pnrouter.ui.activity.file.contract.FileInfosContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileInfosPresenter

import dagger.Module
import dagger.Provides

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: The moduele of FileInfosFragment, provide field for FileInfosFragment
 * @date 2018/09/28 16:46:15
 */
@Module
class FileInfosModule(private val mView: FileInfosContract.View) {

    @Provides
    @ActivityScope
    fun provideFileInfosPresenter(httpAPIWrapper: HttpAPIWrapper, mFragment: FileInfosFragment): FileInfosPresenter {
        return FileInfosPresenter(httpAPIWrapper, mView, mFragment)
    }

    @Provides
    @ActivityScope
    fun provideFileInfosFragment(): FileInfosFragment {
        return mView as FileInfosFragment
    }
}