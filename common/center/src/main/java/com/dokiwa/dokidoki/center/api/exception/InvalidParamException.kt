package com.dokiwa.dokidoki.center.api.exception

import java.lang.Exception

/**
 * Created by Septenary on 2018/11/26.
 * 400000
 * 因为参数错误请求失败，客户端需要将 err_msg 中的信息展示给用户。
 */
class InvalidParamException : Exception() {
}