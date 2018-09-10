package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.MyFragment
import com.stratagile.pnrouter.ui.activity.main.module.MyModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for MyFragment
 * @date 2018/09/10 17:34:05
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(MyModule::class))
interface MyComponent {
    fun inject(MyFragment: MyFragment): MyFragment
}