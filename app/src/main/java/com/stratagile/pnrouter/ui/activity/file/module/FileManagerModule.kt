package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileManagerActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileManagerContract
import com.stratagile.pnrouter.ui.activity.file.presenter.FileManagerPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of FileManagerActivity, provide field for FileManagerActivity
 * @date 2019/01/23 14:15:29
 */
@Module
class FileManagerModule (private val mView: FileManagerContract.View) {

    @Provides
    @ActivityScope
    fun provideFileManagerPresenter(httpAPIWrapper: HttpAPIWrapper) :FileManagerPresenter {
        return FileManagerPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileManagerActivity() : FileManagerActivity {
        return mView as FileManagerActivity
    }
}