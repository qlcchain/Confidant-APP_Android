package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import com.stratagile.pnrouter.ui.activity.user.contract.SendAddFriendContract
import com.stratagile.pnrouter.ui.activity.user.presenter.SendAddFriendPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of SendAddFriendActivity, provide field for SendAddFriendActivity
 * @date 2019/01/02 11:19:43
 */
@Module
class SendAddFriendModule (private val mView: SendAddFriendContract.View) {

    @Provides
    @ActivityScope
    fun provideSendAddFriendPresenter(httpAPIWrapper: HttpAPIWrapper) :SendAddFriendPresenter {
        return SendAddFriendPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideSendAddFriendActivity() : SendAddFriendActivity {
        return mView as SendAddFriendActivity
    }
}