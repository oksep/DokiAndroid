package com.dokiwa.dokidoki.message.im

import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Created by Septenary on 2019-07-11.
 */
data class IMSessionMessage(
    val rawMsg: IMMessage,
    var contactUserInfo: NimUserInfo?, // 联系人信息
    var audioState: IMAudioController.AudioState? = null
) {
    override fun toString(): String {
        return "\n${rawMsg.fromNick}(${rawMsg.fromAccount}): ${rawMsg.msgType} [${rawMsg.status}]"
    }
}

fun IMMessage.toSessionMessage(): IMSessionMessage {
    return IMSessionMessage(
        this,
        getCacheNimUser(this.fromAccount)
    )
}