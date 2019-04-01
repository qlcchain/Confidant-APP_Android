package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.WebViewActivity
import com.stratagile.pnrouter.ui.activity.main.module.WebViewModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for WebViewActivity
 * @date 2019/04/01 18:08:04
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(WebViewModule::class))
interface WebViewComponent {
    fun inject(WebViewActivity: WebViewActivity): WebViewActivity
}