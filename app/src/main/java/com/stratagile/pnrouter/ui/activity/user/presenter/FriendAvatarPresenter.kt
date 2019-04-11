package com.stratagile.pnrouter.ui.activity.user.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.user.contract.FriendAvatarContract
import com.stratagile.pnrouter.ui.activity.user.FriendAvatarActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: presenter of FriendAvatarActivity
 * @date 2019/04/11 18:10:07
 */
class FriendAvatarPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FriendAvatarContract.View) : FriendAvatarContract.FriendAvatarContractPresenter {

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