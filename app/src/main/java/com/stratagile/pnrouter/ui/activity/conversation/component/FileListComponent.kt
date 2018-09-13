package com.stratagile.pnrouter.ui.activity.conversation.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.conversation.module.FileListModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The component for FileListFragment
 * @date 2018/09/13 15:32:14
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileListModule::class))
interface FileListComponent {
    fun inject(FileListFragment: FileListFragment): FileListFragment
}