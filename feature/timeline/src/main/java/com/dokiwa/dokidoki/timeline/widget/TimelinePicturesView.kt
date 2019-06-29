package com.dokiwa.dokidoki.timeline.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.dokiwa.dokidoki.center.ext.loadImgFromNetWork
import com.dokiwa.dokidoki.timeline.R
import com.dokiwa.dokidoki.timeline.api.TimelinePicture
import com.dokiwa.dokidoki.ui.ext.getScreenWidth

/**
 * Created by Septenary on 2019-06-23.
 */
class TimelinePicturesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val fixLayoutRes = listOf(
        R.layout.view_timeline_picture_list1,
        R.layout.view_timeline_picture_list2,
        R.layout.view_timeline_picture_list3,
        R.layout.view_timeline_picture_list4,
        R.layout.view_timeline_picture_list5,
        R.layout.view_timeline_picture_list6,
        R.layout.view_timeline_picture_list7,
        R.layout.view_timeline_picture_list8,
        R.layout.view_timeline_picture_list9
    )

    fun setPictures(rawList: List<TimelinePicture>?, onPictureClick: (List<TimelinePicture>, Int) -> Unit) {
        if (rawList.isNullOrEmpty()) {
            removeAllViews()
            visibility = View.GONE
            return
        }

        val list = if (rawList.size > 9) rawList.subList(0, 9) else rawList

        if (childCount <= 0) {
            View.inflate(context, fixLayoutRes[list.size - 1], this)
        }

        children.forEachIndexed { index, child ->
            if (child is ImageView) {
                val picture = list.getOrNull(index)
                if (picture != null) {
                    ensureLayoutParams(list.size == 1, child, picture)
                    child.loadImgFromNetWork(picture.adaptUrl())
                    child.setOnClickListener {
                        onPictureClick.invoke(list, index)
                    }
                }
            }
        }
    }

    private fun ensureLayoutParams(ensure: Boolean, child: View, picture: TimelinePicture) {
        if (ensure) {
            val w = picture.width
            val h = picture.height
            val maxSize = (getScreenWidth() * 0.6f).toInt()
            child.layoutParams = child.layoutParams.apply {
                when {
                    (w <= 0 || h <= 0 || w == h) -> {
                        width = maxSize
                        height = maxSize
                    }
                    w > h -> {
                        val ratio = 1f * picture.height / picture.width
                        width = maxSize
                        height = (maxSize * ratio).toInt()
                    }
                    else -> {
                        val ratio = 1f * picture.width / picture.height
                        width = (maxSize * ratio).toInt()
                        height = maxSize
                    }
                }
            }
        }
    }
}