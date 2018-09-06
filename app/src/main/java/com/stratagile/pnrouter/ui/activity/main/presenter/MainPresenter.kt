package com.stratagile.pnrouter.ui.activity.main.presenter


import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract

import javax.inject.Inject

import io.reactivex.disposables.CompositeDisposable

/**
 * @author hzp
 * @Package com.stratagile.qlink.ui.activity.main
 * @Description: presenter of MainActivity
 * @date 2018/01/09 09:57:09
 */
class MainPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: MainContract.View) : MainContract.MainContractPresenter {
    override fun sendMessage(message: String) {
        signalServiceMessageReceiver.createMessagePipe().webSocketConnection().send(message)
    }

    override fun showToast() {
        mView.showToast()
    }

    private val mCompositeDisposable: CompositeDisposable

    lateinit var signalServiceMessageReceiver: PNRouterServiceMessageReceiver

    init {
        mCompositeDisposable = CompositeDisposable()
        signalServiceMessageReceiver = AppConfig.instance.messageReceiver!!
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