package com.stratagile.pnrouter.ui.activity.main.presenter


import android.widget.Toast
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract

import javax.inject.Inject

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.qlink.ui.activity.main
 * @Description: presenter of MainActivity
 * @date 2018/01/09 09:57:09
 */
class MainPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: MainContract.View, private val mActivity: MainActivity) : MainContract.MainContractPresenter {
    override fun showToast() {
        mView.showToast()
    }

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

    override fun latlngParseCountry(map: Map<*, *>) {
    }

}