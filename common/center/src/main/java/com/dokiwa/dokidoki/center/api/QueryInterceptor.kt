package com.dokiwa.dokidoki.center.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * Created by Septenary on 2018/11/4.
 */
class QueryInterceptor(
    private val apiKey: String,
    private val map: Map<String, String>,
    var timeDif: Long = 0L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request()
            .url()
            .newBuilder()
            .fillInSeedParams(apiKey, timeDif)
            .apply {
                map.forEach { entry ->
                    addQueryParameter(entry.key, entry.value)
                }
            }
            .build()
        val request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}

fun HttpUrl.Builder.fillInSeedParams(apiKey: String, timeDif: Long = 0): HttpUrl.Builder {
    setQueryParameter("_ts", (System.currentTimeMillis() / 1000 + timeDif).toString())
    setQueryParameter("_nonce", UUID.randomUUID().toString().substring(0, 6))
    setQueryParameter("_ak", apiKey)
    return this
}