package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.SMSEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.SMSEncryptionNodelListActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of SMSEncryptionNodelListActivity
 * @date 2020/02/05 14:49:08
 */
class SMSEncryptionNodelListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SMSEncryptionNodelListContract.View) : SMSEncryptionNodelListContract.SMSEncryptionNodelListContractPresenter {

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