package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.ui.activity.main.module.LogModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for LogActivity
 * @date 2018/09/18 09:45:46
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(LogModule::class))
interface LogComponent {
    fun inject(LogActivity: LogActivity): LogActivity
}