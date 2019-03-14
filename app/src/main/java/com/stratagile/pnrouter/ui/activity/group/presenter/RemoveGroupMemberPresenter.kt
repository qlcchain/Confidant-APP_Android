package com.stratagile.pnrouter.ui.activity.group.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupMemberContract
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupMemberActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: presenter of RemoveGroupMemberActivity
 * @date 2019/03/14 10:20:11
 */
class RemoveGroupMemberPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: RemoveGroupMemberContract.View) : RemoveGroupMemberContract.RemoveGroupMemberContractPresenter {

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