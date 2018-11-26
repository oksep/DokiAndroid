package com.dokiwa.dokidoki.center.api

import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.Log
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

    private const val TAG = "API"

    private const val BASE_URL = BuildConfig.API_BASE_URL

    private val commonQueries = mapOf<String, String>()
    private val commonHeaders = mapOf<String, String>()

    private val queryInterceptor = QueryInterceptor(BuildConfig.API_KEY, commonQueries)
    private val headerInterceptor = HeaderInterceptor(commonHeaders)
    private val tokenInterceptor = TokenInterceptor()
    private val signingInterceptor = SigningInterceptor(BuildConfig.API_KEY, BuildConfig.API_SECRET) {
        queryInterceptor.timeDif = it
    }
    private val curlInterceptor = CURLInterceptor()
    private val gzipInterceptor = GZipInterceptor()

    private val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
        Log.d(TAG, "Response ==> $message")
    })

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
            .addInterceptor(signingInterceptor)
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

//    private fun commonHeaders(context: Context): Map<String, String> {
//        val headers = HashMap<String, String>()
//        headers["User-Agent"] = UserAgentUtil.getUserAgent()
//        headers["Accept-Language"] = "zh-cn"
//        headers["appId"] = DWApkConfig.getAppId()
//        headers["deviceId"] = ContextHelper.getDeviceId(context)
//        headers["sDeviceId"] = ContextHelper.getSdeviceId(context)
//        return headers
//    }
//    private fun commonQueryParams(context: Context): Map<String, String> {
//        val params = HashMap<String, String>()
//        params["appId"] = DWApkConfig.getAppId()
//        params["deviceId"] = ContextHelper.getDeviceId(context)
//        params["sDeviceId"] = ContextHelper.getSdeviceId(context)
//        return params
//    }
}
