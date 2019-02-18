package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.DiskReconfigureContract
import com.stratagile.pnrouter.ui.activity.router.DiskReconfigureActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of DiskReconfigureActivity
 * @date 2019/02/18 17:31:04
 */
class DiskReconfigurePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: DiskReconfigureContract.View) : DiskReconfigureContract.DiskReconfigureContractPresenter {

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