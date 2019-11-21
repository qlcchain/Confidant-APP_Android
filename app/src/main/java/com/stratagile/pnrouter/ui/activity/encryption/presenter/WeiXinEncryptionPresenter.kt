package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeiXinEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.WeiXinEncryptionActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of WeiXinEncryptionActivity
 * @date 2019/11/21 15:26:37
 */
class WeiXinEncryptionPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: WeiXinEncryptionContract.View) : WeiXinEncryptionContract.WeiXinEncryptionContractPresenter {

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