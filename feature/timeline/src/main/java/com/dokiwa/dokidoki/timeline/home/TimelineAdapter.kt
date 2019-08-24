package com.dokiwa.dokidoki.timeline.home

import android.view.View
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.Timeline
import com.dokiwa.dokidoki.timeline.api.TimelineApi
import kotlinx.android.synthetic.main.view_item_timeline.view.*

open class TimelineAdapter(
    private val cdc: CompositeDisposableContext,
    private val onMoreBtnClick: ((TimelineEntity) -> Unit)? = null
) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(listOf()) {

    init {
        // nine grid style
        (0..9).forEach {
            addItemType(it, R.layout.view_item_timeline)
        }
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {

        if (item !is TimelineEntity) return

        val timeline = item.timeline
        val user = timeline.user

        // 头像
        helper.itemView.avatar.glideAvatar(user.avatar)
        helper.itemView.avatar.setOnClickListener {
            IProfilePlugin.get().launchProfileActivity(helper.itemView.context, user.uuid)
        }

        // 昵称
        helper.itemView.name.text = user.nickname
        helper.itemView.name.setOnClickListener {
            IProfilePlugin.get().launchProfileActivity(helper.itemView.context, user.uuid)
        }

        // 发表时间
        helper.itemView.time.text = timeline.createTime.toReadable()

        // 正文
        helper.itemView.content.setText(timeline.content)

        // pictures
        helper.itemView.picturesContainer.setPictures(timeline.pictureList) { l, index ->
            GalleryActivity.launchGallery(helper.itemView.context, index, l.map { it.adaptUrl() })
        }

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
            onUpClick(it, item, helper.adapterPosition)
        }

        // comments
        helper.itemView.commentCount.text = timeline.commentCount.toString()

        // more
        helper.itemView.moreBtn.setOnClickListener {
            onMoreBtnClick?.invoke(item)
        }

        // follow
        helper.itemView.followBtn.relationsStatus = timeline.relationStatus
        helper.itemView.followBtn.onRelationStatusChanged = {
            timeline.relationStatus = it
        }
    }

    private fun onUpClick(view: View, entity: TimelineEntity, position: Int) {
        val isUp = entity.timeline.isUp == true
        if (isUp) {
            entity.timeline.upCount -= 1
            Api.get(TimelineApi::class.java).downTimeline(entity.timeline.id).subscribeApi(cdc)
        } else {
            entity.timeline.upCount += 1
            view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.timeline_heart))
            Api.get(TimelineApi::class.java).upTimeline(entity.timeline.id).subscribeApi(cdc)
        }
        entity.timeline.isUp = !isUp

        notifyItemChanged(position, TimelineEntity(entity.timeline))
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

    open class TimelineEntity(
        val timeline: Timeline
    ) : MultiItemEntity {
        override fun getItemType(): Int {
            val rawSize = timeline.pictureList?.size ?: 0
            return if (rawSize > 9) 9 else rawSize
        }
    }
}