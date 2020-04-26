package com.stratagile.pnrouter.utils;

import android.util.Log;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TestUtil {
    public static String TAG = "TestUtil";

    public static void testRxJava() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("file://a");
        stringList.add("file://b");
        stringList.add("file://c");

//        Observable.just("token").flatMap(new Function<String, ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(String token) throws Exception {
//                return Observable
//                        .fromIterable(stringList)
//                        .map(new Function<String, String>() {
//                            @Override
//                            public String apply(String s) throws Exception {
//                                KLog.i("testRxjavaToList merge token " + Thread.currentThread().getName());
//                                return token + s;
//                            }
//                        })
//                        .flatMap(new Function<String, ObservableSource<String>>() {
//                            @Override
//                            public ObservableSource<String> apply(String s) throws Exception {
//                                return Observable.just(s).observeOn(Schedulers.io()).map(new Function<String, String>() {
//                                    @Override
//                                    public String apply(String s) throws Exception {
//                                        KLog.i("testRxjavaToList request " + Thread.currentThread().getName() + " " + s);
//                                        Thread.sleep(2000);
//                                        if (s.contains("file://b")) {
//                                            throw new NullPointerException("服务器异常");  //服务器异常
//                                        }
//                                        return "request done:" + s;
//                                    }
//                                }).onErrorReturnItem("null"); //当前流出错后，降级处理
//                            }
//                        })
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .map(new Function<String, String>() {
//                            @Override
//                            public String apply(String s) throws Exception {
//                                KLog.i("testRxjavaToList apply map " + Thread.currentThread().getName() + " " + s);
//                                return s;
//                            }
//                        })
//                        .observeOn(Schedulers.io());
//            }
//        }).toList().toObservable().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>() {
//            @Override
//            public void accept(List<String> strings) throws Exception {
//                KLog.i("testRxjavaToList subscribe " + Thread.currentThread().getName() + " " + strings);
//            }
//        });
//
        Observable.fromIterable(stringList).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String strings) throws Exception {
                return Observable
                        .just(strings)
                        .observeOn(Schedulers.io())
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {
                                KLog.i("testRxjavaToList request " + Thread.currentThread().getName() + " " + s);
                                Thread.sleep(2000);
                                if (s.contains("file://b")) {
                                    throw new NullPointerException("服务器异常");  //服务器异常
                                }
                                return "request done:" + s;
                            }
                        })
                        .onErrorReturnItem("null");
            }
        }).toList().toObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> strings) throws Exception {
                KLog.i("testRxjavaToList subscribe " + Thread.currentThread().getName() + " " + strings);
            }
        });

        Observable
                .fromIterable(stringList)
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                KLog.i("testRxjavaToList request " + Thread.currentThread().getName() + " " + s);
                Thread.sleep(2000);
                if (s.contains("file://b")) {
                    throw new NullPointerException("服务器异常");  //服务器异常
                }
                return "request done:" + s;
            }
        })
                .onErrorReturnItem("null")
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> strings) throws Exception {
                KLog.i("testRxjavaToList subscribe " + Thread.currentThread().getName() + " " + strings);
            }
        });


    }
}
