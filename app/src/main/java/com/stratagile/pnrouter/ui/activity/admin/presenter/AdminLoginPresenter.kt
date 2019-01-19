package com.stratagile.pnrouter.ui.activity.admin.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.admin.contract.AdminLoginContract
import com.stratagile.pnrouter.ui.activity.admin.AdminLoginActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.admin
 * @Description: presenter of AdminLoginActivity
 * @date 2019/01/19 15:30:16
 */
class AdminLoginPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: AdminLoginContract.View) : AdminLoginContract.AdminLoginContractPresenter {

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