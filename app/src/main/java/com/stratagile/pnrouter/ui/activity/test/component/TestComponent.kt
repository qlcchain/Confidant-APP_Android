package com.stratagile.pnrouter.ui.activity.test.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.test.TestActivity
import com.stratagile.pnrouter.ui.activity.test.module.TestModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.test
 * @Description: The component for TestActivity
 * @date 2018/09/05 09:35:42
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TestModule::class))
interface TestComponent {
    fun inject(TestActivity: TestActivity): TestActivity
}