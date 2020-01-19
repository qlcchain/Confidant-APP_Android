package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionNodelListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicEncryptionNodelListActivity
 * @date 2019/12/23 16:07:44
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicEncryptionNodelListModule::class))
interface PicEncryptionNodelListComponent {
    fun inject(PicEncryptionNodelListActivity: PicEncryptionNodelListActivity): PicEncryptionNodelListActivity
}