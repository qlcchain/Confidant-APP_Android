package com.stratagile.pnrouter.ui.activity.user.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.user.QRFriendCodeActivity
import com.stratagile.pnrouter.ui.activity.user.module.QRFriendCodeModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: The component for QRFriendCodeActivity
 * @date 2018/12/25 11:45:06
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(QRFriendCodeModule::class))
interface QRFriendCodeComponent {
    fun inject(QRFriendCodeActivity: QRFriendCodeActivity): QRFriendCodeActivity
}