package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.SelectNodeMenuActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.SelectNodeMenuContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SelectNodeMenuPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of SelectNodeMenuActivity, provide field for SelectNodeMenuActivity
 * @date 2019/12/18 09:47:54
 */
@Module
class SelectNodeMenuModule (private val mView: SelectNodeMenuContract.View) {

    @Provides
    @ActivityScope
    fun provideSelectNodeMenuPresenter(httpAPIWrapper: HttpAPIWrapper) :SelectNodeMenuPresenter {
        return SelectNodeMenuPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSelectNodeMenuActivity() : SelectNodeMenuActivity {
        return mView as SelectNodeMenuActivity
    }
}