package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.EmailFileAttachmentShowContract
import com.stratagile.pnrouter.ui.activity.email.EmailFileAttachmentShowActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of EmailFileAttachmentShowActivity
 * @date 2020/05/14 10:45:17
 */
class EmailFileAttachmentShowPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EmailFileAttachmentShowContract.View) : EmailFileAttachmentShowContract.EmailFileAttachmentShowContractPresenter {

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