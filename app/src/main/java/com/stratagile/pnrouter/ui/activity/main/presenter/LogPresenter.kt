package com.stratagile.pnrouter.ui.activity.main.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.main.contract.LogContract
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: presenter of LogActivity
 * @date 2018/09/18 09:45:46
 */
class LogPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: LogContract.View) : LogContract.LogContractPresenter {

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