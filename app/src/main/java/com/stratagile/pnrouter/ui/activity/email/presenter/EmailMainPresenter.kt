package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.EmailMainContract
import com.stratagile.pnrouter.ui.activity.email.EmailMainActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of EmailMainActivity
 * @date 2019/07/02 15:22:53
 */
class EmailMainPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EmailMainContract.View) : EmailMainContract.EmailMainContractPresenter {

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