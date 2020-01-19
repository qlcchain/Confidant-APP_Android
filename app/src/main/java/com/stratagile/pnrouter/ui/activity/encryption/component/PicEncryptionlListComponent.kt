package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionlListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionlListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicEncryptionlListActivity
 * @date 2019/11/21 15:27:22
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicEncryptionlListModule::class))
interface PicEncryptionlListComponent {
    fun inject(PicEncryptionlListActivity: PicEncryptionlListActivity): PicEncryptionlListActivity
}