package com.dokiwa.dokidoki.feed.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.feed.R
import kotlin.random.Random

class FeedPictureListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val fixLayoutRes = listOf(
        R.layout.view_feed_picture_list1,
        R.layout.view_feed_picture_list2,
        R.layout.view_feed_picture_list3,
        R.layout.view_feed_picture_list4
    )

    init {
        if (isInEditMode) {
            View.inflate(context, fixLayoutRes[Random.nextInt(4)], this)
        }
    }

    fun setPictures(rawList: List<UserProfile.Picture>?) {
        if (rawList.isNullOrEmpty()) {
            removeAllViews()
            visibility = View.GONE
            return
        }

        val list = if (rawList.size > 4) rawList.toMutableList().subList(0, 4) else rawList

        if (childCount <= 0) {
            View.inflate(context, fixLayoutRes[list.size - 1], this)
        }

        children.forEachIndexed { index, child ->
            if (child is ImageView) {
                val picture = list.getOrNull(index)
                if (picture != null) {
                    child.glideUrl(picture.adaptUrl(), 5f, R.drawable.ui_placeholder_radius_5dp)
                }
            }
        }
    }
}
