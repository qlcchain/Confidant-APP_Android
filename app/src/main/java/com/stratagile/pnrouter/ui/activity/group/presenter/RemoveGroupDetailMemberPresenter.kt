package com.stratagile.pnrouter.ui.activity.group.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.group.contract.RemoveGroupDetailMemberContract
import com.stratagile.pnrouter.ui.activity.group.RemoveGroupDetailMemberActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: presenter of RemoveGroupDetailMemberActivity
 * @date 2019/03/21 10:15:05
 */
class RemoveGroupDetailMemberPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: RemoveGroupDetailMemberContract.View) : RemoveGroupDetailMemberContract.RemoveGroupDetailMemberContractPresenter {

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