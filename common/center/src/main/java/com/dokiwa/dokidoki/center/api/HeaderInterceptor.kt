package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * Created by Septenary on 2018/11/4.
 */
object HeaderInterceptor : Interceptor {

    private val map = HashMap<String, String>()

    fun addGlobalHeader(key: String, value: String) {
        map[key] = value
    }

    fun addGlobalHeaders(headers: Map<String, String>?) {
        if (headers != null) {
            map.putAll(headers)
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequestBuilder = chain.request().newBuilder()
        for ((key, value) in map) {
            newRequestBuilder.addHeader(key, value)
        }
        return chain.proceed(newRequestBuilder.build())
    }
}