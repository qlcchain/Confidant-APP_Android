package com.stratagile.pnrouter.ui.activity.selectfriend.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendCreateGroupContract
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendCreateGroupActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: presenter of SelectFriendCreateGroupActivity
 * @date 2019/03/12 17:49:51
 */
class SelectFriendCreateGroupPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectFriendCreateGroupContract.View) : SelectFriendCreateGroupContract.SelectFriendCreateGroupContractPresenter {

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