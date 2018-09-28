package com.stratagile.pnrouter.ui.activity.file.presenter

import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.file.FileChooseActivity
import com.stratagile.pnrouter.ui.activity.file.contract.FileChooseContract

import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: presenter of FileChooseActivity
 * @date 2018/05/18 14:15:35
 */
class FileChoosePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileChooseContract.View, private val mActivity: FileChooseActivity) : FileChooseContract.FileInfosContractPresenter {
    private val mCompositeDisposable: CompositeDisposable

    init {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }
}