package com.dokiwa.dokidoki.ui.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.text.style.ReplacementSpan
import androidx.core.content.ContextCompat
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.util.ViewUtil

class UnderlineSpan(
    context: Context,
    style: Style,
    color: Int = ContextCompat.getColor(context, R.color.dd_yellow),
    lineHeight: Int = ViewUtil.dp2px(context, 2F),
    offsetY: Float = ViewUtil.dp2px(context, 4.3F).toFloat()
) : ReplacementSpan() {
    enum class Style {
        DOTTED, SOLID
    }

    private val paint: Paint = Paint()
    private var textWidth: Int = 0

    private val mOffsetY = offsetY
    private val path = Path()

    init {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = lineHeight.toFloat()
        if (style == Style.DOTTED) {
            paint.pathEffect = DashPathEffect(
                floatArrayOf(
                    ViewUtil.dp2px(context, 2F).toFloat(),
                    ViewUtil.dp2px(context, 2F).toFloat()
                ), 0f
            )
        } else {
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
            paint.pathEffect = CornerPathEffect(lineHeight / 2f)
        }
        paint.isAntiAlias = true
        paint.isDither = true
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        textWidth = paint.measureText(text, start, end).toInt()
        return textWidth
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
        path.reset()
        path.moveTo(x, y + mOffsetY)
        path.lineTo(x + textWidth, y + mOffsetY)
        canvas.drawPath(path, this.paint)
    }
}