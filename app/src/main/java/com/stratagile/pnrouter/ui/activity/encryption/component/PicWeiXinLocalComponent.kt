package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicWeiXinLocalFragment
import com.stratagile.pnrouter.ui.activity.encryption.module.PicWeiXinLocalModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicWeiXinLocalFragment
 * @date 2019/12/03 17:30:10
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicWeiXinLocalModule::class))
interface PicWeiXinLocalComponent {
    fun inject(PicWeiXinLocalFragment: PicWeiXinLocalFragment): PicWeiXinLocalFragment
}