package com.stratagile.pnrouter.ui.activity.main.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.WebViewActivity
import com.stratagile.pnrouter.ui.activity.main.contract.WebViewContract
import com.stratagile.pnrouter.ui.activity.main.presenter.WebViewPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The moduele of WebViewActivity, provide field for WebViewActivity
 * @date 2019/04/01 18:08:04
 */
@Module
class WebViewModule (private val mView: WebViewContract.View) {

    @Provides
    @ActivityScope
    fun provideWebViewPresenter(httpAPIWrapper: HttpAPIWrapper) :WebViewPresenter {
        return WebViewPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideWebViewActivity() : WebViewActivity {
        return mView as WebViewActivity
    }
}