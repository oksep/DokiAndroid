package com.dokiwa.dokidoki.home.pages.feed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.util.toLastActiveTime
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.home.api.model.Feed
import com.dokiwa.dokidoki.home.widget.FeedPictureListView
import com.dokiwa.dokidoki.ui.view.RoundImageView
import com.dokiwa.dokidoki.ui.view.TagsView

internal class FeedAdapter : BaseQuickAdapter<Feed, BaseViewHolder>(R.layout.view_item_feed, null) {

    override fun convert(helper: BaseViewHolder, item: Feed) {
        val profile = item.userProfile

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
        helper.getView<TextView>(R.id.ageHeightEdu).text = profile.assembleAgeHeightEdu()

        // 地点 | 职位
        helper.getView<TextView>(R.id.addressPosition).text = profile.assembleAddressPosition()

        // 头像
        helper.getView<RoundImageView>(R.id.avatar).loadAvatar(profile)

        // 活跃时间
        helper.getView<TextView>(R.id.activeState).text = profile.lastActive.toLastActiveTime()

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
        helper.getView<FeedPictureListView>(R.id.pictureListView).setPictureList(profile.pictures)
    }
}