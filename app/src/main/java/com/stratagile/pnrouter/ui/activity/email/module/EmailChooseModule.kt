package com.stratagile.pnrouter.ui.activity.email.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.email.EmailChooseActivity
import com.stratagile.pnrouter.ui.activity.email.contract.EmailChooseContract
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailChoosePresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: The moduele of EmailChooseActivity, provide field for EmailChooseActivity
 * @date 2019/07/10 17:41:08
 */
@Module
class EmailChooseModule (private val mView: EmailChooseContract.View) {

    @Provides
    @ActivityScope
    fun provideEmailChoosePresenter(httpAPIWrapper: HttpAPIWrapper) :EmailChoosePresenter {
        return EmailChoosePresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideEmailChooseActivity() : EmailChooseActivity {
        return mView as EmailChooseActivity
    }
}