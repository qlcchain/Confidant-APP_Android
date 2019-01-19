package com.stratagile.pnrouter.ui.activity.admin.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminUpCodeContract
import com.stratagile.pnrouter.ui.activity.admin.AdminUpCodeActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: presenter of AdminUpCodeActivity
 * @date 2019/01/19 15:31:09
 */
class AdminUpCodePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: AdminUpCodeContract.View) : AdminUpCodeContract.AdminUpCodeContractPresenter {

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