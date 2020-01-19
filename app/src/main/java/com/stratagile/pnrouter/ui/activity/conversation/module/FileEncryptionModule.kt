package com.stratagile.pnrouter.ui.activity.conversation.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.conversation.FileEncryptionFragment
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileEncryptionContract
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileEncryptionPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: The moduele of FileEncryptionFragment, provide field for FileEncryptionFragment
 * @date 2019/11/20 10:12:15
 */
@Module
class FileEncryptionModule (private val mView: FileEncryptionContract.View) {

    @Provides
    @ActivityScope
    fun provideFileEncryptionPresenter(httpAPIWrapper: HttpAPIWrapper) :FileEncryptionPresenter {
        return FileEncryptionPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun provideFileEncryptionFragment() : FileEncryptionFragment {
        return mView as FileEncryptionFragment
    }
}