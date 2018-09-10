package com.stratagile.pnrouter.ui.activity.conversation.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationListContract
import com.stratagile.pnrouter.ui.activity.conversation.ConversationListFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: presenter of ConversationListFragment
 * @date 2018/09/10 17:25:57
 */

class ConversationListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: ConversationListContract.View) : ConversationListContract.ConversationListContractPresenter {

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