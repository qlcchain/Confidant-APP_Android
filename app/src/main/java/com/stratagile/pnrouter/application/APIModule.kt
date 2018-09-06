package com.stratagile.pnrouter.application

import android.app.Application
import android.content.Context
import android.util.Log

import com.stratagile.pnrouter.data.api.API
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.data.api.HttpApi
import com.stratagile.pnrouter.data.api.HttpInfoInterceptor
import com.stratagile.pnrouter.data.api.RequestBodyInterceptor
import com.stratagile.pnrouter.data.qualifier.Remote
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.data.web.*
import com.stratagile.pnrouter.entity.events.ReminderUpdateEvent

import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author hu
 * @desc 功能描述
 * @date 2017/5/31 10:04
 */
@Module
class APIModule(private val application: Application, private val signalServiceNetworkAccess: SignalServiceNetworkAccess) {
    val TAG = APIModule::class.java.simpleName
    var messageReceiver: SignalServiceMessageReceiver? = null
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

    @Provides
    @Singleton
    @Remote
    fun provideSignalServiceMessageReceiver(): SignalServiceMessageReceiver {
        if (this.messageReceiver == null) {
            this.messageReceiver = SignalServiceMessageReceiver(signalServiceNetworkAccess.getConfiguration(application)!!,
                    DynamicCredentialsProvider(application),
                    BuildConfig.USER_AGENT,
                    PipeConnectivityListener())
        }
        return this.messageReceiver!!
    }

    open class PipeConnectivityListener : ConnectivityListener {

        override fun onConnected() {
            Log.i("APIModule", "onConnected()")
        }

        override fun onConnecting() {
            Log.i("APIModule", "onConnecting()")
        }

        override fun onDisconnected() {
            Log.w("APIModule", "onDisconnected()")
        }

        override fun onAuthenticationFailure() {
            Log.w("APIModule", "onAuthenticationFailure()")
//            TextSecurePreferences.setUnauthorizedReceived(application, true)
            EventBus.getDefault().post(ReminderUpdateEvent())
        }

    }

    open class DynamicCredentialsProvider constructor(context: Context) : CredentialsProvider {

        private val context: Context

        override val user: String
            get() = TextSecurePreferences.getLocalNumber(context)!!

        override val password: String
            get() = TextSecurePreferences.getPushServerPassword(context)!!

        override val signalingKey: String
            get() = TextSecurePreferences.getSignalingKey(context)!!

        init {
            this.context = context
        }
    }
}
