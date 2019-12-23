package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionNodelListContract
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionNodelListActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of PicEncryptionNodelListActivity
 * @date 2019/12/23 16:07:44
 */
class PicEncryptionNodelListPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: PicEncryptionNodelListContract.View) : PicEncryptionNodelListContract.PicEncryptionNodelListContractPresenter {

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