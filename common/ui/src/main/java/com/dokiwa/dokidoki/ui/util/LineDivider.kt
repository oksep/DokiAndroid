package com.dokiwa.dokidoki.ui.util

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Septenary on 2018/9/10.
 */
class LineDivider(
    resources: Resources,
    @ColorRes colorRes: Int,
    @DimenRes size: Int,
    @DimenRes leftPadding: Int,
    @DimenRes rightPadding: Int
) : RecyclerView.ItemDecoration() {

    private val dividerDrawable = ColorDrawable(ResourcesCompat.getColor(resources, colorRes, null))
    private val size = resources.getDimensionPixelSize(size)
    private val leftPadding = resources.getDimensionPixelSize(leftPadding)
    private val rightPadding = resources.getDimensionPixelSize(rightPadding)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + leftPadding
        val right = parent.width - parent.paddingRight - rightPadding
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + size
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 0, 0, size)
    }
}