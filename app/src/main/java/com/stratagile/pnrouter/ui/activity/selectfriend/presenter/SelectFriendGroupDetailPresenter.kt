package com.stratagile.pnrouter.ui.activity.selectfriend.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendGroupDetailContract
import com.stratagile.pnrouter.ui.activity.selectfriend.SelectFriendGroupDetailActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: presenter of SelectFriendGroupDetailActivity
 * @date 2019/03/21 10:15:49
 */
class SelectFriendGroupDetailPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectFriendGroupDetailContract.View) : SelectFriendGroupDetailContract.SelectFriendGroupDetailContractPresenter {

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