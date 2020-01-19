package com.stratagile.pnrouter.ui.activity.conversation.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.FileEncryptionFragment
import com.stratagile.pnrouter.ui.activity.conversation.module.FileEncryptionModule

import dagger.Component

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The component for FileEncryptionFragment
 * @date 2019/11/20 10:12:15
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileEncryptionModule::class))
interface FileEncryptionComponent {
    fun inject(FileEncryptionFragment: FileEncryptionFragment): FileEncryptionFragment
}