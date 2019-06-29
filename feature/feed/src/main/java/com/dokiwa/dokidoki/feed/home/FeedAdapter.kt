package com.dokiwa.dokidoki.feed.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.dokiwa.dokidoki.center.ext.glideAvatar
import com.dokiwa.dokidoki.center.util.toReadable
import com.dokiwa.dokidoki.feed.R
import com.dokiwa.dokidoki.feed.api.Feed
import com.dokiwa.dokidoki.feed.widget.FeedPictureListView
import com.dokiwa.dokidoki.gallery.GalleryActivity
import com.dokiwa.dokidoki.ui.view.TagsView

internal class FeedAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(listOf()) {

    init {
        (0..4).forEachIndexed { index, _ ->
            addItemType(index, R.layout.view_item_feed)
        }
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        if (item !is FeedEntity) return

        val feed = item.feed

        val profile = feed.userProfile

        // 官方认证
        helper.getView<ImageView>(R.id.officialVerify).visibility =
            if (profile.verify != null) View.VISIBLE else View.GONE

        // 实名认证
        helper.getView<ImageView>(R.id.certifyRealName).visibility =
            if (profile.certification.identification != null) View.VISIBLE else View.GONE

        // 学历认证
        helper.getView<ImageView>(R.id.certifyAcademic).visibility =
            if (profile.certification.education != null) View.VISIBLE else View.GONE

        // 昵称
        helper.getView<TextView>(R.id.name).text = profile.nickname

        // 年龄 | 身高 | 教育程度
        helper.getView<TextView>(R.id.ageHeightEdu).text = profile.assembleAgeHeightEdu(helper.itemView.context)

        // 地点 | 职位
        helper.getView<TextView>(R.id.addressPosition).text = profile.assembleCityIndustry()

        // 头像
        helper.getView<ImageView>(R.id.avatar).glideAvatar(profile)

        // 活跃时间
        helper.getView<TextView>(R.id.activeState).text = profile.lastActive.toReadable()

        // 个性标签
        val tagsView = helper.getView<TagsView>(R.id.tags)
        val emptyTagsTip = helper.getView<View>(R.id.emptyTagsTip)
        if (profile.tags.isNullOrEmpty()) {
            tagsView.visibility = View.GONE
            emptyTagsTip.visibility = View.VISIBLE
        } else {
            tagsView.setTags(profile.tags!!.map { it.name })
            tagsView.visibility = View.VISIBLE
            emptyTagsTip.visibility = View.GONE
        }

        // 简介
        val introGroup = helper.getView<Group>(R.id.introGroup)
        if (profile.intro.isNullOrEmpty()) {
            introGroup.visibility = View.GONE
        } else {
            introGroup.visibility = View.VISIBLE
            helper.getView<TextView>(R.id.intro).text = profile.intro
        }

        // pictures
        helper.getView<FeedPictureListView>(R.id.pictureListView).setPictures(profile.pictures) { l, index ->
            GalleryActivity.launchGallery(helper.itemView.context, index, l.map { it.adaptUrl() })
        }
    }

    fun setNewRawData(data: List<Feed>?) {
        data?.toMutableList()?.map {
            FeedEntity(it)
        }.also {
            super.setNewData(it)
        }
    }

    fun addRawData(data: List<Feed>?) {
        data?.map {
            FeedEntity(it)
        }?.also {
            super.addData(it)
        }
    }

    class FeedEntity(
        val feed: Feed
    ) : MultiItemEntity {
        override fun getItemType(): Int {
            val rawSize = feed.userProfile.pictures?.size ?: 0
            return if (rawSize > 4) 4 else rawSize
        }
    }
}