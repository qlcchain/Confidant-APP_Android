package com.stratagile.pnrouter.ui.activity.user.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.user.contract.SendAddFriendContract
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: presenter of SendAddFriendActivity
 * @date 2019/01/02 11:19:43
 */
class SendAddFriendPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SendAddFriendContract.View) : SendAddFriendContract.SendAddFriendContractPresenter {

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