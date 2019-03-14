package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupMemberActivity
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupMemberContract
import com.stratagile.pnrouter.ui.activity.group.presenter.RemoveGroupMemberPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of RemoveGroupMemberActivity, provide field for RemoveGroupMemberActivity
 * @date 2019/03/14 10:20:11
 */
@Module
class RemoveGroupMemberModule (private val mView: RemoveGroupMemberContract.View) {

    @Provides
    @ActivityScope
    fun provideRemoveGroupMemberPresenter(httpAPIWrapper: HttpAPIWrapper) :RemoveGroupMemberPresenter {
        return RemoveGroupMemberPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRemoveGroupMemberActivity() : RemoveGroupMemberActivity {
        return mView as RemoveGroupMemberActivity
    }
}