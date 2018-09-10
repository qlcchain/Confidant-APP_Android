package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.MyFragment
import com.stratagile.pnrouter.ui.activity.main.contract.MyContract
import com.stratagile.pnrouter.ui.activity.main.presenter.MyPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of MyFragment, provide field for MyFragment
 * @date 2018/09/10 17:34:05
 */
@Module
class MyModule (private val mView: MyContract.View) {

    @Provides
    @ActivityScope
    fun provideMyPresenter(httpAPIWrapper: HttpAPIWrapper) :MyPresenter {
        return MyPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideMyFragment() : MyFragment {
        return mView as MyFragment
    }
}