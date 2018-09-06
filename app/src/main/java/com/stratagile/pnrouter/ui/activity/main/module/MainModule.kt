package com.stratagile.pnrouter.ui.activity.main.module


import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter

import dagger.Module
import dagger.Provides

/**
 * @author hzp
 * @Package com.stratagile.qlink.ui.activity.main
 * @Description: The moduele of MainActivity, provide field for MainActivity
 * @date 2018/01/09 09:57:09
 */
@Module
class MainModule(private val mView: MainContract.View) {

    @Provides
    @ActivityScope
    fun provideMainPresenter(httpAPIWrapper: HttpAPIWrapper): MainPresenter {
        return MainPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideMainActivity(): MainActivity {
        return mView as MainActivity
    }
}