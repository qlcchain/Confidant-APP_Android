package com.stratagile.pnrouter.ui.activity.selectfriend.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendSendFileContract
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendSendFileActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: presenter of selectFriendSendFileActivity
 * @date 2019/03/06 15:41:57
 */
class selectFriendSendFilePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: selectFriendSendFileContract.View) : selectFriendSendFileContract.selectFriendSendFileContractPresenter {

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