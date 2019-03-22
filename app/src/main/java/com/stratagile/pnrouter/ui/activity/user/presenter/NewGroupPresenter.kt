package com.stratagile.pnrouter.ui.activity.user.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.user.contract.NewGroupContract
import com.stratagile.pnrouter.ui.activity.user.NewGroupFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: presenter of NewGroupFragment
 * @date 2019/03/21 19:41:45
 */

class NewGroupPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: NewGroupContract.View) : NewGroupContract.NewGroupContractPresenter {

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