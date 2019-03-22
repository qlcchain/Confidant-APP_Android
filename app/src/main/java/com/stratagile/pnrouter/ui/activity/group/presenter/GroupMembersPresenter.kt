package com.stratagile.pnrouter.ui.activity.group.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.group.contract.GroupMembersContract
import com.stratagile.pnrouter.ui.activity.group.GroupMembersActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: presenter of GroupMembersActivity
 * @date 2019/03/22 15:19:37
 */
class GroupMembersPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: GroupMembersContract.View) : GroupMembersContract.GroupMembersContractPresenter {

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