package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.UserContract
import com.stratagile.pnrouter.ui.activity.router.UserFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of UserFragment
 * @date 2018/12/06 14:25:43
 */

class UserPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: UserContract.View) : UserContract.UserContractPresenter {

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