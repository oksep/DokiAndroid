package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Septenary on 2018/11/4.
 */
class TokenInterceptor(var token: String? = null) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (token != null) {
            chain.request()
                .newBuilder()
                .addHeader("token", token!!)
                .url(chain.request().url().newBuilder().addQueryParameter("token", token).build())
                .build()
                .run { chain.proceed(this) }
        } else {
            chain.proceed(chain.request())
        }
    }
}