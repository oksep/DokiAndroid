package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.util.ViewUtil

/**
 * Created by Septenary on 2019-06-23.
 */
class GridDrawableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rows: Int
    private val columns: Int
    private val inset: Int

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GridDrawableView)
        rows = ta.getInteger(R.styleable.GridDrawableView_rows, 3)
        columns = ta.getInteger(R.styleable.GridDrawableView_columns, 8)
        inset = ta.getDimensionPixelSize(R.styleable.GridDrawableView_inset, 0)
        ta.recycle()
    }

    private val items = Array(rows * columns) {
        val wrapDrawable = if (isInEditMode) ColorDrawable(ViewUtil.randomColor()) else null
        GridItem(ItemDrawable(wrapDrawable, inset).also { it.callback = this })
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

    fun setUp(list: List<String>) {
        val size = ViewUtil.getScreenWidth() / columns
        items.forEachIndexed { index, item ->
            val assetPath = list.getOrNull(index)
            if (assetPath.isNullOrEmpty()) {
                item.drawable.setDrawable(null)
            } else {
                val url = "file:///android_asset/$assetPath"
                RequestOptions().override(size, size).also { opt ->
                    Glide.with(this).load(url).apply(opt).into(object : SimpleTarget<Drawable>(size, size) {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            item.drawable.setDrawable(resource)
                        }
                    })
                }
            }
        }
    }

    private class GridItem(val drawable: ItemDrawable) {
        fun setBounds(l: Float, t: Float, r: Float, b: Float) {
            drawable.setBounds(Math.round(l), Math.round(t), Math.round(r), Math.round(b))
        }
    }

    class ItemDrawable(
        private var wrapDrawable: Drawable? = null,
        private val inset: Int
    ) : Drawable() {

        private val mPaint: Paint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        private var mBoundsF = Rect()

        override fun draw(canvas: Canvas) {
            wrapDrawable?.bounds = mBoundsF
            wrapDrawable?.draw(canvas)
            canvas.drawRect(mBoundsF, mPaint)
        }

        override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
            super.setBounds(left + inset, top + inset, right - inset, bottom - inset)
        }

        override fun onBoundsChange(bounds: Rect) {
            if (bounds.width() < bounds.height()) {
                mBoundsF.set(
                    bounds.left,
                    bounds.centerY() - bounds.width() / 2,
                    bounds.right,
                    bounds.centerY() + bounds.width() / 2
                )
            } else {
                mBoundsF.set(
                    bounds.centerX() - bounds.height() / 2,
                    bounds.top,
                    bounds.centerX() + bounds.height() / 2,
                    bounds.bottom
                )
            }
            wrapDrawable?.bounds = mBoundsF
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
}