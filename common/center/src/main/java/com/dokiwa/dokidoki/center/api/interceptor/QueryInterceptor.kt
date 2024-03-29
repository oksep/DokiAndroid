package com.dokiwa.dokidoki.center.api.interceptor

import com.dokiwa.dokidoki.center.ext.sha1
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import okio.Buffer
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.UUID

/**
 * Created by Septenary on 2018/11/4.
 */
private val UTF8 = Charset.forName("UTF-8")

class QueryInterceptor(
    private var apiKey: String,
    private var apiSecret: String,
    queries: Map<String, String>,
    private val commonQueries: List<Pair<String, String>> = queries.toList(),
    @Volatile var timeDif: Long = 0L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // seed
        val ts = Pair("_ts", (System.currentTimeMillis() / 1000 + timeDif).toString())
        val nonce = Pair("_nonce", UUID.randomUUID().toString().substring(0, 5))
        val ak = Pair("_ak", apiKey)

        val additionQuery = listOf(ts, nonce, ak) + commonQueries

        val originUrl = chain.request().url()

        // url query
        val urlQueryList = originUrl.queryParameterNames().map {
            Pair(it, originUrl.queryParameter(it) ?: "")
        }.toMutableList().run {
            addAll(additionQuery)
            map { pair ->
                Pair(pair.first, URLEncoder.encode(pair.second, "UTF-8"))
            }
        }.sortedBy { it.first }

        // body form
        val formBodyList = (chain.request().body() as? FormBody)?.run {
            val list = mutableListOf<Pair<String, String>>()
            for (i in 0 until this.size()) {
                list.add(Pair(this.encodedName(i), URLEncoder.encode(this.value(i) ?: "", "UTF-8")))
            }
            list
        } ?: mutableListOf()

        // multi-part
        val multiPartBodyList = (chain.request().body() as? MultipartBody)?.run {
            val list = mutableListOf<Pair<String, String>>()
            for (i in 0 until this.size()) {
                val part = this.part(i)
                val name = part.headers()
                    ?.get("Content-Disposition")
                    ?.split("; ")
                    ?.find { it.startsWith("name=") && !it.startsWith("name=\"file\"") }
                if (name != null && part.body().contentType() == null) {
                    val buffer = Buffer()
                    part.body().writeTo(buffer)
                    val string = buffer.readString(UTF8)
                    list.add(Pair("type", string))
                }
            }
            list
        } ?: mutableListOf()

        // sorted unSign parts
        val sortedUnSignParts = (urlQueryList + formBodyList + multiPartBodyList).sortedBy { it.first }

        // sign
        val sign = sortedUnSignParts.joinToString("&") {
            "${it.first}=${it.second}"
        }.run {
            (this + apiSecret).sha1()
        }

        // new url
        val newUrl = originUrl.newBuilder().apply {
            urlQueryList.forEach {
                setEncodedQueryParameter(it.first, it.second)
            }
            setQueryParameter("_sign", sign)
        }.build()

        // new request
        return chain.request().newBuilder().url(newUrl).build().run { chain.proceed(this) }
    }
}