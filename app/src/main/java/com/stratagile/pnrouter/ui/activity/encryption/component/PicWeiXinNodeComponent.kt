package com.stratagile.pnrouter.ui.activity.encryption.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.encryption.PicWeiXinNodeFragment
import com.stratagile.pnrouter.ui.activity.encryption.module.PicWeiXinNodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: The component for PicWeiXinNodeFragment
 * @date 2019/12/03 17:30:27
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(PicWeiXinNodeModule::class))
interface PicWeiXinNodeComponent {
    fun inject(PicWeiXinNodeFragment: PicWeiXinNodeFragment): PicWeiXinNodeFragment
}