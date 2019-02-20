package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.ImportAccountActivity
import com.stratagile.pnrouter.ui.activity.user.contract.ImportAccountContract
import com.stratagile.pnrouter.ui.activity.user.presenter.ImportAccountPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of ImportAccountActivity, provide field for ImportAccountActivity
 * @date 2019/02/20 14:43:29
 */
@Module
class ImportAccountModule (private val mView: ImportAccountContract.View) {

    @Provides
    @ActivityScope
    fun provideImportAccountPresenter(httpAPIWrapper: HttpAPIWrapper) :ImportAccountPresenter {
        return ImportAccountPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideImportAccountActivity() : ImportAccountActivity {
        return mView as ImportAccountActivity
    }
}