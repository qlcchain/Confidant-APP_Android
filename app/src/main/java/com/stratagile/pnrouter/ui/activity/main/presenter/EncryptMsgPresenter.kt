package com.stratagile.pnrouter.ui.activity.main.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgContract
import com.stratagile.pnrouter.ui.activity.main.EncryptMsgActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: presenter of EncryptMsgActivity
 * @date 2020/02/19 16:13:12
 */
class EncryptMsgPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: EncryptMsgContract.View) : EncryptMsgContract.EncryptMsgContractPresenter {

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