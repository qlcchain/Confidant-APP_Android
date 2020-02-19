package com.stratagile.pnrouter.ui.activity.main.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.main.EncryptMsgActivity
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: The component for EncryptMsgActivity
 * @date 2020/02/19 16:13:12
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(EncryptMsgModule::class))
interface EncryptMsgComponent {
    fun inject(EncryptMsgActivity: EncryptMsgActivity): EncryptMsgActivity
}