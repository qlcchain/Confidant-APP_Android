package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.MyDetailActivity
import com.stratagile.pnrouter.ui.activity.user.contract.MyDetailContract
import com.stratagile.pnrouter.ui.activity.user.presenter.MyDetailPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of MyDetailActivity, provide field for MyDetailActivity
 * @date 2018/09/11 11:06:30
 */
@Module
class MyDetailModule (private val mView: MyDetailContract.View) {

    @Provides
    @ActivityScope
    fun provideMyDetailPresenter(httpAPIWrapper: HttpAPIWrapper) :MyDetailPresenter {
        return MyDetailPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideMyDetailActivity() : MyDetailActivity {
        return mView as MyDetailActivity
    }
}