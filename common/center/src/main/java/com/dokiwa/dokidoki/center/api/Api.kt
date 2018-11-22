package com.dokiwa.dokidoki.center.api

import com.dokiwa.dokidoki.center.Log
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/11/4.
 */

object Api {

    private const val TAG = "API"

    private const val BASE_URL = "https://api.dokiwa.com"

    private val commonQueries = mapOf<String, String>()
    private val commonHeaders = mapOf<String, String>()

    private val queryInterceptor = QueryInterceptor(commonQueries)
    private val headerInterceptor = HeaderInterceptor(commonHeaders)
    private val tokenInterceptor = TokenInterceptor()
    private val curlInterceptor = CURLInterceptor()
    private val gzipInterceptor = GZipInterceptor()

    val unAuthenticationSubject by lazy { BehaviorSubject.create<Unit>() }

    private val baseClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(5L, TimeUnit.SECONDS)
            .readTimeout(5L, TimeUnit.SECONDS)
            .addInterceptor(queryInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(gzipInterceptor)
            .addInterceptor(curlInterceptor)
            .authenticator { _, response ->
                if (response.code() == 401) {
                    Log.e(TAG, "Authentication failed, response:$response")
                    unAuthenticationSubject.onNext(Unit)
                }
                null
            }
            // .dns()
            // .dispatcher()
            .build()
    }

    fun <T> get(clazz: Class<T>, baseUrl: String = BASE_URL): T {
        val client = baseClient.newBuilder().build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(clazz)
    }
}
