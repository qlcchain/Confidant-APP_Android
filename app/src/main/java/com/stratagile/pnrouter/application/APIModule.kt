package com.stratagile.pnrouter.application

import android.app.Application

import com.stratagile.pnrouter.data.api.API
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.data.api.HttpApi
import com.stratagile.pnrouter.data.api.HttpInfoInterceptor
import com.stratagile.pnrouter.data.api.RequestBodyInterceptor
import com.stratagile.pnrouter.data.qualifier.Remote
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author hu
 * @desc 功能描述
 * @date 2017/5/31 10:04
 */
@Module
class APIModule(private val application: Application) {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(HttpInfoInterceptor())
        builder.connectTimeout(API.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(API.IO_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(RequestBodyInterceptor())
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val builder = Retrofit.Builder()
        //线上环境
        builder.client(okHttpClient)
                .baseUrl(API.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideHttpAPI(restAdapter: Retrofit): HttpApi {
        return restAdapter.create(HttpApi::class.java)
    }

    //这里是对外输出部分
    @Provides
    @Singleton
    @Remote
    fun provideHttpAPIWrapper(httpAPI: HttpApi): HttpAPIWrapper {
        return HttpAPIWrapper(httpAPI)
    }
}
