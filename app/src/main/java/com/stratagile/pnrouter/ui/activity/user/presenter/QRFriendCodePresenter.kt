package com.stratagile.pnrouter.ui.activity.user.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.user.contract.QRFriendCodeContract
import com.stratagile.pnrouter.ui.activity.user.QRFriendCodeActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: presenter of QRFriendCodeActivity
 * @date 2018/12/25 11:45:06
 */
class QRFriendCodePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: QRFriendCodeContract.View) : QRFriendCodeContract.QRFriendCodeContractPresenter {

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