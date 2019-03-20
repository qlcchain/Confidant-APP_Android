package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.GroupInfoActivity
import com.stratagile.pnrouter.ui.activity.group.contract.GroupInfoContract
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupInfoPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of GroupInfoActivity, provide field for GroupInfoActivity
 * @date 2019/03/20 11:44:58
 */
@Module
class GroupInfoModule (private val mView: GroupInfoContract.View) {

    @Provides
    @ActivityScope
    fun provideGroupInfoPresenter(httpAPIWrapper: HttpAPIWrapper) :GroupInfoPresenter {
        return GroupInfoPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideGroupInfoActivity() : GroupInfoActivity {
        return mView as GroupInfoActivity
    }
}