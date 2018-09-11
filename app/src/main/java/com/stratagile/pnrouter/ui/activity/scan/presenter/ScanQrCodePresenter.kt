package com.stratagile.pnrouter.ui.activity.scan.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.scan.contract.ScanQrCodeContract
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.scan
 * @Description: presenter of ScanQrCodeActivity
 * @date 2018/09/11 15:29:14
 */
class ScanQrCodePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: ScanQrCodeContract.View, private val mActivity: ScanQrCodeActivity) : ScanQrCodeContract.ScanQrCodeContractPresenter {

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