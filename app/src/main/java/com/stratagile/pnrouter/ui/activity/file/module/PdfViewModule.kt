package com.stratagile.pnrouter.ui.activity.file.module

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.base.ActivityScope
import com.stratagile.pnrouter.ui.activity.file.PdfViewActivity
import com.stratagile.pnrouter.ui.activity.file.contract.PdfViewContract
import com.stratagile.pnrouter.ui.activity.file.presenter.PdfViewPresenter

import dagger.Module;
import dagger.Provides;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: The moduele of PdfViewActivity, provide field for PdfViewActivity
 * @date 2018/10/09 16:03:36
 */
@Module
class PdfViewModule (private val mView: PdfViewContract.View) {

    @Provides
    @ActivityScope
    fun providePdfViewPresenter(httpAPIWrapper: HttpAPIWrapper) :PdfViewPresenter {
        return PdfViewPresenter(httpAPIWrapper, mView)
    }

    @Provides
    @ActivityScope
    fun providePdfViewActivity() : PdfViewActivity {
        return mView as PdfViewActivity
    }
}