package com.stratagile.pnrouter.ui.activity.tox.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.tox.TestToxActivity
import com.stratagile.pnrouter.ui.activity.tox.module.TestToxModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.tox
 * @Description: The component for TestToxActivity
 * @date 2019/02/01 12:07:44
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(TestToxModule::class))
interface TestToxComponent {
    fun inject(TestToxActivity: TestToxActivity): TestToxActivity
}