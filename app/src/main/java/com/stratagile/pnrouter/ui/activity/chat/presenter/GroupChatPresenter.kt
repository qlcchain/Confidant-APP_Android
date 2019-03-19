package com.stratagile.pnrouter.ui.activity.chat.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.chat.contract.GroupChatContract
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.chat
 * @Description: presenter of GroupChatActivity
 * @date 2019/03/18 15:06:56
 */
class GroupChatPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: GroupChatContract.View) : GroupChatContract.GroupChatContractPresenter {

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