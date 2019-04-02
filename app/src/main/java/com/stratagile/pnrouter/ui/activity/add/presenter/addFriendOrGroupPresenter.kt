package com.stratagile.pnrouter.ui.activity.add.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.add.contract.addFriendOrGroupContract
import com.stratagile.pnrouter.ui.activity.add.addFriendOrGroupActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.add
 * @Description: presenter of addFriendOrGroupActivity
 * @date 2019/04/02 16:08:05
 */
class addFriendOrGroupPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: addFriendOrGroupContract.View) : addFriendOrGroupContract.addFriendOrGroupContractPresenter {

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