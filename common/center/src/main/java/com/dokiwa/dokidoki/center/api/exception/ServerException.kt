package com.dokiwa.dokidoki.center.api.exception

/**
 * Created by Septenary on 2018/11/26.
 * 50xxxx
 * 服务器异常导致的失败。客户端需要提示用户稍后重试，此时不应该展示 err_msg 中的错误信息。
 */
class ServerException : Exception() {
}