package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ContactAndGroupFragment
import com.stratagile.pnrouter.ui.activity.main.contract.ContactAndGroupContract
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactAndGroupPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of ContactAndGroupFragment, provide field for ContactAndGroupFragment
 * @date 2019/03/26 11:19:29
 */
@Module
class ContactAndGroupModule (private val mView: ContactAndGroupContract.View) {

    @Provides
    @ActivityScope
    fun provideContactAndGroupPresenter(httpAPIWrapper: HttpAPIWrapper) :ContactAndGroupPresenter {
        return ContactAndGroupPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideContactAndGroupFragment() : ContactAndGroupFragment {
        return mView as ContactAndGroupFragment
    }
}