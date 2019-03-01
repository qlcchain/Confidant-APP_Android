package com.stratagile.pnrouter.ui.activity.login.presenter
import android.os.Environment
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.entity.BaseBackA
import com.stratagile.pnrouter.ui.activity.login.contract.SelectRouterContract
import com.stratagile.pnrouter.utils.SpUtil
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: presenter of SelectRouterActivity
 * @date 2018/09/12 13:59:14
 */
class SelectRouterPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: SelectRouterContract.View) : SelectRouterContract.SelectRouterContractPresenter {
    override fun upLoadFile() {
        val upLoadFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/" + SpUtil.getString(AppConfig.instance, ConstantValue.username, "") + ".png")
        val image = RequestBody.create(MediaType.parse("image/png"), upLoadFile)
        val photo = MultipartBody.Part.createFormData("", SpUtil.getString(AppConfig.instance, ConstantValue.username, "") + ".png", image)
//        RequestBody.create(MediaType.parse("text/plain"), SpUtil.getString(AppConfig.instance, ConstantValue.username, "") + ".png")
        val disposable = httpAPIWrapper.upLoadFile(photo)     //userId, nickName
                .subscribe(Consumer<BaseBackA> { upLoadAvatar ->
                    //isSuccesse
                    KLog.i("onSuccesse")
                    mView.closeProgressDialog()
                }, Consumer<Throwable> { throwable ->
                    //onError
                    KLog.i("onError")
                    throwable.printStackTrace()
                    mView.closeProgressDialog()
                }, Action {
                    //onComplete
                    KLog.i("onComplete")
                })
        mCompositeDisposable.add(disposable)
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
}