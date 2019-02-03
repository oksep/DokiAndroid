package com.dokiwa.dokidoki.center.api

import android.content.Context
import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.convert.CustomConverterFactory
import com.dokiwa.dokidoki.center.api.interceptor.CURLInterceptor
import com.dokiwa.dokidoki.center.api.interceptor.GZipInterceptor
import com.dokiwa.dokidoki.center.api.interceptor.HeaderInterceptor
import com.dokiwa.dokidoki.center.api.interceptor.LocalAssetsInterceptor
import com.dokiwa.dokidoki.center.api.interceptor.QueryInterceptor
import com.dokiwa.dokidoki.center.api.interceptor.TokenInterceptor
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2018/11/4.
 */

object Api {

    const val TAG = "API"

    private val BASE_URL: String
        get() = BuildConfig.API_BASE_URL

    private val commonQueries by lazy {
        mapOf<String, String>()
        /*
        "appId" to AppCenter.get().context.packageName,
        "deviceId" to "",
        "sDeviceId" to ""
        */
    }

    private val commonHeaders by lazy {
        mapOf<String, String>()
        /*
        headers["User-Agent"] = UserAgentUtil.getUserAgent()
        headers["Accept-Language"] = "zh-cn"
        headers["appId"] = DWApkConfig.getAppId()
        headers["deviceId"] = ContextHelper.getDeviceId(context)
        headers["sDeviceId"] = ContextHelper.getSdeviceId(context)
        */
    }

    private val headerInterceptor = HeaderInterceptor(commonHeaders)
    private val queryInterceptor = QueryInterceptor(
        BuildConfig.API_KEY,
        BuildConfig.API_SECRET,
        commonQueries
    )
    private val tokenInterceptor = TokenInterceptor()
    private val curlInterceptor = CURLInterceptor()
    private val gzipInterceptor = GZipInterceptor()

    private val loggingInterceptor = HttpLoggingInterceptor(
        HttpLoggingInterceptor.Logger { message ->
            Log.d(TAG, "Response ==> $message")
        }
    )

    val unAuthenticationSubject by lazy { BehaviorSubject.create<Unit>() }

    fun resetAuthenticationToken(macKey: String?, accessToken: String?) {
        tokenInterceptor.resetAuthenticationToken(macKey, accessToken)
    }

    private val simpleClient by lazy { OkHttpClient.Builder().build() }

    private val dokiClient by lazy {
        simpleClient
            .newBuilder()
            .connectTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(5L, TimeUnit.SECONDS)
            .readTimeout(5L, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(queryInterceptor)
            .addInterceptor(tokenInterceptor)
//            .addInterceptor(gzipInterceptor)
            .addInterceptor(curlInterceptor)
            .addInterceptor(loggingInterceptor.apply { level = HttpLoggingInterceptor.Level.BODY })
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

    // dok api
    fun <T> get(clazz: Class<T>, baseUrl: String = BASE_URL): T {
        val client = dokiClient.newBuilder().build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(CustomConverterFactory.create())
            .client(client)
            .build()
            .create(clazz)
    }

    // simple api
    fun <T> getSimpleClient(clazz: Class<T>, baseUrl: String): T {
        val client = dokiClient.newBuilder().build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(clazz)
    }

    // local api
    fun <T> getLocalAsset(context: Context, clazz: Class<T>): T {
        val client = dokiClient.newBuilder().addInterceptor(LocalAssetsInterceptor(context)).build()
        return Retrofit.Builder()
            .baseUrl("http://local.assets")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(CustomConverterFactory.create())
            .client(client)
            .build()
            .create(clazz)
    }

    // 矫正本地时间
    fun correctTimestamp(timeDif: Long) {
        queryInterceptor.timeDif = timeDif
    }

    fun testUnAuth() {
        Log.d("LoginPlugin", "test unauth")
        unAuthenticationSubject.onNext(Unit)
    }
}