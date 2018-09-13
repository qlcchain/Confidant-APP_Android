package com.stratagile.pnrouter.ui.activity.chat.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.chat.contract.ChatContract
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: presenter of ChatActivity
 * @date 2018/09/13 13:18:46
 */
class ChatPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: ChatContract.View, private val mActivity: ChatActivity) : ChatContract.ChatContractPresenter {

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