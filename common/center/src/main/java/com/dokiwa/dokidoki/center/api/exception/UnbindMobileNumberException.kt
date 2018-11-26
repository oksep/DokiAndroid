package com.dokiwa.dokidoki.center.api.exception

/**
 * Created by Septenary on 2018/11/26.
 * 401002
 * 用户的第三方账号未绑定手机。客户端需引导用户到手机号登陆页面，并在手机号提交时同时提交用户第三方账号相关参数。参考 @使用短信 注册/登录
 */
class UnbindMobileNumberException : Exception() {
}