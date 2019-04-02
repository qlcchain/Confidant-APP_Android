package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.SelectFileActivity
import com.stratagile.pnrouter.ui.activity.file.contract.SelectFileContract
import com.stratagile.pnrouter.ui.activity.file.presenter.SelectFilePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of SelectFileActivity, provide field for SelectFileActivity
 * @date 2019/04/02 17:51:39
 */
@Module
class SelectFileModule (private val mView: SelectFileContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectFilePresenter(httpAPIWrapper: HttpAPIWrapper) :SelectFilePresenter {
        return SelectFilePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectFileActivity() : SelectFileActivity {
        return mView as SelectFileActivity
    }
}