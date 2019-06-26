package com.dokiwa.dokidoki.timeline.home

import android.view.View
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.util.toLastActiveTime
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import kotlinx.android.synthetic.main.view_item_timeline.view.*

internal class TimelineAdapter(
    private val onUpBtnClick: (View, TimelineEntity, Int) -> Unit,
    private val onMoreBtnClick: (TimelineEntity) -> Unit
) : BaseMultiItemQuickAdapter<TimelineAdapter.TimelineEntity, BaseViewHolder>(listOf()) {

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

        // position
        if (timeline.position?.name.isNullOrEmpty()) {
            helper.itemView.position.text = null
            helper.itemView.position.visibility = View.GONE
        } else {
            helper.itemView.position.text = timeline.position?.name
            helper.itemView.position.visibility = View.VISIBLE
        }

        // ups
        helper.itemView.upCount.text = timeline.upCount.toString()
        helper.itemView.upBtn.setImageResource(
            if (timeline.isUp == true) R.drawable.timeline_ic_like else R.drawable.timeline_ic_unlike
        )
        helper.itemView.upBtn.setOnClickListener {
            onUpBtnClick(it, item, helper.adapterPosition)
            convert(helper, item)
        }

        // more
        helper.itemView.moreBtn.setOnClickListener {
            onMoreBtnClick(item)
        }

        // TODO: 2019-06-27 @Septenary 关注字段?
        // follow
        helper.itemView.followBtn.visibility = View.GONE
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