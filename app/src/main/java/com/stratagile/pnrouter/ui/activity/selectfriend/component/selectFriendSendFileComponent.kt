package com.stratagile.pnrouter.ui.activity.selectfriend.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendSendFileActivity
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendSendFileModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: The component for selectFriendSendFileActivity
 * @date 2019/03/06 15:41:57
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(selectFriendSendFileModule::class))
interface selectFriendSendFileComponent {
    fun inject(selectFriendSendFileActivity: selectFriendSendFileActivity): selectFriendSendFileActivity
}