package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Septenary on 2018/11/4.
 */
object TokenInterceptor : Interceptor {

    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request().newBuilder()
            .also { builder ->
                token?.apply {
                    builder.addHeader("token", this)
                    builder.url(chain.request().url().newBuilder().addQueryParameter("token", this).build())
                }
            }
            .build()
            .let {
                chain.proceed(it)
            }
    }
}