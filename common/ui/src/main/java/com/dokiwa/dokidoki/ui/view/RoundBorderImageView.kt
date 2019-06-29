package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import com.dokiwa.dokidoki.ui.R

class RoundBorderImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val paint: Paint
    private val rect = RectF()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundBorderImageView, defStyleAttr, 0)
        val borderColor = typedArray.getColor(R.styleable.RoundBorderImageView_borderColor, Color.TRANSPARENT)
        val borderWith = typedArray.getDimensionPixelSize(R.styleable.RoundBorderImageView_borderWidth, 0)
        typedArray.recycle()

        paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWith.toFloat()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        rect.inset(paint.strokeWidth / 2, paint.strokeWidth / 2)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(width / 2f, height / 2f, width / 2f - paint.strokeWidth / 2, paint)
    }
}
