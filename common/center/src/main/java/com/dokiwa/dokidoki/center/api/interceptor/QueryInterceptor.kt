package com.dokiwa.dokidoki.center.api.interceptor

import com.dokiwa.dokidoki.center.ext.sha1
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder
import java.util.*

/**
 * Created by Septenary on 2018/11/4.
 */
class QueryInterceptor(
    private val apiKey: String,
    private val apiSecret: String,
    queries: Map<String, String>,
    private val commonQueries: List<Pair<String, String>> = queries.toList(),
    @Volatile var timeDif: Long = 0L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // seed
        val ts = Pair("_ts", (System.currentTimeMillis() / 1000 + timeDif).toString())
        val nonce = Pair("_nonce", UUID.randomUUID().toString().substring(0, 6))
        val ak = Pair("_ak", apiKey)

        val originUrl = chain.request().url()

        // sorted sign parts
        val sortedQueries = originUrl.queryParameterNames().map {
            Pair(it, originUrl.queryParameter(it) ?: "")
        }.toMutableList().apply {
            add(ts)
            add(nonce)
            add(ak)
            addAll(commonQueries)
        }.sortedBy {
            it.first
        }

        // sign
        val sign = sortedQueries.joinToString("&") {
            "${it.first}=${URLEncoder.encode(it.second, "UTF-8")}"
        }.run {
            (this + apiSecret).sha1()
        }

        // new request
        val newUrl = originUrl.newBuilder().apply {
            sortedQueries.forEach {
                setQueryParameter(it.first, it.second)
            }
            setQueryParameter("_sign", sign)
        }.build()
        val request = chain.request().newBuilder().url(newUrl).build()
        return chain.proceed(request)
    }
}