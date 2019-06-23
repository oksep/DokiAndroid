package com.dokiwa.dokidoki.timeline.home

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.util.toLastActiveTime
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import kotlinx.android.synthetic.main.view_item_timeline.view.*

internal class TimelineAdapter : BaseQuickAdapter<Timeline, BaseViewHolder>(R.layout.view_item_timeline, null) {

    override fun convert(helper: BaseViewHolder, item: Timeline) {

        val user = item.user

        // 头像
        helper.itemView.avatar.loadImgFromNetWork(user.avatar.adaptUrl())

        // 昵称
        helper.itemView.name.text = user.nickname

        // 发表时间
        helper.itemView.time.text = item.createTime.toLastActiveTime()

        // 正文
        helper.itemView.content.setText(item.content)

        // pictures
        helper.itemView.pictures.visibility = View.GONE
    }
}