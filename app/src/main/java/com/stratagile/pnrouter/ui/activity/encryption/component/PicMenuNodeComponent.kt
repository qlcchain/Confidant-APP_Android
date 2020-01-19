package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicMenuNodeFragment
import com.stratagile.pnrouter.ui.activity.encryption.module.PicMenuNodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicMenuNodeFragment
 * @date 2019/12/02 16:04:58
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicMenuNodeModule::class))
interface PicMenuNodeComponent {
    fun inject(PicMenuNodeFragment: PicMenuNodeFragment): PicMenuNodeFragment
}