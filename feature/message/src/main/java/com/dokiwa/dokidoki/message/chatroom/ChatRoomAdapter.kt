package com.dokiwa.dokidoki.message.chatroom

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMSessionMessage
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum

/**
 * Created by Septenary on 2019-07-13.
 */

private const val TYPE_MESSAGE_LEFT = 0
private const val TYPE_MESSAGE_RIGHT = 1

internal class ChatRoomAdapter(
    private val contractId: String,
    private val onResendClick: (IMSessionMessage) -> Unit
) : BaseMultiItemQuickAdapter<AbsMessageEntity, BaseViewHolder>(null) {

    init {
        addItemType(TYPE_MESSAGE_LEFT, R.layout.view_item_chat_room_message_left)
        addItemType(TYPE_MESSAGE_RIGHT, R.layout.view_item_chat_room_message_right)
    }

    override fun convert(helper: BaseViewHolder, item: AbsMessageEntity) {
        when (item) {
            is LeftMessageEntity -> {
                setUpMessageEntity(helper, item.sessionMsg)
            }
            is RightMessageEntity -> {
                setUpMessageEntity(helper, item.sessionMsg)
            }
        }
    }

    private fun setUpMessageEntity(helper: BaseViewHolder, item: IMSessionMessage) {

        val userInfo = item.contactUserInfo

        val avatar = userInfo?.avatar
        val avatarView = helper.getView<ImageView>(R.id.avatar)
        if (avatar.isNullOrEmpty()) {
            avatarView.setImageResource(R.drawable.ui_ic_avatar_default_female)
        } else {
            avatarView.glideAvatar(avatar, userInfo.genderEnum.value)
        }

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
                if (item.rawMsg.fromAccount != contractId) {
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

        helper.setText(R.id.content, item.rawMsg.content)
    }

    fun setRawData(list: List<IMSessionMessage>) {
        setNewData(list.map { it.toEntity() })
    }

    fun addRawData(msg: IMSessionMessage) {
        addData(msg.toEntity())
    }

    fun addRawData(list: List<IMSessionMessage>) {
        addData(list.map { it.toEntity() })
    }

    private fun IMSessionMessage.toEntity(): AbsMessageEntity {
        return if (rawMsg.fromAccount == contractId)
            LeftMessageEntity(this)
        else
            RightMessageEntity(this)
    }

    fun updateRawData(msg: IMSessionMessage) {
        this.data.indexOfFirst {
            it.sessionMsg.rawMsg.uuid == msg.rawMsg.uuid
        }.let {
            if (it > -1) {
                notifyItemChanged(it)
            }
        }
    }
}

internal abstract class AbsMessageEntity(val sessionMsg: IMSessionMessage) : MultiItemEntity

private class LeftMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_LEFT
}

private class RightMessageEntity(msg: IMSessionMessage) : AbsMessageEntity(msg) {
    override fun getItemType() = TYPE_MESSAGE_RIGHT
}

