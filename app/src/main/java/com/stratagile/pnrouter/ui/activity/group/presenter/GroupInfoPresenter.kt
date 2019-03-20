package com.stratagile.pnrouter.ui.activity.group.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.group.contract.GroupInfoContract
import com.stratagile.pnrouter.ui.activity.group.GroupInfoActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: presenter of GroupInfoActivity
 * @date 2019/03/20 11:44:58
 */
class GroupInfoPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: GroupInfoContract.View) : GroupInfoContract.GroupInfoContractPresenter {

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