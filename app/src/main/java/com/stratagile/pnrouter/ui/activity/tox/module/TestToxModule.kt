package com.stratagile.pnrouter.ui.activity.tox.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.tox.TestToxActivity
import com.stratagile.pnrouter.ui.activity.tox.contract.TestToxContract
import com.stratagile.pnrouter.ui.activity.tox.presenter.TestToxPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.tox
 * @Description: The moduele of TestToxActivity, provide field for TestToxActivity
 * @date 2019/02/01 12:07:44
 */
@Module
class TestToxModule (private val mView: TestToxContract.View) {

    @Provides
    @ActivityScope
    fun provideTestToxPresenter(httpAPIWrapper: HttpAPIWrapper) :TestToxPresenter {
        return TestToxPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideTestToxActivity() : TestToxActivity {
        return mView as TestToxActivity
    }
}