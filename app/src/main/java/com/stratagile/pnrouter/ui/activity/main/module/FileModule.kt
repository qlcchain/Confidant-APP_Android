package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.FileFragment
import com.stratagile.pnrouter.ui.activity.main.contract.FileContract
import com.stratagile.pnrouter.ui.activity.main.presenter.FilePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of FileFragment, provide field for FileFragment
 * @date 2018/09/10 17:32:58
 */
@Module
class FileModule (private val mView: FileContract.View) {

    @Provides
    @ActivityScope
    fun provideFilePresenter(httpAPIWrapper: HttpAPIWrapper) :FilePresenter {
        return FilePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileFragment() : FileFragment {
        return mView as FileFragment
    }
}