package com.dokiwa.dokidoki.ui.ext

import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.dokiwa.dokidoki.ui.Log
import com.dokiwa.dokidoki.ui.R

/**
 * Created by Septenary on 2019-07-13.
 */

private const val TAG = "BravhExt"

fun BaseItemDraggableAdapter<*, *>.enableSwipeRemove(recyclerView: RecyclerView) {
    val onItemSwipeListener = object : OnItemSwipeListener {
        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            // item 滑动的起始位置
            Log.d(TAG, "view swiped start: $pos")
        }

        override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            // item 滑动后的恢复位置
            Log.d(TAG, "View reset: $pos")
        }

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            // item 滑动的移除位置
            Log.d(TAG, "View Swiped: $pos")
        }

        override fun onItemSwipeMoving(
            canvas: Canvas,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            isCurrentlyActive: Boolean
        ) {
            Log.d(TAG, "View SwipeMoving: $dX, $dY, $isCurrentlyActive")
            // item 滑动的过程
            canvas.drawColor(ContextCompat.getColor(recyclerView.context, R.color.dd_red))
        }
    }

    val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(this)
    val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
    itemTouchHelper.attachToRecyclerView(recyclerView)
    itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START)

    enableSwipeItem()
    setOnItemSwipeListener(onItemSwipeListener)
}