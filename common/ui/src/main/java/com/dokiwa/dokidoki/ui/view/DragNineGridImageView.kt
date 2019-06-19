package com.dokiwa.dokidoki.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.dokiwa.dokidoki.ui.util.DragSortHelper
import com.jaeger.ninegridimageview.NineGridImageView

/**
 * Created by Septenary on 2019-06-18.
 */
class DragNineGridImageView<T> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NineGridImageView<T>(context, attrs) {

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

    fun setSwapListner(swapListener: DragSortHelper.OnViewSwapListener) {
        dragSortHelper.swapListener = swapListener
    }
}
