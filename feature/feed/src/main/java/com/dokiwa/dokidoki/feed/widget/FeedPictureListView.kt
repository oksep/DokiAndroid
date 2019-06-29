package com.dokiwa.dokidoki.feed.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.center.ext.glideUrl
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.feed.R

class FeedPictureListView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_feed_picture_list, this)
    }

    fun setPictureList(list: List<UserProfile.Picture>?, onPictureClick: (List<UserProfile.Picture>, Int) -> Unit) {
        if (list.isNullOrEmpty()) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE

            children.forEach {
                (it as ImageView).setImageDrawable(null)
            }

            if (list.size <= 2) {
                (getChildAt(0).layoutParams as LayoutParams).apply { dimensionRatio = "W,1:2" }
                (getChildAt(1).layoutParams as LayoutParams).apply { dimensionRatio = "W,1:2" }
                getChildAt(2).visibility = View.GONE
                getChildAt(3).visibility = View.GONE
            } else {
                (getChildAt(0).layoutParams as LayoutParams).apply { dimensionRatio = "W,1:1" }
                (getChildAt(1).layoutParams as LayoutParams).apply { dimensionRatio = "W,1:1" }
                getChildAt(2).visibility = View.VISIBLE
                getChildAt(3).visibility = View.VISIBLE
            }

            children.forEachIndexed { index, view ->
                list.getOrNull(index)?.let {
                    (view as ImageView).glideUrl(it.adaptUrl(), 5f, R.drawable.ui_placeholder_radius_5dp)
                    view.setOnClickListener {
                        onPictureClick.invoke(list, index)
                    }
                }
            }
        }
    }
}
