package com.dokiwa.dokidoki.message.im

import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Created by Septenary on 2019-07-11.
 */
data class IMSessionMessage(
    val rawMsg: IMMessage,
    var contactUserInfo: NimUserInfo? // 联系人信息
) {
    override fun toString(): String {
        return "${rawMsg.fromAccount}(${rawMsg.fromNick}): ${rawMsg.content} [${rawMsg.status}]"
    }
}

fun IMMessage.toSessionMessage(): IMSessionMessage {
    return IMSessionMessage(
        this,
        getCacheNimUser(this.fromAccount)
    )
}