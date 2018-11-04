package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Septenary on 2018/11/4.
 */
object ParamsInterceptor : Interceptor {
    // mapOf("version" to BuildConfig.VERSION_NAME, "app" to "android"),

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}