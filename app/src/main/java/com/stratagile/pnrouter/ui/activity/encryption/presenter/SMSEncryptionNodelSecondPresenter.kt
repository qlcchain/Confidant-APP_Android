package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelSecondContract
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelSecondActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of SMSEncryptionNodelSecondActivity
 * @date 2020/02/07 23:33:10
 */
class SMSEncryptionNodelSecondPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SMSEncryptionNodelSecondContract.View) : SMSEncryptionNodelSecondContract.SMSEncryptionNodelSecondContractPresenter {

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