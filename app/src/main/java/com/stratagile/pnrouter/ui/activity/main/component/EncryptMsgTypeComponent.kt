package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EncryptMsgTypeActivity
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgTypeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for EncryptMsgTypeActivity
 * @date 2020/02/19 16:13:36
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EncryptMsgTypeModule::class))
interface EncryptMsgTypeComponent {
    fun inject(EncryptMsgTypeActivity: EncryptMsgTypeActivity): EncryptMsgTypeActivity
}