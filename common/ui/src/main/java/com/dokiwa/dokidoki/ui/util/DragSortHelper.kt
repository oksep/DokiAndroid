package com.dokiwa.dokidoki.ui.util

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.customview.widget.ViewDragHelper
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.onLayoutThen

/**
 * Created by Septenary on 2019-06-19.
 */
class DragSortHelper private constructor(private val parentView: ViewGroup) : ViewDragHelper.Callback() {

    companion object {
        fun create(forParent: ViewGroup): DragSortHelper {
            return DragSortHelper(forParent)
        }
    }

    var swapListener: OnViewSwapListener? = null

    private val positionKey = R.id.ui_drag_sort_helper_position_tag

    private val mViewDragHelper by lazy { ViewDragHelper.create(parentView, this) }

    private var dragViewRawLeft: Int = 0
    private var dragViewRawTop: Int = 0

    init {
        parentView.onLayoutThen {
            parentView.children.forEachIndexed { index, view ->
                view.setTag(positionKey, index)
            }
        }
    }

    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
        dragViewRawLeft = child.left
        dragViewRawTop = child.top
        child.fly()
        parentView.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
        return top
    }

    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
        return left
    }

    override fun getViewHorizontalDragRange(child: View): Int {
        return 1
    }

    override fun getViewVerticalDragRange(child: View): Int {
        return 1
    }

    override fun onViewReleased(view: View, xvel: Float, yvel: Float) {
        mViewDragHelper.settleCapturedViewAt(dragViewRawLeft, dragViewRawTop)
        parentView.invalidate()
    }

    override fun onViewPositionChanged(dragedView: View, left: Int, top: Int, dx: Int, dy: Int) {
        sortChildren(dragedView)
    }

    override fun onViewDragStateChanged(state: Int) {
        when (state) {
            ViewDragHelper.STATE_DRAGGING -> {
                // noop
            }
            ViewDragHelper.STATE_SETTLING -> {
                parentView.children.forEach {
                    it.ground()
                }
            }
            ViewDragHelper.STATE_IDLE -> {
                parentView.children.forEach {
                    it.ground()
                }
            }
        }
    }

    private fun sortChildren(dragedView: View) {
        parentView.apply {
            children.forEach { swapView ->
                val left = swapView.left
                val top = swapView.top
                if (swapView != dragedView) {
                    val centX = left + swapView.width / 2
                    val centY = top + swapView.height / 2
                    if (mViewDragHelper.isCapturedViewUnder(centX, centY)) {
                        val dx = dragViewRawLeft - left
                        val dy = dragViewRawTop - top
                        ViewCompat.offsetLeftAndRight(swapView, dx)
                        ViewCompat.offsetTopAndBottom(swapView, dy)
                        swapView.translationX = -dx.toFloat()
                        swapView.translationY = -dy.toFloat()
                        swapView.animate().translationX(0f).translationY(0f).start()
                        dragViewRawLeft = left
                        dragViewRawTop = top
                        onSwap(dragedView, swapView)
                        return@forEach
                    }
                }
            }
        }
    }

    private fun onSwap(dragedView: View, swapView: View) {
        val dragViewPosition = dragedView.getTag(positionKey) as Int
        val swapViewPosition = swapView.getTag(positionKey) as Int

        dragedView.setTag(positionKey, swapViewPosition)
        swapView.setTag(positionKey, dragViewPosition)

        Log.d("DragSortHelper", "$dragViewPosition swap $swapViewPosition")

        swapListener?.onSwap(dragedView, dragViewPosition, swapView, swapViewPosition)
    }

    private fun isFlying(): Boolean {
        return mViewDragHelper.viewDragState == ViewDragHelper.STATE_SETTLING
    }

    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (isFlying()) {
            return false
        }
        return mViewDragHelper.shouldInterceptTouchEvent(event)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (isFlying()) {
            return false
        }
        mViewDragHelper.processTouchEvent(event)
        return true
    }

    fun computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            parentView.invalidate()
        }
    }

    interface OnViewSwapListener {
        fun onSwap(firstView: View, firstPosition: Int, secondView: View, secondPosition: Int)
    }

    private fun View.fly() {
        elevation = 10f
        animate().scaleX(1.15f).scaleY(1.15f).alpha(0.7f).start()
    }

    private fun View.ground() {
        elevation = 0f
        animate().scaleX(1f).scaleY(1f).alpha(1f).start()
    }
}