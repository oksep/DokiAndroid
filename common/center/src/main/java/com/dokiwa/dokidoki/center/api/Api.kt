package com.dokiwa.dokidoki.center.api

import android.content.Context
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.center.BuildConfig
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.convert.CustomConverterFactory
import com.dokiwa.dokidoki.center.api.interceptor.*
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import com.dokiwa.dokidoki.center.util.DeviceUtils
import com.dokiwa.dokidoki.center.util.NetworkUtils
import com.jaredrummler.android.device.DeviceName
import io.reactivex.subjects.PublishSubject
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
        mapOf<String, String>(
            "_av" to AppUtil.getVerName(AppCenter.context),
            "_avc" to AppUtil.getVerCode(AppCenter.context).toString(),
            "_uid" to (ILoginPlugin.get().getLoginUserUUID() ?: ""),
            "_d" to DeviceName.getDeviceName(),
            "_nw" to NetworkUtils.getNetworkType().value,
            "_did" to DeviceUtils.getUniqueDeviceId()
        )
    }

    private val commonHeaders by lazy {
        mapOf<String, String>()
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

    val unAuthenticationSubject by lazy { PublishSubject.create<Unit>() }

    fun resetAuthenticationToken(macKey: String?, accessToken: String?) {
        tokenInterceptor.resetAuthenticationToken(macKey, accessToken)
    }

    private val simpleClient by lazy { OkHttpClient.Builder().build() }

    private val dokiClient by lazy {
        simpleClient
            .newBuilder()
            .connectTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(10L, TimeUnit.SECONDS)
            .readTimeout(10L, TimeUnit.SECONDS)
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
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(CustomConverterFactory.create())
            .client(dokiClient)
            .build()
            .create(clazz)
    }

    fun <T> getNoTimeOut(clazz: Class<T>, baseUrl: String = BASE_URL): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(CustomConverterFactory.create())
            .client(dokiClient.newBuilder().also {
                it.writeTimeout(0, TimeUnit.MILLISECONDS)
                it.readTimeout(0, TimeUnit.MILLISECONDS)
            }.build())
            .build()
            .create(clazz)
    }

    // simple api
    fun <T> getSimpleClient(clazz: Class<T>, baseUrl: String): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create())
            .client(dokiClient)
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