package com.stratagile.pnrouter.ui.activity.file.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.file.contract.FileManagerContract
import com.stratagile.pnrouter.ui.activity.file.FileManagerActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: presenter of FileManagerActivity
 * @date 2019/01/23 14:15:29
 */
class FileManagerPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileManagerContract.View) : FileManagerContract.FileManagerContractPresenter {

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