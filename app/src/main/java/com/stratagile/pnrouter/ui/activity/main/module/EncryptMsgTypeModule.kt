package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EncryptMsgTypeActivity
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgTypeContract
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgTypePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of EncryptMsgTypeActivity, provide field for EncryptMsgTypeActivity
 * @date 2020/02/19 16:13:36
 */
@Module
class EncryptMsgTypeModule (private val mView: EncryptMsgTypeContract.View) {

    @Provides
    @ActivityScope
    fun provideEncryptMsgTypePresenter(httpAPIWrapper: HttpAPIWrapper) :EncryptMsgTypePresenter {
        return EncryptMsgTypePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEncryptMsgTypeActivity() : EncryptMsgTypeActivity {
        return mView as EncryptMsgTypeActivity
    }
}