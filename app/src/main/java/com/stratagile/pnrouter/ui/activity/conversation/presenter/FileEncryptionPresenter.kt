package com.stratagile.pnrouter.ui.activity.conversation.presenter
import android.support.annotation.NonNull
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileEncryptionContract
import com.stratagile.pnrouter.ui.activity.conversation.FileEncryptionFragment
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: presenter of FileEncryptionFragment
 * @date 2019/11/20 10:12:15
 */

class FileEncryptionPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileEncryptionContract.View) : FileEncryptionContract.FileEncryptionContractPresenter {

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