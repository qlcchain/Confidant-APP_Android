package com.stratagile.pnrouter.ui.activity.file.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.file.contract.FileTaskListContract
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.file
 * @Description: presenter of FileTaskListActivity
 * @date 2019/01/25 16:21:04
 */
class FileTaskListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileTaskListContract.View) : FileTaskListContract.FileTaskListContractPresenter {

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