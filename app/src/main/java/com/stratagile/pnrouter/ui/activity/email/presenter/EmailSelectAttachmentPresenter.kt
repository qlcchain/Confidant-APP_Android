package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSelectAttachmentContract
import com.stratagile.pnrouter.ui.activity.email.EmailSelectAttachmentActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of EmailSelectAttachmentActivity
 * @date 2020/05/13 15:04:52
 */
class EmailSelectAttachmentPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EmailSelectAttachmentContract.View) : EmailSelectAttachmentContract.EmailSelectAttachmentContractPresenter {

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