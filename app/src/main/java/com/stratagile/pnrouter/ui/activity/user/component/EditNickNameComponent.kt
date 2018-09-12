package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.ui.activity.user.module.EditNickNameModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for EditNickNameActivity
 * @date 2018/09/12 13:20:58
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EditNickNameModule::class))
interface EditNickNameComponent {
    fun inject(EditNickNameActivity: EditNickNameActivity): EditNickNameActivity
}