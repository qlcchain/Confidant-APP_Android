package com.stratagile.pnrouter.ui.activity.login.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.login.contract.VerifyingFingerprintContract
import com.stratagile.pnrouter.ui.activity.login.VerifyingFingerprintActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: presenter of VerifyingFingerprintActivity
 * @date 2019/02/26 14:40:52
 */
class VerifyingFingerprintPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: VerifyingFingerprintContract.View) : VerifyingFingerprintContract.VerifyingFingerprintContractPresenter {

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