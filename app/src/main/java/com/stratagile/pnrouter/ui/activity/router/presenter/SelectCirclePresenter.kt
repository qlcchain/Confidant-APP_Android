package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.SelectCircleContract
import com.stratagile.pnrouter.ui.activity.router.SelectCircleActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of SelectCircleActivity
 * @date 2019/03/28 13:52:55
 */
class SelectCirclePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectCircleContract.View) : SelectCircleContract.SelectCircleContractPresenter {

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