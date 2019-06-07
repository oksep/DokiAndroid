package com.dokiwa.dokidoki.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.home.R

class FeedPictureListView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_feed_picture_list, this)
    }

    fun setPictureList(list: List<UserProfile.Picture>?) {
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
                    (view as ImageView).loadImgFromNetWork(it.middleUrl)
                }
            }
        }
    }
}
