package com.dokiwa.dokidoki.center.api

import android.text.TextUtils
import android.util.Log
import io.reactivex.subjects.BehaviorSubject
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/11/4.
 */

object DDApi {

    private const val NET_CONNECT_TIME_OUT = 15L
    private const val NET_WRITE_TIME_OUT = 5L
    private const val NET_READ_TIME_OUT = 5L

    private const val BASE_URL = "https://api.dokiwa.com"

    private val unAuthSubject by lazy { BehaviorSubject.create<String>() }

    private var apiToken: String? = null

    private val authenticator = Authenticator { route, response ->
        Log.d("DDApi", "response:$response")
        if (response.code() == 401) {
            if (!TextUtils.isEmpty(apiToken)) {
                unAuthSubject.onNext("UNKNOWN")
                Log.d("DDApi", "response.code() == 401")
            }
        }
        null
    }

    private val BASE_CLIENT by lazy {
        OkHttpClient.Builder()
            .connectTimeout(NET_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(NET_WRITE_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NET_READ_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(ParamsInterceptor)
            .addInterceptor(GZipInterceptor)
            .addInterceptor(TokenInterceptor)
            .addInterceptor(CURLInterceptor)
            .build()
    }

    fun <T> get(clazz: Class<T>): T {
        val client = BASE_CLIENT.newBuilder().authenticator(authenticator).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(clazz)
    }
}
