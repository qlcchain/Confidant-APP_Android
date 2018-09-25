package com.stratagile.pnrouter.ui.activity.selectfriend.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendContract
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendPresenter

import dagger.Module
import dagger.Provides

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The moduele of selectFriendActivity, provide field for selectFriendActivity
 * @date 2018/09/25 14:58:33
 */
@Module
class selectFriendModule(private val mView: selectFriendContract.View) {

    @Provides
    @ActivityScope
    fun provideselectFriendPresenter(httpAPIWrapper: HttpAPIWrapper, mActivity: selectFriendActivity): selectFriendPresenter {
        return selectFriendPresenter(httpAPIWrapper, mView, mActivity)
    }

    @Provides
    @ActivityScope
    fun provideselectFriendActivity(): selectFriendActivity {
        return mView as selectFriendActivity
    }
}