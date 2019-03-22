package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupMembersActivity
import com.stratagile.pnrouter.ui.activity.group.contract.GroupMembersContract
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupMembersPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of GroupMembersActivity, provide field for GroupMembersActivity
 * @date 2019/03/22 15:19:37
 */
@Module
class GroupMembersModule (private val mView: GroupMembersContract.View) {

    @Provides
    @ActivityScope
    fun provideGroupMembersPresenter(httpAPIWrapper: HttpAPIWrapper) :GroupMembersPresenter {
        return GroupMembersPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideGroupMembersActivity() : GroupMembersActivity {
        return mView as GroupMembersActivity
    }
}