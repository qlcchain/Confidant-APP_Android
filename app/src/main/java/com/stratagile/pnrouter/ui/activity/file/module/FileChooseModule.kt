package com.stratagile.pnrouter.ui.activity.file.module


import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileChooseContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileChoosePresenter

import dagger.Module
import dagger.Provides

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: The moduele of FileChooseActivity, provide field for FileChooseActivity
 * @date 2018/09/28 16:46:15
 */
@Module
class FileChooseModule(private val mView: FileChooseContract.View) {

    @Provides
    @ActivityScope
    fun provideFileInfosPresenter(httpAPIWrapper: HttpAPIWrapper, mActivity: FileChooseActivity): FileChoosePresenter {
        return FileChoosePresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideFileInfosActivity(): FileChooseActivity {
        return mView as FileChooseActivity
    }
}