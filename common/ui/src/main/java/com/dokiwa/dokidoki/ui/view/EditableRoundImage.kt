package com.dokiwa.dokidoki.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.graphics.toRectF
import androidx.core.view.GestureDetectorCompat
import com.dokiwa.dokidoki.ui.Log
import com.dokiwa.dokidoki.ui.R
import com.makeramen.roundedimageview.RoundedImageView

class EditableRoundImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RoundedImageView(context, attrs, defStyleAttr) {

    private val detector: GestureDetectorCompat?
    private val icon: Drawable?
    private val w: Int
    private val h: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditableRoundImage)
        icon = typedArray.getDrawable(R.styleable.EditableRoundImage_editIcon)
        w = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconWidth, 0)
        h = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconHeight, 0)
        typedArray.recycle()

        detector = if (icon != null) {
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    Log.d("AAAAAAAA", "onSingleTap")
                    return icon.bounds.toRectF().contains(e.x, e.y)
                }
            })
        } else {
            null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector?.onTouchEvent(event) == true || super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        icon?.setBounds(width - w, 0, width, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        icon?.draw(canvas)
    }
}