package com.dokiwa.dokidoki.center.api.interceptor

import com.dokiwa.dokidoki.center.ext.hmacSha256Base64
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Septenary on 2018/11/4.
 */
class TokenInterceptor : Interceptor {

    private var macKey: String? = null
    private var accessToken: String? = null

    fun resetAuthenticationToken(macKey: String?, accessToken: String?) {
        this.macKey = macKey
        this.accessToken = accessToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (macKey.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
            chain.proceed(chain.request())
        } else {
            val authorization = chain.request().url().run {
                val ts = queryParameter("_ts")
                val nonce = queryParameter("_nonce")
                val mac = listOf(
                    ts,
                    nonce,
                    chain.request().method().toUpperCase(),
                    "${encodedPath()}?${query()}",
                    host(),
                    port(),
                    ""
                ).joinToString(
                    separator = "\n",
                    postfix = "\n"
                ).hmacSha256Base64(
                    macKey!!
                )

                """
                    MAC id="$accessToken",ts="$ts",nonce="$nonce",mac="$mac"
                """.trimIndent()
            }

            return chain.request()
                .newBuilder()
                .addHeader("Authorization", authorization)
                .build()
                .run {
                    chain.proceed(this)
                }
        }
    }
}