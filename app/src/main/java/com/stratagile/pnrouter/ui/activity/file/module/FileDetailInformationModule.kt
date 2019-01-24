package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileDetailInformationActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileDetailInformationContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileDetailInformationPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of FileDetailInformationActivity, provide field for FileDetailInformationActivity
 * @date 2019/01/23 17:49:28
 */
@Module
class FileDetailInformationModule (private val mView: FileDetailInformationContract.View) {

    @Provides
    @ActivityScope
    fun provideFileDetailInformationPresenter(httpAPIWrapper: HttpAPIWrapper) :FileDetailInformationPresenter {
        return FileDetailInformationPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileDetailInformationActivity() : FileDetailInformationActivity {
        return mView as FileDetailInformationActivity
    }
}