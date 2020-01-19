package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicMenuLocalFragment
import com.stratagile.pnrouter.ui.activity.encryption.module.PicMenuLocalModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicMenuLocalFragment
 * @date 2019/12/02 16:00:46
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicMenuLocalModule::class))
interface PicMenuLocalComponent {
    fun inject(PicMenuLocalFragment: PicMenuLocalFragment): PicMenuLocalFragment
}