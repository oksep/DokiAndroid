package com.dokiwa.dokidoki.center.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by Septenary on 2018/11/4.
 */
class HeaderInterceptor(private val map: Map<String, String>) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequestBuilder = chain.request().newBuilder()
        map.forEach { entry ->
            newRequestBuilder.addHeader(entry.key, entry.value)
        }
        return chain.proceed(newRequestBuilder.build())
    }
}