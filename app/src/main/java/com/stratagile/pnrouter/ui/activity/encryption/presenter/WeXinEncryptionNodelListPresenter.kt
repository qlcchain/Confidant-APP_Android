package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionNodelListActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of WeXinEncryptionNodelListActivity
 * @date 2019/12/26 10:33:40
 */
class WeXinEncryptionNodelListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: WeXinEncryptionNodelListContract.View) : WeXinEncryptionNodelListContract.WeXinEncryptionNodelListContractPresenter {

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