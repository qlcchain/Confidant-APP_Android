package com.stratagile.pnrouter.ui.activity.router.presenter
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.DiskManagementContract
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of DIsManagementActivity
 * @date 2019/01/28 11:29:37
 */
class DiskManagementPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: DiskManagementContract.View) : DiskManagementContract.DIsManagementContractPresenter {

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