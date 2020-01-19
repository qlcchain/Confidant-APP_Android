package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of SMSEncryptionActivity
 * @date 2020/01/17 14:47:42
 */
class SMSEncryptionPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SMSEncryptionContract.View) : SMSEncryptionContract.SMSEncryptionContractPresenter {

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