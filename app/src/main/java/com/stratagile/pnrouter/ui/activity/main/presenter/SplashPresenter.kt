package com.stratagile.pnrouter.ui.activity.main.presenter

import android.support.annotation.NonNull
import android.util.Log
import com.socks.library.KLog
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.SplashActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit
import kotlin.math.log

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: presenter of SplashActivity
 * @date 2018/09/10 22:25:34
 */
class SplashPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SplashContract.View) : SplashContract.SplashContractPresenter {
    private val mCompositeDisposable: CompositeDisposable
    private val JUMPTOMAIN = 0
    private val JUMPTOLOGIN = 1
    private val HASPUDATE = 3
    private val getLastVersionBack = false
    private var permissionState = -1    //-1表示原始状态,0表示允许,1表示拒绝.
    private var hasUpdate = false
    private var timeOver = false
    private val jump = JUMPTOLOGIN
    private var jumpToGuest = false

    override fun doAutoLogin() {
        Log.i("splash", "2")
    }

    override fun getLastVersion() {
        Log.i("splash", "1")
    }

    override fun getPermission() {
        Log.i("splash", "3")
    }

    override fun observeJump() {
        Observable.interval(0, 1, TimeUnit.SECONDS).take(3)
                .map { aLong -> 2 - aLong }
                .observeOn(AndroidSchedulers.mainThread())//发射用的是observeOn
                .doOnSubscribe {
                    KLog.i("1")
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        KLog.i("2")
                    }

                    override fun onNext(remainTime: Long) {
                        KLog.i("剩余时间$remainTime")
                    }

                    override fun onError(e: Throwable) {
                        KLog.i("4")
                    }

                    override fun onComplete() {
                        //                        jump = JUMPTOGUEST;
                        timeOver = true
                        KLog.i("时间到，开始跳转")
                        mView.jumpToGuest()
//                        if (permissionState != 0) {
//                            return
//                        }
//                        if (jumpToGuest) {
//                            mView.jumpToGuest()
//                            return
//                        }
//                        if (jump == JUMPTOMAIN) {
//                            mView.loginSuccees()
//                        } else if (jump == JUMPTOLOGIN) {
//                            mView.jumpToLogin()
//                        }
                    }
                })
    }

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