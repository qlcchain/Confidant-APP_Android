package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.RouterAliasSetContract
import com.stratagile.pnrouter.ui.activity.router.RouterAliasSetActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of RouterAliasSetActivity
 * @date 2019/02/21 11:00:31
 */
class RouterAliasSetPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: RouterAliasSetContract.View) : RouterAliasSetContract.RouterAliasSetContractPresenter {

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