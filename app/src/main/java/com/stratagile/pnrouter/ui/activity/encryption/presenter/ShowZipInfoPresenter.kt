package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.ShowZipInfoContract
import com.stratagile.pnrouter.ui.activity.encryption.ShowZipInfoActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of ShowZipInfoActivity
 * @date 2019/12/30 10:42:25
 */
class ShowZipInfoPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: ShowZipInfoContract.View) : ShowZipInfoContract.ShowZipInfoContractPresenter {

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