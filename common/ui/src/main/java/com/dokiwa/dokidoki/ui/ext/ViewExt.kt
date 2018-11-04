package com.dokiwa.dokidoki.ui.ext

import android.view.View
import android.view.ViewGroup

/**
 * Created by Septenary on 2018/11/4.
 */

fun View.sinkBelowStatusBar() {
    var statusBarHeight = 0
    val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (statusBarHeightId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
    }
    if (statusBarHeightId > 0) {
        (layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
            layoutParams.topMargin = layoutParams.topMargin + statusBarHeight
            requestLayout()
        }
    }
}

fun View.rootLeft(): Int {
    return when {
        this.parent === this.rootView -> this.left
        else -> this.left + (this.parent as View).rootLeft()
    }
}

fun View.rootTop(): Int {
    return when {
        this.parent === this.rootView -> this.top
        else -> this.top + (this.parent as View).rootTop()
    }
}