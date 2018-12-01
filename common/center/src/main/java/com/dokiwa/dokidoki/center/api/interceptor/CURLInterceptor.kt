package com.dokiwa.dokidoki.center.api.interceptor

import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.Api
import okhttp3.Interceptor

import okhttp3.Response
import okio.Buffer
import java.io.EOFException

import java.io.IOException
import java.nio.charset.Charset

private val UTF8 = Charset.forName("UTF-8")

class CURLInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val sb = StringBuilder().append("curl").append(" -X ").append(request.method())

        val headers = request.headers()
        if (headers != null) {
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                sb.append(" -H \"").append(name).append(":").append(headers.value(i)).append("\"")

                if ("Content-Encoding".equals(name, ignoreCase = true)) {
                    sb.append(" --compressed ")
                }
                i++
            }
        }

        val requestBody = request.body()
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            var charset: Charset? = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
                sb.append(" -H \"").append("Content-Type:").append(contentType.toString()).append("\"")
            }

            if (isPlaintext(buffer)) {
                sb.append(" --data-binary \"").append("").append(buffer.readString(charset!!)).append("\"")
            }
        }

        sb.append(" \"").append(request.url()).append("\"")

        Log.d(Api.TAG, "╭--- cURL content")
        Log.d(Api.TAG, sb.toString())
        Log.d(Api.TAG, "╰--- cURL (copy and paste the above line to a terminal)")

        return chain.proceed(request)
    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }
}
