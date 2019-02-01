package com.stratagile.pnrouter.ui.activity.tox.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.tox.contract.TestToxContract
import com.stratagile.pnrouter.ui.activity.tox.TestToxActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.tox
 * @Description: presenter of TestToxActivity
 * @date 2019/02/01 12:07:44
 */
class TestToxPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: TestToxContract.View) : TestToxContract.TestToxContractPresenter {

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