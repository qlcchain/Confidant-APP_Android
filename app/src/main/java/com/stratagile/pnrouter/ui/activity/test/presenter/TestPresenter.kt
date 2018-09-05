package com.stratagile.pnrouter.ui.activity.test.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.test.contract.TestContract
import com.stratagile.pnrouter.ui.activity.test.TestActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.test
 * @Description: presenter of TestActivity
 * @date 2018/09/05 09:35:42
 */
class TestPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: TestContract.View, private val mActivity: TestActivity) : TestContract.TestContractPresenter {

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