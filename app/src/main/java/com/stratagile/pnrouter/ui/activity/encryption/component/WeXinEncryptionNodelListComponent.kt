package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionNodelListActivity
import com.stratagile.pnrouter.ui.activity.encryption.module.WeXinEncryptionNodelListModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for WeXinEncryptionNodelListActivity
 * @date 2019/12/26 10:33:40
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(WeXinEncryptionNodelListModule::class))
interface WeXinEncryptionNodelListComponent {
    fun inject(WeXinEncryptionNodelListActivity: WeXinEncryptionNodelListActivity): WeXinEncryptionNodelListActivity
}