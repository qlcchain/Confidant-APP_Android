package com.stratagile.pnrouter.ui.activity.router.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.ShareTempQRCodeContract
import com.stratagile.pnrouter.ui.activity.router.ShareTempQRCodeActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of ShareTempQRCodeActivity
 * @date 2019/04/17 14:04:59
 */
class ShareTempQRCodePresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: ShareTempQRCodeContract.View) : ShareTempQRCodeContract.ShareTempQRCodeContractPresenter {

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