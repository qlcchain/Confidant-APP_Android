package com.stratagile.pnrouter.ui.activity.file.component

import com.stratagile.pnrouter.application.AppComponent
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.FileManagerActivity
import com.stratagile.pnrouter.ui.activity.file.module.FileManagerModule

import dagger.Component

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The component for FileManagerActivity
 * @date 2019/01/23 14:15:29
 */
@ActivityScope
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(FileManagerModule::class))
interface FileManagerComponent {
    fun inject(FileManagerActivity: FileManagerActivity): FileManagerActivity
}