package com.stratagile.pnrouter.ui.activity.file.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.file.contract.FileSendShareContract
import com.stratagile.pnrouter.ui.activity.file.FileSendShareActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: presenter of FileSendShareActivity
 * @date 2019/04/12 15:17:33
 */
class FileSendSharePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileSendShareContract.View) : FileSendShareContract.FileSendShareContractPresenter {

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