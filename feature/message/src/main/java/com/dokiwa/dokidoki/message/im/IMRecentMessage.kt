package com.dokiwa.dokidoki.message.im

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo

/**
 * Created by Septenary on 2019-07-11.
 * [ref](https://dev.yunxin.163.com/docs/product/IM%E5%8D%B3%E6%97%B6%E9%80%9A%E8%AE%AF/SDK%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/Android%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/%E6%9C%80%E8%BF%91%E4%BC%9A%E8%AF%9D)
 */
data class IMRecentMessage(
    val attachment: MsgAttachment?, // 如果最近一条消息是扩展消息类型，获取消息的附件内容
    val contactId: String, // 获取最近联系人的 ID（好友帐号，群 ID 等）
    val content: String, // 获取最近一条消息的缩略内容
    val extension: Map<String, Any>?, // 获取扩展字段
    val fromAccount: String, // 获取与该联系人的最后一条消息的发送方的帐号
    val fromNick: String, // 获取与该联系人的最后一条消息的发送方的昵称
    val msgStatus: MsgStatusEnum, // 获取最近一条消息状态
    val msgType: MsgTypeEnum, // 获取最近一条消息的消息类型
    val recentMessageId: String, // 最近一条消息的消息 ID， 即 IMMessage#getUuid()
    val sessionType: SessionTypeEnum, // 获取会话类型
    val tag: Long?, // 获取标签
    val time: Long, // 获取最近一条消息的时间，单位为 s
    val unreadCount: Int, // 获取该联系人的未读消息条数
    var contactUserInfo: NimUserInfo? // 联系人信息
)

fun RecentContact.toRecentMessage(): IMRecentMessage {
    return IMRecentMessage(
        attachment,
        contactId,
        content,
        extension,
        fromAccount,
        fromNick,
        msgStatus,
        msgType,
        recentMessageId,
        sessionType,
        tag,
        time / 1000,
        unreadCount,
        getCacheNimUser(contactId)
    )
}