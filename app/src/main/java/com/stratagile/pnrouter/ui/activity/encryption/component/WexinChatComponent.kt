package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WexinChatActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.WexinChatModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for WexinChatActivity
 * @date 2019/12/27 16:17:50
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(WexinChatModule::class))
interface WexinChatComponent {
    fun inject(WexinChatActivity: WexinChatActivity): WexinChatActivity
}