package com.stratagile.pnrouter.ui.activity.selectfriend.presenter;
import android.support.annotation.NonNull;
import com.stratagile.pnrouter.data.api.HttpAPIWrapper;
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendContract;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import javax.inject.Inject;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: presenter of selectFriendActivity
 * @date 2018/09/25 14:58:33
 */
public class selectFriendPresenter implements selectFriendContract.selectFriendContractPresenter{

    HttpAPIWrapper httpAPIWrapper;
    private final selectFriendContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private selectFriendActivity mActivity;

    @Inject
    public selectFriendPresenter(@NonNull HttpAPIWrapper httpAPIWrapper, @NonNull selectFriendContract.View view, selectFriendActivity activity) {
        mView = view;
        this.httpAPIWrapper = httpAPIWrapper;
        mCompositeDisposable = new CompositeDisposable();
        this.mActivity = activity;
    }
    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        if (!mCompositeDisposable.isDisposed()) {
             mCompositeDisposable.dispose();
        }
    }

//    @Override
//    public void getUser(HashMap map) {
//        //mView.showProgressDialog();
//        Disposable disposable = httpAPIWrapper.getUser(map)
//                .subscribe(new Consumer<User>() {
//                    @Override
//                    public void accept(User user) throws Exception {
//                        //isSuccesse
//                        KLog.i("onSuccesse");
//                        mView.setText(user);
//                      //mView.closeProgressDialog();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        //onError
//                        KLog.i("onError");
//                        throwable.printStackTrace();
//                      //mView.closeProgressDialog();
//                      //ToastUtil.show(mActivity, mActivity.getString(R.string.loading_failed_1));
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        //onComplete
//                        KLog.i("onComplete");
//                    }
//                });
//        mCompositeDisposable.add(disposable);
//    }
}