package com.dokiwa.dokidoki.message.widget.emoction

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.util.ViewUtil
import kotlin.math.roundToInt

/**
 * Created by Septenary on 2019-06-23.
 */
class GridDrawableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rows: Int
    private val columns: Int
    private val inset: Int

    private var onItemClick: ((GridDrawableItemData) -> Unit)? = null
    private val detector: GestureDetectorCompat

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GridDrawableView)
        rows = ta.getInteger(R.styleable.GridDrawableView_rows, 3)
        columns = ta.getInteger(R.styleable.GridDrawableView_columns, 8)
        inset = ta.getDimensionPixelSize(R.styleable.GridDrawableView_inset, 0)
        ta.recycle()

        detector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return checkTap(e.x, e.y) != null
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val tapItem = checkTap(e.x, e.y)
                return tapItem?.run {
                    val tag: GridDrawableItemData? = this.data
                    if (tag == null) {
                        false
                    } else {
                        onItemClick?.invoke(tag)
                        playSoundEffect(SoundEffectConstants.CLICK)
                        true
                    }
                } ?: false
            }
        })
    }

    private val items = Array(rows * columns) {
        val wrapDrawable = if (isInEditMode) ColorDrawable(ViewUtil.randomColor()) else null
        GridItem(
            ItemDrawable(
                wrapDrawable,
                inset
            ).also { it.callback = this })
    }

    private fun checkTap(x: Float, y: Float): GridItem? {
        return items.firstOrNull {
            it.drawable.bounds.contains(x.toInt(), y.toInt())
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val w = 1f * width / columns
        val h = 1f * height / rows
        items.forEachIndexed { index, gridItem ->
            val t = index / columns * h
            val l = index % columns * w
            gridItem.setBounds(l, t, l + w, t + h)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        items.forEach {
            it.drawable.draw(canvas)
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        if (who is ItemDrawable) {
            return who in items.map { it.drawable }
        }
        return super.verifyDrawable(who)
    }

    fun setUp(list: List<GridDrawableItemData>, onItemClick: (GridDrawableItemData) -> Unit) {
        this.onItemClick = onItemClick
        val size = ViewUtil.getScreenWidth() / columns
        items.forEachIndexed { index, item ->
            val data = list.getOrNull(index)
            val assetPath = data?.path
            item.data = data
            if (assetPath.isNullOrEmpty()) {
                item.drawable.setDrawable(null)
            } else {
                val uri = "file:///android_asset/$assetPath"
                RequestOptions().override(size, size).also { opt ->
                    Glide.with(this).load(uri).apply(opt).into(object : SimpleTarget<Drawable>(size, size) {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            item.drawable.setDrawable(resource)
                        }
                    })
                }
            }
        }
    }

    private class GridItem(val drawable: ItemDrawable, var data: GridDrawableItemData? = null) {
        fun setBounds(l: Float, t: Float, r: Float, b: Float) {
            drawable.setBounds(l.roundToInt(), t.roundToInt(), r.roundToInt(), b.roundToInt())
        }
    }

    private class ItemDrawable(
        private var wrapDrawable: Drawable? = null,
        private val inset: Int
    ) : Drawable() {

        private val mPaint: Paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        private var boundsF = Rect()

        override fun draw(canvas: Canvas) {
            wrapDrawable?.bounds = boundsF
            wrapDrawable?.draw(canvas)
            // canvas.drawRect(boundsF, mPaint)
        }

        override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
            super.setBounds(left + inset, top + inset, right - inset, bottom - inset)
        }

        override fun onBoundsChange(bounds: Rect) {
            if (bounds.width() < bounds.height()) {
                boundsF.set(
                    bounds.left,
                    bounds.centerY() - bounds.width() / 2,
                    bounds.right,
                    bounds.centerY() + bounds.width() / 2
                )
            } else {
                boundsF.set(
                    bounds.centerX() - bounds.height() / 2,
                    bounds.top,
                    bounds.centerX() + bounds.height() / 2,
                    bounds.bottom
                )
            }
            wrapDrawable?.bounds = boundsF
        }

        override fun setAlpha(alpha: Int) {
            mPaint.alpha = alpha
            wrapDrawable?.alpha = alpha
            invalidateSelf()
        }

        override fun getOpacity(): Int = wrapDrawable?.opacity ?: mPaint.alpha

        override fun setColorFilter(colorFilter: ColorFilter?) {
            wrapDrawable?.colorFilter = colorFilter
            mPaint.colorFilter = colorFilter
            invalidateSelf()
        }

        fun setDrawable(drawable: Drawable?) {
            wrapDrawable = drawable?.also {
                it.bounds = bounds
                it.callback = callback
            }
            invalidateSelf()
        }
    }

    data class GridDrawableItemData(val path: String, val tag: String)
}