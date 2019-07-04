package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.EmailLoginContract
import com.stratagile.pnrouter.ui.activity.email.EmailLoginActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of EmailLoginActivity
 * @date 2019/07/02 15:20:41
 */
class EmailLoginPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EmailLoginContract.View) : EmailLoginContract.EmailLoginContractPresenter {

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