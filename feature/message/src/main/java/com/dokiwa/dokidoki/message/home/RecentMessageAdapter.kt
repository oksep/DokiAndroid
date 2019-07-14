package com.dokiwa.dokidoki.message.home

import android.view.View
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.im.IMRecentMessage
import kotlinx.android.synthetic.main.view_item_recent_message.view.*

/**
 * Created by Septenary on 2019-07-13.
 */

internal class RecentMessageAdapter :
    BaseItemDraggableAdapter<IMRecentMessage, BaseViewHolder>(R.layout.view_item_recent_message, null) {

    override fun convert(helper: BaseViewHolder, item: IMRecentMessage) {
        val userInfo = item.contactUserInfo

        val avatar = userInfo?.avatar
        if (avatar.isNullOrEmpty()) {
            helper.itemView.avatar.setImageResource(R.drawable.ui_ic_avatar_default_female)
        } else {
            helper.itemView.avatar.glideAvatar(avatar, userInfo.genderEnum.value)
        }

        helper.itemView.name.text = userInfo?.name ?: item.contactId

        helper.itemView.time.text = item.time.toReadable()

        helper.itemView.content.text = item.content
        if (item.unreadCount > 0) {
            helper.itemView.unreadCount.text = item.unreadCount.toString()
            helper.itemView.unreadCount.visibility = View.VISIBLE
        } else {
            helper.itemView.unreadCount.visibility = View.GONE
        }
    }
}