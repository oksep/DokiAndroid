package com.dokiwa.dokidoki.center.api.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * Created by Septenary on 2019/2/25.
 */
class LocalAssetsInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val path = chain.request().url().encodedPath()

        val json = context.assets.open(path.substring(1)).bufferedReader().use {
            it.readText()
        }

        return Response.Builder()
            .code(200)
            .addHeader("Content-Type", "application/json")
            .body(ResponseBody.create(MediaType.parse("application/json"), json))
            .message(json)
            .request(chain.request())
            .protocol(Protocol.HTTP_2)
            .build()
    }
}