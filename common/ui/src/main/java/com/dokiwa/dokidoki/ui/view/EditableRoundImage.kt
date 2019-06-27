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
import com.dokiwa.dokidoki.ui.R
import com.dokiwa.dokidoki.ui.util.DragSortHelper
import com.makeramen.roundedimageview.RoundedImageView

class EditableRoundImage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RoundedImageView(context, attrs, defStyleAttr), DragSortHelper.IDragSortView {

    private val h: Int
    private val w: Int
    private val icon: Drawable?
    private val detector: GestureDetectorCompat?
    private var isFLy = false

    var onCloseListener: (() -> Unit)? = null
    var editTag: Any? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditableRoundImage)
        icon = typedArray.getDrawable(R.styleable.EditableRoundImage_editIcon)?.mutate()
        w = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconWidth, 0)
        h = typedArray.getDimensionPixelSize(R.styleable.EditableRoundImage_editIconHeight, 0)
        typedArray.recycle()

        detector = if (icon != null) {
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    onCloseListener?.invoke()
                    return true
                }
            })
        } else {
            null
        }
    }

    private fun isTouchInIconArea(e: MotionEvent): Boolean {
        return icon?.bounds?.toRectF()?.contains(e.x, e.y) ?: false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isTouchInIconArea(event)) {
            detector?.onTouchEvent(event)
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        icon?.setBounds(width - w, 0, width, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isFLy) {
            icon?.draw(canvas)
        }
    }

    override fun onFly() {
        isFLy = true
        animate().scaleX(0.85f).scaleY(0.85f).alpha(0.75f).start()
        invalidate()
    }

    override fun onGround() {
        isFLy = false
        animate().scaleX(1f).scaleY(1f).alpha(1f).start()
        invalidate()
    }
}