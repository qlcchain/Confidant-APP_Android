package com.stratagile.pnrouter.ui.activity.encryption.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicWeiXinLocalContract
import com.stratagile.pnrouter.ui.activity.encryption.PicWeiXinLocalFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: presenter of PicWeiXinLocalFragment
 * @date 2019/12/03 17:30:10
 */

class PicWeiXinLocalPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: PicWeiXinLocalContract.View) : PicWeiXinLocalContract.PicWeiXinLocalContractPresenter {

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