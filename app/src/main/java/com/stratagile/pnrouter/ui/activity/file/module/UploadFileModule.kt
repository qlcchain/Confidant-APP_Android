package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.UploadFileActivity
import com.stratagile.pnrouter.ui.activity.file.contract.UploadFileContract
import com.stratagile.pnrouter.ui.activity.file.presenter.UploadFilePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of UploadFileActivity, provide field for UploadFileActivity
 * @date 2019/01/25 14:59:07
 */
@Module
class UploadFileModule (private val mView: UploadFileContract.View) {

    @Provides
    @ActivityScope
    fun provideUploadFilePresenter(httpAPIWrapper: HttpAPIWrapper) :UploadFilePresenter {
        return UploadFilePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideUploadFileActivity() : UploadFileActivity {
        return mView as UploadFileActivity
    }
}