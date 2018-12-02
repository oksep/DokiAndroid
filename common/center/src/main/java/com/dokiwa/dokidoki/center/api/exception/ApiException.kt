package com.dokiwa.dokidoki.center.api.exception

import com.dokiwa.dokidoki.center.api.model.ApiData
import com.google.gson.Gson
import retrofit2.HttpException

sealed class ApiException(m: String, t: Throwable, val action: String? = null) : RuntimeException(m, t)

// 400000 因为参数错误请求失败，客户端需要将 err_msg 中的信息展示给用户。
class InvalidParamException(m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 400001 signature 错误。
class SignatureException(val timeDif: Long, m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 400100 用户被封禁
class UserBannedException(m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 401000 授权失败，客户端需要重新登录
class AuthorizationException(m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 401xxx 其他授权失败情况
class OthersException(m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 401002 用户的第三方账号未绑定手机。客户端需引导用户到手机号登陆页面，并在手机号提交时同时提交用户第三方账号相关参数。参考 @使用短信 注册/登录
class UnbindMobileNumberException(m: String, t: Throwable, a: String?) : ApiException(m, t, a)

// 404 not found
class NotFountException(m: String, t: Throwable) : ApiException(m, t)

// 50xxxx 服务器异常导致的失败。客户端需要提示用户稍后重试，此时不应该展示 err_msg 中的错误信息。
class ServerException(m: String, t: Throwable) : ApiException(m, t)

// 未知错误
class UnknownException(m: String, t: Throwable) : ApiException(m, t)

fun HttpException.toApiException(): ApiException {
    val code = this.code()
    when {
        code == 400 -> {
            this.response().errorBody()?.string()?.let {
                return try {
                    val data = getApiDataFromJson(it)
                    val actionTo = data.action?.to
                    when (data.status.code) {
                        400000 -> InvalidParamException("参数错误", this, actionTo)
                        400001 -> {
                            val localTs = this.response().raw().request().url().queryParameter("_ts")?.toLong()
                            checkNotNull(localTs) { "未添加本地时间戳参数" }
                            val serverTs = data.time
                            SignatureException(serverTs - localTs, "签算错误", this, actionTo)
                        }
                        400100 -> UserBannedException("用户被封禁", this, actionTo)
                        else -> {
                            OthersException(data.status.errorMessage ?: "其它授权错误", this, actionTo)
                        }
                    }
                } catch (e: Exception) {
                    OthersException("code 400, response parse error", this, null)
                }
            }
        }
        code == 401 -> {
            this.response().errorBody()?.string()?.let {
                return try {
                    val data = Gson().fromJson(it, ApiData::class.java)
                    val actionTo = data.action?.to
                    when (data.status.code) {
                        401000 -> AuthorizationException("授权失败，需重新登录", this, actionTo)
                        401002 -> UnbindMobileNumberException("第三方账号未绑定手机", this, actionTo)
                        else -> {
                            OthersException(data.status.errorMessage ?: "其它授权错误", this, actionTo)
                        }
                    }
                } catch (e: Exception) {
                    OthersException("code 401, response parse error", this, null)
                }
            }
        }
        code == 404 -> {
            return NotFountException("api 资源未找到", this)
        }
        code >= 500 -> return ServerException("服务端错误", this)
        else -> return OthersException("其它错误", this, null)
    }
    return UnknownException("未知授权错误", this)
}

@Throws(Exception::class)
internal fun getApiDataFromJson(json: String): ApiData<*> {
    return Gson().fromJson(json, ApiData::class.java)
}