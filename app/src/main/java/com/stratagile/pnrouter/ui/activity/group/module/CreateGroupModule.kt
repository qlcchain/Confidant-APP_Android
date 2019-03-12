package com.stratagile.pnrouter.ui.activity.group.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.group.CreateGroupActivity
import com.stratagile.pnrouter.ui.activity.group.contract.CreateGroupContract
import com.stratagile.pnrouter.ui.activity.group.presenter.CreateGroupPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: The moduele of CreateGroupActivity, provide field for CreateGroupActivity
 * @date 2019/03/12 15:29:49
 */
@Module
class CreateGroupModule (private val mView: CreateGroupContract.View) {

    @Provides
    @ActivityScope
    fun provideCreateGroupPresenter(httpAPIWrapper: HttpAPIWrapper) :CreateGroupPresenter {
        return CreateGroupPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideCreateGroupActivity() : CreateGroupActivity {
        return mView as CreateGroupActivity
    }
}