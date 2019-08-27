package com.dokiwa.dokidoki.message.chatroom

import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMAudioController
import com.dokiwa.dokidoki.message.im.IMSessionMessage
import com.dokiwa.dokidoki.message.util.TimeUtil
import com.dokiwa.dokidoki.message.widget.AttachmentAudioView
import com.dokiwa.dokidoki.message.widget.AttachmentImageView
import com.dokiwa.dokidoki.message.widget.emoction.MoonUtil
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage

/**
 * Created by Septenary on 2019-07-13.
 */

private const val TYPE_MESSAGE_UNKNOWN = -1

private const val TYPE_MESSAGE_LEFT_TXT = 0
private const val TYPE_MESSAGE_LEFT_IMG = 1
private const val TYPE_MESSAGE_LEFT_AUDIO = 2
private const val TYPE_MESSAGE_LEFT_VIDEO = 3

private const val TYPE_MESSAGE_RIGHT_TXT = 4
private const val TYPE_MESSAGE_RIGHT_IMG = 5
private const val TYPE_MESSAGE_RIGHT_AUDIO = 6
private const val TYPE_MESSAGE_RIGHT_VIDEO = 7

internal class ChatRoomAdapter(
    private val contactAccount: String,
    private val onResendClick: (IMSessionMessage) -> Unit
) : BaseMultiItemQuickAdapter<AbsMessageEntity, BaseViewHolder>(null) {

    init {
        addItemType(TYPE_MESSAGE_UNKNOWN, R.layout.view_item_chat_room_message_unknown)

        addItemType(TYPE_MESSAGE_LEFT_TXT, R.layout.view_item_chat_room_message_left_txt)
        addItemType(TYPE_MESSAGE_LEFT_IMG, R.layout.view_item_chat_room_message_left_img)
        addItemType(TYPE_MESSAGE_LEFT_AUDIO, R.layout.view_item_chat_room_message_left_audio)
        addItemType(TYPE_MESSAGE_LEFT_VIDEO, R.layout.view_item_chat_room_message_left_video)

        addItemType(TYPE_MESSAGE_RIGHT_TXT, R.layout.view_item_chat_room_message_right_txt)
        addItemType(TYPE_MESSAGE_RIGHT_IMG, R.layout.view_item_chat_room_message_right_img)
        addItemType(TYPE_MESSAGE_RIGHT_AUDIO, R.layout.view_item_chat_room_message_right_audio)
        addItemType(TYPE_MESSAGE_RIGHT_VIDEO, R.layout.view_item_chat_room_message_right_video)
    }

    override fun convert(helper: BaseViewHolder, item: AbsMessageEntity) {
        when (item) {
            is LeftMessageTxtEntity, is RightMessageTxtEntity -> {
                setUpMessageTxtEntity(helper, item.sessionMsg)
            }
            is LeftImgMessageEntity, is RightImgMessageEntity -> {
                setUpMessageImgEntity(helper, item.sessionMsg)
            }
            is LeftAudioMessageEntity, is RightAudioMessageEntity -> {
                setUpMessageAudioEntity(helper, item.sessionMsg)
            }
        }
        setUpStatus(helper, item.sessionMsg)
        setUpAvatar(helper, item.sessionMsg)
        setUpTime(helper, item.sessionMsg)
    }

    private val timedItems = HashSet<String>() // 需要显示消息时间的消息ID
    private var lastShowTimeItem: IMMessage? = null // 用于消息时间显示,判断和上条消息间的时间间隔
    var displayMsgTimeWithInterval = (5 * 60 * 1000).toLong()

    /**
     * 列表加入新消息时，更新时间显示
     */
    fun updateShowTimeItem(items: List<IMMessage>, fromStart: Boolean, update: Boolean) {
        var anchor: IMMessage? = if (fromStart) null else lastShowTimeItem
        for (message in items) {
            if (setShowTimeFlag(message, anchor)) {
                anchor = message
            }
        }

        if (update) {
            lastShowTimeItem = anchor
        }
    }

    private fun hideTimeAlways(message: IMMessage): Boolean {
        if (message.sessionType == SessionTypeEnum.ChatRoom) {
            return true
        }
        return when (message.msgType) {
            MsgTypeEnum.notification -> true
            else -> false
        }
    }

    /**
     * 是否显示时间item
     */
    private fun setShowTimeFlag(message: IMMessage, anchor: IMMessage?): Boolean {
        var update = false

        if (hideTimeAlways(message)) {
            setShowTime(message, false)
        } else {
            if (anchor == null) {
                setShowTime(message, true)
                update = true
            } else {
                val time = anchor.time
                val now = message.time

                if (now - time == 0L) {
                    // 消息撤回时使用
                    setShowTime(message, true)
                    lastShowTimeItem = message
                    update = true
                } else if (now - time < displayMsgTimeWithInterval) {
                    setShowTime(message, false)
                } else {
                    setShowTime(message, true)
                    update = true
                }
            }
        }

        return update
    }

    private fun setShowTime(message: IMMessage, show: Boolean) {
        if (show) {
            timedItems.add(message.uuid)
        } else {
            timedItems.remove(message.uuid)
        }
    }

    fun needShowTime(message: IMMessage): Boolean {
        return timedItems.contains(message.uuid)
    }

    private fun setUpTime(helper: BaseViewHolder, sessionMsg: IMSessionMessage) {
        val timeTextView = helper.getView<TextView>(R.id.time)
        if (needShowTime(sessionMsg.rawMsg)) {
            timeTextView.visibility = View.VISIBLE
            val text = TimeUtil.getTimeShowString(sessionMsg.rawMsg.time, false)
            timeTextView.text = text
        } else {
            timeTextView.visibility = View.GONE
        }
    }

    private fun relocateShowTimeItemAfterDelete(messageItem: IMMessage, index: Int) {
        // 如果被删的项显示了时间，需要继承
        if (needShowTime(messageItem)) {
            setShowTime(messageItem, false)
            if (data.size > 0) {
                val nextItem: IMMessage = (if (index == data.size) {
                    //删除的是最后一项
                    getItem(index - 1)?.sessionMsg?.rawMsg
                } else {
                    //删除的不是最后一项
                    getItem(index)?.sessionMsg?.rawMsg
                }) ?: return

                // 增加其他不需要显示时间的消息类型判断
                if (hideTimeAlways(nextItem)) {
                    setShowTime(nextItem, false)
                    if (lastShowTimeItem != null && lastShowTimeItem != null
                        && lastShowTimeItem?.isTheSame(messageItem) == true
                    ) {
                        lastShowTimeItem = null
                        for (i in data.size - 1 downTo 0) {
                            val item = getItem(i)
                            if (item != null && needShowTime(item.sessionMsg.rawMsg)) {
                                lastShowTimeItem = item.sessionMsg.rawMsg
                                break
                            }
                        }
                    }
                } else {
                    setShowTime(nextItem, true)
                    if (lastShowTimeItem == null || lastShowTimeItem != null && lastShowTimeItem?.isTheSame(messageItem) == true) {
                        lastShowTimeItem = nextItem
                    }
                }
            } else {
                lastShowTimeItem = null
            }
        }
    }

    private fun setUpStatus(helper: BaseViewHolder, item: IMSessionMessage) {
        val status = item.rawMsg.status
        val statusView = helper.getView<ImageView>(R.id.status)
        statusView.setOnClickListener(null)
        when (status) {
            MsgStatusEnum.sending -> {
                statusView.visibility = View.VISIBLE
                statusView.setImageResource(R.drawable.msg_ic_status_sending)
            }
            MsgStatusEnum.fail -> {
                statusView.visibility = View.VISIBLE
                statusView.setImageResource(R.drawable.msg_ic_status_failed)
                if (item.rawMsg.fromAccount != contactAccount) {
                    statusView.setOnClickListener {
                        onResendClick(item)
                    }
                } else {
                    // TODO: 2019-07-14 @Septenary contract msg status
                }
            }
            else -> {
                statusView.visibility = View.GONE
            }
        }
    }

    private fun setUpAvatar(helper: BaseViewHolder, item: IMSessionMessage) {
        val userInfo = item.contactUserInfo

        val avatar = userInfo?.avatar
        val avatarView = helper.getView<ImageView>(R.id.avatar)
        if (avatar.isNullOrEmpty()) {
            avatarView.setImageResource(R.drawable.ui_ic_avatar_default_female)
        } else {
            avatarView.glideAvatar(avatar, userInfo.genderEnum.value)
        }
    }

    private fun setUpMessageTxtEntity(helper: BaseViewHolder, item: IMSessionMessage) {
        val span = MoonUtil.replaceEmoticons(
            helper.itemView.context,
            item.rawMsg.content,
            0.6f,
            ImageSpan.ALIGN_BOTTOM
        )
        helper.setText(R.id.content, span)
    }

    private fun setUpMessageImgEntity(helper: BaseViewHolder, item: IMSessionMessage) {
        (item.rawMsg.attachment as? ImageAttachment)?.let {
            helper.getView<AttachmentImageView>(R.id.content).setAttachment(it)
        }
    }

    private fun setUpMessageAudioEntity(helper: BaseViewHolder, item: IMSessionMessage) {
        (item.rawMsg.attachment as? AudioAttachment)?.let {
            val attachmentView = helper.getView<AttachmentAudioView>(R.id.content)
            attachmentView.setAttachment(it, View.OnClickListener { v ->
                IMAudioController.playAudio(v.context, item)
                notifyItemChanged(helper.adapterPosition)
            })
            if (item.audioState == IMAudioController.AudioState.Playing) {
                attachmentView.play()
            } else {
                attachmentView.stop()
            }
            helper.getView<View>(R.id.redDotView)?.visibility = if (item.rawMsg.status != MsgStatusEnum.read) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    fun setRawData(list: List<IMSessionMessage>) {
        updateShowTimeItem(list.map { it.rawMsg }, fromStart = true, update = true)
        setNewData(list.map { it.toEntity() })
    }

    fun addNewRawData(msg: IMSessionMessage) {
        addData(msg.toEntity())
    }

    fun addOldRawData(list: List<IMSessionMessage>) {
        addData(0, list.map { it.toEntity() })
    }

    fun addNewRawData(list: List<IMSessionMessage>) {
        addData(list.map { it.toEntity() })
    }

    private fun IMSessionMessage.toEntity(): AbsMessageEntity {
        return if (rawMsg.fromAccount == contactAccount)
            when (rawMsg.msgType) {
                MsgTypeEnum.text -> LeftMessageTxtEntity(this)
                MsgTypeEnum.image -> LeftImgMessageEntity(this)
                MsgTypeEnum.audio -> LeftAudioMessageEntity(this)
                MsgTypeEnum.video -> LeftVideoMessageEntity(this)
                else -> UnknownEntity(this)
            }
        else
            when (rawMsg.msgType) {
                MsgTypeEnum.text -> RightMessageTxtEntity(this)
                MsgTypeEnum.image -> RightImgMessageEntity(this)
                MsgTypeEnum.audio -> RightAudioMessageEntity(this)
                MsgTypeEnum.video -> RightVideoMessageEntity(this)
                else -> UnknownEntity(this)
            }
    }

    fun updateRawData(msg: IMSessionMessage) {
        this.data.indexOfFirst {
            it.sessionMsg.rawMsg.uuid == msg.rawMsg.uuid
        }.let {
            getItem(it)?.sessionMsg?.rawMsg?.status = msg.rawMsg.status
            notifyItemChanged(it + headerLayoutCount)
        }
    }
}

internal abstract class AbsMessageEntity(val sessionMsg: IMSessionMessage) : MultiItemEntity

private class UnknownEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_UNKNOWN
}

// 文本消息
private class LeftMessageTxtEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_LEFT_TXT
}

// 图片消息
private class LeftImgMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_LEFT_IMG
}

// 音频消息
private class LeftAudioMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_LEFT_AUDIO
}

// 视频消息
private class LeftVideoMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_LEFT_VIDEO
}

// 文本消息
private class RightMessageTxtEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_RIGHT_TXT
}

// 图片消息
private class RightImgMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_RIGHT_IMG
}

// 音频消息
private class RightAudioMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_RIGHT_AUDIO
}

// 视频消息
private class RightVideoMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_RIGHT_VIDEO
}
