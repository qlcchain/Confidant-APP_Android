package com.stratagile.pnrouter.ui.activity.add.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.add.addFriendOrGroupActivity
import com.stratagile.pnrouter.ui.activity.add.contract.addFriendOrGroupContract
import com.stratagile.pnrouter.ui.activity.add.presenter.addFriendOrGroupPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.add
 * @Description: The moduele of addFriendOrGroupActivity, provide field for addFriendOrGroupActivity
 * @date 2019/04/02 16:08:05
 */
@Module
class addFriendOrGroupModule (private val mView: addFriendOrGroupContract.View) {

    @Provides
    @ActivityScope
    fun provideaddFriendOrGroupPresenter(httpAPIWrapper: HttpAPIWrapper) :addFriendOrGroupPresenter {
        return addFriendOrGroupPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideaddFriendOrGroupActivity() : addFriendOrGroupActivity {
        return mView as addFriendOrGroupActivity
    }
}