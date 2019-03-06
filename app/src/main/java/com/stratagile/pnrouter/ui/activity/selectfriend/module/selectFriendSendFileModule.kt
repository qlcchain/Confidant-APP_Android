package com.stratagile.pnrouter.ui.activity.selectfriend.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendSendFileActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendSendFileContract
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendSendFilePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The moduele of selectFriendSendFileActivity, provide field for selectFriendSendFileActivity
 * @date 2019/03/06 15:41:57
 */
@Module
class selectFriendSendFileModule (private val mView: selectFriendSendFileContract.View) {

    @Provides
    @ActivityScope
    fun provideselectFriendSendFilePresenter(httpAPIWrapper: HttpAPIWrapper) :selectFriendSendFilePresenter {
        return selectFriendSendFilePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideselectFriendSendFileActivity() : selectFriendSendFileActivity {
        return mView as selectFriendSendFileActivity
    }
}