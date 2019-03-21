package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupDetailMemberActivity
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupDetailMemberContract
import com.stratagile.pnrouter.ui.activity.group.presenter.RemoveGroupDetailMemberPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of RemoveGroupDetailMemberActivity, provide field for RemoveGroupDetailMemberActivity
 * @date 2019/03/21 10:15:05
 */
@Module
class RemoveGroupDetailMemberModule (private val mView: RemoveGroupDetailMemberContract.View) {

    @Provides
    @ActivityScope
    fun provideRemoveGroupDetailMemberPresenter(httpAPIWrapper: HttpAPIWrapper) :RemoveGroupDetailMemberPresenter {
        return RemoveGroupDetailMemberPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideRemoveGroupDetailMemberActivity() : RemoveGroupDetailMemberActivity {
        return mView as RemoveGroupDetailMemberActivity
    }
}