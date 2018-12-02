package com.dokiwa.dokidoki.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.ext.dp2px
import kotlin.random.Random

class WaterMaskView : View {

    private lateinit var paint: TextPaint
    private lateinit var text: String
    private var angle = 45 / 2f
    private var textHeight = 0F
    private val rectF = RectF()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WaterMaskView)
        text = a.getString(R.styleable.WaterMaskView_wm_text) ?: ""
        angle = a.getFloat(R.styleable.WaterMaskView_wm_angle, angle)
        a.recycle()

        paint = TextPaint()
        paint.color = ResourcesCompat.getColor(resources, R.color.white_10, null)
        paint.textSize = context.dp2px(32F).toFloat()
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        paint.textScaleX = 1.1f
        paint.typeface = Typeface.create("Arial", Typeface.NORMAL)
        textHeight = paint.fontMetrics.run { bottom - top - descent }
    }

    override fun onDraw(canvas: Canvas) {
        var dy = 0f
        var flag = true
        while (dy < height) {
            drawRow(canvas, dy, flag)
            dy += textHeight * 3
            flag = !flag
        }
    }

    private fun drawRow(canvas: Canvas, dy: Float, flag: Boolean) {
        val count = canvas.save()
        canvas.rotate(angle, width / 2f, dy + textHeight)
        val columnWidth = width / 3f * 1.5f
        val dx = columnWidth * 0.5f * if (flag) 0 else -1
        for (i in 0 until 3) {
            rectF.set(
                columnWidth * i + dx,
                dy,
                columnWidth * (i + 1) + dx,
                dy + textHeight
            )
            // canvas.drawRect(rectF, paint.withRandomColor())
            canvas.drawText(
                text,
                rectF.centerX(),
                rectF.bottom,
                paint
            )
        }
        canvas.restoreToCount(count)
    }

    private fun Paint.withRandomColor(): Paint {
        this.setARGB(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        return this
    }
}