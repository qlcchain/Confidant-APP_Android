package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSendContract
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of EmailSendActivity
 * @date 2019/07/25 11:21:29
 */
class EmailSendPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EmailSendContract.View) : EmailSendContract.EmailSendContractPresenter {

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