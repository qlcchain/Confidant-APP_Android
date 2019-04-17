package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.CircleMemberDetailActivity
import com.stratagile.pnrouter.ui.activity.user.contract.CircleMemberDetailContract
import com.stratagile.pnrouter.ui.activity.user.presenter.CircleMemberDetailPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of CircleMemberDetailActivity, provide field for CircleMemberDetailActivity
 * @date 2019/04/17 10:21:23
 */
@Module
class CircleMemberDetailModule (private val mView: CircleMemberDetailContract.View) {

    @Provides
    @ActivityScope
    fun provideCircleMemberDetailPresenter(httpAPIWrapper: HttpAPIWrapper) :CircleMemberDetailPresenter {
        return CircleMemberDetailPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideCircleMemberDetailActivity() : CircleMemberDetailActivity {
        return mView as CircleMemberDetailActivity
    }
}