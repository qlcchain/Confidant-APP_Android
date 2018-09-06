package com.stratagile.pnrouter.ui.activity.test.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.test.TestActivity
import com.stratagile.pnrouter.ui.activity.test.contract.TestContract
import com.stratagile.pnrouter.ui.activity.test.presenter.TestPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.test
 * @Description: The moduele of TestActivity, provide field for TestActivity
 * @date 2018/09/05 11:10:38
 */
@Module
class TestModule (private val mView: TestContract.View) {

    @Provides
    @ActivityScope
    fun provideTestPresenter(httpAPIWrapper: HttpAPIWrapper, mActivity : TestActivity) :TestPresenter {
        return TestPresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideTestActivity() : TestActivity {
        return mView as TestActivity
    }
}