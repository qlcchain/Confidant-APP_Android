package com.stratagile.pnrouter.ui.activity.login.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.login.contract.SelectRouterContract
import com.stratagile.pnrouter.ui.activity.login.SelectRouterActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: presenter of SelectRouterActivity
 * @date 2018/09/12 13:59:14
 */
class SelectRouterPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectRouterContract.View) : SelectRouterContract.SelectRouterContractPresenter {

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