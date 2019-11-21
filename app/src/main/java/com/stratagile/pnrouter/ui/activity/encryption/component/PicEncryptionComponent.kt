package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicEncryptionActivity
 * @date 2019/11/21 15:26:11
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicEncryptionModule::class))
interface PicEncryptionComponent {
    fun inject(PicEncryptionActivity: PicEncryptionActivity): PicEncryptionActivity
}