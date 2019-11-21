package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeiXinEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.WeiXinEncryptionModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for WeiXinEncryptionActivity
 * @date 2019/11/21 15:26:37
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(WeiXinEncryptionModule::class))
interface WeiXinEncryptionComponent {
    fun inject(WeiXinEncryptionActivity: WeiXinEncryptionActivity): WeiXinEncryptionActivity
}