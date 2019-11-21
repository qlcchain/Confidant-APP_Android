package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.WeXinEncryptionListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for WeXinEncryptionListActivity
 * @date 2019/11/21 15:27:44
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(WeXinEncryptionListModule::class))
interface WeXinEncryptionListComponent {
    fun inject(WeXinEncryptionListActivity: WeXinEncryptionListActivity): WeXinEncryptionListActivity
}