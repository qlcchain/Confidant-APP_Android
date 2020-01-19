package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.MiFileViewActivity
import com.stratagile.pnrouter.ui.activity.file.contract.MiFileViewContract
import com.stratagile.pnrouter.ui.activity.file.presenter.MiFileViewPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of MiFileViewActivity, provide field for MiFileViewActivity
 * @date 2019/12/23 18:07:51
 */
@Module
class MiFileViewModule (private val mView: MiFileViewContract.View) {

    @Provides
    @ActivityScope
    fun provideMiFileViewPresenter(httpAPIWrapper: HttpAPIWrapper) :MiFileViewPresenter {
        return MiFileViewPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideMiFileViewActivity() : MiFileViewActivity {
        return mView as MiFileViewActivity
    }
}