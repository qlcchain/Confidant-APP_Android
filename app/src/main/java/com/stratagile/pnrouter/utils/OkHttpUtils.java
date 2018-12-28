package com.stratagile.pnrouter.utils;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {
    private static OkHttpUtils okHttpUtils;
    private final Handler myHandler;
    private OkHttpClient client;

    private OkHttpUtils(){
        myHandler = new Handler(Looper.getMainLooper());
        client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
    }
    public static OkHttpUtils getInstance(){
        if (okHttpUtils==null){
            synchronized (OkHttpUtils.class){
                if (okHttpUtils==null){
                    return okHttpUtils=new OkHttpUtils();
                }
            }
        }
        return okHttpUtils;
    }
    public void doGet(String url, final OkCallback okCallback){
        FormBody.Builder builder = new FormBody.Builder();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (okCallback!=null){
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            okCallback.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response!=null && response.isSuccessful()){
                                String string = response.body().string();
                                if (okCallback!=null){
                                    okCallback.onResponse(string);
                                    return;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (okCallback!=null){
                            okCallback.onFailure(new Exception("网络异常"));
                        }
                    }
                });
            }
        });
    }
    public void doPost(String url, Map<String,String> map, final OkCallback okCallback){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key:map.keySet()) {
            builder.add(key,map.get(key));
        }
        FormBody build = builder.build();
        final Request request = new Request.Builder()
                .post(build)
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (okCallback!=null){
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            okCallback.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response!=null && response.isSuccessful()){
                                String string = response.body().string();
                                if (okCallback!=null){
                                    okCallback.onResponse(string);
                                    return;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (okCallback!=null){
                            okCallback.onFailure(new Exception("网络异常"));
                        }
                    }
                });
            }
        });
    }
    public interface OkCallback{
        void onFailure(Exception e);
        void onResponse(String json);
    }
}
