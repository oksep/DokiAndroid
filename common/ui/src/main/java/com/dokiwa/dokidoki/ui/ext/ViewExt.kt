package com.dokiwa.dokidoki.ui.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

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

fun View.hideSoftInputWhenClick() {
    this.setOnClickListener {
        it.hideKeyboard()
    }
}

fun View.onceLayoutThen(onLayout: (View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            onLayout(this@onceLayoutThen)
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun View.onLayoutThen(onLayout: (View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener { onLayout(this@onLayoutThen) }
}

fun View.zoomTouchArea(area: View) {
    val rect = Rect()
    this.getHitRect(rect)
    rect.inset(-rect.left, -rect.left)
    area.touchDelegate = TouchDelegate(rect, this)
}

fun SwipeRefreshLayout.setRefreshListenerHaptic(onRefresh: () -> Unit) {
    this.setOnRefreshListener {
        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onRefresh.invoke()
    }
}

fun View.hideKeyboard() {
    val view = this
    val inputMethodManager = view.context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() {
    val view = this
    view.requestFocus()
    (view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
        view,
        InputMethodManager.SHOW_IMPLICIT
    )
}