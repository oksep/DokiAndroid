package com.dokiwa.dokidoki.message.im

/**
 * Created by Septenary on 2019-07-10.
 */
enum class IMLoginStatus {
    KICK_OUT, // 被踢下线
    NET_BROKEN, // 网络失败
    UNLOGIN, // 未登录
    CONNECTING, // 网络连接中
    LOGINING, // 正在登录
    LOGINED, // 已登录
    UNKNOW
}