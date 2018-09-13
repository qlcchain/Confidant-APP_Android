package com.stratagile.pnrouter.ui.activity.user.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.ModifyAvatarActivity
import com.stratagile.pnrouter.ui.activity.user.contract.ModifyAvatarContract
import com.stratagile.pnrouter.ui.activity.user.presenter.ModifyAvatarPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The moduele of ModifyAvatarActivity, provide field for ModifyAvatarActivity
 * @date 2018/09/12 18:33:54
 */
@Module
class ModifyAvatarModule (private val mView: ModifyAvatarContract.View) {

    @Provides
    @ActivityScope
    fun provideModifyAvatarPresenter(httpAPIWrapper: HttpAPIWrapper) :ModifyAvatarPresenter {
        return ModifyAvatarPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideModifyAvatarActivity() : ModifyAvatarActivity {
        return mView as ModifyAvatarActivity
    }
}