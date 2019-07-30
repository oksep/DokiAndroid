package com.dokiwa.dokidoki.message.chatroom

import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMAudioController
import com.dokiwa.dokidoki.message.im.IMSessionMessage
import com.dokiwa.dokidoki.message.widget.AttachmentAudioView
import com.dokiwa.dokidoki.message.widget.AttachmentImageView
import com.dokiwa.dokidoki.message.widget.emoction.MoonUtil
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum

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
