package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.SearchActivity
import com.stratagile.pnrouter.ui.activity.main.contract.SearchContract
import com.stratagile.pnrouter.ui.activity.main.presenter.SearchPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of SearchActivity, provide field for SearchActivity
 * @date 2019/08/13 14:06:03
 */
@Module
class SearchModule (private val mView: SearchContract.View) {

    @Provides
    @ActivityScope
    fun provideSearchPresenter(httpAPIWrapper: HttpAPIWrapper) :SearchPresenter {
        return SearchPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSearchActivity() : SearchActivity {
        return mView as SearchActivity
    }
}