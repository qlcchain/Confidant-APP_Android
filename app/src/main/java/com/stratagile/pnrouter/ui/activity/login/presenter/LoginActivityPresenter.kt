package com.stratagile.pnrouter.ui.activity.login.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.login.contract.LoginActivityContract
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: presenter of LoginActivityActivity
 * @date 2018/09/10 15:05:29
 */
class LoginActivityPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: LoginActivityContract.View, private val mActivity: LoginActivityActivity) : LoginActivityContract.LoginActivityContractPresenter {

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