package com.stratagile.pnrouter.ui.activity.main.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.main.contract.SearchContract
import com.stratagile.pnrouter.ui.activity.main.SearchActivity
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: presenter of SearchActivity
 * @date 2019/08/13 14:06:03
 */
class SearchPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SearchContract.View) : SearchContract.SearchContractPresenter {

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