package com.dokiwa.dokidoki.center.api

import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.ext.sha1
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder


/**
 * Created by Septenary on 2018/11/4.
 */
private const val TAG = "SigningInterceptor"

class SigningInterceptor(
    private val apiKey: String,
    private val apiSecret: String,
    private val onTimeDif: (timeDif: Long) -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newUrl = chain.request().url().signNewUrl(apiSecret)

        val localTime = newUrl.queryParameter("_ts")?.toLongOrNull()

        checkNotNull(localTime) { "API $TAG => url query not provide param (_ts)" }

        val request = chain.request().newBuilder().url(newUrl).build()
        val response = chain.proceed(request)
        if (response.code() == 400) {
            val timeDif = correctLocalTimeIf400001(localTime, response)
            if (timeDif != 0L) {
                onTimeDif.invoke(timeDif)
                Log.w("API", "$TAG retry by correctLocalTimeIf400001 timeDif: $timeDif")
                return retry(chain, timeDif)
            }
        }
        return response
    }

    private fun retry(chain: Interceptor.Chain, timeDif: Long): Response {
        val newUrl = chain.request()
            .url()
            .newBuilder()
            .fillInSeedParams(apiKey, timeDif)
            .build()
            .signNewUrl(apiSecret)
        val request = chain.request().newBuilder().url(newUrl).build()
        return chain.proceed(request)
    }

    private fun correctLocalTimeIf400001(localTime: Long, response: Response): Long {
        response.body()?.string()?.let { json ->
            val data = Gson().fromJson(json, ErrorResponse::class.java)
            if (data.status.code == 400001 && localTime.compareTo(data.time) != 0) {
                return data.time - localTime
            }
        }
        return 0
    }
}

fun HttpUrl.signNewUrl(secret: String): HttpUrl {
    val unSign = this.queryParameterNames().toList().sorted().joinToString("&") {
        val value = this.queryParameter(it) ?: ""
        val encodeValue = URLEncoder.encode(value, "UTF-8")
        "$it=$encodeValue"
    } + secret
    return this.newBuilder().setQueryParameter("_sign", unSign.sha1()).build()
}

data class ErrorResponse(val status: Status, val time: Int, val action: Action?) {
    data class Action(val to: String)
    data class Status(val code: Int, val err_msg: String?)
}

// status code
// 200000	成功
// 400000	因为参数错误请求失败，客户端需要将 err_msg 中的信息展示给用户。
// 400001	signature 错误。
// 400100	用户被封禁。
// 401000	授权失败，客户端需要重新登录。
// 401xxx	其他授权失败情况。
// 401002	用户的第三方账号未绑定手机。客户端需引导用户到手机号登陆页面，并在手机号提交时同时提交用户第三方账号相关参数。参考 @使用短信 注册/登录
// 50xxxx	服务器异常导致的失败。客户端需要提示用户稍后重试，此时不应该展示 err_msg 中的错误信息。