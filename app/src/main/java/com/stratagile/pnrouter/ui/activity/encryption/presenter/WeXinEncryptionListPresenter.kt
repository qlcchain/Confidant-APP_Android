package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.WeXinEncryptionListActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of WeXinEncryptionListActivity
 * @date 2019/11/21 15:27:44
 */
class WeXinEncryptionListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: WeXinEncryptionListContract.View) : WeXinEncryptionListContract.WeXinEncryptionListContractPresenter {

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