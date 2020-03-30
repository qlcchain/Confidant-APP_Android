package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EncryptMsgActivity
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgContract
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of EncryptMsgActivity, provide field for EncryptMsgActivity
 * @date 2020/02/19 16:13:12
 */
@Module
class EncryptMsgModule (private val mView: EncryptMsgContract.View) {

    @Provides
    @ActivityScope
    fun provideEncryptMsgPresenter(httpAPIWrapper: HttpAPIWrapper) :EncryptMsgPresenter {
        return EncryptMsgPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEncryptMsgActivity() : EncryptMsgActivity {
        return mView as EncryptMsgActivity
    }
}