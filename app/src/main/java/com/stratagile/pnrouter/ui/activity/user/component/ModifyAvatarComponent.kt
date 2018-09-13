package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.ModifyAvatarActivity
import com.stratagile.pnrouter.ui.activity.user.module.ModifyAvatarModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for ModifyAvatarActivity
 * @date 2018/09/12 18:33:54
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ModifyAvatarModule::class))
interface ModifyAvatarComponent {
    fun inject(ModifyAvatarActivity: ModifyAvatarActivity): ModifyAvatarActivity
}