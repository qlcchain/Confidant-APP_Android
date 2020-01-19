package com.stratagile.pnrouter.ui.activity.encryption.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WexinChatActivity
import com.stratagile.pnrouter.ui.activity.encryption.contract.WexinChatContract
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WexinChatPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The moduele of WexinChatActivity, provide field for WexinChatActivity
 * @date 2019/12/27 16:17:50
 */
@Module
class WexinChatModule (private val mView: WexinChatContract.View) {

    @Provides
    @ActivityScope
    fun provideWexinChatPresenter(httpAPIWrapper: HttpAPIWrapper) :WexinChatPresenter {
        return WexinChatPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideWexinChatActivity() : WexinChatActivity {
        return mView as WexinChatActivity
    }
}