package com.stratagile.pnrouter.ui.activity.user.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.user.contract.TermsOfServiceContract
import com.stratagile.pnrouter.ui.activity.user.TermsOfServiceFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: presenter of TermsOfServiceFragment
 * @date 2019/04/22 18:23:24
 */

class TermsOfServicePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: TermsOfServiceContract.View) : TermsOfServiceContract.TermsOfServiceContractPresenter {

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