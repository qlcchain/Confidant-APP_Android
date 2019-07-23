package com.stratagile.pnrouter.ui.activity.email.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.email.contract.SelectEmailFriendContract
import com.stratagile.pnrouter.ui.activity.email.SelectEmailFriendActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: presenter of SelectEmailFriendActivity
 * @date 2019/07/23 17:37:47
 */
class SelectEmailFriendPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectEmailFriendContract.View) : SelectEmailFriendContract.SelectEmailFriendContractPresenter {

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