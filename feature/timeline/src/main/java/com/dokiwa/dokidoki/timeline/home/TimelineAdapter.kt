package com.dokiwa.dokidoki.timeline.home

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.util.toLastActiveTime
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import kotlinx.android.synthetic.main.view_item_timeline.view.*

internal class TimelineAdapter : BaseMultiItemQuickAdapter<TimelineAdapter.TimelineEntity, BaseViewHolder>(listOf()) {

    init {
        (0..9).forEach {
            addItemType(it, R.layout.view_item_timeline)
        }
    }

    override fun convert(helper: BaseViewHolder, item: TimelineEntity) {

        val timeline = item.timeline
        val user = timeline.user

        // 头像
        helper.itemView.avatar.loadImgFromNetWork(user.avatar.adaptUrl())

        // 昵称
        helper.itemView.name.text = user.nickname

        // 发表时间
        helper.itemView.time.text = timeline.createTime.toLastActiveTime()

        // 正文
        helper.itemView.content.setText(timeline.content)

        // pictures
        helper.itemView.picturesContainer.setPictures(timeline.pictureList)
    }

    fun setNewRawData(data: List<Timeline>?) {
        super.setNewData(data?.map {
            TimelineEntity(it)
        })
    }

    fun addRawData(data: List<Timeline>) {
        super.addData(data.map {
            TimelineEntity(it)
        })
    }

    class TimelineEntity(
        val timeline: Timeline
    ) : MultiItemEntity {
        override fun getItemType(): Int {
            val rawSize = timeline.pictureList?.size ?: 0
            return if (rawSize > 9) 9 else rawSize
        }
    }
}