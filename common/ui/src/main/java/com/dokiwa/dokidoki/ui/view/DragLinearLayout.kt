package com.dokiwa.dokidoki.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import com.dokiwa.dokidoki.ui.util.DragSortHelper

/**
 * Created by Septenary on 2019-06-18.
 */
class DragLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val dragSortHelper by lazy { DragSortHelper.create(this) }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return dragSortHelper.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return dragSortHelper.onTouchEvent(event)
    }

    override fun computeScroll() {
        dragSortHelper.computeScroll()
    }

    fun setSwapListener(swapListener: DragSortHelper.OnViewSwapListener) {
        dragSortHelper.swapListener = swapListener
    }
}
