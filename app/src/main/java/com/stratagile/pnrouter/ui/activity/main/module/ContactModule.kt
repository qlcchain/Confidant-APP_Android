package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
import com.stratagile.pnrouter.ui.activity.main.contract.ContactContract
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of ContactFragment, provide field for ContactFragment
 * @date 2018/09/10 17:33:27
 */
@Module
class ContactModule (private val mView: ContactContract.View) {

    @Provides
    @ActivityScope
    fun provideContactPresenter(httpAPIWrapper: HttpAPIWrapper) :ContactPresenter {
        return ContactPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideContactFragment() : ContactFragment {
        return mView as ContactFragment
    }
}