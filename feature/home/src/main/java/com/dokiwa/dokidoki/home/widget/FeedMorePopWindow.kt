package com.dokiwa.dokidoki.home.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.dokiwa.dokidoki.home.R

class FeedMorePopWindow(
    context: Context,
    onFilterSearchClick: () -> Unit,
    onAccurateSearchClick: () -> Unit
) : PopupWindow(context) {
    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.view_feed_more_pop_window, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isOutsideTouchable = true
        isSplitTouchEnabled = false
        (contentView as View).findViewById<View>(R.id.accurateSearch).setOnClickListener {
            dismiss()
            onAccurateSearchClick.invoke()
        }
        (contentView as View).findViewById<View>(R.id.filterSearch).setOnClickListener {
            dismiss()
            onFilterSearchClick.invoke()
        }
    }
}