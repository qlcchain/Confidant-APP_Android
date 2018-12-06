package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.RouterCreateUserContract
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of RouterCreateUserActivity
 * @date 2018/12/06 17:59:39
 */
class RouterCreateUserPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: RouterCreateUserContract.View) : RouterCreateUserContract.RouterCreateUserContractPresenter {

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