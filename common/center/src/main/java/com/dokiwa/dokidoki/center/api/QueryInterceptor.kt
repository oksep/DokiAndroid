package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Septenary on 2018/11/4.
 */
class QueryInterceptor(private val map: Map<String, String>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val urlBuilder = chain.request().url().newBuilder()
        map.forEach { entry ->
            urlBuilder.addQueryParameter(entry.key, entry.value)
        }
        val url = urlBuilder.build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}