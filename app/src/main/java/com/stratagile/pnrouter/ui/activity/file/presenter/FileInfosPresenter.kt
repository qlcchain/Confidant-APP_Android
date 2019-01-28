package com.stratagile.pnrouter.ui.activity.file.presenter

import com.socks.library.KLog
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.data.fileInfo.FileInfo
import com.stratagile.pnrouter.data.fileInfo.FileInfosRepository
import com.stratagile.pnrouter.ui.activity.file.FileInfosFragment
import com.stratagile.pnrouter.ui.activity.file.contract.FileInfosContract


import java.io.File
import java.util.ArrayList
import java.util.Collections

import javax.inject.Inject

import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import rx.functions.Action1

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: presenter of FileInfosFragment
 * @date 2018/09/28 16:46:15
 */
class FileInfosPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: FileInfosContract.View, private val mFragment: FileInfosFragment) : FileInfosContract.FileInfosContractPresenter {
    private val mCompositeDisposable: CompositeDisposable

    private var mFileInfosRepository: FileInfosRepository? = null

    private var mDirectory: FileInfo? = null

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

    override fun loadFileInfos() {
        mFileInfosRepository!!
                .listFileInfos(mDirectory!!)
                .map<List<FileInfo>> { fileInfos ->
                    val retrnList = ArrayList<FileInfo>()
                    for (fileInfo in fileInfos) {
                        if (!fileInfo.getmFile().isHidden && !".".equals(fileInfo.name[0])) {
                            retrnList.add(fileInfo)
                        }
                    }
                    retrnList
                }
                .observeOn(Schedulers.trampoline())
                .subscribe(object : Observer<List<FileInfo>> {
                    lateinit var disposable: Disposable
                    override fun onSubscribe(d: Disposable) {
                        disposable = d
                    }

                    override fun onNext(fileInfos: List<FileInfo>) {
                        KLog.i(fileInfos.size.toString() + " ")
                        if (fileInfos.isEmpty()) {
                            mView.showNoFileInfos()
                        } else {
                            if (fileInfos.size == 1) {
                                if (fileInfos[0].name == "") {
                                    mView.showNoFileInfos()
                                    return
                                }
                            }
                            Collections.sort(fileInfos)
                            mView.showFileInfos(fileInfos)
                        }
                        disposable.dispose()
                    }

                    override fun onError(e: Throwable) {
                        mView.showNoFileInfos()
                        disposable.dispose()
                    }

                    override fun onComplete() {
                        disposable.dispose()
                    }
                })
        //                .subscribe(new Consumer<List<FileInfo>>() {
        //                    @Override
        //                    public void accept(List<FileInfo> fileInfos) throws Exception {
        //                        KLog.i(fileInfos.size() + " ");
        //                        if (fileInfos.isEmpty()) {
        //                            mView.showNoFileInfos();
        //                        } else {
        //                            if (fileInfos.size() == 1) {
        //                                if (fileInfos.get(0).getPath().equals("")) {
        //                                    mView.showNoFileInfos();
        //                                    return;
        //                                }
        //                            }
        //                            mView.showFileInfos(fileInfos);
        //                        }
        //                    }
        //                }, new Consumer<Throwable>() {
        //                    @Override
        //                    public void accept(Throwable throwable) throws Exception {
        //                        //onError
        //                        mView.showNoFileInfos();
        //                        KLog.i("onError");
        //                        throwable.printStackTrace();
        //                    }
        //                }, new Action() {
        //                    @Override
        //                    public void run() throws Exception {
        //                        //onComplete
        //                        KLog.i("onComplete");
        //                    }
        //                });
        //        mCompositeDisposable.add(disposable);
    }

    override fun init(fileInfosRepository: FileInfosRepository, fileInfo: FileInfo) {
        mFileInfosRepository = fileInfosRepository
        mDirectory = fileInfo
    }
}