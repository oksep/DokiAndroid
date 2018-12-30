package com.dokiwa.dokidoki.ui.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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

fun View.fadeOutGone() {
    if (this.isShown) {
        this.visibility = View.VISIBLE
        this.alpha = 1f
        this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                this@fadeOutGone.visibility = View.GONE
            }
        }).start()
    }
}

fun View.fadeInVisible() {
    if (!this.isShown) {
        this.visibility = View.VISIBLE
        this.alpha = 0f
        this.animate().alpha(1f).setListener(null).start()
    }
}