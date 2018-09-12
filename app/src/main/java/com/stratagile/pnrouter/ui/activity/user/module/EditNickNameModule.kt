package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.ui.activity.user.contract.EditNickNameContract
import com.stratagile.pnrouter.ui.activity.user.presenter.EditNickNamePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of EditNickNameActivity, provide field for EditNickNameActivity
 * @date 2018/09/12 13:20:58
 */
@Module
class EditNickNameModule (private val mView: EditNickNameContract.View) {

    @Provides
    @ActivityScope
    fun provideEditNickNamePresenter(httpAPIWrapper: HttpAPIWrapper) :EditNickNamePresenter {
        return EditNickNamePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEditNickNameActivity() : EditNickNameActivity {
        return mView as EditNickNameActivity
    }
}