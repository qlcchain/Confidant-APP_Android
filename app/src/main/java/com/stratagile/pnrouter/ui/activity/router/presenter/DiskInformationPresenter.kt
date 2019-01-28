package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.DiskInformationContract
import com.stratagile.pnrouter.ui.activity.router.DiskInformationActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of DiskInformationActivity
 * @date 2019/01/28 15:21:12
 */
class DiskInformationPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: DiskInformationContract.View) : DiskInformationContract.DiskInformationContractPresenter {

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