package com.dokiwa.dokidoki.center.api

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.INFO
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer

import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by Septenary on 2018/11/4.
 */
object CURLInterceptor : Interceptor {

    private val LOGGER = HttpLoggingInterceptor.Logger { message -> Platform.get().log(INFO, message, null) }

    private val UTF8 = Charset.forName("UTF-8")

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

        LOGGER.log("╭--- cURL (" + request.url() + ")")
        LOGGER.log(sb.toString())
        LOGGER.log("╰--- (copy and paste the above line to a terminal)")

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
